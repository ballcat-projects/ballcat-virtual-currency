package com.lingting.gzm.virtual.currency.util;

import cn.hutool.core.util.StrUtil;

/**
 * @author lingting 2020/12/25 16:13
 */
public class ResolveUtil {

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
	 * 移除字符串前的0
	 *
	 * @author lingting 2020-11-17 21:44
	 */
	public static String removePreZero(String str) {
		return str.replaceAll(REMOVE_ZERO_REG_STR, "");
	}

}
