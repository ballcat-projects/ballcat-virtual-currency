package com.lingting.gzm.virtual.currency.omni;

import cn.hutool.http.HttpRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lingting.gzm.virtual.currency.endpoints.Endpoints;
import com.lingting.gzm.virtual.currency.util.JsonUtil;
import java.math.BigInteger;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lingting 2020-12-14 11:23
 */
@NoArgsConstructor
@Data
public class TokenHistory implements Domain<TokenHistory> {

	@Override
	public TokenHistory of(HttpRequest request, Endpoints endpoints, Object params) {
		return JsonUtil.toObj(
				request.setUrl(endpoints.getHttpUrl("v1/properties/gethistory/" + params)).execute().body(),
				TokenHistory.class);
	}

	@JsonProperty("pages")
	private Integer pages;

	@JsonProperty("total")
	private Integer total;

	@JsonProperty("transactions")
	private List<Transaction> transactions;

	@NoArgsConstructor
	@Data
	public static class Transaction {

		@JsonProperty("amount")
		private String amount;

		@JsonProperty("block")
		private BigInteger block;

		@JsonProperty("blockhash")
		private String blockHash;

		@JsonProperty("blocktime")
		private Long blockTime;

		@JsonProperty("category")
		private String category;

		@JsonProperty("confirmations")
		private Long confirmations;

		@JsonProperty("data")
		private String data;

		@JsonProperty("divisible")
		private Boolean divisible;

		@JsonProperty("ecosystem")
		private String ecosystem;

		@JsonProperty("fee")
		private String fee;

		@JsonProperty("ismine")
		private Boolean isMine;

		@JsonProperty("positioninblock")
		private BigInteger positioninBlock;

		@JsonProperty("propertyid")
		private Integer propertyId;

		@JsonProperty("propertyname")
		private String propertyName;

		@JsonProperty("propertytype")
		private String propertyType;

		@JsonProperty("sendingaddress")
		private String sendingAddress;

		@JsonProperty("subcategory")
		private String subcategory;

		@JsonProperty("txid")
		private String txid;

		@JsonProperty("type")
		private String type;

		@JsonProperty("type_int")
		private Integer typeInt;

		@JsonProperty("url")
		private String url;

		@JsonProperty("valid")
		private Boolean valid;

		@JsonProperty("version")
		private Integer version;

		@JsonProperty("referenceaddress")
		private String referenceAddress;

	}

}
