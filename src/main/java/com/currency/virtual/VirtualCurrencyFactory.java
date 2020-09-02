package com.currency.virtual;

import com.currency.virtual.enums.Platform;
import com.currency.virtual.properties.InfuraProperties;
import com.currency.virtual.properties.OmniProperties;
import com.currency.virtual.properties.PlatformProperties;
import com.currency.virtual.properties.VirtualCurrencyProperties;
import com.currency.virtual.service.VirtualCurrencyService;
import com.currency.virtual.service.impl.InfuraServiceImpl;
import com.currency.virtual.service.impl.OmniServiceImpl;

/**
 * 生成
 *
 * @author lingting 2020-09-01 17:16
 */
public class VirtualCurrencyFactory {
    /**
     * 生成虚拟货币处理类
     *
     * @param properties         虚拟货币配置
     * @param platformProperties 平台配置
     * @return com.currency.virtual.service.VirtualCurrencyService
     * @author lingting 2020-09-01 17:23
     */
    public static VirtualCurrencyService getVirtualCurrencyService(VirtualCurrencyProperties properties, PlatformProperties platformProperties) {
        if (properties.getPlatform() == Platform.INFURA) {
            return new InfuraServiceImpl((InfuraProperties) platformProperties);
        } else if (properties.getPlatform() == Platform.OMNI) {
            return new OmniServiceImpl((OmniProperties) platformProperties);
        }

        throw new RuntimeException("未找到对应处理类");
    }
}
