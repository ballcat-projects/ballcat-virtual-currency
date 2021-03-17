package live.lingting.virtual.currency.bitcoin.model.omni;

import cn.hutool.http.HttpRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;
import live.lingting.virtual.currency.core.Endpoints;
import live.lingting.virtual.currency.core.util.JacksonUtils;

/**
 * @author lingting 2021/3/16 16:04
 */
@NoArgsConstructor
@Data
public class AddressHistory implements Domain<AddressHistory> {

	@JsonProperty("address")
	private String address;

	@JsonProperty("current_page")
	private Integer currentPage;

	@JsonProperty("pages")
	private Integer pages;

	@JsonProperty("txcount")
	private Integer txCount;

	private List<TransactionByHash> transactions;

	@Override
	public AddressHistory of(Endpoints endpoints, Object params) throws JsonProcessingException {
		HttpRequest post = HttpRequest.post(endpoints.getHttpUrl("v1/transaction/address"));
		post.form((Map<String, Object>) params);
		String body = post.execute().body();
		return JacksonUtils.toObj(body, AddressHistory.class);
	}

}
