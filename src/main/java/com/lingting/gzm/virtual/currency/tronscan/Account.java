package com.lingting.gzm.virtual.currency.tronscan;

import cn.hutool.http.HttpRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.lingting.gzm.virtual.currency.endpoints.Endpoints;
import com.lingting.gzm.virtual.currency.util.JsonUtil;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import lombok.NoArgsConstructor;

/**
 * @author lingting 2020-12-11 23:41
 */
@NoArgsConstructor
@lombok.Data
public class Account {

	@JsonProperty("success")
	private Boolean success;

	@JsonProperty("meta")
	private Meta meta;

	@JsonProperty("data")
	private List<Data> data;

	public static Account of(HttpRequest request, Endpoints endpoints, String address) throws JsonProcessingException {
		return JsonUtil.toObj(request.setUrl(endpoints.getHttpUrl("v1/accounts/" + address)).execute().body(),
				Account.class);
	}

	@NoArgsConstructor
	@lombok.Data
	public static class Meta {

		@JsonProperty("at")
		private Long at;

		@JsonProperty("page_size")
		private Integer pageSize;

	}

	@NoArgsConstructor
	@lombok.Data
	public static class Data {

		@JsonProperty("owner_permission")
		private OwnerPermission ownerPermission;

		@JsonProperty("account_resource")
		private AccountResource accountResource;

		@JsonProperty("address")
		private String address;

		@JsonProperty("create_time")
		private Long createTime;

		@JsonProperty("latest_consume_time")
		private Long latestConsumeTime;

		@JsonProperty("allowance")
		private BigDecimal allowance;

		@JsonProperty("latest_opration_time")
		private Long latestOprationTime;

		@JsonProperty("is_witness")
		private Boolean isWitness;

		@JsonProperty("free_net_usage")
		private BigDecimal freeNetUsage;

		@JsonProperty("balance")
		private BigDecimal balance;

		@JsonProperty("latest_consume_free_time")
		private Long latestConsumeFreeTime;

		@JsonProperty("account_name")
		private String accountName;

		@JsonProperty("latest_withdraw_time")
		private Long latestWithdrawTime;

		@JsonProperty("witness_permission")
		private WitnessPermission witnessPermission;

		@JsonProperty("active_permission")
		private List<ActivePermission> activePermission;

		@JsonProperty("free_asset_net_usageV2")
		private List<FreeAssetNetUsageV2> freeAssetNetUsageV2;

		@JsonProperty("assetV2")
		private List<AssetV2> assetV2;

		@JsonProperty("trc20")
		private List<Map<String, BigDecimal>> trc20;

		@NoArgsConstructor
		@lombok.Data
		public static class OwnerPermission {

			@JsonProperty("threshold")
			private Long threshold;

			@JsonProperty("permission_name")
			private String permissionName;

			@JsonProperty("keys")
			private List<Keys> keys;

			@NoArgsConstructor
			@lombok.Data
			public static class Keys {

				@JsonProperty("address")
				private String address;

				@JsonProperty("weight")
				private Integer weight;

			}

		}

		@NoArgsConstructor
		@lombok.Data
		public static class AccountResource {

			@JsonProperty("acquired_delegated_frozen_balance_for_energy")
			private Long acquiredDelegatedFrozenBalanceForEnergy;

		}

		@NoArgsConstructor
		@lombok.Data
		public static class WitnessPermission {

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
			@lombok.Data
			public static class Keys {

				@JsonProperty("address")
				private String address;

				@JsonProperty("weight")
				private Integer weight;

			}

		}

		@NoArgsConstructor
		@lombok.Data
		public static class ActivePermission {

			@JsonProperty("operations")
			private String operations;

			@JsonProperty("threshold")
			private Long threshold;

			@JsonProperty("id")
			private Long id;

			@JsonProperty("type")
			private String type;

			@JsonProperty("permission_name")
			private String permissionName;

			@JsonProperty("keys")
			private List<Keys> keys;

			@NoArgsConstructor
			@lombok.Data
			public static class Keys {

				@JsonProperty("address")
				private String address;

				@JsonProperty("weight")
				private Integer weight;

			}

		}

		@NoArgsConstructor
		@lombok.Data
		public static class FreeAssetNetUsageV2 {

			@JsonProperty("value")
			private BigDecimal value;

			@JsonProperty("key")
			private String key;

		}

		@NoArgsConstructor
		@lombok.Data
		public static class AssetV2 {

			@JsonProperty("value")
			private BigDecimal value;

			@JsonProperty("key")
			private String key;

		}

	}

}
