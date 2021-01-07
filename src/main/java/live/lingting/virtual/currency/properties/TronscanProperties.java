package live.lingting.virtual.currency.properties;

import lombok.Data;
import lombok.experimental.Accessors;
import live.lingting.virtual.currency.endpoints.Endpoints;

/**
 * tronscan 平台配置
 *
 * @author lingting 2020-09-01 16:53
 */
@Data
@Accessors(chain = true)
public class TronscanProperties implements PlatformProperties {

	/**
	 * 节点
	 */
	private Endpoints endpoints;

	private Integer confirmationsMin = 19;

	/**
	 * 自定义url, 可为空
	 */
	private String url;

}
