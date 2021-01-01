package live.lingting.virtual.currency.util;

import cn.hutool.core.util.StrUtil;
import java.math.BigInteger;
import org.bouncycastle.util.encoders.Hex;

/**
 * @author lingting 2020/12/25 22:07
 */
public class AbiUtil {

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

}
