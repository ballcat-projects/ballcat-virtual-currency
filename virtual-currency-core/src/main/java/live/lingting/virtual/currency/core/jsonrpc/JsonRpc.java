package live.lingting.virtual.currency.core.jsonrpc;

/**
 * json rpc 顶级类
 *
 * @author lingting 2021/3/31 15:57
 */
public interface JsonRpc {

	/**
	 * 执行方法
	 * @param method 方法名
	 * @param t 返回值转换成目标类
	 * @param args 参数
	 * @return T
	 * @author lingting 2021-03-31 16:18
	 */
	<T> T invoke(String method, Class<T> t, Object... args) throws JsonRpcException;

}
