package live.lingting.virtual.currency.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import org.bitcoinj.core.Base58;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.LegacyAddress;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.SegwitAddress;
import org.bitcoinj.core.Sha256Hash;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.util.encoders.Hex;
import live.lingting.virtual.currency.Account;
import live.lingting.virtual.currency.TransferParams;

/**
 * @author lingting 2020/12/28 17:49
 */
public class BitcoinUtil {

	/**
	 * omni 合约转账 script 开头字符串
	 */
	public static final String PROPERTY_PREFIX = "6a146f6d6e69";

	/**
	 * 基础地址
	 * @param parameters 表示地址在哪个网络使用, 使用
	 * {@link NetworkParameters#fromID(java.lang.String)} 此方法进行生成, id 可在 [
	 * {@link NetworkParameters#ID_MAINNET},{@link NetworkParameters#ID_TESTNET} ] 中选择
	 */
	public static Account createLegacyAddress(NetworkParameters parameters) {
		ECKey ecKey = new ECKey(new SecureRandom());
		return new Account()
				// 地址
				.setAddress(LegacyAddress.fromKey(parameters, ecKey).toString())
				// 私钥
				.setPrivateKey(ecKey.getPrivateKeyAsHex())
				// 公钥
				.setPublicKey(ecKey.getPublicKeyAsHex());
	}

	/**
	 * 隔离见证地址
	 * @author lingting 2021-01-12 13:24
	 */
	public static Account createSegwitAddress(NetworkParameters parameters) {
		ECKey ecKey = new ECKey(new SecureRandom());
		return new Account()
				// 地址
				.setAddress(SegwitAddress.fromKey(parameters, ecKey).toString())
				// 私钥
				.setPrivateKey(ecKey.getPrivateKeyAsHex())
				// 公钥
				.setPublicKey(ecKey.getPublicKeyAsHex());
	}

	/**
	 * 创建多签地址
	 * @param min 最小值, 即多少个人签名即可进行交易
	 * @param number 通过多少个地址来生成
	 * @author lingting 2021-01-12 17:10
	 */
	public static Account createMultiAddress(NetworkParameters parameters, int min, int number) {
		List<ECKey> keys = new ArrayList<>();
		for (int i = 0; i < number; i++) {
			keys.add(new ECKey(new SecureRandom()));
		}

		return createMultiAddress(parameters, min, keys);
	}

	public static Account createMultiAddress(NetworkParameters parameters, int min, List<ECKey> keys) {
		// 构筑脚本
		StringBuilder script = new StringBuilder(Hex.toHexString(new byte[] { (byte) (80 + min) }));

		List<String> publicKeys = new ArrayList<>(keys.size());
		List<String> privateKeys = new ArrayList<>(keys.size());

		for (ECKey key : keys) {
			String publicKeyAsHex = key.getPublicKeyAsHex();
			publicKeys.add(publicKeyAsHex);
			privateKeys.add(key.getPrivateKeyAsHex());
			script.append("21").append(publicKeyAsHex);
		}
		script.append(Hex.toHexString(new byte[] { (byte) (80 + keys.size()) })).append("ae");

		// sha256
		byte[] sha256 = Sha256Hash.hash(Hex.decode(script.toString()));

		// 散列脚本
		RIPEMD160Digest digest = new RIPEMD160Digest();
		digest.update(sha256, 0, sha256.length);
		byte[] out = new byte[20];
		digest.doFinal(out, 0);

		// 转为 p2sh地址
		String address = Base58.encodeChecked(parameters.getP2SHHeader(), out);
		return new Account().setAddress(address).setMulti(true).setPrivateKeyArray(privateKeys)
				.setPublicKeyArray(publicKeys);
	}

	/**
	 * 根据公钥和签名要求数量生成对应 public key hash
	 * @author lingting 2021-01-19 11:28
	 */
	public static byte[] generateMultiPublicKeyHash(int min, List<String> publicKeyList) {
		// 构筑脚本
		StringBuilder script = new StringBuilder(Hex.toHexString(new byte[] { (byte) (80 + min) }));

		for (String key : publicKeyList) {
			script.append("21").append(key);
		}
		script.append(Hex.toHexString(new byte[] { (byte) (80 + publicKeyList.size()) })).append("ae");

		// sha256
		byte[] sha256 = Sha256Hash.hash(Hex.decode(script.toString()));

		// 散列脚本
		RIPEMD160Digest digest = new RIPEMD160Digest();
		digest.update(sha256, 0, sha256.length);
		byte[] out = new byte[20];
		digest.doFinal(out, 0);

		return out;
	}

	/**
	 * 根据私钥获取账户
	 * @param address 地址
	 * @param privateKey 私钥
	 * @return com.lingting.gzm.virtual.currency.VirtualCurrencyAccount
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
	 * @return com.lingting.gzm.virtual.currency.VirtualCurrencyAccount
	 * @author lingting 2020-12-23 14:05
	 */
	public static Account getAccountOfKey(String address, String publicKey, String privateKey) {
		// 地址不能为空
		Assert.isFalse(StrUtil.isBlank(address));
		// 私钥不能为空
		Assert.isFalse(StrUtil.isBlank(privateKey));
		Account account = new Account(address, publicKey, privateKey);
		// 公钥不存在
		if (StrUtil.isBlank(publicKey)) {
			ECKey ecKey = ECKey.fromPrivate(Hex.decode(privateKey));
			// 计算公钥
			publicKey = ecKey.getPublicKeyAsHex();
		}
		// 设置公钥
		account.setPublicKey(publicKey);
		return account;
	}

	/**
	 * 生成多签账号
	 * @param multiNum 最少签名个数, 简单来说必须转入与 multiNum 对应数量的可用私钥
	 * @param address 地址
	 * @param publicKeyArray 所有 公钥 请注意顺序, 如果顺序与生成时不一致可能导致无法正常交易
	 * @param privateKeyArray 请与 公钥 一一对应, 如果您未拥有该 公钥 对应的 私钥, 则插入 空值
	 * @return live.lingting.virtual.currency.Account
	 * @author lingting 2021-01-12 20:51
	 */
	public static Account getMultiAccountOfKey(String address, int multiNum, List<String> publicKeyArray,
			List<String> privateKeyArray) {
		// 最少一个签名
		Assert.isFalse(multiNum < 1);
		// 地址不能为空
		Assert.isFalse(StrUtil.isBlank(address));
		// 密钥不能为空
		Assert.isFalse(CollectionUtil.isEmpty(publicKeyArray));
		Assert.isFalse(CollectionUtil.isEmpty(privateKeyArray));
		// 公私钥长度必须一致
		Assert.isFalse(publicKeyArray.size() != privateKeyArray.size());
		return new Account(address, multiNum, publicKeyArray, privateKeyArray);
	}

	/**
	 * btc金额转为 聪, 单位 个
	 * @param btc 多少个 btc
	 * @author lingting 2021-01-07 13:54
	 */
	public static Coin btcToCoin(BigDecimal btc) {
		return btcToCoin(btc.multiply(BigDecimal.TEN.pow(8)).toBigInteger());
	}

	/**
	 * btc 数量转为 聪, 单位 个
	 * @param btc btc 数量
	 * @return org.bitcoinj.core.Coin
	 * @author lingting 2021-01-20 17:42
	 */
	public static Coin btcToCoin(BigInteger btc) {
		return Coin.valueOf(btc.longValue());
	}

	/**
	 * 聪 转为 btc数量, 单位 个
	 * @param coin 多少个 coin
	 * @author lingting 2021-01-07 13:54
	 */
	public static BigInteger coinToBtcBalance(Coin coin) {
		return new BigInteger(coin.toString());
	}

	/**
	 * 聪 转为 btc金额, 单位 个
	 * @param coin 多少个 coin
	 * @author lingting 2021-01-07 13:54
	 */
	public static BigDecimal coinToBtc(Coin coin) {
		return new BigDecimal(coin.toPlainString());
	}

	/**
	 * 计算 btc 交易的手续费
	 * @param inNumber 输入数量
	 * @param outNumber 输出数量
	 * @param fee 每字节手续费单价
	 * @return org.bitcoinj.core.Coin
	 * @author lingting 2021-01-07 14:02
	 */
	public static Coin getSumFee(long inNumber, long outNumber, Coin fee) {
		return fee.multiply(inNumber * 148 + outNumber * 34 + 10);
	}

	/**
	 * 计算 btc 交易的手续费
	 * @param inNumber 输入数量
	 * @param outNumber 输出数量
	 * @param params 转账配置
	 * @return org.bitcoinj.core.Coin
	 * @author lingting 2021-01-07 14:02
	 */
	public static Coin getSumFee(long inNumber, long outNumber, TransferParams params) {
		// 未配置总价手续费
		if (params.getSumFee() == null) {
			// 计算手续费
			return getSumFee(inNumber, outNumber, params.getFee());
		}
		// 已配置总手续费
		return params.getSumFee();
	}

	/**
	 * 私钥转 wif格式
	 * @param pk 私钥
	 * @return java.lang.String
	 * @author lingting 2021-01-19 17:36
	 */
	public static String toWif(NetworkParameters np, String pk) {
		return toWif(np, ECKey.fromPrivate(Hex.decode(pk)));
	}

	/**
	 * 私钥转 wif格式
	 * @param np 环境
	 * @param key 私钥
	 * @return java.lang.String
	 * @author lingting 2021-01-19 17:36
	 */
	public static String toWif(NetworkParameters np, ECKey key) {
		return key.getPrivateKeyAsWiF(np);
	}

}
