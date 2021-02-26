package live.lingting.virtual.currency.etherscan.util;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import org.bitcoinj.crypto.DeterministicKey;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import live.lingting.virtual.currency.core.model.Account;
import live.lingting.virtual.currency.core.enums.AbiMethod;
import live.lingting.virtual.currency.core.exception.VirtualCurrencyException;
import live.lingting.virtual.currency.core.util.AbiUtils;
import live.lingting.virtual.currency.etherscan.contract.EtherscanContract;
import live.lingting.virtual.currency.etherscan.model.Input;

/**
 * @author lingting 2020/12/23 20:14
 */
public class EtherscanUtils {

	public static final String START = "0x";

	public static final Map<String, Consumer<Input>> METHOD_HANDLER;

	static {
		// 合约函数数据解析方法
		METHOD_HANDLER = new ConcurrentHashMap<>(AbiMethod.values().length);
		METHOD_HANDLER.put(AbiMethod.TRANSFER.getMethodId(), input -> {
			String str = input.getData();
			if (str.startsWith(START + AbiMethod.TRANSFER.getMethodId())) {
				str = str.substring((START + AbiMethod.TRANSFER.getMethodId()).length());
			}
			String[] array = AbiUtils.stringToArrayBy64(str);
			input.setTo(array[0]);
			input.setValue(new BigInteger(array[1], 16));
		});

		METHOD_HANDLER.put(AbiMethod.SEND_MULTI_SIG_TOKEN.getMethodId(), input -> {
			String str = input.getData();
			if (str.startsWith(START + AbiMethod.SEND_MULTI_SIG_TOKEN.getMethodId())) {
				str = str.substring((START + AbiMethod.SEND_MULTI_SIG_TOKEN.getMethodId()).length());
			}
			String[] array = AbiUtils.stringToArrayBy64(str);
			input.setTo(array[0]);
			input.setValue(new BigInteger(array[1], 16));
			String address = START + AbiUtils.removePreZero(array[2]);
			input.setContract(EtherscanContract.getByHash(address));
			input.setContractAddress(address);
		});

		METHOD_HANDLER.put(AbiMethod.SEND_MULTI_SIG.getMethodId(), input -> {
			String str = input.getData();
			if (str.startsWith(START + AbiMethod.SEND_MULTI_SIG.getMethodId())) {
				str = str.substring((START + AbiMethod.SEND_MULTI_SIG.getMethodId()).length());
			}
			String[] array = AbiUtils.stringToArrayBy64(str);
			input.setTo(array[0]);
			input.setValue(new BigInteger(array[1], 16));
		});

		METHOD_HANDLER.put(AbiMethod.TRANSFER_FROM.getMethodId(), input -> {
			String str = input.getData();
			if (str.startsWith(START + AbiMethod.TRANSFER_FROM.getMethodId())) {
				str = str.substring((START + AbiMethod.TRANSFER_FROM.getMethodId()).length());
			}
			String[] array = AbiUtils.stringToArrayBy64(str);
			input.setFrom(array[0]);
			input.setTo(array[1]);
			input.setValue(new BigInteger(array[2], 16));
		});
	}

	/**
	 * 解析input数据
	 *
	 * @author lingting 2020-09-02 14:23
	 */
	public static Input resolve(String inputString) throws VirtualCurrencyException {
		// 获取方法id
		String methodId = inputString.substring(2, 10);
		Input input = new Input().setMethod(AbiMethod.getById(methodId)).setData(inputString);

		if (!METHOD_HANDLER.containsKey(methodId)) {
			throw new VirtualCurrencyException("无法正确解析input data 请额外开发支持");
		}
		// 处理
		METHOD_HANDLER.get(methodId).accept(input);
		return input;
	}

	/**
	 * 创建eth账号
	 * @author lingting 2020-12-22 17:32
	 */
	public static Account createAccount()
			throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
		return createAccount(Keys.createEcKeyPair());
	}

	public static Account createAccount(ECKeyPair keyPair) {
		// 私钥
		String privateKey = keyDeserialization(keyPair.getPrivateKey());
		// 公钥
		String publicKey = keyDeserialization(keyPair.getPublicKey());

		// 获取钱包地址
		String address = addStart(Keys.getAddress(keyPair));
		// 生成 account 对象
		return new Account(address, publicKey, privateKey);
	}

	public static Account createAccount(DeterministicKey key) {
		return createAccount(ECKeyPair.create(key.getPrivKeyBytes()));
	}

	/**
	 * 根据私钥获取账户
	 * @param address 地址
	 * @param privateKey 私钥
	 * @return live.lingting.virtual.currency.VirtualCurrencyAccount
	 * @author lingting 2020-12-23 14:04
	 */
	public static Account getAccountOfKey(String address, String privateKey) {
		return getAccountOfKey(address, null, privateKey);
	}

	/**
	 * 根据公私钥获取账户
	 * @param address 地址
	 * @param publicKey 公钥, 忘记了可以留空
	 * @param privateKey 私钥
	 * @return live.lingting.virtual.currency.VirtualCurrencyAccount
	 * @author lingting 2020-12-23 14:05
	 */
	public static Account getAccountOfKey(String address, String publicKey, String privateKey) {
		address = addStart(address);
		// 地址不能为空
		Assert.isFalse(StrUtil.isBlank(address));
		// 私钥不能为空
		Assert.isFalse(StrUtil.isBlank(privateKey));
		Account account = new Account(address, publicKey, privateKey);
		// 公钥不存在
		if (StrUtil.isBlank(publicKey)) {
			ECKeyPair keyPair = ECKeyPair.create(keySerialization(privateKey));
			// 计算公钥
			publicKey = keyDeserialization(keyPair.getPublicKey());
		}
		// 设置公钥
		account.setPublicKey(publicKey);
		return account;
	}

	public static BigInteger toBigInteger(String str) {
		return new BigInteger(removeStart(str), 16);
	}

	public static String removeStart(String str) {
		if (str.startsWith(START)) {
			str = str.substring(START.length());
		}
		return str;
	}

	public static String addStart(String str) {
		if (!str.startsWith(START)) {
			str = START + str;
		}
		return str;
	}

	/**
	 * 序列化key
	 * @author lingting 2020-12-22 19:20
	 */
	public static BigInteger keySerialization(String key) {
		return new BigInteger(key, 16);
	}

	/**
	 * 反序列化key
	 * @author lingting 2020-12-22 19:20
	 */
	public static String keyDeserialization(BigInteger key) {
		return key.toString(16);
	}

}
