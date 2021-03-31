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
public class JsonRpcResponse extends AbstractJsonRpc {

	private Object result;

}
