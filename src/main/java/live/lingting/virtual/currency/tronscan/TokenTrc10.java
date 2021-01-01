package live.lingting.virtual.currency.tronscan;

import cn.hutool.http.HttpRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import live.lingting.virtual.currency.endpoints.Endpoints;
import live.lingting.virtual.currency.util.JsonUtil;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lingting 2020-12-13 15:14
 */
@NoArgsConstructor
@Data
public class TokenTrc10 {

	public static TokenTrc10 of(HttpRequest request, Endpoints endpoints, String hash) throws JsonProcessingException {
		return JsonUtil.toObj(request.setUrl(endpoints.getHttpUrl("token?showAll=1&id=" + hash)).execute().body(),
				TokenTrc10.class);
	}

	@JsonProperty("total")
	private BigInteger total;

	@JsonProperty("contractMap")
	private Map<String, Boolean> contractMap;

	@JsonProperty("totalAll")
	private BigInteger totalAll;

	@JsonProperty("data")
	private List<TokenData> data;

	@NoArgsConstructor
	@Data
	public static class TokenData {

		@JsonProperty("country")
		private String country;

		@JsonProperty("tokenID")
		private Long tokenId;

		@JsonProperty("participated")
		private Long participated;

		@JsonProperty("precision")
		private Integer precision;

		@JsonProperty("num")
		private BigInteger num;

		@JsonProperty("available")
		private Long available;

		@JsonProperty("reputation")
		private String reputation;

		@JsonProperty("description")
		private String description;

		@JsonProperty("issuedPercentage")
		private BigDecimal issuedPercentage;

		@JsonProperty("nrOfTokenHolders")
		private Long nrOfTokenHolders;

		@JsonProperty("voteScore")
		private Long voteScore;

		@JsonProperty("dateCreated")
		private Long dateCreated;

		@JsonProperty("price")
		private Long price;

		@JsonProperty("percentage")
		private Long percentage;

		@JsonProperty("startTime")
		private Long startTime;

		@JsonProperty("id")
		private Long id;

		@JsonProperty("issued")
		private BigDecimal issued;

		@JsonProperty("trxNum")
		private Long trxNum;

		@JsonProperty("abbr")
		private String abbr;

		@JsonProperty("email")
		private String email;

		@JsonProperty("website")
		private String website;

		@JsonProperty("github")
		private String github;

		@JsonProperty("level")
		private String level;

		@JsonProperty("availableSupply")
		private Long availableSupply;

		@JsonProperty("totalSupply")
		private Long totalSupply;

		@JsonProperty("frozenTotal")
		private Long frozenTotal;

		@JsonProperty("canShow")
		private String canShow;

		@JsonProperty("remaining")
		private BigDecimal remaining;

		@JsonProperty("url")
		private String url;

		@JsonProperty("frozenPercentage")
		private Long frozenPercentage;

		@JsonProperty("imgUrl")
		private String imgUrl;

		@JsonProperty("isBlack")
		private Boolean isBlack;

		@JsonProperty("new_social_media")
		private String newSocialMedia;

		@JsonProperty("remainingPercentage")
		private Long remainingPercentage;

		@JsonProperty("name")
		private String name;

		@JsonProperty("ownerAddress")
		private String ownerAddress;

		@JsonProperty("endTime")
		private Long endTime;

		@JsonProperty("white_paper")
		private String whitePaper;

		@JsonProperty("rank_order")
		private Long rankOrder;

		@JsonProperty("vip")
		private Boolean vip;

		@JsonProperty("totalTransactions")
		private Long totalTransactions;

		@JsonProperty("index")
		private Long index;

		@JsonProperty("frozen")
		private List<?> frozen;

		@JsonProperty("frozen_supply")
		private List<?> frozenSupply;

		@JsonProperty("social_media")
		private List<SocialMediaDTO> socialMedia;

		@NoArgsConstructor
		@Data
		public static class SocialMediaDTO {

			/**
			 * name : Weibo url :
			 */

			@JsonProperty("name")
			private String name;

			@JsonProperty("url")
			private String url;

		}

	}

}
