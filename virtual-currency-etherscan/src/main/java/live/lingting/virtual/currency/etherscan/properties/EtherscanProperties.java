package live.lingting.virtual.currency.etherscan.properties;

import cn.hutool.core.util.StrUtil;
import java.util.Map;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import live.lingting.virtual.currency.core.JsonRpcClient;
import live.lingting.virtual.currency.core.Endpoints;

/**
 * infura平台配置
 *
 * @author lingting 2020-09-01 16:53
 */
@Data
@Accessors(chain = true)
public class EtherscanProperties {

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

	/**
	 * 表示交易被确认的最小确认数
	 */
	private Integer confirmationsMin = 0;

	/**
	 * 自定义url, 可为空
	 */
	private String url;

	/**
	 * rpc 请求时的请求头
	 */
	private Map<String, String> headers;

	@SneakyThrows
	public JsonRpcClient getHttpClient() {
		// 是否自定义 web3j url
		if (StrUtil.isNotBlank(url)) {
			return JsonRpcClient.of(url, headers);
		}
		return JsonRpcClient.of(getEndpoints().getHttpUrl(getProjectId()));
	}

}
