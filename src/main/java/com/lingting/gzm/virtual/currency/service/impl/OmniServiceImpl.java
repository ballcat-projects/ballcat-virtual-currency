package com.lingting.gzm.virtual.currency.service.impl;

import cn.hutool.http.HttpRequest;
import com.lingting.gzm.virtual.currency.VirtualCurrencyTransaction;
import com.lingting.gzm.virtual.currency.contract.Contract;
import com.lingting.gzm.virtual.currency.contract.Etherscan;
import com.lingting.gzm.virtual.currency.contract.Omni;
import com.lingting.gzm.virtual.currency.endpoints.OmniEndpoints;
import com.lingting.gzm.virtual.currency.enums.TransactionStatus;
import com.lingting.gzm.virtual.currency.enums.VcPlatform;
import com.lingting.gzm.virtual.currency.omni.Balances;
import com.lingting.gzm.virtual.currency.omni.TokenHistory;
import com.lingting.gzm.virtual.currency.omni.TransactionByHash;
import com.lingting.gzm.virtual.currency.properties.OmniProperties;
import com.lingting.gzm.virtual.currency.service.VirtualCurrencyService;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
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

	private final HttpRequest tokenHistoryRequest = HttpRequest.post(OmniEndpoints.MAINNET.getHttp())
			.body("{\"page\":0}", "application/x-www-form-urlencoded");

	private final HttpRequest balanceRequest = HttpRequest.post(OmniEndpoints.MAINNET.getHttp());

	@Getter
	private static final Map<Contract, Integer> CONTRACT_DECIMAL_CACHE;

	static {
		CONTRACT_DECIMAL_CACHE = new ConcurrentHashMap<>(Etherscan.values().length + 1);
		CONTRACT_DECIMAL_CACHE.put(Omni.BTC, 0);
		CONTRACT_DECIMAL_CACHE.put(Omni.OMNI, 8);
		CONTRACT_DECIMAL_CACHE.put(Omni.USDT, 8);
		CONTRACT_DECIMAL_CACHE.put(Omni.MAID_SAFE_COIN, 0);
	}

	@Override
	@SneakyThrows
	public Optional<VirtualCurrencyTransaction> getTransactionByHash(String hash) {
		TransactionByHash response = TransactionByHash.of(balanceRequest, properties.getEndpoints(), hash);
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
	public Integer getDecimalsByContract(Contract contract) {
		if (CONTRACT_DECIMAL_CACHE.containsKey(contract)) {
			return CONTRACT_DECIMAL_CACHE.get(contract);
		}

		TokenHistory history = TokenHistory.of(tokenHistoryRequest, properties.getEndpoints(), contract.getHash());
		int decimals = getDecimalsByString(history.getTransactions().get(0).getAmount());
		CONTRACT_DECIMAL_CACHE.put(contract, decimals);
		return decimals;
	}

	@Override
	public BigDecimal getBalanceByAddressAndContract(String address, Contract contract) {
		Balances balances = Balances.of(balanceRequest, properties.getEndpoints(), address);
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
	public BigDecimal getNumberByBalanceAndContract(BigDecimal balance, Contract contract, MathContext mathContext) {
		if (contract == null) {
			return balance;
		}

		// 计算返回值
		return balance.divide(BigDecimal.TEN.pow(getDecimalsByContract(contract)), mathContext);
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

}
