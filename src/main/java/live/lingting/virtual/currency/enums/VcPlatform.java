package live.lingting.virtual.currency.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;

/**
 * 虚拟货币平台
 *
 * @author lingting 2020-09-02 11:44
 */
@AllArgsConstructor
public enum VcPlatform {

	/**
	 * 以太坊 https://etherscan.io/
	 */
	ETHERSCAN,
	/**
	 * 比特
	 */
	OMNI,
	/**
	 * 波场
	 */
	TRONSCAN,

	;

	@JsonCreator
	public static VcPlatform of(String str) {
		VcPlatform[] enums = VcPlatform.values();
		for (VcPlatform e : enums) {
			if (e.toString().equals(str)) {
				return e;
			}
		}
		return null;
	}

}
