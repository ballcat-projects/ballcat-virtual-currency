package com.lingting.gzm.virtual.currency.service.impl;

import cn.hutool.http.HttpRequest;
import com.lingting.gzm.virtual.currency.contract.Btc;
import com.lingting.gzm.virtual.currency.endpoints.OmniEndpoints;
import com.lingting.gzm.virtual.currency.enums.Protocol;
import com.lingting.gzm.virtual.currency.enums.TransactionStatus;
import com.lingting.gzm.virtual.currency.properties.OmniProperties;
import com.lingting.gzm.virtual.currency.service.VirtualCurrencyService;
import com.lingting.gzm.virtual.currency.transaction.OmniTransaction;
import com.lingting.gzm.virtual.currency.transaction.VirtualCurrencyTransaction;
import com.lingting.gzm.virtual.currency.util.JsonUtil;
import java.math.BigDecimal;
import java.util.Optional;
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
	 * 至少多少个确认数， 认得交易成功
	 */
	private static final int SUCCESS_CONFIRMATIONS_MIN = 6;

	private final OmniProperties properties;

	private final HttpRequest request = HttpRequest.get(OmniEndpoints.MAINNET.getHttp());

	@Override
	@SneakyThrows
	public Optional<VirtualCurrencyTransaction> getTransactionByHash(String hash) {
		OmniTransaction omniTransaction = JsonUtil.readValue(
				request.setUrl(properties.getTransactionUrlByHash(hash)).execute().body(), OmniTransaction.class);

		// 返回值为null 或者 转账方为null
		if (omniTransaction == null || omniTransaction.getFrom() == null) {
			return Optional.empty();
		}

		return Optional.of(new VirtualCurrencyTransaction().setBlock(omniTransaction.getBlock())
				.setBlockHash(omniTransaction.getBlockHash())
				.setContract(Btc.getByHash(omniTransaction.getPropertyId().toString()))
				.setFrom(omniTransaction.getFrom()).setTo(omniTransaction.getTo())
				.setValue(new BigDecimal(omniTransaction.getAmount())).setProtocol(Protocol.BTC)
				// 如果已确数小于 SUCCESS_CONFIRMATIONS_MIN 值，则不算交易成功
				.setStatus(omniTransaction.getConfirmations() < SUCCESS_CONFIRMATIONS_MIN ? TransactionStatus.FAIL
						: TransactionStatus.SUCCESS)
				.setHash(hash).setTime(omniTransaction.getBlockTime()).setDelay(properties.getDelay()));
	}

}
