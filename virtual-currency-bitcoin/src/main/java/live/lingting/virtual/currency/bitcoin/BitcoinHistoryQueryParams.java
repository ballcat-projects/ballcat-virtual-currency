package live.lingting.virtual.currency.bitcoin;

import lombok.Data;
import lombok.experimental.Accessors;
import live.lingting.virtual.currency.core.Contract;

/**
 * @author lingting 2021/3/15 11:15
 */
@Data
@Accessors(chain = true)
public class BitcoinHistoryQueryParams {

	/**
	 * 是否只查询omni上的交易. 优先级低于 contract.
	 *
	 * 即如果 contract 不为 null, 则本值即便为 false, 也会当成 true 处理
	 */
	private boolean onlyOmni = false;

	/**
	 * 查询的地址
	 */
	private String address;

	/**
	 * 第几页数据
	 */
	private int pageIndex = 1;

	/**
	 * 每页多少条数据
	 *
	 * - onlyOmni = true 时, 本值固定为10, 修改无效
	 *
	 * - 本值最大值为50
	 */
	private int pageSize = 10;

}
