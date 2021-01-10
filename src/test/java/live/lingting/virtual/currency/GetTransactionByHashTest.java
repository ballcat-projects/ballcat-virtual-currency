package live.lingting.virtual.currency;

import java.util.Optional;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import live.lingting.virtual.currency.endpoints.InfuraEndpoints;
import live.lingting.virtual.currency.endpoints.OmniEndpoints;
import live.lingting.virtual.currency.endpoints.TronscanEndpoints;
import live.lingting.virtual.currency.properties.InfuraProperties;
import live.lingting.virtual.currency.properties.OmniProperties;
import live.lingting.virtual.currency.properties.TronscanProperties;
import live.lingting.virtual.currency.service.PlatformService;
import live.lingting.virtual.currency.service.impl.InfuraServiceImpl;
import live.lingting.virtual.currency.service.impl.OmniHttpServiceImpl;
import live.lingting.virtual.currency.service.impl.TronscanServiceImpl;

/**
 * @author lingting 2020-09-01 19:56
 */
@Slf4j
public class GetTransactionByHashTest {

	private PlatformService service;

	@Test
	@SneakyThrows
	public void ethTest() {
		service = new InfuraServiceImpl(new InfuraProperties()
				// url
				.setUrl("http://192.168.1.206:8545/")
				// 节点
				.setEndpoints(InfuraEndpoints.MAINNET)
				// project Id
				.setProjectId("b6066b4cfce54e7384ea38d52f9260ac"));
		String txnHash = "0x3bc7bdb895dac07022bd433d188314a279226810d718344e976a5ff2f40c2169";

		Optional<Transaction> optional = service.getTransactionByHash(txnHash.trim());

		System.out.println(optional.isPresent());
		if (optional.isPresent()) {
			Transaction transaction = optional.get();
			// 获取系统默认时区的交易创建时间
			System.out.println(transaction.getTime());
			// 获取utc时区的交易创建时间
			System.out.println(transaction.getTimeByUtc());
		}
	}

	@Test
	@SneakyThrows
	public void btcTest() {
		service = new OmniHttpServiceImpl(new OmniProperties().setOmniEndpoints(OmniEndpoints.MAINNET));
		Optional<Transaction> optional = service
				.getTransactionByHash("3597e8f38ed307a9bf8ff7dab4b6769e6d88067ec95f3e785e404f0b86ff7f74")
		// .getTransactionByHash("e13e475a613d96f655e6f3c08d5a51a55a5f9d7287e34c60c45c892be39b19b7")
		;

		System.out.println(optional.isPresent());
		if (optional.isPresent()) {
			Transaction transaction = optional.get();
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
		Optional<Transaction> optional = service
				// 这笔交易贼奇怪.
				// .getTransactionByHash("99a1e33afa08194951275d028aeb0af9a019f4f4ed7da5bb52285135fc4bea2f");
				// .getTransactionByHash("e7226047b86d35a6cd3d530864deecb295011a786060a9269b21498069eb27f1");
				// .getTransactionByHash("15dbacb7e0323b7fd07c4b794835947a807845c3d1eca3fc54770ff4e57b0f76");
				.getTransactionByHash("7e9d94fbff84e8ed097a95e8619aab0cb12ad3bc7388dc1fe6002e552015e069");

		System.out.println(optional.isPresent());
		if (optional.isPresent()) {
			Transaction transaction = optional.get();
			// 获取系统默认时区的交易创建时间
			System.out.println(transaction.getTime());
			// 获取utc时区的交易创建时间
			System.out.println(transaction.getTimeByUtc());
		}
	}

}
