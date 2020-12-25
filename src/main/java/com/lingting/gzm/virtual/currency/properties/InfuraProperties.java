package com.lingting.gzm.virtual.currency.properties;

import cn.hutool.core.util.StrUtil;
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
	 * 自定义url, 可为空
	 */
	private String url;

	public HttpService getHttpService() {
		// 是否自定义 web3j url
		if (StrUtil.isNotBlank(url)) {
			return new HttpService(url);
		}
		return new HttpService(getEndpoints().getHttpUrl(getProjectId()));
	}

	@Override
	public Integer getConfirmationsMin() {
		return 0;
	}

}
