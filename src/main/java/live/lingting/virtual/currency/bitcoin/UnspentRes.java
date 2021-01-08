package live.lingting.virtual.currency.bitcoin;

import cn.hutool.http.HttpRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.math.BigInteger;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import live.lingting.virtual.currency.endpoints.Endpoints;
import live.lingting.virtual.currency.util.JsonUtil;

/**
 * @author lingting 2021/1/7 11:19
 */
@NoArgsConstructor
@Data
public class UnspentRes {

	public static UnspentRes of(Endpoints endpoints, String address) throws JsonProcessingException {
		HttpRequest request = HttpRequest.get(endpoints.getHttpUrl("unspent?confirmations=6&active=" + address));
		return JsonUtil.toObj(request.execute().body(), UnspentRes.class);
	}

	@JsonProperty("notice")
	private String notice;

	@JsonProperty("unspent_outputs")
	private List<Unspent> unspentList;

	@NoArgsConstructor
	@Data
	public static class Unspent {

		@JsonProperty("tx_hash")
		private String txHash;

		/**
		 * tx id
		 */
		@JsonProperty("tx_hash_big_endian")
		private String txHashBigEndian;

		@JsonProperty("tx_output_n")
		private Long txOutputN;

		@JsonProperty("script")
		private String script;

		/**
		 * 交易数量
		 */
		@JsonProperty("value")
		private Long value;

		@JsonProperty("value_hex")
		private String valueHex;

		@JsonProperty("confirmations")
		private BigInteger confirmations;

		@JsonProperty("tx_index")
		private BigInteger txIndex;

		public String getTxId() {
			return txHashBigEndian;
		}

	}

}
