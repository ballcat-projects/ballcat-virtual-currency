package com.lingting.gzm.virtual.currency.util;

import java.math.BigInteger;

/**
 * @author lingting 2020/12/23 21:39
 */
public class KeyUtil {

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
