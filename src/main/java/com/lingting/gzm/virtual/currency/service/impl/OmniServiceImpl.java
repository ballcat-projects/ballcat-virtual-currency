package com.lingting.gzm.virtual.currency.service.impl;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.http.HttpRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.lingting.gzm.virtual.currency.VirtualCurrencyAccount;
import com.lingting.gzm.virtual.currency.VirtualCurrencyTransaction;
import com.lingting.gzm.virtual.currency.VirtualCurrencyTransferResult;
import com.lingting.gzm.virtual.currency.contract.Contract;
import com.lingting.gzm.virtual.currency.contract.Etherscan;
import com.lingting.gzm.virtual.currency.contract.Omni;
import com.lingting.gzm.virtual.currency.endpoints.Endpoints;
import com.lingting.gzm.virtual.currency.endpoints.OmniEndpoints;
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
import lombok.SneakyThrows;
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

	private final HttpRequest transactionByHashRequest = HttpRequest.get(OmniEndpoints.MAINNET.getHttp());

	private final HttpRequest tokenHistoryRequest = HttpRequest.post(OmniEndpoints.MAINNET.getHttp()).form("page", 0);

	private final HttpRequest balanceRequest = HttpRequest.post(OmniEndpoints.MAINNET.getHttp());

	@Getter
	private static final Map<Contract, Integer> CONTRACT_DECIMAL_CACHE;

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

	static {
		CONTRACT_DECIMAL_CACHE = new ConcurrentHashMap<>(Etherscan.values().length + 1);
		CONTRACT_DECIMAL_CACHE.put(Omni.BTC, 0);
		CONTRACT_DECIMAL_CACHE.put(Omni.OMNI, 8);
		CONTRACT_DECIMAL_CACHE.put(Omni.USDT, 8);
		CONTRACT_DECIMAL_CACHE.put(Omni.MAID_SAFE_COIN, 0);
	}

	@Override
	public Optional<VirtualCurrencyTransaction> getTransactionByHash(String hash) throws JsonProcessingException {
		TransactionByHash response = request(STATIC_TRANSACTION_HASH, transactionByHashRequest,
				properties.getEndpoints(), hash);
		// 交易查询不到 或者 valid 为 false
		if (response.getAmount() == null || !response.getValid()) {
			return Optional.empty();
		}

		VirtualCurrencyTransaction transaction = new VirtualCurrencyTransaction()

				.setContract(Omni.getById(response.getPropertyId()))

				.setContractAddress(response.getPropertyId().toString())

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
		if (CONTRACT_DECIMAL_CACHE.containsKey(contract)) {
			return CONTRACT_DECIMAL_CACHE.get(contract);
		}

		TokenHistory history = request(STATIC_TOKEN_HISTORY, tokenHistoryRequest, properties.getEndpoints(),
				contract.getHash());
		int decimals = getDecimalsByString(history.getTransactions().get(0).getAmount());
		CONTRACT_DECIMAL_CACHE.put(contract, decimals);
		return decimals;
	}

	@Override
	public BigDecimal getBalanceByAddressAndContract(String address, Contract contract) throws JsonProcessingException {
		Balances balances = request(STATIC_BALANCES, balanceRequest, properties.getEndpoints(), address);
		for (Balances.Balance balance : balances.getBalance()) {
			// 协助缓存精度
			if (!CONTRACT_DECIMAL_CACHE.containsKey(contract)) {
				CONTRACT_DECIMAL_CACHE.put(contract, getDecimalsByString(balance.getPropertyInfo().getTotalTokens()));
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
	private <T> T request(Domain<T> domain, HttpRequest request, Endpoints endpoints, Object params)
			throws JsonProcessingException {
		// 获取锁
		if (properties.getLock().get()) {
			// 执行请求方法
			T t = domain.of(request, endpoints, params);
			// 释放锁
			properties.getUnlock().get();
			return t;
		}
		// 休眠, 然后调用自身
		ThreadUtil.sleep(sleepTime());
		return request(domain, request, endpoints, params);
	}

}
