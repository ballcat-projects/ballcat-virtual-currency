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

	@Override
	public Integer getConfirmationsMin() {
		return 19;
	}

}
