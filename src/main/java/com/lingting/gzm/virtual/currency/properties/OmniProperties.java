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
	 * 延迟时间, 单位: 分钟 如果订单创建时间与当前时间差小于设定时间,则交易状态为等待
	 */
	private long delay = 0;

	/**
	 * 拼接 根据hash查询交易信息的url
	 *
	 * @author lingting 2020-09-02 17:11
	 */
	public String getTransactionUrlByHash(String hash) {
		return endpoints.getHttpUrl("v1/transaction/tx/") + hash;
	}

}
