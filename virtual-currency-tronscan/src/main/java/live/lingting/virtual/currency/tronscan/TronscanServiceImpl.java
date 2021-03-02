package live.lingting.virtual.currency.tronscan;

import static live.lingting.virtual.currency.tronscan.model.Transaction.Ret;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Hex;
import org.tron.tronj.crypto.SECP256K1;
import org.tron.tronj.crypto.tuweniTypes.Bytes32;
import live.lingting.virtual.currency.core.Contract;
import live.lingting.virtual.currency.core.Endpoints;
import live.lingting.virtual.currency.core.PlatformService;
import live.lingting.virtual.currency.core.enums.TransactionStatus;
import live.lingting.virtual.currency.core.enums.VirtualCurrencyPlatform;
import live.lingting.virtual.currency.core.model.Account;
import live.lingting.virtual.currency.core.model.TransactionInfo;
import live.lingting.virtual.currency.core.model.TransferParams;
import live.lingting.virtual.currency.core.model.TransferResult;
import live.lingting.virtual.currency.core.util.AbiUtils;
import live.lingting.virtual.currency.core.util.JacksonUtils;
import live.lingting.virtual.currency.tronscan.contract.TronscanContract;
import live.lingting.virtual.currency.tronscan.model.Transaction;
import live.lingting.virtual.currency.tronscan.model.Transaction.RawData;
import live.lingting.virtual.currency.tronscan.model.Trc10;
import live.lingting.virtual.currency.tronscan.model.Trc20Data;
import live.lingting.virtual.currency.tronscan.model.TriggerRequest;
import live.lingting.virtual.currency.tronscan.model.TriggerResult;
import live.lingting.virtual.currency.tronscan.model.TriggerResult.TransferBroadcastResult;
import live.lingting.virtual.currency.tronscan.model.TriggerResult.Trc10TransferGenerateResult;
import live.lingting.virtual.currency.tronscan.model.TriggerResult.Trc20TransferGenerateResult;
import live.lingting.virtual.currency.tronscan.properties.TronscanProperties;
import live.lingting.virtual.currency.tronscan.util.TronscanUtils;

/**
 * @author lingting 2020-09-01 17:16
 */
@Slf4j
public class TronscanServiceImpl implements PlatformService<TronscanTransactionGenerate> {

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
	public Optional<TransactionInfo> getTransactionByHash(String hash) throws Throwable {
		Transaction transaction = Transaction.of(endpoints, hash);

		// 没有返回txId 表示此交易未被确认 或 不存在
		if (StrUtil.isBlank(transaction.getTxId())) {
			return Optional.empty();
		}

		// 生成返回值
		TransactionInfo vcTransactionInfo = new TransactionInfo()
				// 平台
				.setVirtualCurrencyPlatform(VirtualCurrencyPlatform.TRONSCAN)
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
				vcTransactionInfo.setContract(TronscanContract.TRX);
			}
			// trc10 交易
			else {
				// 合约
				vcTransactionInfo.setContract(TronscanContract.getByHash(data.getAssetName()));
				// 如果合约未找到
				if (vcTransactionInfo.getContract() == null) {
					vcTransactionInfo.setContract(AbiUtils.createContract(data.getAssetName()));
				}
			}

			vcTransactionInfo
					// 转账金额
					.setValue(getNumberByBalanceAndContract(data.getAmount(), vcTransactionInfo.getContract()))
					// 转账人
					.setFrom(data.getOwnerAddress())
					// 收款人
					.setTo(data.getToAddress());
		}
		// trc20 交易
		else {
			// 解析数据
			Trc20Data resolve = TronscanUtils.resolve(data.getData());

			// 解析数据中有合约
			if (resolve.getContract() != null) {
				vcTransactionInfo.setContract(resolve.getContract());
			}
			// 没有合约
			else {
				vcTransactionInfo.setContract(TronscanContract.getByHash(data.getContractAddress()));
			}
			// 如果合约未找到
			if (vcTransactionInfo.getContract() == null) {
				vcTransactionInfo.setContract(AbiUtils.createContract(data.getContractAddress()));
			}

			vcTransactionInfo
					// 转账金额
					.setValue(getNumberByBalanceAndContract(resolve.getAmount(), vcTransactionInfo.getContract()))
					// 转账人
					.setFrom(StrUtil.isNotBlank(resolve.getFrom()) ? resolve.getFrom() : data.getOwnerAddress())
					// 收款人
					.setTo(resolve.getTo());
		}

		// 获取交易详细信息
		live.lingting.virtual.currency.tronscan.model.TransactionInfo info = live.lingting.virtual.currency.tronscan.model.TransactionInfo
				.of(endpoints, hash);
		vcTransactionInfo
				// 块高度
				.setBlock(info.getBlockNumber())
				// 交易时间, 毫秒转秒
				.setTime(info.getBlockTimeStamp() / 1000);
		// 交易状态
		List<Ret> rets = transaction.getRet();
		// 失败
		if (CollectionUtil.isEmpty(rets) || !rets.get(0).getContractRet().equals(Ret.SUCCESS)) {
			vcTransactionInfo.setStatus(TransactionStatus.FAIL);
		}
		// 成功
		else {
			vcTransactionInfo.setStatus(TransactionStatus.SUCCESS);
		}
		return Optional.of(vcTransactionInfo);
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
		if (TronscanUtils.isTrc20(contract.getHash())) {
			TriggerResult.TriggerConstantResult result = TriggerRequest.trc20Decimals(endpoints, contract).exec();
			decimals = Integer.valueOf(result.getConstantResult().get(0), 16);
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
	public BigInteger getBalanceByAddressAndContract(String address, Contract contract) throws JsonProcessingException {
		// 独立处理trc20余额查询
		if (contract != TronscanContract.TRX && TronscanUtils.isTrc20(contract.getHash())) {
			TriggerResult.TriggerConstantResult triggerResult = TriggerRequest
					.trc20BalanceOf(endpoints, contract, address).exec();

			return new BigInteger(triggerResult.getConstantResult().get(0), 16);
		}

		live.lingting.virtual.currency.tronscan.model.Account account = live.lingting.virtual.currency.tronscan.model.Account
				.of(endpoints, address);

		// 搜索拥有的数据
		if (CollectionUtil.isEmpty(account.getData())) {
			return BigInteger.ZERO;
		}

		live.lingting.virtual.currency.tronscan.model.Account.Data data = account.getData().get(0);
		if (contract == TronscanContract.TRX) {
			return data.getBalance();
		}

		// trc10 处理
		if (CollectionUtil.isEmpty(data.getAssetV2())) {
			return BigInteger.ZERO;
		}

		// 从assetV2中寻找
		for (live.lingting.virtual.currency.tronscan.model.Account.Data.AssetV2 v2 : data.getAssetV2()) {
			// 如果指定合约的hash 与当前v2数据相同
			if (v2.getKey().equals(contract.getHash())) {
				return v2.getValue();
			}
		}

		// 未找到合约, 返回 0
		return BigInteger.ZERO;
	}

	@Override
	public BigDecimal getNumberByBalanceAndContract(BigInteger balance, Contract contract, MathContext mathContext)
			throws JsonProcessingException {
		// 合约为null 返回原值
		if (contract == null) {
			return new BigDecimal(balance);
		}
		if (balance == null) {
			return BigDecimal.ZERO;
		}
		// 计算返回值
		return new BigDecimal(balance).divide(BigDecimal.TEN.pow(getDecimalsByContract(contract)), mathContext);
	}

	@Override
	public TronscanTransactionGenerate transactionGenerate(Account from, String to, Contract contract, BigDecimal value,
			TransferParams params) throws Throwable {
		if (value.compareTo(BigDecimal.ZERO) <= 0) {
			return TronscanTransactionGenerate.failed("转账金额必须大于0!");
		}
		String baseAddress = TronscanUtils.getBaseAddressByPublicKey(from.getPublicKey());
		// 传入 base58 地址. 与base58地址比对
		boolean isHexAddress = TronscanUtils.isHexAddress(from.getAddress());
		if (!isHexAddress && !from.getAddress().equals(baseAddress)) {
			return TronscanTransactionGenerate.failed("由公钥推导出的地址与传入地址不符!");
		}
		// 传入 hex 地址. 与 hex地址比对
		else if (isHexAddress && !from.getAddress().equals(TronscanUtils.baseToHex(baseAddress))) {
			return TronscanTransactionGenerate.failed("由公钥推导出的地址与传入地址不符!");
		}
		// 以 base58 地址为基准
		from.setAddress(baseAddress);
		// 计算转账数量
		BigInteger amount = valueToBalanceByContract(value, contract);
		String txId;
		RawData rawData;
		String rawDataHex;
		BigInteger feeLimit = null;
		BigInteger callValue = null;
		// trx 转账
		if (contract == TronscanContract.TRX) {
			TriggerResult.TrxTransferGenerateResult generateResult = TriggerRequest
					.trxTransferGenerate(endpoints, from, to, amount, contract).exec();

			if (StrUtil.isNotBlank(generateResult.getError())) {
				return TronscanTransactionGenerate.failed(generateResult.getError());
			}
			txId = generateResult.getTxId();
			rawData = generateResult.getRawData();
			rawDataHex = generateResult.getRawDataHex();
		}
		// trc10 转账
		else if (!TronscanUtils.isTrc20(contract.getHash())) {
			Trc10TransferGenerateResult generateResult = TriggerRequest
					.trc10TransferGenerate(endpoints, from, to, amount, contract).exec();
			if (StrUtil.isNotBlank(generateResult.getError())) {
				return TronscanTransactionGenerate.failed(generateResult.getError());
			}
			txId = generateResult.getTxId();
			rawData = generateResult.getRawData();
			rawDataHex = generateResult.getRawDataHex();
		}
		// 触发trc20 转账合约
		else {
			feeLimit = params.getFeeLimit() != null ? params.getFeeLimit() : BigInteger.TEN.pow(9);
			callValue = params.getCallValue() != null ? params.getCallValue() : BigInteger.ZERO;
			Trc20TransferGenerateResult generateResult = TriggerRequest
					.trc20TransferGenerate(endpoints, from, to, amount, contract, feeLimit, callValue).exec();

			live.lingting.virtual.currency.tronscan.model.Transaction transaction = generateResult.getTransaction();
			txId = transaction.getTxId();
			rawData = transaction.getRawData();
			rawDataHex = transaction.getRawDataHex();
		}
		return TronscanTransactionGenerate.success(from, to, amount, contract,
				new TronscanTransactionGenerate.Tronscan(txId, rawData, rawDataHex, feeLimit, callValue));
	}

	@Override
	public TronscanTransactionGenerate transactionSign(TronscanTransactionGenerate generate) throws Throwable {
		// 如果上一步失败则直接返回
		if (!generate.getSuccess()) {
			return generate;
		}
		// 创建密钥对
		SECP256K1.KeyPair keyPair = SECP256K1.KeyPair
				.create(SECP256K1.PrivateKey.create(generate.getFrom().getPrivateKey()));
		// 对 txId 进行签名
		SECP256K1.Signature sign = SECP256K1.sign(Bytes32.wrap(Hex.decode(generate.getTronscan().getTxId())), keyPair);

		// 保存数据
		generate.setSignHex(sign.encodedBytes().toHexString());
		return generate;
	}

	@Override
	public TransferResult transactionBroadcast(TronscanTransactionGenerate generate) throws Throwable {
		// 如果上一步失败则直接返回
		if (!generate.getSuccess()) {
			return TransferResult.failed(generate);
		}
		TronscanTransactionGenerate.Tronscan tronscan = generate.getTronscan();
		String txId = tronscan.getTxId();
		// 广播交易
		TransferBroadcastResult broadcastResult = TriggerRequest.trc10TransferBroadcast(endpoints, txId,
				tronscan.getRawData(), tronscan.getRawDataHex(), generate.getSignHex()).exec();

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

	@Override
	public boolean validate(String address) {
		HttpRequest request = HttpRequest.post(endpoints.getHttpUrl("wallet/validateaddress"));
		request.body("{\"address\": \"" + address + "\"}");
		try {
			String response = request.execute().body();
			Map<String, String> map = JacksonUtils.toObj(response, new TypeReference<Map<String, String>>() {
			});
			if ("true".equals(map.get("result"))) {
				return true;
			}
			return false;
		}
		catch (Exception e) {
			return false;
		}
	}

}
