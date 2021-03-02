package live.lingting.virtual.currency.bitcoin.model.blockchain;

import cn.hutool.http.HttpRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.math.BigInteger;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import live.lingting.virtual.currency.core.Endpoints;
import live.lingting.virtual.currency.core.util.JacksonUtils;

/**
 * @author lingting 2021/1/10 17:51
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class RawTransaction {

	private String response;

	private String reason;

	private String code;

	private String uuid;

	@JsonProperty("ver")
	private Integer ver;

	@JsonProperty("weight")
	private BigInteger weight;

	@JsonProperty("block_height")
	private BigInteger blockHeight;

	@JsonProperty("relayed_by")
	private String relayedBy;

	@JsonProperty("lock_time")
	private Long lockTime;

	@JsonProperty("size")
	private BigInteger size;

	@JsonProperty("block_index")
	private BigInteger blockIndex;

	@JsonProperty("time")
	private Long time;

	@JsonProperty("tx_index")
	private Long txIndex;

	@JsonProperty("vin_sz")
	private Integer vinSz;

	@JsonProperty("hash")
	private String hash;

	@JsonProperty("vout_sz")
	private Integer voutSz;

	@JsonProperty("inputs")
	private List<In> ins;

	@JsonProperty("out")
	private List<Out> outs;

	public static RawTransaction of(Endpoints endpoints, String txId) throws JsonProcessingException {
		HttpRequest request = HttpRequest.get(endpoints.getHttpUrl("rawtx/" + txId));
		String response = request.execute().body();
		return JacksonUtils.toObj(response, RawTransaction.class).setResponse(response);
	}

	@NoArgsConstructor
	@Data
	public static class In {

		@JsonProperty("sequence")
		private BigInteger sequence;

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

		@JsonProperty("tx_index")
		private Long txIndex;

		@JsonProperty("type")
		private Long type;

		@JsonProperty("addr")
		private String addr;

		@JsonProperty("value")
		private BigInteger value;

		@JsonProperty("n")
		private Integer n;

		@JsonProperty("script")
		private String script;

		@JsonProperty("spending_outpoints")
		private List<SpendingOutpoint> spendingOutpoints;

		@NoArgsConstructor
		@Data
		public static class SpendingOutpoint {

			@JsonProperty("tx_index")
			private Long txIndex;

			@JsonProperty("n")
			private Integer n;

		}

	}

}
