package live.lingting.virtual.currency.bitcoin.model.omni;

import com.fasterxml.jackson.core.JsonProcessingException;
import live.lingting.virtual.currency.core.Endpoints;

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
	 * @return live.lingting.virtual.currency.bitcoin.model.omni.Balances
	 * @author lingting 2020-12-14 16:40
	 */
	T of(Endpoints endpoints, Object params) throws JsonProcessingException;

}
