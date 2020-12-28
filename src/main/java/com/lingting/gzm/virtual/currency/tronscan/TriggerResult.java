package com.lingting.gzm.virtual.currency.tronscan;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 触发合约返回
 *
 * @author lingting 2020/12/25 19:20
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class TriggerResult {

	private String response;

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
