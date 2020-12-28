package com.lingting.gzm.virtual.currency.service.impl;

import cn.hutool.core.thread.ThreadUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.lingting.gzm.virtual.currency.VirtualCurrencyAccount;
import com.lingting.gzm.virtual.currency.VirtualCurrencyTransaction;
import com.lingting.gzm.virtual.currency.VirtualCurrencyTransferResult;
import com.lingting.gzm.virtual.currency.contract.Contract;
import com.lingting.gzm.virtual.currency.contract.OmniContract;
import com.lingting.gzm.virtual.currency.endpoints.Endpoints;
import com.lingting.gzm.virtual.currency.enums.TransactionStatus;
import com.lingting.gzm.virtual.currency.enums.VcPlatform;
import com.lingting.gzm.virtual.currency.omni.Balances;
import com.lingting.gzm.virtual.currency.omni.Domain;
import com.lingting.gzm.virtual.currency.omni.TokenHistory;
import com.lingting.gzm.virtual.currency.omni.TransactionByHash;
import com.lingting.gzm.virtual.currency.properties.OmniProperties;
import com.lingting.gzm.virtual.currency.service.VirtualCurrencyService;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lingting 2020-09-01 17:16
 */
@Slf4j
@RequiredArgsConstructor
public class OmniServiceImpl implements VirtualCurrencyService {

	/**
	 * 精度需要计算的标志
	 */
	public static final String FLAG = ".";

	private final OmniProperties properties;

	@Getter
	private static final Map<String, Integer> CONTRACT_DECIMAL_CACHE = new ConcurrentHashMap<>();

	/**
	 * 用于调用of方法生成新对象
	 */
	private static final TransactionByHash STATIC_TRANSACTION_HASH = new TransactionByHash();

	/**
	 * 用于调用of方法生成新对象
	 */
	private static final Balances STATIC_BALANCES = new Balances();

	/**
	 * 用于调用of方法生成新对象
	 */
	private static final TokenHistory STATIC_TOKEN_HISTORY = new TokenHistory();

	@Override
	public Optional<VirtualCurrencyTransaction> getTransactionByHash(String hash) throws JsonProcessingException {
		TransactionByHash response = request(STATIC_TRANSACTION_HASH, properties.getEndpoints(), hash);
		// 交易查询不到 或者 valid 为 false
		if (response.getAmount() == null || !response.getValid()) {
			return Optional.empty();
		}

		OmniContract contract = OmniContract.getById(response.getPropertyId());
		VirtualCurrencyTransaction transaction = new VirtualCurrencyTransaction()

				.setContract(contract != null ? contract : new Contract() {
					@Override
					public String getHash() {
						return response.getPropertyId().toString();
					}

					@Override
					public Integer getDecimals() {
						return null;
					}
				})

				.setBlock(response.getBlock())

				.setHash(hash)

				.setValue(response.getAmount())

				.setVcPlatform(VcPlatform.OMNI)

				.setTime(response.getBlockTime())

				.setFrom(response.getSendingAddress())

				.setTo(response.getReferenceAddress())
				// 大于等于 配置的最小值则确认导致,否则等待
				.setStatus(response.getConfirmations() >= properties.getConfirmationsMin() ? TransactionStatus.SUCCESS
						: TransactionStatus.WAIT);
		return Optional.of(transaction);
	}

	@Override
	public Integer getDecimalsByContract(Contract contract) throws JsonProcessingException {
		if (contract == null) {
			return 0;
		}

		if (contract.getDecimals() != null) {
			return contract.getDecimals();
		}

		if (CONTRACT_DECIMAL_CACHE.containsKey(contract.getHash())) {
			return CONTRACT_DECIMAL_CACHE.get(contract.getHash());
		}

		TokenHistory history = request(STATIC_TOKEN_HISTORY, properties.getEndpoints(), contract.getHash());
		int decimals = getDecimalsByString(history.getTransactions().get(0).getAmount());
		CONTRACT_DECIMAL_CACHE.put(contract.getHash(), decimals);
		return decimals;
	}

	@Override
	public BigDecimal getBalanceByAddressAndContract(String address, Contract contract) throws JsonProcessingException {
		Balances balances = request(STATIC_BALANCES, properties.getEndpoints(), address);
		for (Balances.Balance balance : balances.getBalance()) {
			// 协助缓存精度
			if (!CONTRACT_DECIMAL_CACHE.containsKey(contract.getHash())) {
				CONTRACT_DECIMAL_CACHE.put(contract.getHash(),
						getDecimalsByString(balance.getPropertyInfo().getTotalTokens()));
			}

			if (balance.getId().equals(contract.getHash())) {
				return balance.getValue();
			}
		}
		return BigDecimal.ZERO;
	}

	@Override
	public BigDecimal getNumberByBalanceAndContract(BigDecimal balance, Contract contract, MathContext mathContext)
			throws JsonProcessingException {
		if (contract == null) {
			return balance;
		}
		if (balance == null) {
			return BigDecimal.ZERO;
		}
		// 计算返回值
		return balance.divide(BigDecimal.TEN.pow(getDecimalsByContract(contract)), mathContext);
	}

	@Override
	public VirtualCurrencyTransferResult transfer(VirtualCurrencyAccount from, String to, Contract contract,
			BigDecimal value) {
		return null;
	}

	/**
	 * 通过 str 计算精度
	 *
	 * @author lingting 2020-12-14 13:51
	 */
	private int getDecimalsByString(String str) {
		if (!str.contains(FLAG)) {
			return 0;
		}
		return str.substring(str.indexOf(FLAG)).length() - 1;
	}

	/**
	 * 休眠时间,如果不允许请求,则手动休眠, 默认5秒
	 * @return 单位: 毫秒
	 * @author lingting 2020-12-14 16:38
	 */
	public long sleepTime() {
		return TimeUnit.SECONDS.toMillis(5);
	}

	/**
	 * 发起请求
	 *
	 * @author lingting 2020-12-14 16:46
	 */
	private <T> T request(Domain<T> domain, Endpoints endpoints, Object params) throws JsonProcessingException {
		// 获取锁
		if (properties.getLock().get()) {
			// 执行请求方法
			T t = domain.of(endpoints, params);
			// 释放锁
			properties.getUnlock().get();
			return t;
		}
		// 休眠, 然后调用自身
		ThreadUtil.sleep(sleepTime());
		return request(domain, endpoints, params);
	}

}
