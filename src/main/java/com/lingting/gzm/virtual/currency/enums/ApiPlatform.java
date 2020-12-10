package com.lingting.gzm.virtual.currency.enums;

import com.lingting.gzm.virtual.currency.properties.TronscanProperties;
import lombok.AllArgsConstructor;

/**
 * api 接口 平台
 *
 * @author lingting 2020-09-01 17:20
 */
@AllArgsConstructor
public enum ApiPlatform {

	/**
	 * 以太坊 https://infura.io/ 请使用
	 * {@link com.lingting.gzm.virtual.currency.properties.InfuraProperties} 类进行配置
	 */
	INFURA(VcPlatform.ETHERSCAN),
	/**
	 * 比特 https://omniexplorer.info/ 请使用
	 * {@link com.lingting.gzm.virtual.currency.properties.OmniProperties} 类进行配置
	 */
	OMNI(VcPlatform.ETHERSCAN),
	/**
	 * 波场 https://tronscan.org/ 请使用 {@link TronscanProperties} 类进行配置
	 */
	TRONSCAN(VcPlatform.TRONSCAN),;

	/**
	 * 协议
	 */
	private final VcPlatform vcPlatform;

}
