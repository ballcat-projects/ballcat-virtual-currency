package live.lingting.virtual.currency.bitcoin.endpoints;

import lombok.AllArgsConstructor;
import lombok.Getter;
import live.lingting.virtual.currency.core.Endpoints;

/**
 * Omni节点
 *
 * @author lingting 2020-09-02 17:04
 */
@Getter
@AllArgsConstructor
public enum OmniEndpoints implements Endpoints {

	/**
	 * 主节点 <a href="https://api.omniexplorer.info/"/>
	 */
	MAINNET("https://api.omniexplorer.info/", "主节点"),

	;

	private final String http;

	private final String desc;

}
