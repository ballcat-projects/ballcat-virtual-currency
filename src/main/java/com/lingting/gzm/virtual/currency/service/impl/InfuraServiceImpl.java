package com.lingting.gzm.virtual.currency.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.lingting.gzm.virtual.currency.VirtualCurrencyAccount;
import com.lingting.gzm.virtual.currency.VirtualCurrencyTransaction;
import com.lingting.gzm.virtual.currency.VirtualCurrencyTransferResult;
import com.lingting.gzm.virtual.currency.contract.Contract;
import com.lingting.gzm.virtual.currency.contract.EtherscanContract;
import com.lingting.gzm.virtual.currency.enums.EtherscanReceiptStatus;
import com.lingting.gzm.virtual.currency.enums.TransactionStatus;
import com.lingting.gzm.virtual.currency.enums.VcPlatform;
import com.lingting.gzm.virtual.currency.etherscan.Input;
import com.lingting.gzm.virtual.currency.properties.InfuraProperties;
import com.lingting.gzm.virtual.currency.service.VirtualCurrencyService;
import com.lingting.gzm.virtual.currency.util.EtherscanUtil;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthEstimateGas;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Numeric;

/**
 * @author lingting 2020-09-01 17:16
 */
@Slf4j
public class InfuraServiceImpl implements VirtualCurrencyService {

	private static final String INPUT_EMPTY = "0x";

	@Getter
	private final Web3j web3j;

	private final InfuraProperties properties;

	private static final String EMPTY_ADDRESS = "0x0000000000000000000000000000000000000000";

	@Getter
	private static final Map<String, Integer> CONTRACT_DECIMAL_CACHE = new ConcurrentHashMap<>();

	public InfuraServiceImpl(InfuraProperties properties) {
		this.properties = properties;
		// 使用web3j连接infura客户端
		web3j = Web3j.build(properties.getHttpService());
	}

	@Override
	public Optional<VirtualCurrencyTransaction> getTransactionByHash(String hash) throws Exception {
		EthTransaction ethTransaction = web3j.ethGetTransactionByHash(hash).send();

		Optional<Transaction> optional;
		// 订单出错
		if (ethTransaction.hasError()) {
			log.error("查询eth订单出错: code: {}, message:{}", ethTransaction.getError().getCode(),
					ethTransaction.getError().getMessage());
			optional = Optional.empty();
		}
		// 订单没出错
		else {
			optional = ethTransaction.getTransaction();
		}

		/*
		 * 订单信息为空 如果交易还没有被打包，就查询不到交易信息
		 */
		if (!optional.isPresent()) {
			return Optional.empty();
		}
		Transaction transaction = optional.get();

		// 获取合约代币
		Contract contract = EtherscanContract.getByHash(transaction.getTo());
		// 合约地址
		String contractAddress = contract == null ? StrUtil.EMPTY : contract.getHash();
		// 解析input数据
		Input input;
		// 不是使用代币交易，而是直接使用eth交易
		if (INPUT_EMPTY.equals(transaction.getInput())) {
			input = new Input().setTo(transaction.getTo()).setValue(new BigDecimal(transaction.getValue()))
					.setContract(EtherscanContract.ETH);
		}
		else {
			input = EtherscanUtil.resolve(transaction.getInput());
		}

		if (input.getContract() != null) {
			contract = input.getContract();
			contractAddress = contract.getHash();
		}

		if (contract == null) {
			String finalContractAddress = contractAddress;
			contract = new Contract() {
				@Override
				public String getHash() {
					return finalContractAddress;
				}

				@Override
				public Integer getDecimals() {
					return null;
				}
			};
		}
		VirtualCurrencyTransaction virtualCurrencyTransaction = new VirtualCurrencyTransaction()

				.setVcPlatform(VcPlatform.ETHERSCAN)

				.setBlock(transaction.getBlockNumber()).setHash(transaction.getHash()).setFrom(transaction.getFrom())

				.setTo(input.getTo())
				// 设置合约类型, input 中的优先
				.setContract(contract)
				// 设置金额
				.setValue(getNumberByBalanceAndContract(input.getValue(), contract));

		// 获取交易状态
		Optional<TransactionReceipt> receiptOptional = web3j.ethGetTransactionReceipt(hash).send()
				.getTransactionReceipt();
		if (receiptOptional.isPresent()
				&& receiptOptional.get().getStatus().equals(EtherscanReceiptStatus.SUCCESS.getValue())) {
			// 交易成功
			virtualCurrencyTransaction.setStatus(TransactionStatus.SUCCESS);
		}
		else {
			virtualCurrencyTransaction.setStatus(TransactionStatus.FAIL);
		}

		// 获取交易时间
		EthBlock block = web3j.ethGetBlockByHash(transaction.getBlockHash(), false).send();

		// 从平台获取的交易是属于 UTC 时区的
		return Optional.of(virtualCurrencyTransaction.setTime(
				LocalDateTime.ofEpochSecond(Convert.toLong(block.getBlock().getTimestamp()), 0, ZoneOffset.UTC)));
	}

	@Override
	@SuppressWarnings("all")
	public Integer getDecimalsByContract(Contract contract) throws IOException {
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
	public BigDecimal getBalanceByAddressAndContract(String address, Contract contract) throws IOException {
		if (contract == EtherscanContract.ETH) {
			return new BigDecimal(web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send().getBalance());
		}
		// 执行方法
		List<Type> list = ethCall("balanceOf", ListUtil.toList(new Address(address)),
				ListUtil.toList(new TypeReference<Uint256>() {
				}), address, contract.getHash());
		// 返回值不为空
		if (!CollectionUtil.isEmpty(list)) {
			return new BigDecimal(list.get(0).getValue().toString());
		}
		return BigDecimal.ZERO;
	}

	@Override
	public BigDecimal getNumberByBalanceAndContract(BigDecimal balance, Contract contract, MathContext mathContext)
			throws IOException {
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
	public VirtualCurrencyTransferResult transfer(VirtualCurrencyAccount from, String to, Contract contract,
			BigDecimal value) throws IOException {
		// 合约地址
		String cHash = contract.getHash();
		// 获取账户信息
		Credentials credentials = Credentials.create(from.getPrivateKey(), from.getPublicKey());
		// 计算转账数量
		BigInteger amount = value.multiply(BigDecimal.TEN.pow(getDecimalsByContract(contract))).toBigInteger();
		// nonce, 由于要保证每笔交易递增, 所以直接使用eth数量
		BigInteger nonce = web3j.ethGetTransactionCount(from.getAddress(), DefaultBlockParameterName.PENDING).send()
				.getTransactionCount();
		BigInteger gasPrice = DefaultGasProvider.GAS_PRICE;
		BigInteger gasLimit = DefaultGasProvider.GAS_LIMIT;
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
			// 创建 gas 查询交易
			org.web3j.protocol.core.methods.request.Transaction callTransaction = org.web3j.protocol.core.methods.request.Transaction
					.createFunctionCallTransaction(from.getAddress(), nonce, gasPrice, null, cHash, fData);
			// 发送查询请求
			EthEstimateGas estimateGas = web3j.ethEstimateGas(callTransaction).send();
			// 处理返回值
			if (estimateGas.hasError()) {
				return new VirtualCurrencyTransferResult()
						// 失败
						.setSuccess(false)
						// 错误信息
						.setMessage(estimateGas.getError().getMessage())
						// 错误码
						.setCode(Convert.toStr(estimateGas.getError().getCode()));
			}
			// 获取 gasLimit
			gasLimit = estimateGas.getAmountUsed();
			// 获取交易原始数据
			rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, cHash, fData);

		}
		// 签名
		byte[] sign = TransactionEncoder.signMessage(rawTransaction, credentials);
		// 转16进制
		String hex = Numeric.toHexString(sign);
		// 广播交易
		EthSendTransaction transaction = web3j.ethSendRawTransaction(hex).send();
		// 结果处理
		if (transaction.hasError()) {
			return new VirtualCurrencyTransferResult()
					// 失败
					.setSuccess(false)
					// 错误消息
					.setMessage(transaction.getError().getMessage())
					// 错误码
					.setCode(Convert.toStr(transaction.getError().getCode()));
		}
		return new VirtualCurrencyTransferResult()
				// 交易hash
				.setHash(transaction.getTransactionHash())
				// 成功
				.setSuccess(true);
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
			throws IOException {
		return ethCall(method, input, out, from, to, DefaultBlockParameterName.LATEST);
	}

	@SuppressWarnings("all")
	private List<Type> ethCall(String method, List<Type> input, List<TypeReference<?>> out, String from, String to,
			DefaultBlockParameter block) throws IOException {
		Assert.notNull(input);
		Assert.notNull(out);
		// 编译方法
		Function function = new Function(method, input, out);
		// 编码
		String data = FunctionEncoder.encode(function);
		// 创建交易
		org.web3j.protocol.core.methods.request.Transaction transaction = org.web3j.protocol.core.methods.request.Transaction
				.createEthCallTransaction(from, to, data);
		// 执行
		EthCall call = web3j.ethCall(transaction, block).send();
		// 解析返回值
		return FunctionReturnDecoder.decode(call.getValue(), function.getOutputParameters());
	}

}
