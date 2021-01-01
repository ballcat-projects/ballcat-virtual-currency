package live.lingting.virtual.currency.properties;

import live.lingting.virtual.currency.endpoints.Endpoints;
import java.util.function.Supplier;
import lombok.Data;
import lombok.experimental.Accessors;

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
	 * 多少确认数以上才认为交易成功
	 */
	private Integer confirmationsMin = 6;

	/**
	 * 获取锁, 成功则允许发送请求, 手动实现限制, 限制请求在 5-10s 一次
	 */
	private Supplier<Boolean> lock = () -> true;

	/**
	 * 释放锁, 请求完成后执行, 返回值无效, 返回false也不会继续尝试释放锁
	 */
	private Supplier<Boolean> unlock = () -> true;

	/**
	 * 拼接 根据hash查询交易信息的url
	 *
	 * @author lingting 2020-09-02 17:11
	 */
	public String getTransactionUrlByHash(String hash) {
		return endpoints.getHttpUrl("v1/transaction/tx/") + hash;
	}

}
