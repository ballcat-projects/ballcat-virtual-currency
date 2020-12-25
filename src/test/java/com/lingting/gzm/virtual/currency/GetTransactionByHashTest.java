package com.lingting.gzm.virtual.currency;

import com.lingting.gzm.virtual.currency.endpoints.InfuraEndpoints;
import com.lingting.gzm.virtual.currency.endpoints.OmniEndpoints;
import com.lingting.gzm.virtual.currency.endpoints.TronscanEndpoints;
import com.lingting.gzm.virtual.currency.properties.InfuraProperties;
import com.lingting.gzm.virtual.currency.properties.OmniProperties;
import com.lingting.gzm.virtual.currency.properties.TronscanProperties;
import com.lingting.gzm.virtual.currency.service.VirtualCurrencyService;
import com.lingting.gzm.virtual.currency.service.impl.InfuraServiceImpl;
import com.lingting.gzm.virtual.currency.service.impl.OmniServiceImpl;
import com.lingting.gzm.virtual.currency.service.impl.TronscanServiceImpl;
import java.util.Optional;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.web3j.utils.Numeric;

/**
 * @author lingting 2020-09-01 19:56
 */
@Slf4j
public class GetTransactionByHashTest {

	private VirtualCurrencyService service;

	@Test
	@SneakyThrows
	public void ethTest() {
		service = new InfuraServiceImpl(new InfuraProperties().setEndpoints(InfuraEndpoints.MAINNET)
				.setProjectId("b6066b4cfce54e7384ea38d52f9260ac"));
		String txnHash = "0xcce82c876641d6cab25f71c9f2287ef662e47ba8a5bbfc3d15b9e0054db4af9c";

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
	@SneakyThrows
	public void btcTest() {
		service = new OmniServiceImpl(new OmniProperties().setEndpoints(OmniEndpoints.MAINNET));
		Optional<VirtualCurrencyTransaction> optional = service
				.getTransactionByHash("e13e475a613d96f655e6f3c08d5a51a55a5f9d7287e34c60c45c892be39b19b7");

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
	@SneakyThrows
	public void tronscanTest() {
		service = new TronscanServiceImpl(new TronscanProperties().setEndpoints(TronscanEndpoints.MAINNET));
		Optional<VirtualCurrencyTransaction> optional = service
				// 这笔交易贼奇怪.
				// .getTransactionByHash("99a1e33afa08194951275d028aeb0af9a019f4f4ed7da5bb52285135fc4bea2f");
				//.getTransactionByHash("e7226047b86d35a6cd3d530864deecb295011a786060a9269b21498069eb27f1");
				//.getTransactionByHash("15dbacb7e0323b7fd07c4b794835947a807845c3d1eca3fc54770ff4e57b0f76");
				.getTransactionByHash("7e9d94fbff84e8ed097a95e8619aab0cb12ad3bc7388dc1fe6002e552015e069");

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
