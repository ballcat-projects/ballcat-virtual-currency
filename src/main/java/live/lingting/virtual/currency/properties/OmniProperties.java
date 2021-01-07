package live.lingting.virtual.currency.properties;

import java.util.Map;
import java.util.function.Supplier;
import lombok.Data;
import lombok.experimental.Accessors;
import live.lingting.virtual.currency.endpoints.Endpoints;

/**
 * omni平台配置
 *
 * @author lingting 2020-09-01 16:53
 */
@Data
@Accessors(chain = true)
public class OmniProperties implements PlatformProperties {

	/**
	 * 节点
	 */
	private Endpoints endpoints;

	/**
	 * 获取锁, 成功则允许发送请求, 手动实现限制, 限制请求在 5-10s 一次
	 */
	private Supplier<Boolean> lock = () -> true;

	/**
	 * 释放锁, 请求完成后执行, 返回值无效, 返回false也不会继续尝试释放锁
	 */
	private Supplier<Boolean> unlock = () -> true;

	private Integer confirmationsMin = 6;

	/**
	 * 自定义url, 可为空
	 */
	private String url;

	/**
	 * rpc 请求时的请求头
	 */
	private Map<String, String> headers;

}
