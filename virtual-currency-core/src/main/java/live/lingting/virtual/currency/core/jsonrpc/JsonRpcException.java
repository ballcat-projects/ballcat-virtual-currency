package live.lingting.virtual.currency.core.jsonrpc;

import lombok.Getter;
import lombok.Setter;
import live.lingting.virtual.currency.core.jsonrpc.http.model.AbstractJsonRpc;

/**
 * json rpc 请求处理异常
 *
 * @author lingting 2021/4/1 10:10
 */
@Getter
@Setter
public class JsonRpcException extends Exception {

	private final Long code;

	private final String message;

	public JsonRpcException(Long code, String message) {
		super(message);
		this.code = code;
		this.message = message;
	}

	public JsonRpcException(Long code, String message, Throwable cause) {
		super(message, cause);
		this.code = code;
		this.message = message;
	}

	public JsonRpcException(AbstractJsonRpc.Error error) {
		this(error.getCode(), error.getMessage());
	}

}
