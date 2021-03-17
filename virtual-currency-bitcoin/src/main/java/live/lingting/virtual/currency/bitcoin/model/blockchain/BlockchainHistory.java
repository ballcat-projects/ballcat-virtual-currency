package live.lingting.virtual.currency.bitcoin.model.blockchain;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.math.BigInteger;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import live.lingting.virtual.currency.bitcoin.endpoints.BlockchainEndpoints;
import live.lingting.virtual.currency.core.util.HttpUtils;
import live.lingting.virtual.currency.core.util.JacksonUtils;

/**
 * @author lingting 2021/3/15 11:14
 */
@NoArgsConstructor
@Data
public class BlockchainHistory {

	public static BlockchainHistory of(BlockchainEndpoints endpoints, String address, int index, int size)
			throws JsonProcessingException {
		String url = endpoints
				.getHttpUrl(StrUtil.format("rawaddr/{}?limit={}&offset={}", address, size, size * (index - 1)));

		String body = HttpUtils.get(url);

		return JacksonUtils.toObj(body, BlockchainHistory.class);
	}

	@JsonProperty("hash160")
	private String hash160;

	@JsonProperty("address")
	private String address;

	@JsonProperty("n_tx")
	private Integer nTx;

	@JsonProperty("total_received")
	private BigInteger totalReceived;

	@JsonProperty("total_sent")
	private BigInteger totalSent;

	@JsonProperty("final_balance")
	private BigInteger finalBalance;

	@JsonProperty("txs")
	private List<Txs> txs;

	@NoArgsConstructor
	@Data
	public static class Txs {

		@JsonProperty("ver")
		private Integer ver;

		@JsonProperty("inputs")
		private List<Input> inputs;

		@JsonProperty("weight")
		private BigInteger weight;

		@JsonProperty("block_height")
		private BigInteger blockHeight;

		@JsonProperty("relayed_by")
		private String relayedBy;

		@JsonProperty("out")
		private List<Out> out;

		@JsonProperty("lock_time")
		private Long lockTime;

		@JsonProperty("result")
		private Long result;

		@JsonProperty("size")
		private Long size;

		@JsonProperty("block_index")
		private BigInteger blockIndex;

		@JsonProperty("time")
		private Long time;

		@JsonProperty("tx_index")
		private Integer txIndex;

		@JsonProperty("vin_sz")
		private Integer vinSz;

		@JsonProperty("hash")
		private String hash;

		@JsonProperty("vout_sz")
		private Integer voutSz;

		@NoArgsConstructor
		@Data
		public static class Input {

			@JsonProperty("sequence")
			private Long sequence;

			@JsonProperty("witness")
			private String witness;

			@JsonProperty("prev_out")
			private Out prevOut;

			@JsonProperty("script")
			private String script;

		}

		@NoArgsConstructor
		@Data
		public static class Out {

			@JsonProperty("spent")
			private Boolean spent;

			@JsonProperty("spending_outpoints")
			private List<SpendingOutpoints> spendingOutpoints;

			@JsonProperty("tx_index")
			private Integer txIndex;

			@JsonProperty("type")
			private Integer type;

			@JsonProperty("addr")
			private String addr;

			@JsonProperty("value")
			private BigInteger value;

			@JsonProperty("n")
			private Integer n;

			@JsonProperty("script")
			private String script;

			@NoArgsConstructor
			@Data
			public static class SpendingOutpoints {

				@JsonProperty("tx_index")
				private Integer txIndex;

				@JsonProperty("n")
				private Integer n;

			}

		}

	}

}
