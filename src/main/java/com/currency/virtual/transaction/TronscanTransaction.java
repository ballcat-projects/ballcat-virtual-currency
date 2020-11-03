package com.currency.virtual.transaction;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * @author lingting 2020-09-02 18:52
 */
@Getter
@Setter
public class TronscanTransaction {

	@JsonProperty("block")
	private BigInteger block;

	@JsonProperty("hash")
	private String hash;

	@JsonProperty("timestamp")
	private long timestamp;

	@JsonProperty("ownerAddress")
	private String ownerAddress;

	@JsonProperty("signature_addresses")
	private List<String> signatureAddresses;

	@JsonProperty("contractType")
	private Long contractType;

	@JsonProperty("toAddress")
	private String toAddress;

	@JsonProperty("confirmations")
	private Long confirmations;

	@JsonProperty("confirmed")
	private boolean confirmed;

	@JsonProperty("revert")
	private boolean revert;

	@JsonProperty("contractRet")
	private String contractRet;

	@JsonProperty("contractData")
	private ContractData contractData;

	@JsonProperty("data")
	private Object data;

	@JsonProperty("cost")
	private Cost cost;

	@JsonProperty("trigger_info")
	private TriggerInfo triggerInfo;

	@JsonProperty("Integerernal_transactions")
	private Object integerernalTransactions;

	@JsonProperty("tokenTransferInfo")
	private TokenTransferInfo tokenTransferInfo;

	@JsonProperty("trc20TransferInfo")
	private List<Trc20TransferInfo> trc20TransferInfo;

	@JsonProperty("info")
	private Map<Object, Object> info;

	@JsonProperty("contract_map")
	private Map<Object, Object> contractMap;

	@Getter
	@Setter
	public static class ContractData {

		private BigInteger amount;

		@JsonProperty("asset_name")
		private String assetName;

		@JsonProperty("data")
		private String data;

		@JsonProperty("owner_address")
		private String ownerAddress;

		@JsonProperty("to_address")
		private String toAddress;

		@JsonProperty("contract_address")
		private String contractAddress;

	}

	@Getter
	@Setter
	public static class Cost {

		@JsonProperty("net_usage")
		private Long netUsage;

		@JsonProperty("net_fee")
		private Long netFee;

		@JsonProperty("energy_usage")
		private Long energyUsage;

		@JsonProperty("energy_fee")
		private long energyFee;

		@JsonProperty("origin_energy_usage")
		private Long originEnergyUsage;

		@JsonProperty("energy_usage_total")
		private Long energyUsageTotal;

	}

	@Getter
	@Setter
	public static class Parameter {

		@JsonProperty("_value")
		private String value;

		@JsonProperty("_to")
		private String to;

	}

	@Getter
	@Setter
	public static class TriggerInfo {

		@JsonProperty("method")
		private String method;

		@JsonProperty("parameter")
		private Parameter parameter;

		@JsonProperty("contract_address")
		private String contractAddress;

		@JsonProperty("call_value")
		private Long callValue;

	}

	@Getter
	@Setter
	public static class TokenTransferInfo {

		@JsonProperty("icon_url")
		private String iconUrl;

		@JsonProperty("symbol")
		private String symbol;

		@JsonProperty("decimals")
		private Long decimals;

		@JsonProperty("name")
		private String name;

		@JsonProperty("to_address")
		private String toAddress;

		@JsonProperty("contract_address")
		private String contractAddress;

		@JsonProperty("from_address")
		private String fromAddress;

		@JsonProperty("amount_str")
		private String amountStr;

	}

	@Getter
	@Setter
	public static class Trc20TransferInfo {

		@JsonProperty("icon_url")
		private String iconUrl;

		@JsonProperty("symbol")
		private String symbol;

		@JsonProperty("decimals")
		private Long decimals;

		@JsonProperty("name")
		private String name;

		@JsonProperty("to_address")
		private String toAddress;

		@JsonProperty("contract_address")
		private String contractAddress;

		@JsonProperty("from_address")
		private String fromAddress;

		@JsonProperty("amount_str")
		private String amountStr;

	}

}
