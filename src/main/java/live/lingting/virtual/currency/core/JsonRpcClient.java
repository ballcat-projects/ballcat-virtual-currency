package live.lingting.virtual.currency.core;

import com.fasterxml.jackson.core.type.TypeReference;
import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * @author lingting 2021/1/5 19:50
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonRpcClient {

	private JsonRpcHttpClient client;

	public static JsonRpcClient of(JsonRpcHttpClient client) {
		return new JsonRpcClient(client);
	}

	public static JsonRpcClient of(String url) throws MalformedURLException {
		return of(new JsonRpcHttpClient(new URL(url)));
	}

	public static JsonRpcClient of(String url, Map<String, String> headers) throws MalformedURLException {
		return of(new JsonRpcHttpClient(new URL(url), headers));
	}

	public Object invokeObj(String method, Object... args) throws Throwable {
		return client.invoke(method, args, Object.class);
	}

	public <T> T invoke(String method, Class<T> t, Object... args) throws Throwable {
		return client.invoke(method, args, t);
	}
}
