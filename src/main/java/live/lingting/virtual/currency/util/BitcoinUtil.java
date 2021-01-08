package live.lingting.virtual.currency.util;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.LegacyAddress;
import org.bitcoinj.core.NetworkParameters;
import org.bouncycastle.util.encoders.Hex;
import live.lingting.virtual.currency.Account;
import live.lingting.virtual.currency.TransferParams;
import live.lingting.virtual.currency.bitcoin.UnspentRes;

/**
 * @author lingting 2020/12/28 17:49
 */
public class BitcoinUtil {

	public static final Coin COIN_TEN = Coin.valueOf(10);

	/**
	 * @param id id 可在 [
	 * {@link NetworkParameters#ID_MAINNET},{@link NetworkParameters#ID_TESTNET} ] 中选择
	 */
	public static Account create(String id) {
		return create(NetworkParameters.fromID(id));
	}

	/**
	 * @param parameters 表示地址在哪个网络使用, 使用
	 * {@link NetworkParameters#fromID(java.lang.String)} 此方法进行生成, id 可在 [
	 * {@link NetworkParameters#ID_MAINNET},{@link NetworkParameters#ID_TESTNET} ] 中选择
	 */
	public static Account create(NetworkParameters parameters) {
		ECKey ecKey = new ECKey();
		return new Account()
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
	 * 未使用余额中的 value 转为 聪
	 * @see UnspentRes.Unspent#getValue()
	 * @author lingting 2021-01-07 16:41
	 */
	public static Coin unspentValueToCoin(BigInteger value) {
		return Coin.valueOf(value.longValue());
	}

	/**
	 * btc金额转为 聪, 单位 个
	 * @param btc 多少个 btc
	 * @author lingting 2021-01-07 13:54
	 */
	public static Coin btcToCoin(BigDecimal btc) {
		return Coin.valueOf(btc.multiply(BigDecimal.TEN.pow(8)).longValue());
	}

	/**
	 * 聪 转为 btc金额, 单位 个
	 * @param coin 多少个 coin
	 * @author lingting 2021-01-07 13:54
	 */
	public static BigDecimal coinToBtc(Coin coin) {
		return new BigDecimal(coin.longValue()).divide(BigDecimal.TEN.pow(Coin.SMALLEST_UNIT_EXPONENT),
				MathContext.UNLIMITED);
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

}
