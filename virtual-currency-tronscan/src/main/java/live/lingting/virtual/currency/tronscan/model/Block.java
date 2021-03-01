package live.lingting.virtual.currency.tronscan.model;

import cn.hutool.http.HttpRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigInteger;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import live.lingting.virtual.currency.core.Endpoints;
import live.lingting.virtual.currency.core.util.JacksonUtils;

/**
 * @author lingting 2021/2/26 18:06
 */
@NoArgsConstructor
@Data
public class Block {

	/**
	 * 当前的固化块
	 * @author lingting 2021-02-26 18:12
	 */
	@SneakyThrows
	public Block solidityNow(Endpoints endpoints) {
		HttpRequest request = HttpRequest.get(endpoints.getHttpUrl("walletsolidity/getnowblock"));
		String res = request.execute().body();
		return JacksonUtils.toObj(res, Block.class);
	}

	@JsonProperty("blockID")
	private String blockId;

	@JsonProperty("block_header")
	private BlockHeader blockHeader;

	private List<Transaction> transactions;

	@NoArgsConstructor
	@Data
	public static class BlockHeader {

		@JsonProperty("raw_data")
		private RawData rawData;

		@JsonProperty("witness_signature")
		private String witnessSignature;

		@NoArgsConstructor
		@Data
		public static class RawData {

			@JsonProperty("number")
			private BigInteger number;

			@JsonProperty("txTrieRoot")
			private String txTrieRoot;

			@JsonProperty("witness_address")
			private String witnessAddress;

			@JsonProperty("parentHash")
			private String parentHash;

			@JsonProperty("version")
			private BigInteger version;

			@JsonProperty("timestamp")
			private Long timestamp;

		}

	}

}
