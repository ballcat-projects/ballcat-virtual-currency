package com.lingting.gzm.virtual.currency.tronscan;

import cn.hutool.http.HttpRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.lingting.gzm.virtual.currency.endpoints.Endpoints;
import com.lingting.gzm.virtual.currency.util.JsonUtil;
import java.math.BigInteger;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lingting 2020/12/25 17:35
 */
@NoArgsConstructor
@Data
public class TransactionInfo {

	public static TransactionInfo of(HttpRequest request, Endpoints endpoints, String address)
			throws JsonProcessingException {
		request.setUrl(endpoints.getHttpUrl("wallet/gettransactioninfobyid"));
		request.body("{\"value\":\"" + address + "\",\"visible\":true}");
		return JsonUtil.toObj(request.execute().body(), TransactionInfo.class);
	}

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
