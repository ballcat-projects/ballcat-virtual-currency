package com.lingting.gzm.virtual.currency.tronscan;

import cn.hutool.http.HttpRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.lingting.gzm.virtual.currency.endpoints.Endpoints;
import com.lingting.gzm.virtual.currency.util.JsonUtil;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 获取合约信息返回值
 *
 * @author lingting 2020-12-11 20:55
 */
@NoArgsConstructor
@Data
public class Contract {

	public static Contract of(HttpRequest request, Endpoints endpoints, String hash) throws JsonProcessingException {
		return JsonUtil.toObj(request.setUrl(endpoints.getHttpUrl("contract?contract=" + hash)).execute().body(),
				Contract.class);
	}

	@JsonProperty("status")
	private Status status;

	@JsonProperty("data")
	private List<Data> data;

	@NoArgsConstructor
	@lombok.Data
	public static class Status {

		@JsonProperty("code")
		private Integer code;

		@JsonProperty("message")
		private String message;

	}

	@NoArgsConstructor
	@lombok.Data
	public static class Data {

		@JsonProperty("balance")
		private BigDecimal balance;

		@JsonProperty("verify_status")
		private Integer verifyStatus;

		@JsonProperty("balanceInUsd")
		private BigDecimal balanceInUsd;

		@JsonProperty("trxCount")
		private Integer trxCount;

		@JsonProperty("date_created")
		private String dateCreated;

		@JsonProperty("call_value")
		private BigDecimal callValue;

		@JsonProperty("call_token_value")
		private BigDecimal callTokenValue;

		@JsonProperty("call_token_id")
		private String callTokenId;

		@JsonProperty("call_token_info")
		private Data.CallTokenInfo callTokenInfo;

		@JsonProperty("creator")
		private Data.Creator creator;

		@JsonProperty("tokenInfo")
		private Data.TokenInfo tokenInfo;

		@NoArgsConstructor
		@lombok.Data
		public static class CallTokenInfo {

			/**
			 * tokenInfo :
			 * {"tokenId":"_","tokenAbbr":"trx","tokenName":"trx","tokenDecimal":6,"tokenCanShow":1,
			 * "tokenType":"trc10","tokenLogo":"https://coin.top/production/logo/trx.png","vip":false}
			 */

			@JsonProperty("tokenInfo")
			private Data.CallTokenInfo.TokenInfo tokenInfo;

			@NoArgsConstructor
			@lombok.Data
			public static class TokenInfo {

				@JsonProperty("tokenId")
				private String tokenId;

				@JsonProperty("tokenAbbr")
				private String tokenAbbr;

				@JsonProperty("tokenName")
				private String tokenName;

				@JsonProperty("tokenDecimal")
				private Integer tokenDecimal;

				@JsonProperty("tokenCanShow")
				private Integer tokenCanShow;

				@JsonProperty("tokenType")
				private String tokenType;

				@JsonProperty("tokenLogo")
				private String tokenLogo;

				@JsonProperty("vip")
				private Boolean vip;

			}

		}

		@NoArgsConstructor
		@lombok.Data
		public static class Creator {

			@JsonProperty("address_is_contract")
			private Boolean addressIsContract;

			@JsonProperty("token_balance")
			private BigDecimal tokenBalance;

			@JsonProperty("consume_user_resource_percent")
			private Integer consumeUserResourcePercent;

		}

		@NoArgsConstructor
		@lombok.Data
		public static class TokenInfo {

		}

	}

}
