package live.lingting.virtual.currency.bitcoin;

import cn.hutool.http.HttpRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.math.BigInteger;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import live.lingting.virtual.currency.endpoints.BitcoinCypherEndpoints;
import live.lingting.virtual.currency.endpoints.Endpoints;
import live.lingting.virtual.currency.util.JsonUtil;

/**
 * @author lingting 2021/1/14 15:16
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class Balance {

	public static Balance of(Endpoints endpoints, String address) throws JsonProcessingException {
		HttpRequest request = HttpRequest
				.get(BitcoinCypherEndpoints.of(endpoints).getHttpUrl("addrs/" + address + "/balance"));
		String response = request.execute().body();
		return JsonUtil.toObj(response, Balance.class);
	}

	private String error;

	@JsonProperty("address")
	private String address;

	@JsonProperty("total_received")
	private BigInteger totalReceived;

	@JsonProperty("total_sent")
	private BigInteger totalSent;

	@JsonProperty("balance")
	private BigInteger balance;

	@JsonProperty("unconfirmed_balance")
	private BigInteger unconfirmedBalance;

	@JsonProperty("final_balance")
	private BigInteger finalBalance;

	@JsonProperty("n_tx")
	private BigInteger nTx;

	@JsonProperty("unconfirmed_n_tx")
	private BigInteger unconfirmedNumberTx;

	@JsonProperty("final_n_tx")
	private BigInteger finalNumberTx;

}
