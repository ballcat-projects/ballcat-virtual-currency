package com.lingting.gzm.virtual.currency.contract;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 波场 合约地址
 *
 * @author lingting 2020-09-02 13:40
 */
@Getter
@AllArgsConstructor
public enum TronscanContract implements Contract {

	/**
	 * trx
	 */
	TRX("_", 6),
	/**
	 * USDT
	 */
	USDT("TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t", 6),
	/**
	 * JUST
	 */
	JUST("TCFLL5dx5ZJdKnWuesXxi1VPwjLVmWZZy9", 18),
	/**
	 * PEER
	 */
	PEER("1003406", 6),
	/**
	 * PW
	 */
	PW("1002263", 0),

	/**
	 * 测试网用, 测完删除
	 */
	USDJ("TLBaRhANQoJFTqre9Nf1mjuwNWjCJeYqUL", 18),

	/**
	 * 测试网用, 测完删除
	 */
	TRZ("1000016", 6),

	;

	/**
	 * 比特合约地址, 仅提供正式服
	 */
	private final String hash;

	/**
	 * 精度
	 */
	private final Integer decimals;

	/**
	 * 通过合约hash获取属于哪个 比特合约
	 *
	 * @author lingting 2020-09-02 13:44
	 */
	public static TronscanContract getByHash(String hash) {
		if (StrUtil.isBlank(hash)) {
			return null;
		}
		for (TronscanContract c : values()) {
			if (c.hash.equalsIgnoreCase(hash)) {
				return c;
			}
		}
		return null;
	}

}
