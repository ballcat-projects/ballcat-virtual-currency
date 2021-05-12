package live.lingting.virtual.currency.bitcoin.endpoints;

import lombok.AllArgsConstructor;
import lombok.Getter;
import live.lingting.virtual.currency.core.Endpoints;

/**
 * https://www.blockcypher.com/dev/bitcoin/
 * @author lingting 2021/1/19 16:48
 */
@Getter
@AllArgsConstructor
public enum BitcoinCypherEndpoints implements Endpoints {

	/**
	 * MAINNET
	 */
	MAINNET("https://api.blockcypher.com/v1/btc/main", "主节点"),

	/**
	 * Test
	 */
	TEST("https://api.blockcypher.com/v1/btc/test3", "测试节点"),

	;

	private final String http;

	private final String desc;

}
