package live.lingting.virtual.currency.util;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import live.lingting.virtual.currency.VirtualCurrencyAccount;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.LegacyAddress;
import org.bitcoinj.core.NetworkParameters;
import org.bouncycastle.util.encoders.Hex;

/**
 * @author lingting 2020/12/28 17:49
 */
public class BitcoinUtil {

	/**
	 * @param id id 可在 [
	 * {@link NetworkParameters#ID_MAINNET},{@link NetworkParameters#ID_TESTNET} ] 中选择
	 */
	public static VirtualCurrencyAccount create(String id) {
		return create(NetworkParameters.fromID(id));
	}

	/**
	 * @param parameters 表示地址在哪个网络使用, 使用
	 * {@link NetworkParameters#fromID(java.lang.String)} 此方法进行生成, id 可在 [
	 * {@link NetworkParameters#ID_MAINNET},{@link NetworkParameters#ID_TESTNET} ] 中选择
	 */
	public static VirtualCurrencyAccount create(NetworkParameters parameters) {
		ECKey ecKey = new ECKey();
		return new VirtualCurrencyAccount()
				// 地址
				.setAddress(LegacyAddress.fromKey(parameters, ecKey).toBase58())
				// 私钥
				.setPrivateKey(ecKey.getPrivateKeyAsHex())
				// 公钥
				.setPublicKey(ecKey.getPublicKeyAsHex());
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
			ECKey ecKey = ECKey.fromPrivate(Hex.decode(privateKey));
			// 计算公钥
			publicKey = ecKey.getPublicKeyAsHex();
		}
		// 设置公钥
		account.setPublicKey(publicKey);
		return account;
	}

}
