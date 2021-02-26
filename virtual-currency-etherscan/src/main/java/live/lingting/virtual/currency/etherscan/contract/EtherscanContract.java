package live.lingting.virtual.currency.etherscan.contract;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import live.lingting.virtual.currency.core.Contract;

/**
 * 以太坊代币 合约地址
 *
 * @author lingting 2020-09-02 13:40
 */
@Getter
@AllArgsConstructor
public enum EtherscanContract implements Contract {

	/**
	 * ETH
	 */
	ETH("", 18),
	/**
	 * 代币名称
	 */
	USDT("0xdac17f958d2ee523a2206206994597c13d831ec7", 6),

	;

	/**
	 * 以太坊合约地址, 仅提供正式服
	 */
	private final String hash;

	/**
	 * 精度
	 */
	private final Integer decimals;

	/**
	 * 通过合约hash获取属于哪个 以太坊合约
	 *
	 * @author lingting 2020-09-02 13:44
	 */
	public static EtherscanContract getByHash(String hash) {
		if (StrUtil.isEmpty(hash)) {
			return null;
		}
		for (EtherscanContract c : values()) {
			if (c.hash.equalsIgnoreCase(hash)) {
				return c;
			}
		}
		return null;
	}

}
