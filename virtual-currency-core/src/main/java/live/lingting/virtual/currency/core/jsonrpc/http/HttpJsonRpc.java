package live.lingting.virtual.currency.core.jsonrpc.http;

import cn.hutool.http.HttpRequest;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.SneakyThrows;
import live.lingting.virtual.currency.core.jsonrpc.JsonRpc;
import live.lingting.virtual.currency.core.jsonrpc.http.model.JsonRpcRequest;
import live.lingting.virtual.currency.core.jsonrpc.http.model.JsonRpcResponse;
import live.lingting.virtual.currency.core.util.JacksonUtils;

/**
 * @author lingting 2021/3/31 15:57
 */
@Getter
public class HttpJsonRpc implements JsonRpc {

	public static final String CONTENT_TYPE = "application/json-rpc";

	private final ObjectMapper mapper;

	private final String url;

	private final Map<String, String> headers;

	public HttpJsonRpc(ObjectMapper mapper, String url, Map<String, String> headers) {
		this.url = url;
		this.mapper = mapper;
		this.headers = headers;
	}

	public static HttpJsonRpc of(String url) {
		return of(url, Collections.emptyMap());
	}

	public static HttpJsonRpc of(String url, Map<String, String> headers) {
		ObjectMapper mapper = new ObjectMapper();
		// 如果有未知字段不报错
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		// 有特殊需要转移字符, 不报错
		mapper.enable(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature());
		return of(mapper, url, headers);
	}

	public static HttpJsonRpc of(ObjectMapper mapper, String url, Map<String, String> headers) {
		return new HttpJsonRpc(mapper, url, headers);
	}

	/**
	 * 自定义 jackson 配置
	 * @param consumer 提供当前类使用的 object mapper ,用于自定义配置
	 * @author lingting 2021-03-31 16:33
	 */
	public void config(Consumer<ObjectMapper> consumer) {
		consumer.accept(mapper);
	}

	@Override
	public <T> T invoke(String method, Class<T> t, Object... args) {
		return invoke(method, t, Collections.emptyMap(), args);
	}

	/**
	 *
	 * 执行方法
	 * @param method 方法名
	 * @param t 返回值转换成目标类
	 * @param extHeaders 额外的请求头
	 * @param args 参数
	 * @return T
	 * @author lingting 2021-03-31 16:39
	 */
	@SneakyThrows
	@SuppressWarnings("unchecked")
	public <T> T invoke(String method, Class<T> t, Map<String, String> extHeaders, Object... args) {
		HttpRequest post = HttpRequest.post(url)
				// json rpc type
				.contentType(CONTENT_TYPE)
				// 先添加默认请求头
				.addHeaders(headers)
				// 再添加额外的请求头
				.addHeaders(extHeaders)
				// 参数
				.body(JsonRpcRequest.of(method, args).toString());

		String json = post.execute().body();

		// 解析返回值
		JsonRpcResponse response = JacksonUtils.toObj(json, JsonRpcResponse.class);

		if (response.getResult() == null) {
			return null;
		}

		// 单独处理string
		if (t.isAssignableFrom(String.class)) {
			return (T) response.getResult();
		}

		return JacksonUtils.toObj(JacksonUtils.toJson(response.getResult()), t);
	}

}
