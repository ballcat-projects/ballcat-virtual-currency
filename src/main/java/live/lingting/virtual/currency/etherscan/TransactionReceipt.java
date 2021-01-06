package live.lingting.virtual.currency.etherscan;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import live.lingting.virtual.currency.core.JsonRpcClient;

/**
 * @author lingting 2021/1/5 20:03
 */
@NoArgsConstructor
@Getter
@Setter
public class TransactionReceipt extends BaseResponse {

	@JsonProperty("blockHash")
	private String blockHash;

	@JsonProperty("blockNumber")
	private String blockNumber;

	@JsonProperty("contractAddress")
	private String contractAddress;

	@JsonProperty("cumulativeGasUsed")
	private String cumulativeGasUsed;

	@JsonProperty("from")
	private String from;

	@JsonProperty("gasUsed")
	private String gasUsed;

	@JsonProperty("logsBloom")
	private String logsBloom;

	@JsonProperty("status")
	private String status;

	@JsonProperty("to")
	private String to;

	@JsonProperty("transactionHash")
	private String transactionHash;

	@JsonProperty("transactionIndex")
	private String transactionIndex;

	@JsonProperty("logs")
	private List<Log> logs;

	public static TransactionReceipt of(JsonRpcClient client, String hash) throws Throwable {
		return client.invoke("eth_getTransactionReceipt", TransactionReceipt.class, hash);
	}

	@NoArgsConstructor
	@Data
	public static class Log {

		@JsonProperty("address")
		private String address;

		@JsonProperty("blockHash")
		private String blockHash;

		@JsonProperty("blockNumber")
		private String blockNumber;

		@JsonProperty("data")
		private String data;

		@JsonProperty("logIndex")
		private String logIndex;

		@JsonProperty("removed")
		private Boolean removed;

		@JsonProperty("transactionHash")
		private String transactionHash;

		@JsonProperty("transactionIndex")
		private String transactionIndex;

		@JsonProperty("topics")
		private List<String> topics;

	}

}
