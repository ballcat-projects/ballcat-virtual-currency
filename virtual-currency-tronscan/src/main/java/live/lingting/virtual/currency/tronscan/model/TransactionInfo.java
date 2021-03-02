package live.lingting.virtual.currency.tronscan.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigInteger;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import live.lingting.virtual.currency.tronscan.properties.TronscanProperties;
import live.lingting.virtual.currency.tronscan.util.TronscanModelUtils;

/**
 * @author lingting 2020/12/25 17:35
 */
@NoArgsConstructor
@Data
public class TransactionInfo {

	@JsonProperty("id")
	private String id;

	@JsonProperty("fee")
	private BigInteger fee;

	@JsonProperty("blockNumber")
	private BigInteger blockNumber;

	@JsonProperty("blockTimeStamp")
	private Long blockTimeStamp;

	@JsonProperty("contract_address")
	private String contractAddress;

	@JsonProperty("receipt")
	private Receipt receipt;

	@JsonProperty("contractResult")
	private List<String> contractResult;

	@JsonProperty("log")
	private List<Log> log;

	public static TransactionInfo of(TronscanProperties properties, String address) {
		return TronscanModelUtils.post(properties, "wallet/gettransactioninfobyid",
				"{\"value\":\"" + address + "\",\"visible\":true}", TransactionInfo.class);
	}

	@NoArgsConstructor
	@Data
	public static class Receipt {

		@JsonProperty("energy_fee")
		private BigInteger energyFee;

		@JsonProperty("energy_usage_total")
		private BigInteger energyUsageTotal;

		@JsonProperty("net_usage")
		private BigInteger netUsage;

		@JsonProperty("result")
		private String result;

	}

	@NoArgsConstructor
	@Data
	public static class Log {

		@JsonProperty("address")
		private String address;

		@JsonProperty("data")
		private String data;

		@JsonProperty("topics")
		private List<String> topics;

	}

}
