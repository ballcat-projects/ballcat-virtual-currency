package live.lingting.virtual.currency.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.utils.Numeric;
import live.lingting.virtual.currency.Account;
import live.lingting.virtual.currency.Transaction;
import live.lingting.virtual.currency.TransferParams;
import live.lingting.virtual.currency.TransferResult;
import live.lingting.virtual.currency.contract.Contract;
import live.lingting.virtual.currency.contract.EtherscanContract;
import live.lingting.virtual.currency.core.JsonRpcClient;
import live.lingting.virtual.currency.enums.EtherscanReceiptStatus;
import live.lingting.virtual.currency.enums.TransactionStatus;
import live.lingting.virtual.currency.enums.VcPlatform;
import live.lingting.virtual.currency.etherscan.Balance;
import live.lingting.virtual.currency.etherscan.Block;
import live.lingting.virtual.currency.etherscan.BlockEnum;
import live.lingting.virtual.currency.etherscan.EtherscanTransaction;
import live.lingting.virtual.currency.etherscan.Input;
import live.lingting.virtual.currency.etherscan.TransactionByHash;
import live.lingting.virtual.currency.etherscan.TransactionReceipt;
import live.lingting.virtual.currency.properties.InfuraProperties;
import live.lingting.virtual.currency.service.PlatformService;
import live.lingting.virtual.currency.util.AbiUtil;
import live.lingting.virtual.currency.util.EtherscanUtil;

/**
 * @author lingting 2020-09-01 17:16
 */
@Slf4j
public class InfuraServiceImpl implements PlatformService {

	private static final String EMPTY_ADDRESS = "0x0000000000000000000000000000000000000000";

	@Getter
	private static final Map<String, Integer> CONTRACT_DECIMAL_CACHE = new ConcurrentHashMap<>();

	private final InfuraProperties properties;

	private final JsonRpcClient client;

	public InfuraServiceImpl(InfuraProperties properties) {
		this.properties = properties;
		client = properties.getHttpClient();
	}

	@Override
	public Optional<Transaction> getTransactionByHash(String hash) throws Throwable {
		TransactionByHash byHash = TransactionByHash.of(client, hash);

		// 返回值为null 或者 错误码不为null
		if (byHash == null || byHash.getCode() != null) {
			return Optional.empty();
		}

		// 获取合约代币
		Contract contract = EtherscanContract.getByHash(byHash.getTo());
		// 合约地址
		String contractAddress = contract == null ? byHash.getTo() : contract.getHash();
		// 解析input数据
		Input input;
		// 不是使用代币交易，而是直接使用eth交易
		if (EtherscanUtil.START.equals(byHash.getInput())) {
			input = new Input().setTo(byHash.getTo()).setValue(EtherscanUtil.toBigInteger(byHash.getValue()))
					.setContract(EtherscanContract.ETH);
		}
		else {
			input = EtherscanUtil.resolve(byHash.getInput());
		}

		if (input.getContract() != null) {
			contract = input.getContract();
			contractAddress = contract.getHash();
		}

		if (contract == null) {
			contract = AbiUtil.createContract(contractAddress);
		}

		Transaction transaction = new Transaction()
				// 平台
				.setVcPlatform(VcPlatform.ETHERSCAN)
				// 交易hash
				.setHash(byHash.getHash())
				// 转账人
				.setFrom(StrUtil.isNotBlank(input.getFrom()) ? input.getFrom() : byHash.getFrom())
				// 收款人
				.setTo(input.getTo())
				// 设置合约类型, input 中的优先
				.setContract(contract)
				// 设置金额
				.setValue(getNumberByBalanceAndContract(input.getValue(), contract));

		// 交易不存在块
		if (byHash.getBlockNumber() == null) {
			// 继续等待
			return Optional.of(transaction.setStatus(TransactionStatus.WAIT));
		}

		// 设置块
		transaction.setBlock(EtherscanUtil.toBigInteger(byHash.getBlockNumber()));
		// 查询交易凭证
		TransactionReceipt receipt = TransactionReceipt.of(client, hash);
		// 返回值为 null
		if (receipt == null) {
			transaction.setStatus(TransactionStatus.WAIT);
		}
		// 错误码不为null 或 失败
		else if (receipt.getCode() != null || !EtherscanReceiptStatus.SUCCESS.getValue().equals(receipt.getStatus())) {
			transaction.setStatus(TransactionStatus.FAIL);
		}
		// 成功
		else {
			transaction.setStatus(TransactionStatus.SUCCESS);
		}

		// 获取交易时间
		Block block = Block.of(client, byHash.getBlockHash());

		return Optional.of(transaction.setTime(EtherscanUtil.toBigInteger(block.getTimestamp()).longValue()));
	}

	@Override
	@SuppressWarnings("all")
	public Integer getDecimalsByContract(Contract contract) throws Throwable {
		if (contract == null) {
			return 0;
		}

		// 已知合约精度
		if (contract.getDecimals() != null) {
			return contract.getDecimals();
		}

		// 已缓存合约精度, 且已存在指定合约的精度缓存
		if (CONTRACT_DECIMAL_CACHE.containsKey(contract.getHash())) {
			return CONTRACT_DECIMAL_CACHE.get(contract.getHash());
		}

		Integer decimals = 0;

		List<Type> types = ethCall("decimals", new ArrayList<>(0), ListUtil.toList(new TypeReference<Uint8>() {
		}), EMPTY_ADDRESS, contract.getHash());
		// 返回值不为空
		if (!CollectionUtil.isEmpty(types)) {
			decimals = Convert.toInt(types.get(0).getValue().toString(), 0);
		}
		// 缓存合约精度
		CONTRACT_DECIMAL_CACHE.put(contract.getHash(), decimals);
		return decimals;
	}

	@Override
	@SuppressWarnings("all")
	public BigInteger getBalanceByAddressAndContract(String address, Contract contract) throws Throwable {
		if (contract == EtherscanContract.ETH) {
			return EtherscanUtil
					.toBigInteger(client.invoke("eth_getBalance", String.class, address, BlockEnum.LATEST.getVal()));
		}
		// 执行方法
		List<Type> list = ethCall("balanceOf", ListUtil.toList(new Address(address)),
				ListUtil.toList(new TypeReference<Uint256>() {
				}), address, contract.getHash());
		// 返回值不为空
		if (!CollectionUtil.isEmpty(list)) {
			return new BigInteger(list.get(0).getValue().toString());
		}
		return BigInteger.ZERO;
	}

	@Override
	public BigDecimal getNumberByBalanceAndContract(BigInteger balance, Contract contract, MathContext mathContext)
			throws Throwable {
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
	public TransferResult transfer(Account from, String to, Contract contract, BigDecimal value, TransferParams params)
			throws Throwable {
		// 合约地址
		String cHash = contract.getHash();
		// 获取账户信息
		Credentials credentials = Credentials.create(from.getPrivateKey(), from.getPublicKey());
		// 计算转账数量
		BigInteger amount = valueToBalanceByContract(value, contract);
		// nonce, 由于要保证每笔交易递增, 所以直接使用eth数量
		BigInteger nonce = EtherscanUtil.toBigInteger(
				client.invoke("eth_getTransactionCount", String.class, from.getAddress(), BlockEnum.PENDING.getVal()));
		BigInteger gasPrice = params.getGasPrice();
		BigInteger gasLimit = params.getGasLimit();

		if (gasPrice == null) {
			gasPrice = EtherscanUtil.toBigInteger(client.invoke("eth_gasPrice", String.class));
		}

		if (gasLimit == null) {
			gasLimit = EtherscanUtil.toBigInteger(Block.of(client, BlockEnum.PENDING).getGasLimit());
		}

		// 交易原始数据
		RawTransaction rawTransaction;
		// 构造数据- eth
		if (contract == EtherscanContract.ETH) {
			// 获取交易原始数据
			rawTransaction = RawTransaction.createEtherTransaction(nonce, gasPrice, gasLimit, to, amount);
		}
		// 构建请求 合约转账
		else {
			// 创建转账方法
			Function function = new Function("transfer", Arrays.asList(new Address(to), new Uint256(amount)),
					Collections.emptyList());
			// 编码
			String fData = FunctionEncoder.encode(function);
			// 获取交易原始数据
			rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, cHash, fData);
		}

		// 签名
		byte[] sign = TransactionEncoder.signMessage(rawTransaction, credentials);
		// 转16进制
		String hex = Numeric.toHexString(sign);

		// 广播交易
		String hash = client.invoke("eth_sendRawTransaction", String.class, hex);

		return TransferResult.success(hash);
	}

	@Override
	public boolean validate(String address) throws Throwable {
		Balance of = Balance.of(client, address);
		return of.getAmount() != null;
	}

	/**
	 * 创建交易
	 * @param method 执行方法名
	 * @param input 输入参数
	 * @param out 输出参数
	 * @param from 从
	 * @param to 去
	 * @return java.util.List
	 * @author lingting 2020-12-11 16:21
	 */
	@SuppressWarnings("all")
	private List<Type> ethCall(String method, List<Type> input, List<TypeReference<?>> out, String from, String to)
			throws Throwable {
		return ethCall(method, input, out, from, to, BlockEnum.LATEST.getVal());
	}

	@SuppressWarnings("all")
	private List<Type> ethCall(String method, List<Type> input, List<TypeReference<?>> out, String from, String to,
			String block) throws Throwable {
		Assert.notNull(input);
		Assert.notNull(out);
		// 编译方法
		Function function = new Function(method, input, out);
		// 编码
		String data = FunctionEncoder.encode(function);
		// 创建交易
		EtherscanTransaction transaction = EtherscanTransaction.of(from, to, data);
		// 执行
		String call = client.invoke("eth_call", String.class, transaction, BlockEnum.LATEST.getVal());
		// 解析返回值
		return FunctionReturnDecoder.decode(call, function.getOutputParameters());
	}

}
