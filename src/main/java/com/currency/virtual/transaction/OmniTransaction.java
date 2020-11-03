package com.currency.virtual.transaction;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigInteger;

/**
 * omni平台交易信息
 *
 * @author lingting 2020-09-02 17:16
 */
@Getter
@Setter
@Accessors(chain = true)
public class OmniTransaction {

	/**
	 * 交易金额
	 */
	@JsonProperty("amount")
	String amount;

	@JsonProperty("block")
	BigInteger block;

	@JsonProperty("blockhash")
	String blockHash;

	@JsonProperty("blocktime")
	Long blockTime;

	/**
	 * 确认人数
	 */
	@JsonProperty("confirmations")
	Long confirmations;

	@JsonProperty("divisible")
	Boolean divisible;

	@JsonProperty("fee")
	String fee;

	@JsonProperty("flags")
	Object flags;

	@JsonProperty("ismine")
	Boolean isMine;

	@JsonProperty("positioninblock")
	Long positionInBlock;

	@JsonProperty("propertyid")
	Integer propertyId;

	@JsonProperty("propertyname")
	String propertyName;

	/**
	 * 接收方
	 */
	@JsonProperty("referenceaddress")
	String to;

	/**
	 * 转账方
	 */
	@JsonProperty("sendingaddress")
	String from;

	/**
	 * 订单hash
	 */
	@JsonProperty("txid")
	String hash;

	@JsonProperty("type")
	String type;

	@JsonProperty("type_int")
	Long typeInt;

	@JsonProperty("valid")
	Boolean valid;

	@JsonProperty("version")
	Long version;

}
