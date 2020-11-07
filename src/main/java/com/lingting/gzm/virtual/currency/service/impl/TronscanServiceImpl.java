package com.lingting.gzm.virtual.currency.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import com.lingting.gzm.virtual.currency.contract.Tronscan;
import com.lingting.gzm.virtual.currency.endpoints.TronscanEndpoints;
import com.lingting.gzm.virtual.currency.enums.TransactionStatus;
import com.lingting.gzm.virtual.currency.properties.TronscanProperties;
import com.lingting.gzm.virtual.currency.service.VirtualCurrencyService;
import com.lingting.gzm.virtual.currency.transaction.TronscanTransaction;
import com.lingting.gzm.virtual.currency.transaction.VirtualCurrencyTransaction;
import com.lingting.gzm.virtual.currency.util.JsonUtil;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lingting 2020-09-01 17:16
 */
@Slf4j
@RequiredArgsConstructor
public class TronscanServiceImpl implements VirtualCurrencyService {

	/**
	 * 交易成功时的返回结果
	 */
	private static final String SUCCESS_RET = "SUCCESS";

	private final TronscanProperties properties;

	private final HttpRequest request = HttpRequest.get(TronscanEndpoints.MAINNET.getHttp());

	@Override
	@SneakyThrows
	public Optional<VirtualCurrencyTransaction> getTransactionByHash(String hash) {
		String res = request.setUrl(properties.getTransactionUrlByHash(hash)).execute().body();
		TronscanTransaction tronscanTransaction = JsonUtil.readValue(res, TronscanTransaction.class);
		if (tronscanTransaction == null || StrUtil.isEmpty(tronscanTransaction.getContractRet())) {
			return Optional.empty();
		}

		Tronscan tronscan = Tronscan.getByHash(tronscanTransaction.getContractType().toString());

		VirtualCurrencyTransaction transaction = new VirtualCurrencyTransaction()
				.setStatus(SUCCESS_RET.equals(tronscanTransaction.getContractRet()) ? TransactionStatus.SUCCESS
						: TransactionStatus.FAIL)
				.setHash(hash).setBlock(tronscanTransaction.getBlock()).setContract(tronscan);

		// usdt
		if (tronscan == Tronscan.USDT) {
			TronscanTransaction.TokenTransferInfo tokenTransferInfo = tronscanTransaction.getTokenTransferInfo();
			transaction.setFrom(tokenTransferInfo.getFromAddress()).setTo(tokenTransferInfo.getToAddress())
					.setValue(new BigDecimal(tokenTransferInfo.getAmountStr()).divide(
							BigDecimal.valueOf(Math.pow(10, tokenTransferInfo.getDecimals())), MathContext.UNLIMITED));
		}
		else if (tronscan == Tronscan.TOKEN) {
			TronscanTransaction.ContractData contractData = tronscanTransaction.getContractData();
			transaction.setFrom(contractData.getOwnerAddress()).setTo(contractData.getToAddress())
					.setValue(new BigDecimal(contractData.getAmount()));
		}
		else {
			TronscanTransaction.ContractData contractData = tronscanTransaction.getContractData();
			transaction.setFrom(contractData.getOwnerAddress()).setTo(contractData.getToAddress()).setValue(
					new BigDecimal(contractData.getAmount()).divide(new BigDecimal(1000000), MathContext.UNLIMITED));
		}

		// 这里返回的时间单位是 毫秒，需要转为秒
		return Optional
				.of(transaction.setTime(tronscanTransaction.getTimestamp() / 1000).setDelay(properties.getDelay()));
	}

}
