package live.lingting.virtual.currency;

import live.lingting.virtual.currency.endpoints.InfuraEndpoints;
import live.lingting.virtual.currency.endpoints.OmniEndpoints;
import live.lingting.virtual.currency.endpoints.TronscanEndpoints;
import live.lingting.virtual.currency.properties.InfuraProperties;
import live.lingting.virtual.currency.properties.OmniProperties;
import live.lingting.virtual.currency.properties.TronscanProperties;
import live.lingting.virtual.currency.service.VirtualCurrencyService;
import live.lingting.virtual.currency.service.impl.InfuraServiceImpl;
import live.lingting.virtual.currency.service.impl.OmniServiceImpl;
import live.lingting.virtual.currency.service.impl.TronscanServiceImpl;
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
				.getTransactionByHash("e3a6e7685ec3f3b57e8fd77fadc8ac1964d5dd3bd7c75c0f7474198d45c251f3");

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
