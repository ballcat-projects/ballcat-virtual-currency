package com.lingting.gzm.virtual.currency.tronscan;

import cn.hutool.http.HttpRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lingting.gzm.virtual.currency.endpoints.Endpoints;
import com.lingting.gzm.virtual.currency.util.JsonUtil;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lingting 2020-12-13 15:10
 */
@NoArgsConstructor
@Data
public class TokenTrc20 {

	public static TokenTrc20 of(HttpRequest request, Endpoints endpoints, String hash) {
		return JsonUtil.toObj(
				request.setUrl(endpoints.getHttpUrl("token_trc20?showAll=1&contract=" + hash)).execute().body(),
				TokenTrc20.class);
	}

	@JsonProperty("total")
	private BigInteger total;

	@JsonProperty("contractMap")
	private Map<String, Boolean> contractMap;

	@JsonProperty("rangeTotal")
	private BigInteger rangeTotal;

	@JsonProperty("trc20_tokens")
	private List<Trc20Token> trc20Tokens;

	@NoArgsConstructor
	@Data
	public static class Trc20Token {

		@JsonProperty("issue_ts")
		private Integer issueTs;

		@JsonProperty("symbol")
		private String symbol;

		@JsonProperty("contract_address")
		private String contractAddress;

		@JsonProperty("gain")
		private String gain;

		@JsonProperty("home_page")
		private String homePage;

		@JsonProperty("token_desc")
		private String tokenDesc;

		@JsonProperty("price_trx")
		private Integer priceTrx;

		@JsonProperty("git_hub")
		private String gitHub;

		@JsonProperty("price")
		private String price;

		@JsonProperty("total_supply_with_decimals")
		private String totalSupplyWithDecimals;

		@JsonProperty("vip")
		private Boolean vip;

		@JsonProperty("email")
		private String email;

		@JsonProperty("icon_url")
		private String iconUrl;

		@JsonProperty("total_supply")
		private Integer totalSupply;

		@JsonProperty("level")
		private String level;

		@JsonProperty("total_supply_str")
		private String totalSupplyStr;

		@JsonProperty("volume24h")
		private Integer volume24h;

		@JsonProperty("index")
		private Integer index;

		@JsonProperty("market_info")
		private MarketInfo marketInfo;

		@JsonProperty("volume")
		private Integer volume;

		@JsonProperty("issue_address")
		private String issueAddress;

		@JsonProperty("holders_count")
		private Integer holdersCount;

		@JsonProperty("decimals")
		private Integer decimals;

		@JsonProperty("name")
		private String name;

		@JsonProperty("issue_time")
		private String issueTime;

		@JsonProperty("white_paper")
		private String whitePaper;

		@JsonProperty("social_media")
		private String socialMedia;

		@JsonProperty("social_media_list")
		private List<SocialMediaList> socialMediaList;

		@NoArgsConstructor
		@Data
		public static class MarketInfo {

			@JsonProperty("fPrecision")
			private Integer fPrecision;

			@JsonProperty("fShortName")
			private String fShortName;

			@JsonProperty("fTokenAddr")
			private String fTokenAddr;

			@JsonProperty("gain")
			private Integer gain;

			@JsonProperty("pairId")
			private Integer pairId;

			@JsonProperty("priceInTrx")
			private Double priceInTrx;

			@JsonProperty("sPrecision")
			private Integer sPrecision;

			@JsonProperty("sShortName")
			private String sShortName;

			@JsonProperty("sTokenAddr")
			private String sTokenAddr;

			@JsonProperty("txCount24h")
			private Integer txCount24h;

			@JsonProperty("volume24hInTrx")
			private Double volume24hInTrx;

		}

		@NoArgsConstructor
		@Data
		public static class SocialMediaList {

			@JsonProperty("name")
			private String name;

			@JsonProperty("url")
			private String url;

		}

	}

}
