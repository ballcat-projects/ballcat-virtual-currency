package com.lingting.gzm.virtual.currency;

import com.lingting.gzm.virtual.currency.enums.Platform;
import com.lingting.gzm.virtual.currency.properties.*;
import com.lingting.gzm.virtual.currency.service.VirtualCurrencyService;
import com.lingting.gzm.virtual.currency.service.impl.InfuraServiceImpl;
import com.lingting.gzm.virtual.currency.service.impl.OmniServiceImpl;
import com.lingting.gzm.virtual.currency.service.impl.TronscanServiceImpl;

/**
 * 生成
 *
 * @author lingting 2020-09-01 17:16
 */
public class VirtualCurrencyFactory {

	/**
	 * 生成虚拟货币处理类
	 * @param properties 虚拟货币配置
	 * @param platformProperties 平台配置
	 * @return com.lingting.gzm.virtual.currency.service.VirtualCurrencyService
	 * @author lingting 2020-09-01 17:23
	 */
	public static VirtualCurrencyService getVirtualCurrencyService(VirtualCurrencyProperties properties,
			PlatformProperties platformProperties) {
		if (properties.getPlatform() == Platform.INFURA) {
			return new InfuraServiceImpl((InfuraProperties) platformProperties);
		}
		else if (properties.getPlatform() == Platform.OMNI) {
			return new OmniServiceImpl((OmniProperties) platformProperties);
		}
		else if (properties.getPlatform() == Platform.TRONSCAN) {
			return new TronscanServiceImpl((TronscanProperties) platformProperties);
		}

		throw new RuntimeException("未找到对应处理类");
	}

}
