package live.lingting.virtual.currency.core.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import live.lingting.virtual.currency.core.Endpoints;
import live.lingting.virtual.currency.core.enums.HttpMethod;
import live.lingting.virtual.currency.core.model.HttpHeader;

/**
 * @author lingting 2021/3/2 15:44
 */
public class HttpUtils {

	/**
	 * get
	 * @author lingting 2021-03-02 15:56
	 */
	public static String get(Endpoints endpoints, String suffix, HttpHeader... headers) {
		return get(endpoints.getHttpUrl(suffix), headers);
	}

	public static String get(String url, HttpHeader... headers) {
		return request(url, StrUtil.EMPTY, HttpMethod.GET, headers);
	}

	/**
	 * post
	 * @author lingting 2021-03-02 15:56
	 */
	public static String post(Endpoints endpoints, String suffix, HttpHeader... headers) {
		return post(endpoints.getHttpUrl(suffix), headers);
	}

	public static String post(Endpoints endpoints, String suffix, String body, HttpHeader... headers) {
		return post(endpoints.getHttpUrl(suffix), body, headers);
	}

	public static String post(String url, HttpHeader... headers) {
		return post(url, StrUtil.EMPTY, headers);
	}

	public static String post(String url, String body, HttpHeader... headers) {
		return request(url, body, HttpMethod.POST, headers);
	}

	public static String request(String url, String body, HttpMethod method, HttpHeader... headers) {
		HttpRequest request;

		if (method == HttpMethod.GET) {
			request = HttpRequest.get(url);
		}
		else {
			request = HttpRequest.post(url);
			// post 请求才设置 body
			if (StrUtil.isNotBlank(body)) {
				request.body(body);
			}
		}

		for (HttpHeader header : headers) {
			request.header(header.getName(), header.getValue());
		}
		return request.execute().body();
	}

}
