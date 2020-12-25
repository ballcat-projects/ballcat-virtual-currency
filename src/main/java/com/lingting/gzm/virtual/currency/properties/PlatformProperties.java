package com.lingting.gzm.virtual.currency.properties;

/**
 * 平台配置需实现此类
 *
 * @author lingting 2020-09-01 17:21
 */
public interface PlatformProperties {

	/**
	 * 表示交易被确认的最小确认数
	 * @return java.lang.Integer
	 * @author lingting 2020-12-25 14:18
	 */
	Integer getConfirmationsMin();

}
