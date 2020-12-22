package com.lingting.gzm.virtual.currency.omni;

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
 * @author lingting 2020-12-14 11:38
 */
@NoArgsConstructor
@Data
public class Balances implements Domain<Balances> {

	@Override
	public Balances of(HttpRequest request, Endpoints endpoints, Object params)throws JsonProcessingException {
		request.setUrl(endpoints.getHttpUrl("v1/address/addr/"));
		request.form("addr", params);
		String response = request.execute().body();
		return JsonUtil.toObj(response, Balances.class);
	}

	@JsonProperty("balance")
	private List<Balance> balance;

	@NoArgsConstructor
	@Data
	public static class Balance {

		@JsonProperty("divisible")
		private Boolean divisible;

		@JsonProperty("frozen")
		private String frozen;

		@JsonProperty("id")
		private String id;

		@JsonProperty("pendingneg")
		private String pendingNeg;

		@JsonProperty("pendingpos")
		private String pendingPos;

		@JsonProperty("propertyinfo")
		private PropertyInfo propertyInfo;

		@JsonProperty("reserved")
		private String reserved;

		@JsonProperty("symbol")
		private String symbol;

		@JsonProperty("value")
		private BigDecimal value;

		@JsonProperty("error")
		private Boolean error;

		@NoArgsConstructor
		@Data
		public static class PropertyInfo {

			@JsonProperty("amount")
			private String amount;

			@JsonProperty("block")
			private Integer block;

			@JsonProperty("blockhash")
			private String blockHash;

			@JsonProperty("blocktime")
			private Integer blockTime;

			@JsonProperty("category")
			private String category;

			@JsonProperty("confirmations")
			private Integer confirmations;

			@JsonProperty("creationtxid")
			private String creationTxId;

			@JsonProperty("data")
			private String data;

			@JsonProperty("divisible")
			private Boolean divisible;

			@JsonProperty("ecosystem")
			private String ecosystem;

			@JsonProperty("fee")
			private String fee;

			@JsonProperty("fixedissuance")
			private Boolean fixedIssuance;

			@JsonProperty("flags")
			private Flags flags;

			@JsonProperty("freezingenabled")
			private Boolean freeZingEnabled;

			@JsonProperty("ismine")
			private Boolean isMine;

			@JsonProperty("issuer")
			private String issuer;

			@JsonProperty("managedissuance")
			private Boolean managedIssuance;

			@JsonProperty("name")
			private String name;

			@JsonProperty("positioninblock")
			private Integer positionInBlock;

			@JsonProperty("propertyid")
			private Integer propertyId;

			@JsonProperty("propertyname")
			private String propertyName;

			@JsonProperty("propertytype")
			private String propertyType;

			@JsonProperty("rdata")
			private Object rdata;

			@JsonProperty("registered")
			private Boolean registered;

			@JsonProperty("sendingaddress")
			private String sendingAddress;

			@JsonProperty("subcategory")
			private String subcategory;

			@JsonProperty("totaltokens")
			private String totalTokens;

			@JsonProperty("txid")
			private String txId;

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

			@NoArgsConstructor
			@Data
			public static class Flags {

			}

		}

	}

}
