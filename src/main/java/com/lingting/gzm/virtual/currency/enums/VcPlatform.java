package com.lingting.gzm.virtual.currency.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 虚拟货币平台
 *
 * @author lingting 2020-09-02 11:44
 */
@Getter
@AllArgsConstructor
public enum VcPlatform {

	/**
	 * 以太坊 https://etherscan.io/
	 */
	ETHERSCAN("0x"),
	/**
	 * 比特
	 */
	OMNI("未知"),
	/**
	 * 波场
	 */
	TRONSCAN("T"),;

	/**
	 * 地址，交易号等前缀
	 */
	private final String start;

}
