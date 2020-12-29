package com.lingting.gzm.virtual.currency;

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
public enum AbiMethod {

	/**
	 * 普通转账 transfer(address _to,uint256 _value)
	 */
	TRANSFER("a9059cbb"),

	/**
	 * 获取合约精度 decimals()
	 */
	DECIMALS("313ce567"),

	/**
	 * SEND_MULTI_SIG_TOKEN
	 */
	SEND_MULTI_SIG_TOKEN("0dcd7a6c"),

	/**
	 * SEND_MULTI_SIG
	 */
	SEND_MULTI_SIG("39125215"),
	/**
	 * 转账 transferFrom(address _from, address _to, uint256 _value)
	 */
	TRANSFER_FROM("23b872dd"),

	;

	/**
	 * 方法id
	 */
	private final String methodId;

	public static AbiMethod getById(String data) throws VirtualCurrencyException {
		for (AbiMethod e : AbiMethod.values()) {
			if (data.startsWith(e.getMethodId())) {
				return e;
			}
		}
		return null;
	}

}
