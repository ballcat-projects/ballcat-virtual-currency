package com.lingting.gzm.virtual.currency.util;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.lingting.gzm.virtual.currency.VirtualCurrencyAccount;
import com.lingting.gzm.virtual.currency.exception.VirtualCurrencyException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
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

	/**
	 * 创建eth账号
	 * @author lingting 2020-12-22 17:32
	 */
	public static VirtualCurrencyAccount createAccount() throws InvalidAlgorithmParameterException,
			NoSuchAlgorithmException, NoSuchProviderException, CipherException {
		ECKeyPair keyPair = Keys.createEcKeyPair();
		// 私钥
		String privateKey = keyDeserialization(keyPair.getPrivateKey());
		// 公钥
		String publicKey = keyDeserialization(keyPair.getPublicKey());
		// 钱包文件
		WalletFile walletFile = Wallet.createStandard(StrUtil.EMPTY, keyPair);
		// 获取钱包地址
		String address = walletFile.getAddress();
		// 生成 account 对象
		return new VirtualCurrencyAccount(address, publicKey, privateKey);
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
		ECKeyPair keyPair;
		// 公钥不存在
		if (StrUtil.isBlank(publicKey)) {
			keyPair = ECKeyPair.create(keySerialization(privateKey));
			// 计算公钥
			publicKey = keyDeserialization(keyPair.getPublicKey());
		}
		// 设置公钥
		account.setPublicKey(publicKey);
		// 设置证书
		account.setCredentials(Credentials.create(privateKey, publicKey));
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
				// 证书
				.setCredentials(credentials)
				// 私钥
				.setPrivateKey(keyDeserialization(keyPair.getPrivateKey()))
				// 公钥
				.setPublicKey(keyDeserialization(keyPair.getPublicKey()));
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
