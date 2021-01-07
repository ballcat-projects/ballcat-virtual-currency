package live.lingting.virtual.currency.service.impl;

import static live.lingting.virtual.currency.tronscan.Transaction.Ret;
import static live.lingting.virtual.currency.tronscan.Transaction.of;
import static live.lingting.virtual.currency.util.TronscanUtil.isTrc20;
import static live.lingting.virtual.currency.util.TronscanUtil.resolve;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Hex;
import org.tron.tronj.crypto.SECP256K1;
import org.tron.tronj.crypto.tuweniTypes.Bytes32;
import live.lingting.virtual.currency.Account;
import live.lingting.virtual.currency.Transaction;
import live.lingting.virtual.currency.TransferParams;
import live.lingting.virtual.currency.TransferResult;
import live.lingting.virtual.currency.contract.Contract;
import live.lingting.virtual.currency.contract.TronscanContract;
import live.lingting.virtual.currency.endpoints.Endpoints;
import live.lingting.virtual.currency.enums.TransactionStatus;
import live.lingting.virtual.currency.enums.VcPlatform;
import live.lingting.virtual.currency.properties.TronscanProperties;
import live.lingting.virtual.currency.service.VirtualCurrencyService;
import live.lingting.virtual.currency.tronscan.Transaction.RawData;
import live.lingting.virtual.currency.tronscan.TransactionInfo;
import live.lingting.virtual.currency.tronscan.Trc10;
import live.lingting.virtual.currency.tronscan.Trc20Data;
import live.lingting.virtual.currency.tronscan.TriggerRequest;
import live.lingting.virtual.currency.tronscan.TriggerResult;
import live.lingting.virtual.currency.tronscan.TriggerResult.TransferBroadcastResult;
import live.lingting.virtual.currency.tronscan.TriggerResult.Trc10TransferGenerateResult;
import live.lingting.virtual.currency.tronscan.TriggerResult.Trc20TransferGenerateResult;
import live.lingting.virtual.currency.util.AbiUtil;
import live.lingting.virtual.currency.util.TronscanUtil;

/**
 * @author lingting 2020-09-01 17:16
 */
@Slf4j
public class TronscanServiceImpl implements VirtualCurrencyService {

	@Getter
	private static final Map<String, Integer> CONTRACT_DECIMAL_CACHE = new ConcurrentHashMap<>();

	private final TronscanProperties properties;

	private final Endpoints endpoints;

	public TronscanServiceImpl(TronscanProperties properties) {
		this.properties = properties;
		// 自定义url
		if (StrUtil.isNotBlank(properties.getUrl())) {
			endpoints = properties::getUrl;
		}
		// 未自定义url
		else {
			this.endpoints = properties.getEndpoints();
		}
	}

	@Override
	public Optional<Transaction> getTransactionByHash(String hash) throws Throwable {
		live.lingting.virtual.currency.tronscan.Transaction transaction = of(endpoints, hash);

		// 没有返回txId 表示此交易未被确认 或 不存在
		if (StrUtil.isBlank(transaction.getTxId())) {
			return Optional.empty();
		}

		// 生成返回值
		Transaction vcTransaction = new Transaction()
				// 平台
				.setVcPlatform(VcPlatform.TRONSCAN)
				// hash
				.setHash(hash);
		// 原始数据
		RawData rawData = transaction.getRawData();
		// 合约参数
		RawData.Contract.Parameter.Value data = rawData.getContract().get(0).getParameter().getValue();
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
					vcTransaction.setContract(AbiUtil.createContract(data.getAssetName()));
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
			// 解析数据
			Trc20Data resolve = resolve(data.getData());

			// 解析数据中有合约
			if (resolve.getContract() != null) {
				vcTransaction.setContract(resolve.getContract());
			}
			// 没有合约
			else {
				vcTransaction.setContract(TronscanContract.getByHash(data.getContractAddress()));
			}
			// 如果合约未找到
			if (vcTransaction.getContract() == null) {
				vcTransaction.setContract(AbiUtil.createContract(data.getContractAddress()));
			}

			vcTransaction
					// 转账金额
					.setValue(getNumberByBalanceAndContract(resolve.getAmount(), vcTransaction.getContract()))
					// 转账人
					.setFrom(StrUtil.isNotBlank(resolve.getFrom()) ? resolve.getFrom() : data.getOwnerAddress())
					// 收款人
					.setTo(resolve.getTo());
		}

		// 获取交易详细信息
		TransactionInfo info = TransactionInfo.of(endpoints, hash);
		vcTransaction
				// 块高度
				.setBlock(info.getBlockNumber())
				// 交易时间, 毫秒转秒
				.setTime(info.getBlockTimeStamp() / 1000);
		// 交易状态
		List<Ret> rets = transaction.getRet();
		// 失败
		if (CollectionUtil.isEmpty(rets) || !rets.get(0).getContractRet().equals(Ret.SUCCESS)) {
			vcTransaction.setStatus(TransactionStatus.FAIL);
		}
		// 成功
		else {
			vcTransaction.setStatus(TransactionStatus.SUCCESS);
		}
		return Optional.of(vcTransaction);
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
		int decimals;

		// trc20 查询
		if (isTrc20(contract.getHash())) {
			decimals = TronscanUtil.getDecimalByTrc20(endpoints, contract);
		}
		// trc10 查询
		else {
			Trc10 trc10 = Trc10.of(endpoints, contract.getHash());
			decimals = trc10.getPrecision() == null ? 0 : trc10.getPrecision();
		}

		CONTRACT_DECIMAL_CACHE.put(contract.getHash(), decimals);
		return decimals;
	}

	@Override
	public BigDecimal getBalanceByAddressAndContract(String address, Contract contract) throws JsonProcessingException {
		live.lingting.virtual.currency.tronscan.Account account = live.lingting.virtual.currency.tronscan.Account
				.of(endpoints, address);

		// 搜索拥有的数据
		if (account.getData().size() == 0) {
			return BigDecimal.ZERO;
		}

		live.lingting.virtual.currency.tronscan.Account.Data data = account.getData().get(0);
		if (contract == TronscanContract.TRX) {
			return data.getBalance();
		}

		// trc20
		if (isTrc20(contract.getHash())) {
			if (CollectionUtil.isEmpty(data.getTrc20())) {
				return BigDecimal.ZERO;
			}
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
			if (CollectionUtil.isEmpty(data.getAssetV2())) {
				return BigDecimal.ZERO;
			}
			// 从assetV2中寻找
			for (live.lingting.virtual.currency.tronscan.Account.Data.AssetV2 v2 : data.getAssetV2()) {
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
	public BigDecimal getNumberByBalanceAndContract(BigDecimal balance, Contract contract, MathContext mathContext)
			throws JsonProcessingException {
		// 合约为null 返回原值
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
	public TransferResult transfer(Account from, String to, Contract contract, BigDecimal value, TransferParams params)
			throws JsonProcessingException, NoSuchAlgorithmException {
		// 计算转账数量
		BigInteger amount = value.multiply(BigDecimal.TEN.pow(getDecimalsByContract(contract))).toBigInteger();
		String txId;
		RawData rawData;
		String rawDataHex;
		// trx 转账
		if (contract == TronscanContract.TRX) {
			TriggerResult.TrxTransferGenerateResult generateResult = TriggerRequest
					.trxTransferGenerate(endpoints, from, to, amount, contract).exec();

			if (StrUtil.isNotBlank(generateResult.getError())) {
				return new TransferResult()
						// 错误信息
						.setMessage(generateResult.getError())
						// 失败
						.setSuccess(false);
			}
			txId = generateResult.getTxId();
			rawData = generateResult.getRawData();
			rawDataHex = generateResult.getRawDataHex();
		}
		// trc10 转账
		else if (!isTrc20(contract.getHash())) {
			Trc10TransferGenerateResult generateResult = TriggerRequest
					.trc10TransferGenerate(endpoints, from, to, amount, contract).exec();
			if (StrUtil.isNotBlank(generateResult.getError())) {
				return new TransferResult()
						// 错误信息
						.setMessage(generateResult.getError())
						// 失败
						.setSuccess(false);
			}
			txId = generateResult.getTxId();
			rawData = generateResult.getRawData();
			rawDataHex = generateResult.getRawDataHex();
		}
		// 触发trc20 转账合约
		else {
			BigInteger feeLimit = params.getFeeLimit() != null ? params.getFeeLimit() : BigInteger.TEN.pow(9);
			BigInteger callValue = params.getCallValue() != null ? params.getCallValue() : BigInteger.ZERO;
			Trc20TransferGenerateResult generateResult = TriggerRequest
					.trc20TransferGenerate(endpoints, from, to, amount, contract, feeLimit, callValue).exec();

			live.lingting.virtual.currency.tronscan.Transaction transaction = generateResult.getTransaction();
			txId = transaction.getTxId();
			rawData = transaction.getRawData();
			rawDataHex = transaction.getRawDataHex();
		}

		// 创建密钥对
		SECP256K1.KeyPair keyPair = SECP256K1.KeyPair.create(SECP256K1.PrivateKey.create(from.getPrivateKey()));
		// 对 txId 进行签名
		SECP256K1.Signature sign = SECP256K1.sign(Bytes32.wrap(Hex.decode(txId)), keyPair);

		// 广播交易
		TransferBroadcastResult broadcastResult = TriggerRequest
				.trc10TransferBroadcast(endpoints, txId, rawData, rawDataHex, sign.encodedBytes().toHexString()).exec();

		// 设置返回结果
		TransferResult result = new TransferResult().setHash(txId);

		// 成功
		if (broadcastResult.getResult() != null && broadcastResult.getResult()) {
			result.setSuccess(true);
		}
		// 失败
		else {
			result.setCode(broadcastResult.getCode()).setMessage(broadcastResult.getMessage()).setSuccess(false);
		}
		return result;
	}

}
