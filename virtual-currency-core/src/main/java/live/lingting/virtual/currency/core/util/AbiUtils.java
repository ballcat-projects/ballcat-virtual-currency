package live.lingting.virtual.currency.core.util;

import cn.hutool.core.util.StrUtil;
import java.math.BigInteger;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bouncycastle.util.encoders.Hex;
import live.lingting.virtual.currency.core.Contract;

/**
 * @author lingting 2020/12/25 22:07
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AbiUtils {

	public static final String REMOVE_ZERO_REG_STR = "^(0+)";

	/**
	 * 字符串以 64 长度分割
	 *
	 * @author lingting 2020-11-17 21:40
	 */
	public static String[] stringToArrayBy64(String str) {
		return StrUtil.cut(str, 64);
	}

	/**
	 * 字符串 移除前缀后 以 64 长度分割
	 * @author lingting 2021-04-01 15:49
	 */
	public static String[] stringToArrayBy64(String prefix, String str) {
		if (str.startsWith(prefix)) {
			str = str.substring(prefix.length());
		}
		return stringToArrayBy64(str);
	}

	/**
	 * 移除字符串前的0
	 *
	 * @author lingting 2020-11-17 21:44
	 */
	public static String removePreZero(String str) {
		return str.replaceAll(REMOVE_ZERO_REG_STR, "");
	}

	/**
	 * 在字符前补0, 使其长度变为64位
	 * @author lingting 2020-12-25 22:07
	 */
	public static String addZeroTo64InPre(String str) {
		return StrUtil.padPre(str, 64, "0");
	}

	/**
	 * 数字转 uint256 参数
	 * @param uint 十进制数字字符串
	 * @author lingting 2020-12-25 22:26
	 */
	public static String encodeUint256Params(BigInteger uint) {
		return addZeroTo64InPre(Hex.toHexString(uint.toByteArray()));
	}

	/**
	 * 创建合约
	 * @author lingting 2021-01-05 19:48
	 */
	public static Contract createContract(String hash) {
		return createContract(hash, null);
	}

	public static Contract createContract(String hash, Integer decimals) {
		return new Contract() {
			@Override
			public String getHash() {
				return hash;
			}

			@Override
			public Integer getDecimals() {
				return decimals;
			}
		};
	}

}
