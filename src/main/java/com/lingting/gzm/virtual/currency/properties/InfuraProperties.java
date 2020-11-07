package com.lingting.gzm.virtual.currency.properties;

import com.lingting.gzm.virtual.currency.endpoints.Endpoints;
import lombok.Data;
import lombok.experimental.Accessors;
import org.web3j.protocol.http.HttpService;

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

	/**
	 * 延迟时间, 单位: 分钟 如果订单创建时间与当前时间差小于设定时间,则交易状态为等待
	 */
	private long delay = 0;

	public HttpService getHttpService() {
		return new HttpService(getEndpoints().getHttpUrl(getProjectId()));
	}

}
