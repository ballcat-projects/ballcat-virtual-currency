package com.lingting.gzm.virtual.currency;

import com.lingting.gzm.virtual.currency.endpoints.InfuraEndpoints;
import com.lingting.gzm.virtual.currency.enums.ApiPlatform;
import com.lingting.gzm.virtual.currency.properties.InfuraProperties;
import com.lingting.gzm.virtual.currency.properties.VirtualCurrencyProperties;
import com.lingting.gzm.virtual.currency.service.VirtualCurrencyService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * 归集测试
 *
 * @author lingting 2020-12-06 16:47
 */
@Slf4j
public class VcCollectionTest {

	private VirtualCurrencyService service;

	@SneakyThrows
	@Test
	public void ethTest() {
		service = VirtualCurrencyFactory.getVirtualCurrencyService(
				new VirtualCurrencyProperties().setApiPlatform(ApiPlatform.INFURA), new InfuraProperties()
						.setEndpoints(InfuraEndpoints.MAINNET).setProjectId("b6066b4cfce54e7384ea38d52f9260ac"));

	}

}
