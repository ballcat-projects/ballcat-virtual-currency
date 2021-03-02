package live.lingting.virtual.currency.tronscan.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.math.BigInteger;
import lombok.Data;
import lombok.NoArgsConstructor;
import live.lingting.virtual.currency.tronscan.properties.TronscanProperties;
import live.lingting.virtual.currency.tronscan.util.TronscanModelUtils;

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

	public static Trc10 of(TronscanProperties properties, String address) throws JsonProcessingException {
		return TronscanModelUtils.post(properties, "wallet/getassetissuebyid",
				"{\"value\":\"" + address + "\",\"visible\":true}", Trc10.class);
	}

}
