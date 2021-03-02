package live.lingting.virtual.currency.tronscan.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.math.BigInteger;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import live.lingting.virtual.currency.tronscan.properties.TronscanProperties;
import live.lingting.virtual.currency.tronscan.util.TronscanModelUtils;

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

	public static Transaction of(TronscanProperties properties, String address) {
		// 固化块api, 仅能查询到已确认交易
		return TronscanModelUtils.post(properties, "walletsolidity/gettransactionbyid",
				"{\"value\":\"" + address + "\",\"visible\":true}", Transaction.class);
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

		@Accessors(chain = true)
		@NoArgsConstructor
		@Data
		public static class Contract {

			@JsonProperty("parameter")
			private Parameter parameter;

			@JsonProperty("type")
			private String type;

			@Accessors(chain = true)
			@NoArgsConstructor
			@Data
			public static class Parameter {

				@JsonProperty("value")
				private Value value;

				@JsonProperty("type_url")
				private String typeUrl;

				@Accessors(chain = true)
				@NoArgsConstructor
				@Data
				public static class Value {

					private String data;

					@JsonProperty("contract_address")
					private String contractAddress;

					@JsonProperty("amount")
					private BigInteger amount;

					@JsonProperty("call_value")
					private BigInteger callValue;

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
