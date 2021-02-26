package live.lingting.virtual.currency.etherscan.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import live.lingting.virtual.currency.core.JsonRpcClient;

/**
 * 拥有块中的所有交易详细数据
 *
 * @author lingting 2021/1/5 20:03
 */
@NoArgsConstructor
@Getter
@Setter
public class BlockAndTransaction extends BaseResponse {

	@JsonProperty("difficulty")
	private String difficulty;

	@JsonProperty("extraData")
	private String extraData;

	@JsonProperty("gasLimit")
	private String gasLimit;

	@JsonProperty("gasUsed")
	private String gasUsed;

	@JsonProperty("hash")
	private String hash;

	@JsonProperty("logsBloom")
	private String logsBloom;

	@JsonProperty("miner")
	private String miner;

	@JsonProperty("mixHash")
	private String mixHash;

	@JsonProperty("nonce")
	private String nonce;

	@JsonProperty("number")
	private String number;

	@JsonProperty("parentHash")
	private String parentHash;

	@JsonProperty("receiptsRoot")
	private String receiptsRoot;

	@JsonProperty("sha3Uncles")
	private String sha3Uncles;

	@JsonProperty("size")
	private String size;

	@JsonProperty("stateRoot")
	private String stateRoot;

	@JsonProperty("timestamp")
	private String timestamp;

	@JsonProperty("totalDifficulty")
	private String totalDifficulty;

	@JsonProperty("transactionsRoot")
	private String transactionsRoot;

	@JsonProperty("transactions")
	private List<TransactionByHash> transactions;

	@JsonProperty("uncles")
	private List<Object> uncles;

	public static BlockAndTransaction of(JsonRpcClient client, String hash) throws Throwable {
		return client.invoke("eth_getBlockByHash", BlockAndTransaction.class, hash, true);
	}

}
