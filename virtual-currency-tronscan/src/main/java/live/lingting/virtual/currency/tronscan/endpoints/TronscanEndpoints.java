package live.lingting.virtual.currency.tronscan.endpoints;

import lombok.AllArgsConstructor;
import lombok.Getter;
import live.lingting.virtual.currency.core.Endpoints;

/**
 * tronscan 节点, 文档 https://cn.developers.tron.network/
 *
 * @author lingting 2020-09-02 17:04
 */
@Getter
@AllArgsConstructor
public enum TronscanEndpoints implements Endpoints {

	/**
	 * 主节点 https://tronscan.org/#/
	 */
	MAINNET("https://api.trongrid.io/", "主节点"),
	/**
	 * 测试节点 https://nile.tronscan.org/#/
	 */
	NILE("https://nile.trongrid.io/", "nile测试节点"),
	/**
	 * 测试节点 https://shasta.tronscan.org/#/
	 */
	SHASTA("https://api.shasta.trongrid.io/", "Shasta测试节点"),

	;

	private final String http;

	private final String desc;

}
