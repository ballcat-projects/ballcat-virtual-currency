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
 * @author lingting 2020-12-11 21:47
 */
@NoArgsConstructor
@Data
public class TransactionByHash {

	public static TransactionByHash of(HttpRequest request, Endpoints endpoints, String hash)
			throws JsonProcessingException {
		String response = request.setUrl(endpoints.getHttpUrl("transaction-info?hash=") + hash).execute().body();
		// 由于查询 trx 转账时, 返回的 internal_transactions 值可能为空字符串,会导致后续转换异常, 直接处理掉
		response = response.replace("\"internal_transactions\":\"\"", "\"internal_transactions\":{}");
		return JsonUtil.toObj(response, TransactionByHash.class);
	}

	@JsonProperty("block")
	private BigInteger block;

	@JsonProperty("hash")
	private String hash;

	@JsonProperty("timestamp")
	private Long timestamp;

	@JsonProperty("ownerAddress")
	private String ownerAddress;

	@JsonProperty("contractType")
	private Integer contractType;

	@JsonProperty("toAddress")
	private String toAddress;

	@JsonProperty("confirmations")
	private Integer confirmations;

	@JsonProperty("confirmed")
	private Boolean confirmed;

	@JsonProperty("revert")
	private Boolean revert;

	@JsonProperty("contractRet")
	private String contractRet;

	@JsonProperty("contractData")
	private ContractData contractData;

	@JsonProperty("data")
	private String data;

	@JsonProperty("cost")
	private Cost cost;

	@JsonProperty("trigger_info")
	private TriggerInfo triggerInfo;

	@JsonProperty("internal_transactions")
	private Map<String, List<InternalTransaction>> internalTransactions;

	@JsonProperty("tokenTransferInfo")
	private TokenTransferInfo tokenTransferInfo;

	@JsonProperty("info")
	private Info info;

	@JsonProperty("contract_map")
	private Map<String, Boolean> contractMap;

	@JsonProperty("signature_addresses")
	private List<?> signatureAddresses;

	@JsonProperty("trc20TransferInfo")
	private List<Trc20TransferInfo> trc20TransferInfo;

	@NoArgsConstructor
	@Data
	public static class ContractData {

		@JsonProperty("amount")
		private BigDecimal amount;

		@JsonProperty("data")
		private String data;

		@JsonProperty("owner_address")
		private String ownerAddress;

		@JsonProperty("contract_address")
		private String contractAddress;

		@JsonProperty("asset_name")
		private String assetName;

		@JsonProperty("to_address")
		private String toAddress;

		@JsonProperty("tokenInfo")
		private TokenInfo tokenInfo;

		@NoArgsConstructor
		@Data
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
	@Data
	public static class Cost {

		@JsonProperty("net_fee")
		private Integer netFee;

		@JsonProperty("net_fee_cost")
		private Integer netFeeCost;

		@JsonProperty("energy_usage")
		private Integer energyUsage;

		@JsonProperty("energy_fee_cost")
		private Integer energyFeeCost;

		@JsonProperty("energy_fee")
		private Integer energyFee;

		@JsonProperty("energy_usage_total")
		private Integer energyUsageTotal;

		@JsonProperty("origin_energy_usage")
		private Integer originEnergyUsage;

		@JsonProperty("net_usage")
		private Integer netUsage;

	}

	@NoArgsConstructor
	@Data
	public static class TriggerInfo {

		@JsonProperty("method")
		private String method;

		@JsonProperty("parameter")
		private Parameter parameter;

		@JsonProperty("contract_address")
		private String contractAddress;

		@JsonProperty("call_value")
		private BigDecimal callValue;

		@NoArgsConstructor
		@Data
		public static class Parameter {

			@JsonProperty("_interestRateMode")
			private String interestRateMode;

			@JsonProperty("_referralCode")
			private String referralCode;

			@JsonProperty("_amount")
			private BigDecimal amount;

			@JsonProperty("_reserve")
			private String reserve;

			@JsonProperty("_value")
			private BigDecimal value;

			@JsonProperty("_to")
			private String to;

			private String matrix;

			private String level;

		}

	}

	@NoArgsConstructor
	@Data
	public static class InternalTransaction {

		@JsonProperty("transaction_id")
		private String transactionId;

		@JsonProperty("caller_address")
		private String callerAddress;

		@JsonProperty("note")
		private String note;

		@JsonProperty("rejected")
		private Boolean rejected;

		@JsonProperty("value_info_list")
		private String valueInfoList;

		@JsonProperty("date_created")
		private String dateCreated;

		@JsonProperty("contract")
		private String contract;

		@JsonProperty("block")
		private Integer block;

		@JsonProperty("confirmed")
		private Boolean confirmed;

		@JsonProperty("hash")
		private String hash;

		@JsonProperty("transfer_to_address")
		private String transferToAddress;

		@JsonProperty("token_list")
		private List<TokenList> tokenList;

		@NoArgsConstructor
		@Data
		public static class TokenList {

			@JsonProperty("token_id")
			private String tokenId;

			@JsonProperty("call_value")
			private BigDecimal callValue;

			@JsonProperty("tokenInfo")
			private TokenInfo tokenInfo;

			@NoArgsConstructor
			@Data
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

	}

	@NoArgsConstructor
	@Data
	public static class TokenTransferInfo {

		@JsonProperty("icon_url")
		private String iconUrl;

		@JsonProperty("symbol")
		private String symbol;

		@JsonProperty("decimals")
		private Integer decimals;

		@JsonProperty("name")
		private String name;

		@JsonProperty("to_address")
		private String toAddress;

		@JsonProperty("contract_address")
		private String contractAddress;

		@JsonProperty("type")
		private String type;

		@JsonProperty("from_address")
		private String fromAddress;

		@JsonProperty("amount_str")
		private String amountStr;

	}

	@NoArgsConstructor
	@Data
	public static class Info {

	}

	@NoArgsConstructor
	@Data
	public static class Trc20TransferInfo {

		@JsonProperty("icon_url")
		private String iconUrl;

		@JsonProperty("symbol")
		private String symbol;

		@JsonProperty("decimals")
		private Integer decimals;

		@JsonProperty("name")
		private String name;

		@JsonProperty("to_address")
		private String toAddress;

		@JsonProperty("contract_address")
		private String contractAddress;

		@JsonProperty("type")
		private String type;

		@JsonProperty("from_address")
		private String fromAddress;

		@JsonProperty("amount_str")
		private String amountStr;

	}

}
