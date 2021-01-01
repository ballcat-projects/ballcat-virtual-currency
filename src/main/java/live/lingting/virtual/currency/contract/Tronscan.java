package live.lingting.virtual.currency.contract;

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
public enum Tronscan implements Contract {

	/**
	 * trx
	 */
	TRX("_"),
	/**
	 * USDT
	 */
	USDT("TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t"),
	/**
	 * JUST
	 */
	JUST("TCFLL5dx5ZJdKnWuesXxi1VPwjLVmWZZy9"),
	/**
	 * PEER
	 */
	PEER("1003406"),
	/**
	 * PW
	 */
	PW("1002263"),

	;

	/**
	 * 比特合约地址, 仅提供正式服
	 */
	private final String hash;

	/**
	 * 通过合约hash获取属于哪个 比特合约
	 *
	 * @author lingting 2020-09-02 13:44
	 */
	public static Tronscan getByHash(String hash) {
		if (StrUtil.isEmpty(hash)) {
			return TRX;
		}
		for (Tronscan c : values()) {
			if (c.hash.equalsIgnoreCase(hash)) {
				return c;
			}
		}
		return null;
	}

}
