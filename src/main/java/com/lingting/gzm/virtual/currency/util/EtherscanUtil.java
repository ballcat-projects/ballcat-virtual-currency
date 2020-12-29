package com.lingting.gzm.virtual.currency.util;

import static com.lingting.gzm.virtual.currency.util.KeyUtil.keyDeserialization;
import static com.lingting.gzm.virtual.currency.util.KeyUtil.keySerialization;
import static com.lingting.gzm.virtual.currency.util.ResolveUtil.removePreZero;
import static com.lingting.gzm.virtual.currency.util.ResolveUtil.stringToArrayBy64;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.lingting.gzm.virtual.currency.AbiMethod;
import com.lingting.gzm.virtual.currency.VirtualCurrencyAccount;
import com.lingting.gzm.virtual.currency.contract.EtherscanContract;
import com.lingting.gzm.virtual.currency.etherscan.Input;
import com.lingting.gzm.virtual.currency.exception.VirtualCurrencyException;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;
import org.web3j.crypto.WalletUtils;

/**
 * @author lingting 2020/12/23 20:14
 */
public class EtherscanUtil {

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
			String[] array = stringToArrayBy64(str);
			input.setTo(array[0]);
			input.setValue(new BigDecimal(Long.parseLong(array[1], 16)));
		});

		METHOD_HANDLER.put(AbiMethod.SEND_MULTI_SIG_TOKEN.getMethodId(), input -> {
			String str = input.getData();
			if (str.startsWith(START + AbiMethod.SEND_MULTI_SIG_TOKEN.getMethodId())) {
				str = str.substring((START + AbiMethod.SEND_MULTI_SIG_TOKEN.getMethodId()).length());
			}
			String[] array = stringToArrayBy64(str);
			input.setTo(array[0]);
			input.setValue(new BigDecimal(Long.parseLong(array[1], 16)));
			String address = START + removePreZero(array[2]);
			input.setContract(EtherscanContract.getByHash(address));
			input.setContractAddress(address);
		});

		METHOD_HANDLER.put(AbiMethod.SEND_MULTI_SIG.getMethodId(), input -> {
			String str = input.getData();
			if (str.startsWith(START + AbiMethod.SEND_MULTI_SIG.getMethodId())) {
				str = str.substring((START + AbiMethod.SEND_MULTI_SIG.getMethodId()).length());
			}
			String[] array = stringToArrayBy64(str);
			input.setTo(array[0]);
			input.setValue(new BigDecimal(Long.parseLong(array[1], 16)));
		});

		METHOD_HANDLER.put(AbiMethod.TRANSFER_FROM.getMethodId(), input -> {
			String str = input.getData();
			if (str.startsWith(START + AbiMethod.TRANSFER_FROM.getMethodId())) {
				str = str.substring((START + AbiMethod.TRANSFER_FROM.getMethodId()).length());
			}
			String[] array = stringToArrayBy64(str);
			input.setFrom(array[0]);
			input.setTo(array[1]);
			input.setValue(new BigDecimal(Long.parseLong(array[2], 16)));
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
	public static VirtualCurrencyAccount createAccount() throws InvalidAlgorithmParameterException,
			NoSuchAlgorithmException, NoSuchProviderException, CipherException, JsonProcessingException {
		return createAccount(StrUtil.EMPTY);
	}

	/**
	 * 创建eth账号
	 * @param password 密码
	 * @author lingting 2020-12-22 17:32
	 */
	public static VirtualCurrencyAccount createAccount(String password) throws InvalidAlgorithmParameterException,
			NoSuchAlgorithmException, NoSuchProviderException, CipherException, JsonProcessingException {
		ECKeyPair keyPair = Keys.createEcKeyPair();
		// 私钥
		String privateKey = keyDeserialization(keyPair.getPrivateKey());
		// 公钥
		String publicKey = keyDeserialization(keyPair.getPublicKey());
		// 钱包文件
		WalletFile walletFile = Wallet.createStandard(password, keyPair);
		// 获取钱包地址
		String address = walletFile.getAddress();
		// 生成 account 对象
		return new VirtualCurrencyAccount(address, publicKey, privateKey).setKeystore(JsonUtil.toJson(walletFile));
	}

	/**
	 * 根据私钥获取账户
	 * @param address 地址
	 * @param privateKey 私钥
	 * @return com.lingting.gzm.virtual.currency.VirtualCurrencyAccount
	 * @author lingting 2020-12-23 14:04
	 */
	public static VirtualCurrencyAccount getAccountOfKey(String address, String privateKey) {
		return getAccountOfKey(address, null, privateKey);
	}

	/**
	 * 根据公私钥获取账户
	 * @param address 地址
	 * @param publicKey 公钥, 忘记了可以留空
	 * @param privateKey 私钥
	 * @return com.lingting.gzm.virtual.currency.VirtualCurrencyAccount
	 * @author lingting 2020-12-23 14:05
	 */
	public static VirtualCurrencyAccount getAccountOfKey(String address, String publicKey, String privateKey) {
		// 地址不能为空
		Assert.isFalse(StrUtil.isBlank(address));
		// 私钥不能为空
		Assert.isFalse(StrUtil.isBlank(privateKey));
		VirtualCurrencyAccount account = new VirtualCurrencyAccount(address, publicKey, privateKey);
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

	/**
	 * 根据 keystore文件内容获取账号
	 * @param address 地址
	 * @param password 密码. 生成keystore时的密码
	 * @param ketStore keystore 内容
	 * @return com.lingting.gzm.virtual.currency.VirtualCurrencyAccount
	 * @author lingting 2020-12-23 14:05
	 */
	public static VirtualCurrencyAccount getAccountOfKeystore(String address, String password, String ketStore)
			throws IOException, CipherException, VirtualCurrencyException {
		Credentials credentials = WalletUtils.loadJsonCredentials(password, ketStore);
		if (!address.equals(credentials.getAddress())) {
			throw new VirtualCurrencyException("地址错误!");
		}
		ECKeyPair keyPair = credentials.getEcKeyPair();
		return new VirtualCurrencyAccount()
				// 地址
				.setAddress(address)
				// 私钥
				.setPrivateKey(keyDeserialization(keyPair.getPrivateKey()))
				// 公钥
				.setPublicKey(keyDeserialization(keyPair.getPublicKey()))
				// keystore
				.setKeystore(ketStore);
	}

}
