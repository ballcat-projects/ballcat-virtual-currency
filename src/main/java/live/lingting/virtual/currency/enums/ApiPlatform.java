package live.lingting.virtual.currency.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import live.lingting.virtual.currency.properties.InfuraProperties;
import live.lingting.virtual.currency.properties.OmniProperties;
import live.lingting.virtual.currency.properties.TronscanProperties;

/**
 * api 接口 平台
 *
 * @author lingting 2020-09-01 17:20
 */
@AllArgsConstructor
public enum ApiPlatform {

	/**
	 * 以太坊 https://infura.io/ 请使用 {@link InfuraProperties} 类进行配置
	 */
	INFURA(VcPlatform.ETHERSCAN),
	/**
	 * 比特 https://omniexplorer.info/ 请使用 {@link OmniProperties} 类进行配置
	 */
	OMNI(VcPlatform.BITCOIN),
	/**
	 * 波场 https://tronscan.org/ 请使用 {@link TronscanProperties} 类进行配置
	 */
	TRONSCAN(VcPlatform.TRONSCAN),;

	/**
	 * 协议
	 */
	private final VcPlatform vcPlatform;

	@JsonCreator
	public static ApiPlatform of(String str) {
		ApiPlatform[] enums = ApiPlatform.values();
		for (ApiPlatform e : enums) {
			if (e.toString().equals(str)) {
				return e;
			}
		}
		return null;
	}

}
