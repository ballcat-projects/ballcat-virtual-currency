package com.lingting.gzm.virtual.currency.tronscan;

import com.lingting.gzm.virtual.currency.exception.VirtualCurrencyException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * abi方法
 *
 * @author lingting 2020-12-25 16:08
 */
@Getter
@AllArgsConstructor
public enum Method {

	/**
	 * 普通转账 transfer(address _to,uint256 _value)
	 */
	TRANSFER("a9059cbb"),

	/**
	 *  获取合约精度 decimals
	 */
	DECIMALS("313ce567"),
	;

	/**
	 * 方法id
	 */
	private final String methodId;

	public static Method getById(String data) throws VirtualCurrencyException {
		for (Method e : Method.values()) {
			if (data.startsWith(e.getMethodId())) {
				return e;
			}
		}
		return null;
	}

}
