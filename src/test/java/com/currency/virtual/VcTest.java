package com.currency.virtual;

import com.currency.virtual.endpoints.InfuraEndpoints;
import com.currency.virtual.endpoints.OmniEndpoints;
import com.currency.virtual.endpoints.TronscanEndpoints;
import com.currency.virtual.enums.Platform;
import com.currency.virtual.properties.InfuraProperties;
import com.currency.virtual.properties.OmniProperties;
import com.currency.virtual.properties.TronscanProperties;
import com.currency.virtual.properties.VirtualCurrencyProperties;
import com.currency.virtual.service.VirtualCurrencyService;
import com.currency.virtual.transaction.VirtualCurrencyTransaction;
import java.util.Optional;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.web3j.utils.Numeric;

/**
 * @author lingting 2020-09-01 19:56
 */
@Slf4j
public class VcTest {

	private VirtualCurrencyService service;

	@SneakyThrows
	@Test
	public void ethTest() {
		service = VirtualCurrencyFactory.getVirtualCurrencyService(
				new VirtualCurrencyProperties().setPlatform(Platform.INFURA), new InfuraProperties()
						.setEndpoints(InfuraEndpoints.MAINNET).setProjectId("b6066b4cfce54e7384ea38d52f9260ac"));
		String txnHash = "0xedce8a92d886242bb9dc73db3c6412ed8b3e9810cf247c93254352a50eaadc4a";

		Numeric.decodeQuantity(txnHash);
		Optional<VirtualCurrencyTransaction> optional = service.getTransactionByHash(txnHash.trim());

		System.out.println(optional.isPresent());
		if (optional.isPresent()) {
			VirtualCurrencyTransaction transaction = optional.get();
			// 获取系统默认时区的交易创建时间
			System.out.println(transaction.getTime());
			// 获取utc时区的交易创建时间
			System.out.println(transaction.getTimeByUtc());
		}
	}

	@Test
	public void btcTest() {
		service = VirtualCurrencyFactory.getVirtualCurrencyService(
				new VirtualCurrencyProperties().setPlatform(Platform.OMNI),
				new OmniProperties().setEndpoints(OmniEndpoints.MAINNET));
		Optional<VirtualCurrencyTransaction> optional = service
				.getTransactionByHash("91c9500c36613719604ce4d59d738ce125f6af1730569a326d1da425c43bd82e");

		System.out.println(optional.isPresent());
		if (optional.isPresent()) {
			VirtualCurrencyTransaction transaction = optional.get();
			// 获取系统默认时区的交易创建时间
			System.out.println(transaction.getTime());
			// 获取utc时区的交易创建时间
			System.out.println(transaction.getTimeByUtc());
		}
	}

	@Test
	public void tronscanTest() {
		service = VirtualCurrencyFactory.getVirtualCurrencyService(
				new VirtualCurrencyProperties().setPlatform(Platform.TRONSCAN),
				new TronscanProperties().setEndpoints(TronscanEndpoints.MAINNET));
		Optional<VirtualCurrencyTransaction> optional = service
				.getTransactionByHash("99a1e33afa08194951275d028aeb0af9a019f4f4ed7da5bb52285135fc4bea2f");

		System.out.println(optional.isPresent());
		if (optional.isPresent()) {
			VirtualCurrencyTransaction transaction = optional.get();
			// 获取系统默认时区的交易创建时间
			System.out.println(transaction.getTime());
			// 获取utc时区的交易创建时间
			System.out.println(transaction.getTimeByUtc());
		}
	}

}
