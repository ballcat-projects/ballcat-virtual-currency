package com.lingting.gzm.virtual.currency.enums;

import lombok.AllArgsConstructor;

/**
 * 虚拟货币协议
 *
 * @author lingting 2020-09-02 11:44
 */
@AllArgsConstructor
public enum Protocol {

	/**
	 * 以太坊 https://etherscan.io/
	 */
	ETHERSCAN("0x"),
	/**
	 * 比特
	 */
	BTC("未知"),
	/**
	 * 波场
	 */
	TRONSCAN("未知"),;

	/**
	 * 地址，交易号等前缀
	 */
	private final String start;

}
