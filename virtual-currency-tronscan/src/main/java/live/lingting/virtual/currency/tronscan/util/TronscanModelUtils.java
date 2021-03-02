package live.lingting.virtual.currency.tronscan.util;

import java.lang.reflect.Type;
import lombok.SneakyThrows;
import live.lingting.virtual.currency.core.enums.HttpMethod;
import live.lingting.virtual.currency.core.model.HttpHeader;
import live.lingting.virtual.currency.core.util.HttpUtils;
import live.lingting.virtual.currency.core.util.JacksonUtils;
import live.lingting.virtual.currency.tronscan.constant.TronConstants;
import live.lingting.virtual.currency.tronscan.properties.TronscanProperties;

/**
 * @author lingting 2021/3/2 16:04
 */
public class TronscanModelUtils {

	@SneakyThrows
	public static <T> T get(TronscanProperties properties, String suffix, Class<T> t) {
		return request(HttpMethod.GET, properties, suffix, "", t);
	}

	@SneakyThrows
	public static <T> T post(TronscanProperties properties, String suffix, String body, Class<T> t) {
		return request(HttpMethod.POST, properties, suffix, body, t);
	}

	public static HttpHeader getHeader(TronscanProperties properties) {
		HttpHeader header = new HttpHeader();
		header.setName(TronConstants.API_KEY_HEADER);
		header.setValue(properties.getApiKey());
		return header;
	}

	@SneakyThrows
	public static <T> T request(HttpMethod method, TronscanProperties properties, String suffix, String body,
			Class<T> t) {
		String s;
		if (HttpMethod.GET == method) {
			s = HttpUtils.get(properties.getEndpoints(), suffix, getHeader(properties));
		}
		else {
			s = HttpUtils.post(properties.getEndpoints(), suffix, body, getHeader(properties));
		}
		return JacksonUtils.toObj(s, (Type) t);
	}

}
