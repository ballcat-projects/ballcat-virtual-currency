package com.lingting.gzm.virtual.currency.omni;

import cn.hutool.http.HttpRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.lingting.gzm.virtual.currency.endpoints.Endpoints;
import com.lingting.gzm.virtual.currency.util.JsonUtil;
import java.math.BigDecimal;
import java.math.BigInteger;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lingting 2020-12-14 10:13
 */
@NoArgsConstructor
@Data
public class TransactionByHash implements Domain<TransactionByHash> {

	@Override
	public TransactionByHash of(HttpRequest request, Endpoints endpoints, Object params) throws JsonProcessingException {
		return JsonUtil.toObj(request.setUrl(endpoints.getHttpUrl("v1/transaction/tx/" + params)).execute().body(),
				TransactionByHash.class);
	}

	@JsonProperty("amount")
	private BigDecimal amount;

	@JsonProperty("block")
	private BigInteger block;

	@JsonProperty("blockhash")
	private String blockHash;

	@JsonProperty("blocktime")
	private Long blockTime;

	@JsonProperty("confirmations")
	private Long confirmations;

	@JsonProperty("divisible")
	private Boolean divisible;

	@JsonProperty("fee")
	private String fee;

	@JsonProperty("flags")
	private Object flags;

	@JsonProperty("ismine")
	private Boolean isMine;

	@JsonProperty("positioninblock")
	private Integer positionInBlock;

	@JsonProperty("propertyid")
	private Integer propertyId;

	@JsonProperty("propertyname")
	private String propertyName;

	@JsonProperty("referenceaddress")
	private String referenceAddress;

	@JsonProperty("sendingaddress")
	private String sendingAddress;

	@JsonProperty("txid")
	private String txId;

	@JsonProperty("type")
	private String type;

	@JsonProperty("type_int")
	private Integer typeInt;

	@JsonProperty("valid")
	private Boolean valid;

	@JsonProperty("version")
	private Integer version;

}
