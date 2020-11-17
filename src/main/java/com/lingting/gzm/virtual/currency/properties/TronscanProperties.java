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
public class TronscanProperties implements PlatformProperties {

	/**
	 * 节点
	 */
	private Endpoints endpoints;

	/**
	 * 拼接 根据hash查询交易信息的url
	 *
	 * @author lingting 2020-09-02 17:11
	 */
	public String getTransactionUrlByHash(String hash) {
		return endpoints.getHttpUrl("transaction-info?hash=") + hash;
	}

}
