package live.lingting.virtual.currency.service.impl;

import static org.bitcoinj.core.Transaction.Purpose;
import static org.bitcoinj.core.Transaction.SigHash;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Context;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.TransactionConfidence;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.core.TransactionOutPoint;
import org.bitcoinj.core.Utils;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcoinj.script.ScriptError;
import org.bitcoinj.script.ScriptException;
import org.bitcoinj.script.ScriptPattern;
import org.bouncycastle.util.encoders.Hex;
import live.lingting.virtual.currency.Account;
import live.lingting.virtual.currency.Transaction;
import live.lingting.virtual.currency.TransferParams;
import live.lingting.virtual.currency.TransferResult;
import live.lingting.virtual.currency.bitcoin.FeeAndSpent;
import live.lingting.virtual.currency.bitcoin.UnspentRes;
import live.lingting.virtual.currency.bitcoin.UnspentRes.Unspent;
import live.lingting.virtual.currency.contract.Contract;
import live.lingting.virtual.currency.contract.OmniContract;
import live.lingting.virtual.currency.endpoints.Endpoints;
import live.lingting.virtual.currency.enums.TransactionStatus;
import live.lingting.virtual.currency.enums.VcPlatform;
import live.lingting.virtual.currency.exception.VirtualCurrencyException;
import live.lingting.virtual.currency.omni.Balances;
import live.lingting.virtual.currency.omni.Domain;
import live.lingting.virtual.currency.omni.PushTx;
import live.lingting.virtual.currency.omni.TokenHistory;
import live.lingting.virtual.currency.omni.TransactionByHash;
import live.lingting.virtual.currency.properties.OmniProperties;
import live.lingting.virtual.currency.service.PlatformService;
import live.lingting.virtual.currency.util.AbiUtil;
import live.lingting.virtual.currency.util.BitcoinUtil;

/**
 * @author lingting 2020-09-01 17:16
 */
@Slf4j
public class OmniHttpServiceImpl implements PlatformService {

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

	private final OmniProperties properties;

	private final Endpoints omniEndpoints;

	private final Endpoints bitcoinEndpoints;

	public OmniHttpServiceImpl(OmniProperties properties) {
		this.properties = properties;
		// 自定义 omni url
		if (StrUtil.isNotBlank(properties.getOmniUrl())) {
			omniEndpoints = properties::getOmniUrl;
		}
		// 未自定义 omni url
		else {
			omniEndpoints = properties.getOmniEndpoints();
		}
		// 自定义 bitcoin url
		if (StrUtil.isNotBlank(properties.getOmniUrl())) {
			bitcoinEndpoints = properties::getBitcoinUrl;
		}
		// 未自定义 bitcoin url
		else {
			bitcoinEndpoints = properties.getBitcoinEndpoints();
		}

	}

	@Override
	public Optional<Transaction> getTransactionByHash(String hash) throws JsonProcessingException {
		TransactionByHash response = request(STATIC_TRANSACTION_HASH, omniEndpoints, hash);
		// 交易查询不到 或者 valid 为 false
		if (response.getAmount() == null || !response.getValid()) {
			return Optional.empty();
		}

		OmniContract contract = OmniContract.getById(response.getPropertyId());
		Transaction transaction = new Transaction()

				.setContract(contract != null ? contract : AbiUtil.createContract(response.getPropertyId().toString()))

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
		Balances balances = request(STATIC_BALANCES, omniEndpoints, address);
		if (CollectionUtil.isEmpty(balances.getBalance())) {
			return BigInteger.ZERO;
		}
		for (Balances.Balance balance : balances.getBalance()) {
			// 协助缓存精度
			if (!CONTRACT_DECIMAL_CACHE.containsKey(contract.getHash())) {
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
	public TransferResult transfer(Account from, String to, Contract contract, BigDecimal value, TransferParams params)
			throws Throwable {
		NetworkParameters np = properties.getNp();
		// BTC 转账数量
		Coin btcAmount;
		// 转账比特
		if (contract == OmniContract.BTC) {
			btcAmount = BitcoinUtil.btcToCoin(value);
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
				UnspentRes.of(bitcoinEndpoints, properties.getConfirmationsMin(), from.getAddress()).getUnspentList(),
				// 转账数量
				btcAmount,
				// 最小确认数
				new BigInteger(properties.getConfirmationsMin().toString()));

		// 输入地址密钥
		ECKey key = ECKey.fromPrivate(Hex.decode(from.getPrivateKey()));

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
					// 找零 = 输出数量 - 手续费 - 转账数量
					fs.getOutNumber().subtract(fs.getFee()).subtract(btcAmount),
					// 找零给自己
					key);
		}

		// 转账合约输出
		if (contract != OmniContract.BTC) {
			// 转账合约数量
			BigInteger number = valueToBalanceByContract(value, contract);
			// 构筑输出hex
			String contractHex = StrUtil.format("6a146f6d6e69{}{}",
					// 合约hash 的 十六进制 前面补0 到 16位
					StrUtil.padPre(new BigInteger(contract.getHash()).toString(16), 16, "0"),
					// 转账数量 的 十六进制 前面补0 到 16位
					StrUtil.padPre(number.toString(16), 16, "0"));

			// 加入输出
			tx.addOutput(Coin.ZERO, new Script(Utils.HEX.decode(contractHex)));
		}

		// 添加输入
		for (int i = 0; i < fs.getList().size(); i++) {
			Unspent spent = fs.getList().get(i);
			TransactionOutPoint outPoint = new TransactionOutPoint(np, spent.getTxOutputN(),
					Sha256Hash.wrap(spent.getTxId()));

			TransactionInput input = new TransactionInput(np, tx, Hex.decode(spent.getScript()), outPoint,
					Coin.valueOf(Long.parseLong(spent.getValue())));
			tx.addInput(input);
		}

		List<TransactionInput> inputs = tx.getInputs();
		// 签名输入
		for (int i = 0; i < inputs.size(); i++) {
			Script scriptPubKey = ScriptBuilder.createOutputScript(fromAddress);
			Sha256Hash hash = tx.hashForSignature(i, scriptPubKey, SigHash.ALL, true);
			ECKey.ECDSASignature ecdsaSignature = key.sign(hash);
			TransactionSignature txSignature = new TransactionSignature(ecdsaSignature, SigHash.ALL, true);
			if (ScriptPattern.isP2PK(scriptPubKey)) {
				tx.getInput(i).setScriptSig(ScriptBuilder.createInputScript(txSignature));
			}
			else {
				if (!ScriptPattern.isP2PKH(scriptPubKey)) {
					throw new ScriptException(ScriptError.SCRIPT_ERR_UNKNOWN_ERROR,
							"Unable to sign this scrptPubKey: " + scriptPubKey);
				}
				tx.getInput(i).setScriptSig(ScriptBuilder.createInputScript(txSignature, key));
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

		// 广播交易, 返回 交易hash
		PushTx pushTx = properties.getBroadcastTransaction().apply(raw, omniEndpoints);
		if (!pushTx.isSuccess()) {
			if (pushTx.getE() != null) {
				throw new VirtualCurrencyException("转账失败", pushTx.getE());
			}
			throw new VirtualCurrencyException("转账失败");
		}
		return TransferResult.success(pushTx.getTxId());
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
			// 执行请求方法
			T t = domain.of(endpoints, params);
			// 释放锁
			properties.getUnlock().get();
			return t;
		}
		// 休眠, 然后调用自身
		ThreadUtil.sleep(sleepTime());
		return request(domain, endpoints, params);
	}

}
