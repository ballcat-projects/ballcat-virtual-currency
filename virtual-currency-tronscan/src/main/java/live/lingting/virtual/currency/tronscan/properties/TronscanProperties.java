package live.lingting.virtual.currency.tronscan.properties;

import java.util.Map;
import lombok.Data;
import lombok.experimental.Accessors;
import live.lingting.virtual.currency.core.Endpoints;

/**
 * tronscan 平台配置
 *
 * @author lingting 2020-09-01 16:53
 */
@Data
@Accessors(chain = true)
public class TronscanProperties {

	/**
	 * 节点
	 */
	private Endpoints endpoints;

	/**
	 * 表示交易被确认的最小确认数
	 */
	private Integer confirmationsMin = 19;

	/**
	 * 自定义url, 可为空
	 */
	private String url;

	/**
	 * rpc 请求时的请求头
	 */
	private Map<String, String> headers;

}
