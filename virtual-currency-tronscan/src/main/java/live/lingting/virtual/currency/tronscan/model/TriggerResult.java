package live.lingting.virtual.currency.tronscan.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 触发合约返回
 *
 * @author lingting 2020/12/25 19:20
 */
@Data
public class TriggerResult {

	private String response;

	@Getter
	@Setter
	public static class TriggerConstantResult extends TriggerResult {

		private String code;

		@JsonProperty("result")
		private Result result;

		@JsonProperty("transaction")
		private Transaction transaction;

		@JsonProperty("constant_result")
		private List<String> constantResult;

		@NoArgsConstructor
		@Data
		public static class Result {

			@JsonProperty("result")
			private Boolean result;

		}

	}

	@Getter
	@Setter
	public static class Trc20TransferGenerateResult extends TriggerResult {

		private String code;

		@JsonProperty("txid")
		private String txId;

		private String message;

		@JsonProperty("result")
		private Result result;

		@JsonProperty("transaction")
		private Transaction transaction;

		@JsonProperty("constant_result")
		private List<String> constantResult;

		@NoArgsConstructor
		@Data
		public static class Result {

			@JsonProperty("result")
			private Boolean result;

		}

	}

	@Getter
	@Setter
	public static class TransferBroadcastResult extends TriggerResult {

		private String code;

		@JsonProperty("txid")
		private String txId;

		private String message;

		@JsonProperty("result")
		private Boolean result;

	}

	@Getter
	@Setter
	public static class Trc10TransferGenerateResult extends TriggerResult {

		@JsonProperty("Error")
		private String error;

		@JsonProperty("txID")
		private String txId;

		@JsonProperty("raw_data")
		private Transaction.RawData rawData;

		@JsonProperty("raw_data_hex")
		private String rawDataHex;

		private Boolean visible;

	}

	@Getter
	@Setter
	public static class TrxTransferGenerateResult extends TriggerResult {

		@JsonProperty("Error")
		private String error;

		@JsonProperty("txID")
		private String txId;

		@JsonProperty("raw_data")
		private Transaction.RawData rawData;

		@JsonProperty("raw_data_hex")
		private String rawDataHex;

		private Boolean visible;

	}

}
