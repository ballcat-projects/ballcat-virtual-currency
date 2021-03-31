package live.lingting.virtual.currency.etherscan.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import live.lingting.virtual.currency.core.jsonrpc.http.HttpJsonRpc;

/**
 * @author lingting 2021/1/5 19:27
 */
@NoArgsConstructor
@Getter
@Setter
public class TransactionByHash extends BaseResponse {

	@JsonProperty("blockHash")
	private String blockHash;

	@JsonProperty("blockNumber")
	private String blockNumber;

	@JsonProperty("from")
	private String from;

	@JsonProperty("gas")
	private String gas;

	@JsonProperty("gasPrice")
	private String gasPrice;

	@JsonProperty("hash")
	private String hash;

	@JsonProperty("input")
	private String input;

	@JsonProperty("nonce")
	private String nonce;

	@JsonProperty("r")
	private String r;

	@JsonProperty("s")
	private String s;

	@JsonProperty("to")
	private String to;

	@JsonProperty("transactionIndex")
	private String transactionIndex;

	private String type;

	@JsonProperty("v")
	private String v;

	@JsonProperty("value")
	private String value;

	public static TransactionByHash of(HttpJsonRpc client, String hash) throws Throwable {
		return client.invoke("eth_getTransactionByHash", TransactionByHash.class, hash);
	}

}
