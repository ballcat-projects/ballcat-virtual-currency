package com.lingting.gzm.virtual.currency.tronscan;

import cn.hutool.http.HttpRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.lingting.gzm.virtual.currency.endpoints.Endpoints;
import com.lingting.gzm.virtual.currency.util.JsonUtil;
import java.math.BigInteger;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lingting 2020/12/25 18:44
 */
@NoArgsConstructor
@Data
public class Trc10 {

	@JsonProperty("owner_address")
	private String ownerAddress;

	@JsonProperty("name")
	private String name;

	@JsonProperty("abbr")
	private String abbr;

	@JsonProperty("total_supply")
	private BigInteger totalSupply;

	@JsonProperty("trx_num")
	private BigInteger trxNum;

	@JsonProperty("precision")
	private Integer precision;

	@JsonProperty("num")
	private BigInteger num;

	@JsonProperty("start_time")
	private Long startTime;

	@JsonProperty("end_time")
	private Long endTime;

	@JsonProperty("description")
	private String description;

	@JsonProperty("url")
	private String url;

	@JsonProperty("id")
	private String id;

	public static Trc10 of(HttpRequest request, Endpoints endpoints, String address) throws JsonProcessingException {
		request.setUrl(endpoints.getHttpUrl("wallet/getassetissuebyid"));
		request.body("{\"value\":\"" + address + "\",\"visible\":true}");
		return JsonUtil.toObj(request.execute().body(), Trc10.class);
	}

}
