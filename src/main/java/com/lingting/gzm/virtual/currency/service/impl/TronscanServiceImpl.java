package com.lingting.gzm.virtual.currency.service.impl;

import static com.lingting.gzm.virtual.currency.util.TronscanUtil.isTrc20;
import static com.lingting.gzm.virtual.currency.util.TronscanUtil.resolve;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.lingting.gzm.virtual.currency.VirtualCurrencyAccount;
import com.lingting.gzm.virtual.currency.VirtualCurrencyTransaction;
import com.lingting.gzm.virtual.currency.VirtualCurrencyTransferResult;
import com.lingting.gzm.virtual.currency.contract.Contract;
import com.lingting.gzm.virtual.currency.contract.TronscanContract;
import com.lingting.gzm.virtual.currency.endpoints.Endpoints;
import com.lingting.gzm.virtual.currency.enums.TransactionStatus;
import com.lingting.gzm.virtual.currency.enums.VcPlatform;
import com.lingting.gzm.virtual.currency.exception.VirtualCurrencyException;
import com.lingting.gzm.virtual.currency.properties.TronscanProperties;
import com.lingting.gzm.virtual.currency.service.VirtualCurrencyService;
import com.lingting.gzm.virtual.currency.tronscan.Account;
import com.lingting.gzm.virtual.currency.tronscan.Transaction;
import com.lingting.gzm.virtual.currency.tronscan.TransactionInfo;
import com.lingting.gzm.virtual.currency.tronscan.Trc10;
import com.lingting.gzm.virtual.currency.tronscan.Trc20Data;
import com.lingting.gzm.virtual.currency.util.TronscanUtil;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lingting 2020-09-01 17:16
 */
@Slf4j
public class TronscanServiceImpl implements VirtualCurrencyService {

	private final TronscanProperties properties;

	private final Endpoints endpoints;

	private final HttpRequest accountRequest;

	private final HttpRequest transactionRequest;

	private final HttpRequest transactionInfoRequest;

	private final HttpRequest trc10Request;

	@Getter
	private static final Map<String, Integer> CONTRACT_DECIMAL_CACHE = new ConcurrentHashMap<>();

	public TronscanServiceImpl(TronscanProperties properties) throws VirtualCurrencyException {
		this.properties = properties;
		this.endpoints = properties.getEndpoints();

		accountRequest = HttpRequest.get(endpoints.getHttp());
		transactionRequest = HttpRequest.post(endpoints.getHttp());
		transactionInfoRequest = HttpRequest.post(endpoints.getHttp());
		trc10Request = HttpRequest.post(endpoints.getHttp());
	}

	@Override
	public Optional<VirtualCurrencyTransaction> getTransactionByHash(String hash) throws Exception {
		Transaction transaction = Transaction.of(transactionRequest, endpoints, hash);

		// 没有返回txId 表示此交易未被确认 或 不存在
		if (StrUtil.isBlank(transaction.getTxId())) {
			return Optional.empty();
		}

		// 生成返回值
		VirtualCurrencyTransaction vcTransaction = new VirtualCurrencyTransaction()
				// 平台
				.setVcPlatform(VcPlatform.TRONSCAN)
				// hash
				.setHash(hash);
		// 原始数据
		Transaction.RawData rawData = transaction.getRawData();
		// 合约参数
		Transaction.RawData.Contract.Parameter.Value data = rawData.getContract().get(0).getParameter().getValue();
		// trx 或 trc10 交易
		if (data.getAmount() != null) {
			// trx 交易
			if (StrUtil.isBlank(data.getAssetName())) {
				// 合约
				vcTransaction.setContract(TronscanContract.TRX);
			}
			// trc10 交易
			else {
				// 合约
				vcTransaction.setContract(TronscanContract.getByHash(data.getAssetName()));
				// 如果合约未找到
				if (vcTransaction.getContract() == null) {
					vcTransaction.setContract(new Contract() {
						@Override
						public String getHash() {
							return data.getAssetName();
						}

						@Override
						public Integer getDecimals() {
							return null;
						}
					});
				}
			}

			vcTransaction
					// 转账金额
					.setValue(getNumberByBalanceAndContract(data.getAmount(), vcTransaction.getContract()))
					// 转账人
					.setFrom(data.getOwnerAddress())
					// 收款人
					.setTo(data.getToAddress());
		}
		// trc20 交易
		else {
			// 合约
			vcTransaction.setContract(TronscanContract.getByHash(data.getContractAddress()));
			// 如果合约未找到
			if (vcTransaction.getContract() == null) {
				vcTransaction.setContract(new Contract() {
					@Override
					public String getHash() {
						return data.getContractAddress();
					}

					@Override
					public Integer getDecimals() {
						return null;
					}
				});
			}
			// 解析数据
			Trc20Data resolve = resolve(data.getData());

			vcTransaction
					// 转账金额
					.setValue(getNumberByBalanceAndContract(resolve.getAmount(), vcTransaction.getContract()))
					// 转账人
					.setFrom(data.getOwnerAddress())
					// 收款人
					.setTo(resolve.getTo());
		}

		// 获取交易详细信息
		TransactionInfo info = TransactionInfo.of(transactionInfoRequest, endpoints, hash);
		vcTransaction
				// 块高度
				.setBlock(info.getBlockNumber())
				// 交易时间, 毫秒转秒
				.setTime(info.getBlockTimeStamp() / 1000);
		// 交易状态
		List<Transaction.Ret> rets = transaction.getRet();
		// 失败
		if (CollectionUtil.isEmpty(rets) || !rets.get(0).getContractRet().equals(Transaction.Ret.SUCCESS)) {
			vcTransaction.setStatus(TransactionStatus.FAIL);
		}
		// 成功
		else {
			vcTransaction.setStatus(TransactionStatus.SUCCESS);
		}
		return Optional.of(vcTransaction);
	}

	@Override
	public Integer getDecimalsByContract(com.lingting.gzm.virtual.currency.contract.Contract contract)
			throws JsonProcessingException {
		if (contract == null) {
			return 0;
		}
		if (contract.getDecimals() != null) {
			return contract.getDecimals();
		}
		if (CONTRACT_DECIMAL_CACHE.containsKey(contract.getHash())) {
			return CONTRACT_DECIMAL_CACHE.get(contract.getHash());
		}
		int decimals;

		// trc20 查询
		if (isTrc20(contract.getHash())) {
			decimals = TronscanUtil.getDecimalByTrc20(endpoints, contract);
		}
		// trc10 查询
		else {
			Trc10 trc10 = Trc10.of(trc10Request, endpoints, contract.getHash());
			decimals = trc10.getPrecision() == null ? 0 : trc10.getPrecision();
		}

		CONTRACT_DECIMAL_CACHE.put(contract.getHash(), decimals);
		return decimals;
	}

	@Override
	public BigDecimal getBalanceByAddressAndContract(String address,
			com.lingting.gzm.virtual.currency.contract.Contract contract) throws JsonProcessingException {
		Account account = Account.of(accountRequest, endpoints, address);

		// 搜索拥有的数据
		if (account.getData().size() == 0) {
			return BigDecimal.ZERO;
		}

		Account.Data data = account.getData().get(0);
		if (contract == TronscanContract.TRX) {
			return data.getBalance();
		}

		// trc20
		if (isTrc20(contract.getHash())) {
			// 从trc20中寻找
			for (Map<String, BigDecimal> map : data.getTrc20()) {
				for (Map.Entry<String, BigDecimal> entry : map.entrySet()) {
					// 如果指定合约的hash 与当前trc20 key相同
					if (entry.getKey().equals(contract.getHash())) {
						return entry.getValue();
					}
				}
			}
		}
		// 非 trc20
		else {
			// 从assetV2中寻找
			for (Account.Data.AssetV2 v2 : data.getAssetV2()) {
				// 如果指定合约的hash 与当前v2数据相同
				if (v2.getKey().equals(contract.getHash())) {
					return v2.getValue();
				}
			}
		}

		// 未找到合约, 返回 0
		return BigDecimal.ZERO;
	}

	@Override
	public BigDecimal getNumberByBalanceAndContract(BigDecimal balance,
			com.lingting.gzm.virtual.currency.contract.Contract contract, MathContext mathContext)
			throws JsonProcessingException {
		// 合约为null 返回原值
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

}
