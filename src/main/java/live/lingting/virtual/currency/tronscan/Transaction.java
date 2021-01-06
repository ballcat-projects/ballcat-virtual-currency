package live.lingting.virtual.currency.tronscan;

import cn.hutool.http.HttpRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import live.lingting.virtual.currency.endpoints.Endpoints;
import live.lingting.virtual.currency.util.JsonUtil;

/**
 * @author lingting 2020/12/25 14:33
 */
@NoArgsConstructor
@Data
public class Transaction {

	@JsonProperty("txID")
	private String txId;

	@JsonProperty("raw_data")
	private RawData rawData;

	@JsonProperty("raw_data_hex")
	private String rawDataHex;

	private Boolean visible;

	@JsonProperty("ret")
	private List<Ret> ret;

	@JsonProperty("signature")
	private List<String> signature;

	public static Transaction of(Endpoints endpoints, String address) throws JsonProcessingException {
		// 固化块api, 仅能查询到已确认交易
		HttpRequest request = HttpRequest.post(endpoints.getHttpUrl("walletsolidity/gettransactionbyid"));
		request.body("{\"value\":\"" + address + "\",\"visible\":true}");
		return JsonUtil.toObj(request.execute().body(), Transaction.class);
	}

	@NoArgsConstructor
	@Data
	public static class RawData {

		@JsonProperty("data")
		private String data;

		@JsonProperty("ref_block_bytes")
		private String refBlockBytes;

		@JsonProperty("ref_block_hash")
		private String refBlockHash;

		@JsonProperty("expiration")
		private Long expiration;

		@JsonProperty("fee_limit")
		private BigInteger feeLimit;

		@JsonProperty("timestamp")
		private Long timestamp;

		@JsonProperty("contract")
		private List<Contract> contract;

		@NoArgsConstructor
		@Data
		public static class Contract {

			@JsonProperty("parameter")
			private Parameter parameter;

			@JsonProperty("type")
			private String type;

			@NoArgsConstructor
			@Data
			public static class Parameter {

				@JsonProperty("value")
				private Value value;

				@JsonProperty("type_url")
				private String typeUrl;

				@NoArgsConstructor
				@Data
				public static class Value {

					private String data;

					@JsonProperty("contract_address")
					private String contractAddress;

					@JsonProperty("amount")
					private BigDecimal amount;

					@JsonProperty("asset_name")
					private String assetName;

					@JsonProperty("owner_address")
					private String ownerAddress;

					@JsonProperty("to_address")
					private String toAddress;

				}

			}

		}

	}

	@NoArgsConstructor
	@Data
	public static class Ret {

		public static final String SUCCESS = "SUCCESS";

		/**
		 * 返回值如果与 {@link Ret#SUCCESS} 相同表示已成功
		 */
		@JsonProperty("contractRet")
		private String contractRet;

	}

}
