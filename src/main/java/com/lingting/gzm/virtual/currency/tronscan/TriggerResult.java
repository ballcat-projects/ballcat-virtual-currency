package com.lingting.gzm.virtual.currency.tronscan;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lingting.gzm.virtual.currency.tronscan.Transaction;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 触发合约返回
 *
 * @author lingting 2020/12/25 19:20
 */
@NoArgsConstructor
@Data
public class TriggerResult {

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
