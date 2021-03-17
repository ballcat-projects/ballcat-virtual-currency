package live.lingting.virtual.currency.bitcoin;

import static live.lingting.virtual.currency.bitcoin.util.BitcoinUtils.PROPERTY_PREFIX;
import static org.bitcoinj.core.Transaction.Purpose;
import static org.bitcoinj.core.Transaction.SigHash;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Context;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.TransactionConfidence;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.core.TransactionOutPoint;
import org.bitcoinj.core.TransactionWitness;
import org.bitcoinj.core.Utils;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcoinj.script.ScriptChunk;
import org.bitcoinj.script.ScriptPattern;
import org.bouncycastle.util.encoders.Hex;
import live.lingting.virtual.currency.bitcoin.contract.OmniContract;
import live.lingting.virtual.currency.bitcoin.endpoints.BitcoinCypherEndpoints;
import live.lingting.virtual.currency.bitcoin.endpoints.BitcoinEndpoints;
import live.lingting.virtual.currency.bitcoin.endpoints.BitcoinSochainEndpoints;
import live.lingting.virtual.currency.bitcoin.endpoints.BlockchainEndpoints;
import live.lingting.virtual.currency.bitcoin.endpoints.OmniEndpoints;
import live.lingting.virtual.currency.bitcoin.model.FeeAndSpent;
import live.lingting.virtual.currency.bitcoin.model.Properties;
import live.lingting.virtual.currency.bitcoin.model.Unspent;
import live.lingting.virtual.currency.bitcoin.model.blockchain.BlockchainHistory;
import live.lingting.virtual.currency.bitcoin.model.blockchain.LatestBlock;
import live.lingting.virtual.currency.bitcoin.model.blockchain.RawTransaction;
import live.lingting.virtual.currency.bitcoin.model.cypher.Balance;
import live.lingting.virtual.currency.bitcoin.model.omni.AddressHistory;
import live.lingting.virtual.currency.bitcoin.model.omni.Balances;
import live.lingting.virtual.currency.bitcoin.model.omni.Domain;
import live.lingting.virtual.currency.bitcoin.model.omni.PushTx;
import live.lingting.virtual.currency.bitcoin.model.omni.TokenHistory;
import live.lingting.virtual.currency.bitcoin.model.omni.TransactionByHash;
import live.lingting.virtual.currency.bitcoin.properties.BitcoinProperties;
import live.lingting.virtual.currency.bitcoin.util.BitcoinUtils;
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
import live.lingting.virtual.currency.core.util.AssertUtils;

/**
 * @author lingting 2020-09-01 17:16
 */
@Slf4j
public class BitcoinServiceImpl implements PlatformService<BitcoinTransactionGenerate, BitcoinHistoryQueryParams> {

	/**
	 * 精度需要计算的标志
	 */
	public static final String FLAG = ".";

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

	/**
	 * 用于调用of方法生成新对象
	 */
	private static final AddressHistory STATIC_ADDRESS_HISTORY = new AddressHistory();

	private final BitcoinProperties properties;

	private final NetworkParameters np;

	private final BigInteger confirmationsMin;

	private final BlockchainEndpoints blockchainEndpoints;

	private final OmniEndpoints omniEndpoints = OmniEndpoints.MAINNET;

	private final BitcoinCypherEndpoints cypherEndpoints;

	private final BitcoinSochainEndpoints sochainEndpoints;

	public BitcoinServiceImpl(BitcoinProperties properties) {
		this.properties = properties;
		this.np = properties.getNp();
		this.confirmationsMin = new BigInteger(properties.getConfirmationsMin().toString());

		if (properties.getEndpoints() == BitcoinEndpoints.MAINNET) {
			blockchainEndpoints = BlockchainEndpoints.MAINNET;
			cypherEndpoints = BitcoinCypherEndpoints.MAINNET;
			sochainEndpoints = BitcoinSochainEndpoints.MAINNET;
		}
		else {
			blockchainEndpoints = BlockchainEndpoints.TEST;
			cypherEndpoints = BitcoinCypherEndpoints.TEST;
			sochainEndpoints = BitcoinSochainEndpoints.TEST;
		}

	}

	@Override
	public Optional<TransactionInfo> getTransactionByHash(String hash) throws Throwable {
		RawTransaction rawTransaction = RawTransaction.of(blockchainEndpoints, hash);

		if (rawTransaction == null || StrUtil.isBlank(rawTransaction.getHash())
				|| CollectionUtil.isEmpty(rawTransaction.getOuts())) {
			return Optional.empty();
		}

		// 不包含特征数据, 肯定是btc
		if (!rawTransaction.getResponse().contains(PROPERTY_PREFIX)) {
			return btcTransactionHandler(rawTransaction);
		}
		// 包含特征数据, 需要搜索
		boolean isBtc = true;

		// 总输出数量
		BigInteger sumOut = BigInteger.ZERO;
		// 输出详情
		Map<String, BigDecimal> outInfos = new HashMap<>(rawTransaction.getOuts().size());

		// 搜索输出, 判断是否为 btc交易
		for (RawTransaction.Out out : rawTransaction.getOuts()) {
			String script = out.getScript();
			// 指定字符串开头
			if (script.startsWith(PROPERTY_PREFIX)
					// 长度为 44
					&& script.length() == 44) {
				isBtc = false;
				break;
			}
			// 统计输出数量, 如果为btc交易, 可以正常统计完成
			sumOut = statisticsDetails(sumOut, outInfos, out);
		}

		// btc 交易处理
		if (isBtc) {
			return btcTransactionHandler(sumOut, outInfos, rawTransaction);
		}

		return getTransactionInfoOfOmni(hash);
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

		TokenHistory history = request(STATIC_TOKEN_HISTORY, omniEndpoints, contract.getHash());
		int decimals = getDecimalsByString(history.getTransactions().get(0).getAmount());
		CONTRACT_DECIMAL_CACHE.put(contract.getHash(), decimals);
		return decimals;
	}

	@Override
	public BigInteger getBalanceByAddressAndContract(String address, Contract contract) throws JsonProcessingException {
		if (contract == OmniContract.BTC) {
			Balance balance = Balance.of(cypherEndpoints, address);
			if (balance == null || StrUtil.isNotBlank(balance.getError()) || balance.getFinalBalance() == null) {
				return BigInteger.ZERO;
			}
			return balance.getFinalBalance();
		}
		Balances balances = request(STATIC_BALANCES, omniEndpoints, address);
		if (CollectionUtil.isEmpty(balances.getBalance())) {
			return BigInteger.ZERO;
		}
		for (Balances.Balance balance : balances.getBalance()) {
			// 协助缓存精度
			if (!CONTRACT_DECIMAL_CACHE.containsKey(balance.getId())) {
				CONTRACT_DECIMAL_CACHE.put(contract.getHash(),
						getDecimalsByString(balance.getPropertyInfo().getTotalTokens()));
			}

			if (balance.getId().equals(contract.getHash())) {
				return balance.getValue();
			}
		}
		return BigInteger.ZERO;
	}

	@Override
	public BigDecimal getNumberByBalanceAndContract(BigInteger balance, Contract contract, MathContext mathContext)
			throws JsonProcessingException {
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
	public BitcoinTransactionGenerate transactionGenerate(Account from, String to, Contract contract, BigDecimal value,
			TransferParams params) throws Throwable {
		if (value.compareTo(BigDecimal.ZERO) <= 0) {
			return BitcoinTransactionGenerate.failed("转账金额必须大于0!");
		}
		// BTC 转账数量
		Coin btcAmount;
		// 转账合约数量
		BigInteger contractAmount = BigInteger.ZERO;
		// 转账比特
		if (contract == OmniContract.BTC) {
			btcAmount = BitcoinUtils.btcToCoin(value);
		}
		// 转账合约
		else {
			// 最小比特转账要求
			btcAmount = Coin.valueOf(546);
		}

		// 未设置总价 进行 手续费单价配置
		if (params.getSumFee() == null) {
			params.setFee(params.getFee() == null ? properties.feeByByte.get() : params.getFee());
		}

		// 计算手续费, 是否找零 , 使用的余额
		FeeAndSpent fs = FeeAndSpent.of(
				// 服务
				this,
				// 合约
				contract,
				// 参数
				params,
				// 未使用余额
				properties.getUnspent().apply(from.getAddress(), properties.getEndpoints()),
				// 转账数量
				btcAmount,
				// 最小确认数
				new BigInteger(properties.getConfirmationsMin().toString()));

		// 构筑交易
		org.bitcoinj.core.Transaction tx = new org.bitcoinj.core.Transaction(np);

		// 输入地址
		Address fromAddress = Address.fromString(np, from.getAddress());
		// 输出地址
		Address toAddress = Address.fromString(np, to);
		// 转账比特输出
		tx.addOutput(btcAmount, toAddress);

		// 找零输出
		if (fs.getZero()) {
			tx.addOutput(
					// 找零 = 输出数量 - 总手续费 - 转账数量
					fs.getOutNumber().subtract(fs.getFee()).subtract(btcAmount),
					// 找零给自己
					fromAddress);
		}

		// 转账合约输出
		if (contract != OmniContract.BTC) {
			contractAmount = valueToBalanceByContract(value, contract);
			// 构筑输出hex
			String contractHex = Properties.of(contract.getHash(), contractAmount).toScript();

			// 加入输出
			tx.addOutput(Coin.ZERO, new Script(Utils.HEX.decode(contractHex)));
		}

		// 添加输入
		for (int i = 0; i < fs.getList().size(); i++) {
			Unspent spent = fs.getList().get(i);
			TransactionOutPoint outPoint = new TransactionOutPoint(np, spent.getOut(),
					Sha256Hash.wrap(spent.getHash()));

			TransactionInput input = new TransactionInput(np, tx, Hex.decode(spent.getScript()), outPoint,
					Coin.valueOf(spent.getValue().longValue()));
			tx.addInput(input);
		}
		return BitcoinTransactionGenerate.success(from, to,
				// 转账数量配置
				contract != OmniContract.BTC ? contractAmount : BitcoinUtils.coinToBtcBalance(btcAmount), contract,
				new BitcoinTransactionGenerate.Bitcoin(tx, fs.getFee()));
	}

	@Override
	public BitcoinTransactionGenerate transactionSign(BitcoinTransactionGenerate generate) throws Throwable {
		// 如果上一步失败则直接返回
		if (!generate.getSuccess()) {
			return generate;
		}
		org.bitcoinj.core.Transaction tx = generate.getBitcoin().getTransaction();
		Account from = generate.getFrom();

		// 初始密钥
		List<ECKey> keys = getEcKeysByFrom(from);
		// 签名输入
		for (int inputIndex = 0; inputIndex < tx.getInputs().size(); inputIndex++) {
			TransactionInput txIn = tx.getInput(inputIndex);
			Script script = txIn.getScriptSig();
			// 多签 并且是 p2sh处理 或者 非 第一次签名
			boolean isNativeP2sh = from.getMulti()
					&& (!generate.getBitcoin().getFirstSign() || ScriptPattern.isP2SH(script));
			if (isNativeP2sh) {
				List<TransactionSignature> signatures;
				signatures = new ArrayList<>(from.getMultiNum());

				// 非第一次签名脚本创建
				if (!generate.getBitcoin().getFirstSign()) {
					Iterator<ScriptChunk> sci = txIn.getScriptSig().getChunks().iterator();

					while (sci.hasNext()) {
						ScriptChunk sc = sci.next();
						// 如果是 op code 不为0
						if (sc.opcode != 0) {
							// 如果不是最后一个, 表示这是一个签名
							if (sci.hasNext()) {
								// 解析签名
								TransactionSignature signature = TransactionSignature.decodeFromBitcoin(sc.data, true,
										true);
								signatures.add(signature);
							}
						}
					}
				}
				script = ScriptBuilder.createMultiSigOutputScript(from.getMultiNum(), keys);
				for (ECKey ecKey : keys) {
					// 如果要求的签名数量与已有签名数量一致, 则停止插入签名
					if (signatures.size() == from.getMultiNum()) {
						continue;
					}
					if (ecKey.hasPrivKey()) {
						signatures.add(new TransactionSignature(
								// 签名
								ecKey.sign(
										// 生成hash
										tx.hashForSignature(inputIndex, script, SigHash.ALL, false)),
								SigHash.ALL, false)

						);
					}
				}

				Script scriptSig = ScriptBuilder.createP2SHMultiSigInputScript(signatures, script);
				txIn.setScriptSig(scriptSig);
				continue;
			}

			ECKey key = keys.get(0);

			// p2sh-p2wpkh
			if (ScriptPattern.isP2SH(script)) {
				// 脚本
				Script redeemScript = ScriptBuilder.createP2WPKHOutputScript(key);
				Script witnessScript = ScriptBuilder.createP2PKHOutputScript(key);

				TransactionSignature signature = tx.calculateWitnessSignature(inputIndex, key, witnessScript,
						txIn.getValue(), SigHash.ALL, false);

				txIn.setWitness(TransactionWitness.redeemP2WPKH(signature, key));
				txIn.setScriptSig(new ScriptBuilder().data(redeemScript.getProgram()).build());
				continue;
			}

			if (ScriptPattern.isP2WPKH(script)) {
				script = ScriptBuilder.createP2PKHOutputScript(key);
				TransactionSignature signature = tx.calculateWitnessSignature(inputIndex, key, script, txIn.getValue(),
						SigHash.ALL, false);
				txIn.setScriptSig(ScriptBuilder.createEmpty());
				txIn.setWitness(TransactionWitness.redeemP2WPKH(signature, key));
				continue;
			}

			TransactionSignature txSignature = tx.calculateSignature(inputIndex, key, script, SigHash.ALL, false);

			if (ScriptPattern.isP2PK(script)) {
				txIn.setScriptSig(ScriptBuilder.createInputScript(txSignature));
			}
			else if (ScriptPattern.isP2PKH(script)) {
				txIn.setScriptSig(ScriptBuilder.createInputScript(txSignature, key));
			}
			else {
				return BitcoinTransactionGenerate.failed("无法解析此脚本!");
			}
		}

		// 验证
		tx.verify();
		// 创建上下文
		Context.getOrCreate(np);
		// 设置来源
		tx.getConfidence().setSource(TransactionConfidence.Source.SELF);

		tx.setPurpose(Purpose.USER_PAYMENT);
		// 生成用于广播的hex字符串
		String raw = Hex.toHexString(tx.bitcoinSerialize());
		generate.setSignHex(raw);
		return generate;
	}

	@Override
	public TransferResult transactionBroadcast(BitcoinTransactionGenerate generate) throws Throwable {
		// 如果上一步失败则直接返回
		if (!generate.getSuccess()) {
			return TransferResult.failed(generate);
		}
		// 广播交易, 返回 交易hash
		PushTx pushTx = properties.getBroadcastTransaction().apply(generate.getSignHex(), omniEndpoints);
		if (!pushTx.isSuccess()) {
			if (pushTx.getE() != null) {
				return TransferResult.failed(pushTx.getE());
			}
			return TransferResult.failed("转账失败");
		}
		return TransferResult.success(pushTx.getTxId());
	}

	@Override
	public boolean validate(String address) throws JsonProcessingException {
		Balance balance = Balance.of(cypherEndpoints, address);
		return StrUtil.isBlank(balance.getError());
	}

	@Override
	public List<TransactionInfo> listHistoryByAddress(BitcoinHistoryQueryParams query) throws Throwable {
		AssertUtils.isFalse(StrUtil.isBlank(query.getAddress()), "查询地址不能为空!");
		int index = Math.max(query.getPageIndex(), 1);
		// 仅查询omni交易
		if (query.isOnlyOmni()) {
			Map<String, Object> map = new HashMap<>();
			map.put("addr", query.getAddress());
			map.put("page", index - 1);
			AddressHistory history = request(STATIC_ADDRESS_HISTORY, omniEndpoints, map);
			List<TransactionInfo> list = new ArrayList<>(history.getTransactions().size());

			for (var tx: history.getTransactions()){
				list.add(tx.toTransactionInfo(confirmationsMin));
			}
			return list;
		}
		return listBitcoinHistoryByAddress(query, index);
	}

	public List<TransactionInfo> listBitcoinHistoryByAddress(BitcoinHistoryQueryParams query, int index)
			throws Throwable {
		int size = query.getPageIndex() < 0 || query.getPageIndex() > 50 ? 10 : query.getPageSize();
		// 查询所有交易
		BlockchainHistory history = BlockchainHistory.of(blockchainEndpoints, query.getAddress(), index, size);
		// 当前块
		LatestBlock block = LatestBlock.of(blockchainEndpoints);
		List<TransactionInfo> list = new ArrayList<>(history.getTxs().size());
		TransactionInfo info;
		boolean isBtc = true;
		// 总输入
		BigDecimal sumIn;
		// 总输出
		BigDecimal sumOut;
		// 输出的 omni 上的合约数量
		BigDecimal omniOut;
		// 合约
		Contract contract;

		TransactionInfo.BtcInfo btcInfo;
		for (var tx : history.getTxs()) {
			sumIn = BigDecimal.ZERO;
			sumOut = BigDecimal.ZERO;
			omniOut = BigDecimal.ZERO;
			btcInfo = new TransactionInfo.BtcInfo();
			contract = null;
			isBtc = true;

			// 计算输入
			for (var in : tx.getInputs()) {
				// 转化输入数量. 新单位为 个
				BigDecimal value = getNumberByBalanceAndContract(in.getPrevOut().getValue(), OmniContract.BTC);
				sumIn = sumIn.add(value);
				btcInfo.addIn(new Script(Hex.decode(in.getPrevOut().getScript())).getToAddress(np).toString(), value);
			}

			// 计算输出
			for (var out : tx.getOut()) {
				// 指定字符串开头
				if (out.getScript().startsWith(PROPERTY_PREFIX)
						// 长度为 44
						&& out.getScript().length() == 44) {
					isBtc = false;
					// 解析脚本
					Properties properties = Properties.of(out.getScript());
					// 解析合约
					contract = OmniContract.getByHash(properties.getHash().toString());
					// 如果合约未预置
					if (contract == null) {
						contract = AbiUtils.createContract(properties.getHash().toString(),
								// 查询精度
								getDecimalsByContract(AbiUtils.createContract(properties.getHash().toString())));
					}
					// 合约输出
					omniOut = getNumberByBalanceAndContract(properties.getAmounts(), contract);
				}
				else {
					BigDecimal value = getNumberByBalanceAndContract(out.getValue(), OmniContract.BTC);
					sumOut = sumOut.add(value);
					btcInfo.addOut(new Script(Hex.decode(out.getScript())).getToAddress(np).toString(), value);
				}
			}

			// 计算手续费
			btcInfo.setFee(sumIn.subtract(sumOut));

			info = new TransactionInfo()
					// hash
					.setHash(tx.getHash())
					// 块
					.setBlock(tx.getBlockHeight())
					// 交易状态
					.setStatus(
							// 交易没有块
							tx.getBlockHeight() == null
									// 当前块 - 交易所在块 < 最小确认数
									|| block.getHeight().subtract(tx.getBlockHeight()).compareTo(confirmationsMin) < 0
											? TransactionStatus.WAIT : TransactionStatus.SUCCESS)
					// 合约
					.setContract(isBtc ? OmniContract.BTC : contract)
					// 平台
					.setVirtualCurrencyPlatform(VirtualCurrencyPlatform.BITCOIN)
					// 时间
					.setTime(tx.getTime()).setBtcInfo(btcInfo);

			// omni 交易 付款人, 收款人, 以及转账金额处理
			if (!isBtc) {
				// 转账金额
				info.setValue(omniOut);
				// 付款人,收款人处理
				omniTransactionResolve(info, btcInfo.getIn(), btcInfo.getOut());
			}

			list.add(info);
		}
		return list;
	}

	/**
	 * 根据输入和输出解析出omni的转账人和收款人
	 * @param info 交易对象
	 * @param inMap 所有输入数据
	 * @param outMap 所有输出数据
	 * @author lingting 2021-03-16 14:08
	 */
	private void omniTransactionResolve(TransactionInfo info, Map<String, BigDecimal> inMap,
			Map<String, BigDecimal> outMap) throws JsonProcessingException {

		// 付款人处理
		if (inMap.size() == 1) {
			info.setFrom(inMap.keySet().toArray(new String[0])[0]);
		}
		// 收款人处理
		if (outMap.size() == 1) {
			info.setTo(outMap.keySet().toArray(new String[0])[0]);
		}

		if (StrUtil.isNotBlank(info.getFrom()) && StrUtil.isNotBlank(info.getTo())) {
			// 简单处理完成
			return;
		}

		// 筛选后的输入地址
		List<String> inList = new ArrayList<>();
		// 筛选后的输出地址
		List<String> outList = new ArrayList<>();

		Script script;
		for (String in : inMap.keySet()) {
			script = ScriptBuilder.createOutputScript(Address.fromString(np, in));

			// 地址在输出中. 可能是收款人
			if (outMap.containsKey(in)) {
				continue;
			}

			// omni 不支持,所以不可能是输入地址
			if (ScriptPattern.isP2WH(script)) {
				continue;
			}

			inList.add(in);
		}

		// 筛选后的输入不为空 , 筛选输出
		if (!inList.isEmpty()) {
			for (String in : outMap.keySet()) {
				script = ScriptBuilder.createOutputScript(Address.fromString(np, in));

				// omni 不支持,所以不可能是输入地址
				if (ScriptPattern.isP2WH(script)) {
					continue;
				}

				outList.add(in);
			}
		}

		// 如果输入 或 输出 为空. 输入 或 输出 数量大于1
		if ((inList.isEmpty() || outList.isEmpty()) || inList.size() > 1 || outList.size() > 1) {
			// 从omni拿数据
			Optional<TransactionInfo> optional = getTransactionInfoOfOmni(info.getHash());
			if (optional.isPresent()) {
				TransactionInfo omni = optional.get();
				info.setFrom(omni.getFrom());
				info.setTo(omni.getTo());
			}
			else {
				// 无法查询到数据. 修改状态
				info.setStatus(TransactionStatus.WAIT);
			}
		}
		else {
			info.setFrom(inList.get(0));
			info.setTo(outList.get(0));
		}
	}

	/**
	 * 直接从omni服务获取交易信息
	 * @param hash 交易hash
	 * @return java.util.Optional<live.lingting.virtual.currency.core.model.TransactionInfo>
	 * @author lingting 2021-03-16 14:39
	 */
	public Optional<TransactionInfo> getTransactionInfoOfOmni(String hash) throws JsonProcessingException {
		TransactionByHash response = request(STATIC_TRANSACTION_HASH, omniEndpoints, hash);

		TransactionInfo info = response.toTransactionInfo(confirmationsMin);
		if (info == null) {
			return Optional.empty();
		}
		return Optional.of(info);
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
			try {
				// 执行请求方法
				return domain.of(endpoints, params);
			}
			finally {
				// 释放锁
				properties.getUnlock().get();
			}
		}
		// 休眠, 然后调用自身
		ThreadUtil.sleep(sleepTime());
		return request(domain, endpoints, params);
	}

	/**
	 * 解析原始交易数据, 返回结果
	 * @author lingting 2021-01-10 19:00
	 */
	private Optional<TransactionInfo> btcTransactionHandler(RawTransaction rawTransaction) throws Throwable {
		// 总输出数量
		BigInteger sumOut = BigInteger.ZERO;
		// 输出详情
		Map<String, BigDecimal> outInfos = new HashMap<>(rawTransaction.getOuts().size());

		// 输出统计
		for (RawTransaction.Out out : rawTransaction.getOuts()) {
			// 统计输出数量
			sumOut = statisticsDetails(sumOut, outInfos, out);
		}
		return btcTransactionHandler(sumOut, outInfos, rawTransaction);
	}

	private Optional<TransactionInfo> btcTransactionHandler(BigInteger sumOut, Map<String, BigDecimal> outInfos,
			RawTransaction rawTransaction) throws Throwable {
		// 总输入数量
		BigInteger sumIn = BigInteger.ZERO;
		// 输入详情
		Map<String, BigDecimal> inInfos = new HashMap<>(rawTransaction.getIns().size());

		// 输入统计
		for (RawTransaction.In in : rawTransaction.getIns()) {
			sumIn = statisticsDetails(sumIn, inInfos, in.getPrevOut());
		}
		// 手续费 = 输入 - 输出 转换为 btc
		BigDecimal fee = getNumberByBalanceAndContract(sumIn.subtract(sumOut), OmniContract.BTC);

		TransactionInfo transactionInfo = new TransactionInfo().setContract(OmniContract.BTC)

				.setBlock(rawTransaction.getBlockHeight())

				.setHash(rawTransaction.getHash())

				.setVirtualCurrencyPlatform(VirtualCurrencyPlatform.BITCOIN)

				.setTime(rawTransaction.getTime())
				// btc 详情
				.setBtcInfo(new TransactionInfo.BtcInfo(inInfos, outInfos, fee));

		// 没有高度
		if (rawTransaction.getBlockHeight() == null) {
			// 等待
			transactionInfo.setStatus(TransactionStatus.WAIT);
		}
		// 计算确认数
		else {
			// 获取最新区块
			LatestBlock block = LatestBlock.of(blockchainEndpoints);
			// 计算确认数
			BigInteger confirmationNumber = block.getHeight().subtract(transactionInfo.getBlock());
			transactionInfo.setStatus(
					// 大于等于 配置的最小值则 交易成功,否则等待
					confirmationNumber.compareTo(BigInteger.valueOf(properties.getConfirmationsMin())) >= 0
							? TransactionStatus.SUCCESS : TransactionStatus.WAIT);
		}
		return Optional.of(transactionInfo);
	}

	/**
	 * 抽取统计操作
	 * @author lingting 2021-01-10 19:31
	 */
	private BigInteger statisticsDetails(BigInteger sumIn, Map<String, BigDecimal> inInfos, RawTransaction.Out out)
			throws Throwable {
		// 统计输入数量
		sumIn = sumIn.add(out.getValue());
		// 存在统计详情
		if (inInfos.containsKey(out.getAddr())) {
			inInfos.put(out.getAddr(),
					inInfos.get(out.getAddr()).add(getNumberByBalanceAndContract(out.getValue(), OmniContract.BTC)));
		}
		// 不存在统计详情
		else {
			inInfos.put(out.getAddr(), getNumberByBalanceAndContract(out.getValue(), OmniContract.BTC));
		}
		return sumIn;
	}

	private List<ECKey> getEcKeysByFrom(Account from) {
		List<ECKey> keys;
		// 多签
		if (from.getMulti()) {
			keys = new ArrayList<>(from.getPublicKeyArray().size());
			List<String> publicKeyArray = from.getPublicKeyArray();
			for (int keyIndex = 0; keyIndex < publicKeyArray.size(); keyIndex++) {
				// 私钥为空
				if (StrUtil.isBlank(from.getPrivateKeyArray().get(keyIndex))) {
					keys.add(ECKey.fromPublicOnly(Hex.decode(publicKeyArray.get(keyIndex))));
				}
				// 私钥不为空
				else {
					ECKey ecKey = ECKey.fromPrivate(Hex.decode(from.getPrivateKeyArray().get(keyIndex)));
					keys.add(ecKey);
				}
			}
		}
		// 单签
		else {
			keys = ListUtil.toList(ECKey.fromPrivate(Hex.decode(from.getPrivateKey())));
		}
		return keys;
	}

}
