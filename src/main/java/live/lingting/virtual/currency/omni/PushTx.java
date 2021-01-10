package live.lingting.virtual.currency.omni;

import cn.hutool.http.HttpRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Data;
import live.lingting.virtual.currency.endpoints.Endpoints;
import live.lingting.virtual.currency.util.JsonUtil;

/**
 * @author lingting 2021/1/8 14:41
 */
@Data
public class PushTx {

	private Throwable e;

	@JsonProperty("status")
	private String status;

	@JsonProperty("pushed")
	private String pushed;

	@JsonProperty("tx")
	private String txId;

	public PushTx(Throwable e) {
		this.e = e;
	}

	public PushTx(String status, String pushed, String txId) {
		this.status = status;
		this.pushed = pushed;
		this.txId = txId;
	}

	public static PushTx of(Endpoints endpoints, String raw) throws JsonProcessingException {
		HttpRequest request = HttpRequest.post(endpoints.getHttpUrl("v1/transaction/pushtx/"));
		request.form("signedTransaction", raw);
		return JsonUtil.toObj(request.execute().body(), PushTx.class);
	}

	public static PushTx success(String txId) {
		return new PushTx("OK", "Success", txId);
	}

	public boolean isSuccess() {
		return "OK".equals(status) && "Success".equals(pushed);
	}

}
