package live.lingting.virtual.currency.bitcoin.model;

import static live.lingting.virtual.currency.bitcoin.util.BitcoinUtils.PROPERTY_PREFIX;

import cn.hutool.core.util.StrUtil;
import java.math.BigInteger;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import live.lingting.virtual.currency.core.util.AbiUtils;

/**
 * omni的合约
 *
 * @author lingting 2021/3/16 10:26
 */
@Getter
@Setter
@Accessors(chain = true)
public class Properties {

	public static Properties of(String hash, BigInteger amounts) {
		return new Properties().setHash(new BigInteger(hash)).setAmounts(amounts);
	}

	public static Properties of(String script) {
		String[] split = StrUtil.split(script.substring(PROPERTY_PREFIX.length()), 16);
		return new Properties()
				// hash
				.setHash(new BigInteger(AbiUtils.removePreZero(split[0]), 16))
				// 数量
				.setAmounts(new BigInteger(AbiUtils.removePreZero(split[1]), 16));
	}

	private Properties() {
	}

	/**
	 * 合约hash
	 */
	private BigInteger hash;

	/**
	 * 转账数量
	 */
	private BigInteger amounts;

	public String toScript() {
		return StrUtil.format("{}{}{}",
				// 合约转账 开头字符串
				PROPERTY_PREFIX,
				// 合约hash 的 十六进制 前面补0 到 16位
				StrUtil.padPre(hash.toString(16), 16, "0"),
				// 转账数量 的 十六进制 前面补0 到 16位
				StrUtil.padPre(amounts.toString(16), 16, "0"));
	}

	@Override
	public String toString() {
		return toScript();
	}

}
