package live.lingting.virtual.currency.core.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;

/**
 * 虚拟货币平台
 *
 * @author lingting 2020-09-02 11:44
 */
@AllArgsConstructor
public enum VirtualCurrencyPlatform {

	/**
	 * 以太坊 https://etherscan.io/
	 */
	ETHERSCAN,
	/**
	 * 比特
	 */
	BITCOIN,
	/**
	 * 波场
	 */
	TRONSCAN,

	;

	@JsonCreator
	public static VirtualCurrencyPlatform of(String str) {
		VirtualCurrencyPlatform[] enums = VirtualCurrencyPlatform.values();
		for (VirtualCurrencyPlatform e : enums) {
			if (e.toString().equals(str)) {
				return e;
			}
		}
		return null;
	}

}
