package live.lingting.virtual.currency.bitcoin.endpoints;

import lombok.AllArgsConstructor;
import lombok.Getter;
import live.lingting.virtual.currency.core.Endpoints;

/**
 *
 * @author lingting 2020-09-02 17:04
 */
@Getter
@AllArgsConstructor
public enum BitcoinSochainEndpoints implements Endpoints {

	/**
	 * 主节点 https://sochain.com/api
	 */
	MAINNET("https://sochain.com/api/", "主节点"),

	/**
	 * 测试节点 https://sochain.com/api
	 */
	TEST("https://sochain.com/api/", "测试节点"),

	;

	private final String http;

	private final String desc;

}
