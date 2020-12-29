package com.lingting.gzm.virtual.currency.omni;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lingting.gzm.virtual.currency.endpoints.Endpoints;

/**
 * 用于返回值解析后的实体类
 *
 * @author lingting 2020-12-14 16:39
 */
public interface Domain<T> {

	/**
	 * 来源
	 * @param endpoints 节点
	 * @param params 参数
	 * @return com.lingting.gzm.virtual.currency.omni.Balances
	 * @author lingting 2020-12-14 16:40
	 */
	T of(Endpoints endpoints, Object params) throws JsonProcessingException;

}
