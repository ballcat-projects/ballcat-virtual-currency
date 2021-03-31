package live.lingting.virtual.currency.core.jsonrpc.http.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author lingting 2021/3/31 17:18
 */
@Getter
@Setter
@Accessors(chain = true)
public class JsonRpcRequest extends AbstractJsonRpc {

	private String method;

	private Object params;

	public static JsonRpcRequest of(String method, Object... args) {
		JsonRpcRequest param = new JsonRpcRequest();
		param.setId(generateId());

		return param.setMethod(method).setParams(args);
	}

}
