package com.currency.virtual.enums;

import com.currency.virtual.properties.TronscanProperties;
import lombok.AllArgsConstructor;

/**
 * 平台
 *
 * @author lingting 2020-09-01 17:20
 */
@AllArgsConstructor
public enum Platform {

	/**
	 * 以太坊 https://infura.io/ 请使用 {@link com.currency.virtual.properties.InfuraProperties}
	 * 类进行配置
	 */
	INFURA(Protocol.ETHERSCAN),
	/**
	 * 比特 https://omniexplorer.info/ 请使用
	 * {@link com.currency.virtual.properties.OmniProperties} 类进行配置
	 */
	OMNI(Protocol.ETHERSCAN),
	/**
	 * 波场 https://tronscan.org/ 请使用 {@link TronscanProperties} 类进行配置
	 */
	TRONSCAN(Protocol.TRONSCAN),;

	/**
	 * 协议
	 */
	private final Protocol protocol;

}
