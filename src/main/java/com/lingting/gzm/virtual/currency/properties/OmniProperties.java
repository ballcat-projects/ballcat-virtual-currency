package com.lingting.gzm.virtual.currency.properties;

import com.lingting.gzm.virtual.currency.endpoints.Endpoints;
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
	 * 拼接 根据hash查询交易信息的url
	 *
	 * @author lingting 2020-09-02 17:11
	 */
	public String getTransactionUrlByHash(String hash) {
		return endpoints.getHttpUrl("v1/transaction/tx/") + hash;
	}

}
