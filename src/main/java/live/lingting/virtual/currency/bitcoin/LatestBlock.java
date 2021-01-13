package live.lingting.virtual.currency.bitcoin;

import cn.hutool.http.HttpRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.math.BigInteger;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import live.lingting.virtual.currency.endpoints.Endpoints;
import live.lingting.virtual.currency.util.JsonUtil;

/**
 * @author lingting 2021/1/10 19:21
 */
@NoArgsConstructor
@Data
public class LatestBlock {

	public static LatestBlock of(Endpoints endpoints) throws JsonProcessingException {
		HttpRequest request = HttpRequest.get(endpoints.getHttpUrl("latestblock"));
		return JsonUtil.toObj(request.execute().body(), LatestBlock.class);
	}

	@JsonProperty("hash")
	private String hash;

	@JsonProperty("time")
	private Long time;

	@JsonProperty("block_index")
	private BigInteger blockIndex;

	@JsonProperty("height")
	private BigInteger height;

	@JsonProperty("txIndexes")
	private List<Object> txIndexes;

}
