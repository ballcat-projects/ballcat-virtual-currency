package live.lingting.virtual.currency.contract;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 比特代币 合约地址
 *
 * @author lingting 2020-09-02 13:40
 */
@Getter
@AllArgsConstructor
public enum OmniContract implements Contract {

	/**
	 * 代币名称
	 */
	USDT("31", 8),

	BTC("0", 8),

	OMNI("1", 8),

	MAID_SAFE_COIN("3", 0),

	;

	/**
	 * 比特合约地址, 仅提供正式服
	 */
	private final String hash;

	/**
	 * 合约精度
	 */
	private final Integer decimals;

	/**
	 * 通过合约hash获取属于哪个 比特合约
	 *
	 * @author lingting 2020-09-02 13:44
	 */
	public static OmniContract getByHash(String hash) {
		if (StrUtil.isEmpty(hash)) {
			return null;
		}
		for (OmniContract c : values()) {
			if (c.hash.equalsIgnoreCase(hash)) {
				return c;
			}
		}
		return null;
	}

	public static OmniContract getById(Integer id) {
		return getByHash(id.toString());
	}

}
