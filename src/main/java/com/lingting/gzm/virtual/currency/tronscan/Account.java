package com.lingting.gzm.virtual.currency.tronscan;

import cn.hutool.http.HttpRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lingting.gzm.virtual.currency.endpoints.Endpoints;
import com.lingting.gzm.virtual.currency.util.JsonUtil;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lingting 2020-12-11 23:41
 */
@NoArgsConstructor
@Data
public class Account {

	public static Account of(HttpRequest request, Endpoints endpoints, String address) {
		return JsonUtil.toObj(request.setUrl(endpoints.getHttpUrl("account?address=" + address)).execute().body(),
				Account.class);
	}

	@JsonProperty("address")
	private String address;

	@JsonProperty("bandwidth")
	private Bandwidth bandwidth;

	@JsonProperty("date_created")
	private String dateCreated;

	@JsonProperty("accountType")
	private Integer accountType;

	@JsonProperty("frozen")
	private Frozen frozen;

	@JsonProperty("accountResource")
	private AccountResource accountResource;

	@JsonProperty("ownerPermission")
	private OwnerPermission ownerPermission;

	@JsonProperty("balance")
	private BigDecimal balance;

	@JsonProperty("voteTotal")
	private Integer voteTotal;

	@JsonProperty("name")
	private String name;

	@JsonProperty("delegated")
	private Delegated delegated;

	@JsonProperty("totalTransactionCount")
	private Integer totalTransactionCount;

	@JsonProperty("representative")
	private Representative representative;

	@JsonProperty("trc20token_balances")
	private List<Trc20tokenBalances> trc20tokenBalances;

	@JsonProperty("allowExchange")
	private List<?> allowExchange;

	@JsonProperty("frozen_supply")
	private List<?> frozenSupply;

	@JsonProperty("exchanges")
	private List<?> exchanges;

	@JsonProperty("tokenBalances")
	private List<TokenBalances> tokenBalances;

	@JsonProperty("balances")
	private List<Balances> balances;

	@JsonProperty("tokens")
	private List<Tokens> tokens;

	@JsonProperty("activePermissions")
	private List<ActivePermissions> activePermissions;

	@NoArgsConstructor
	@Data
	public static class Bandwidth {

		@JsonProperty("energyRemaining")
		private Integer energyRemaining;

		@JsonProperty("totalEnergyLimit")
		private Long totalEnergyLimit;

		@JsonProperty("totalEnergyWeight")
		private Long totalEnergyWeight;

		@JsonProperty("netUsed")
		private Integer netUsed;

		@JsonProperty("storageLimit")
		private Integer storageLimit;

		@JsonProperty("storagePercentage")
		private Integer storagePercentage;

		@JsonProperty("assets")
		private Map<String, Asset> assets;

		@JsonProperty("netPercentage")
		private Integer netPercentage;

		@JsonProperty("storageUsed")
		private Integer storageUsed;

		@JsonProperty("storageRemaining")
		private Integer storageRemaining;

		@JsonProperty("freeNetLimit")
		private Integer freeNetLimit;

		@JsonProperty("energyUsed")
		private Integer energyUsed;

		@JsonProperty("freeNetRemaining")
		private Integer freeNetRemaining;

		@JsonProperty("netLimit")
		private Integer netLimit;

		@JsonProperty("netRemaining")
		private Integer netRemaining;

		@JsonProperty("energyLimit")
		private Integer energyLimit;

		@JsonProperty("freeNetUsed")
		private Integer freeNetUsed;

		@JsonProperty("totalNetWeight")
		private Long totalNetWeight;

		@JsonProperty("freeNetPercentage")
		private Double freeNetPercentage;

		@JsonProperty("energyPercentage")
		private Integer energyPercentage;

		@JsonProperty("totalNetLimit")
		private Long totalNetLimit;

		@NoArgsConstructor
		@Data
		public static class Asset {

			@JsonProperty("netPercentage")
			private Integer netPercentage;

			@JsonProperty("netLimit")
			private Integer netLimit;

			@JsonProperty("netRemaining")
			private Integer netRemaining;

			@JsonProperty("netUsed")
			private Integer netUsed;

		}

	}

	@NoArgsConstructor
	@Data
	public static class Frozen {

		@JsonProperty("total")
		private Integer total;

		@JsonProperty("balances")
		private List<?> balances;

	}

	@NoArgsConstructor
	@Data
	public static class AccountResource {

		@JsonProperty("frozen_balance_for_energy")
		private FrozenBalanceForEnergy frozenBalanceForEnergy;

		@NoArgsConstructor
		@Data
		public static class FrozenBalanceForEnergy {

		}

	}

	@NoArgsConstructor
	@Data
	public static class OwnerPermission {

		/**
		 * keys : [{"address":"TBAbX6ezwTRLEoYW2DUhQQjQ2aA3KWxFkp","weight":1}] threshold
		 * : 1 permission_name : owner
		 */

		@JsonProperty("threshold")
		private Integer threshold;

		@JsonProperty("permission_name")
		private String permissionName;

		@JsonProperty("keys")
		private List<Keys> keys;

		@NoArgsConstructor
		@Data
		public static class Keys {

			/**
			 * address : TBAbX6ezwTRLEoYW2DUhQQjQ2aA3KWxFkp weight : 1
			 */

			@JsonProperty("address")
			private String address;

			@JsonProperty("weight")
			private Integer weight;

		}

	}

	@NoArgsConstructor
	@Data
	public static class Delegated {

		@JsonProperty("sentDelegatedBandwidth")
		private List<?> sentDelegatedBandwidth;

		@JsonProperty("sentDelegatedResource")
		private List<?> sentDelegatedResource;

		@JsonProperty("receivedDelegatedResource")
		private List<?> receivedDelegatedResource;

		@JsonProperty("receivedDelegatedBandwidth")
		private List<?> receivedDelegatedBandwidth;

	}

	@NoArgsConstructor
	@Data
	public static class Representative {

		@JsonProperty("lastWithDrawTime")
		private Integer lastWithDrawTime;

		@JsonProperty("allowance")
		private Integer allowance;

		@JsonProperty("enabled")
		private Boolean enabled;

		@JsonProperty("url")
		private String url;

	}

	@NoArgsConstructor
	@Data
	public static class Trc20tokenBalances {

		@JsonProperty("tokenId")
		private String tokenId;

		@JsonProperty("balance")
		private BigDecimal balance;

		@JsonProperty("tokenName")
		private String tokenName;

		@JsonProperty("tokenAbbr")
		private String tokenAbbr;

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

		@JsonProperty("tokenPriceInTrx")
		private Double tokenPriceInTrx;

		@JsonProperty("amount")
		private BigDecimal amount;

	}

	@NoArgsConstructor
	@Data
	public static class TokenBalances {

		@JsonProperty("amount")
		private BigDecimal amount;

		@JsonProperty("tokenPriceInTrx")
		private Integer tokenPriceInTrx;

		@JsonProperty("tokenId")
		private String tokenId;

		@JsonProperty("balance")
		private BigDecimal balance;

		@JsonProperty("tokenName")
		private String tokenName;

		@JsonProperty("tokenDecimal")
		private Integer tokenDecimal;

		@JsonProperty("tokenAbbr")
		private String tokenAbbr;

		@JsonProperty("tokenCanShow")
		private Integer tokenCanShow;

		@JsonProperty("tokenType")
		private String tokenType;

		@JsonProperty("vip")
		private Boolean vip;

		@JsonProperty("tokenLogo")
		private String tokenLogo;

		@JsonProperty("owner_address")
		private String ownerAddress;

	}

	@NoArgsConstructor
	@Data
	public static class Balances {

		@JsonProperty("amount")
		private BigDecimal amount;

		@JsonProperty("tokenPriceInTrx")
		private Integer tokenPriceInTrx;

		@JsonProperty("tokenId")
		private String tokenId;

		@JsonProperty("balance")
		private BigDecimal balance;

		@JsonProperty("tokenName")
		private String tokenName;

		@JsonProperty("tokenDecimal")
		private Integer tokenDecimal;

		@JsonProperty("tokenAbbr")
		private String tokenAbbr;

		@JsonProperty("tokenCanShow")
		private Integer tokenCanShow;

		@JsonProperty("tokenType")
		private String tokenType;

		@JsonProperty("vip")
		private Boolean vip;

		@JsonProperty("tokenLogo")
		private String tokenLogo;

		@JsonProperty("owner_address")
		private String ownerAddress;

	}

	@NoArgsConstructor
	@Data
	public static class Tokens {

		@JsonProperty("amount")
		private BigDecimal amount;

		@JsonProperty("tokenPriceInTrx")
		private Integer tokenPriceInTrx;

		@JsonProperty("tokenId")
		private String tokenId;

		@JsonProperty("balance")
		private BigDecimal balance;

		@JsonProperty("tokenName")
		private String tokenName;

		@JsonProperty("tokenDecimal")
		private Integer tokenDecimal;

		@JsonProperty("tokenAbbr")
		private String tokenAbbr;

		@JsonProperty("tokenCanShow")
		private Integer tokenCanShow;

		@JsonProperty("tokenType")
		private String tokenType;

		@JsonProperty("vip")
		private Boolean vip;

		@JsonProperty("tokenLogo")
		private String tokenLogo;

		@JsonProperty("owner_address")
		private String ownerAddress;

	}

	@NoArgsConstructor
	@Data
	public static class ActivePermissions {

		@JsonProperty("operations")
		private String operations;

		@JsonProperty("threshold")
		private Integer threshold;

		@JsonProperty("id")
		private Integer id;

		@JsonProperty("type")
		private String type;

		@JsonProperty("permission_name")
		private String permissionName;

		@JsonProperty("keys")
		private List<Keys> keys;

		@NoArgsConstructor
		@Data
		public static class Keys {

			/**
			 * address : TBAbX6ezwTRLEoYW2DUhQQjQ2aA3KWxFkp weight : 1
			 */

			@JsonProperty("address")
			private String address;

			@JsonProperty("weight")
			private Integer weight;

		}

	}

}
