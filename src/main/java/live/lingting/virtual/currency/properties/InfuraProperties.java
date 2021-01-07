package live.lingting.virtual.currency.properties;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import live.lingting.virtual.currency.core.JsonRpcClient;
import live.lingting.virtual.currency.endpoints.Endpoints;

/**
 * infura平台配置
 *
 * @author lingting 2020-09-01 16:53
 */
@Data
@Accessors(chain = true)
public class InfuraProperties implements PlatformProperties {

	/**
	 * PROJECT ID
	 */
	private String projectId;

	/**
	 * PROJECT SECRET 非必须
	 */
	private String projectSecret;

	/**
	 * 节点
	 */
	private Endpoints endpoints;

	private Integer confirmationsMin = 0;

	/**
	 * 自定义url, 可为空
	 */
	private String url;

	@SneakyThrows
	public JsonRpcClient getHttpClient() {
		// 是否自定义 web3j url
		if (StrUtil.isNotBlank(url)) {
			return JsonRpcClient.of(url);
		}
		return JsonRpcClient.of(getEndpoints().getHttpUrl(getProjectId()));
	}

}
