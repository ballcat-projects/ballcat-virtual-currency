package live.lingting.virtual.currency.bitcoin;

import cn.hutool.http.HttpRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import live.lingting.virtual.currency.endpoints.Endpoints;
import live.lingting.virtual.currency.util.JsonUtil;

/**
 * @author lingting 2021/1/7 11:19
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class UnspentRes {

	public static final String ERROR = "No free outputs to spend";

	@JsonProperty("notice")
	private String notice;

	@JsonProperty("unspent_outputs")
	private List<Unspent> unspentList;

	/**
	 * 获取指定地址未使用utxo
	 * @param endpoints 节点
	 * @param min 最小确认数
	 * @param address 地址
	 * @return live.lingting.virtual.currency.bitcoin.UnspentRes
	 * @author lingting 2021-01-08 18:40
	 */
	public static UnspentRes of(Endpoints endpoints, int min, String address) throws JsonProcessingException {
		HttpRequest request = HttpRequest
				.get(endpoints.getHttpUrl("unspent?confirmations=" + min + "&active=" + address));
		String response = request.execute().body();
		if (response.equals(ERROR)) {
			return new UnspentRes().setUnspentList(new ArrayList<>());
		}
		return JsonUtil.toObj(response, UnspentRes.class);
	}

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
		private String value;

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
