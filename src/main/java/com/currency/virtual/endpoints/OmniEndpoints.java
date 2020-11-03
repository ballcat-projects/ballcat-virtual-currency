package com.currency.virtual.endpoints;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Omni节点
 *
 * @author lingting 2020-09-02 17:04
 */
@Getter
@AllArgsConstructor
public enum OmniEndpoints implements Endpoints {

	/**
	 * 节点名
	 */
	MAINNET("https://api.omniexplorer.info/", "主节点"),;

	private final String http;

	private final String desc;

}
