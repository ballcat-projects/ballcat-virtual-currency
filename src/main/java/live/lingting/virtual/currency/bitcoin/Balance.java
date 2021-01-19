package live.lingting.virtual.currency.bitcoin;

import cn.hutool.http.HttpRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.math.BigInteger;
import lombok.Data;
import lombok.NoArgsConstructor;
import live.lingting.virtual.currency.endpoints.Endpoints;
import live.lingting.virtual.currency.util.JsonUtil;

/**
 * @author lingting 2021/1/14 15:16
 */
@NoArgsConstructor
@Data
public class Balance {

	public static Balance of(Endpoints endpoints, String address) throws JsonProcessingException {
		HttpRequest request = HttpRequest.get(endpoints.getHttpUrl("rawaddr/" + address + "?limit=0"));
		String response = request.execute().body();
		return JsonUtil.toObj(response, Balance.class);
	}

	@JsonProperty("hash160")
	private String hash160;

	@JsonProperty("address")
	private String address;

	@JsonProperty("n_tx")
	private BigInteger nTx;

	@JsonProperty("total_received")
	private BigInteger totalReceived;

	@JsonProperty("total_sent")
	private BigInteger totalSent;

	@JsonProperty("final_balance")
	private BigInteger finalBalance;

}
