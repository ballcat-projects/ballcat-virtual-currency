package com.lingting.gzm.virtual.currency.tronscan;

import cn.hutool.http.HttpRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.lingting.gzm.virtual.currency.contract.Contract;
import com.lingting.gzm.virtual.currency.endpoints.Endpoints;
import com.lingting.gzm.virtual.currency.util.JsonUtil;
import java.math.BigDecimal;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 使用请参考 <a href=
 * "https://cn.developers.tron.network/reference#%E8%A7%A6%E5%8F%91%E6%99%BA%E8%83%BD%E5%90%88%E7%BA%A6">文档</a>
 *
 * @author lingting 2020/12/25 20:56
 */
@Data
@Accessors(chain = true)
public class TriggerRequest {

	public static TriggerRequest decimals(Endpoints endpoints, Contract contract) {
		return new TriggerRequest()
				// url
				.setUrl(endpoints.getHttpUrl("wallet/triggerconstantcontract"))
				// owner_address
				.setOwnerAddress("T9yD14Nj9j7xAB4dbGeiX9h8unkKHxuWwb")
				// contract_address
				.setContractAddress(contract.getHash())
				// function_selector
				.setFunctionSelector("decimals()");
	}

	/**
	 * 请求的url
	 */
	private String url;

	@JsonProperty("owner_address")
	private String ownerAddress;

	@JsonProperty("contract_address")
	private String contractAddress;

	@JsonProperty("function_selector")
	private String functionSelector;

	private String parameter;

	/**
	 * 最大消耗的 TRX 数量, 以 SUN 为单位
	 */
	@JsonProperty("fee_limit")
	private BigDecimal feeLimit;

	@JsonProperty("call_value")
	private BigDecimal callValue;

	@JsonProperty("permission_id")
	private BigDecimal permissionId;

	/**
	 * 账户地址是否为 Base58check 格式, 为 false, 使用 HEX 地址
	 */
	private Boolean visible = true;

	/**
	 * 执行请求
	 * @author lingting 2020-12-25 21:04
	 */
	public TriggerResult exec() throws JsonProcessingException {
		return JsonUtil.toObj(HttpRequest.post(url).body(JsonUtil.toJson(this)).execute().body(), TriggerResult.class);
	}

}
