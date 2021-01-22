package live.lingting.virtual.currency.omni;

import java.util.Optional;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import live.lingting.virtual.currency.Transaction;
import live.lingting.virtual.currency.endpoints.BitcoinEndpoints;
import live.lingting.virtual.currency.endpoints.OmniEndpoints;
import live.lingting.virtual.currency.properties.OmniProperties;
import live.lingting.virtual.currency.service.PlatformService;
import live.lingting.virtual.currency.service.impl.BtcOmniServiceImpl;
import live.lingting.virtual.currency.util.JsonUtil;

/**
 * @author lingting 2021/1/10 15:54
 */
@Slf4j
public class Select {

	private static PlatformService service;

	static {
		service = new BtcOmniServiceImpl(new OmniProperties().setOmniEndpoints(OmniEndpoints.MAINNET)
				.setBitcoinEndpoints(BitcoinEndpoints.MAINNET));
	}

	@Test
	@SneakyThrows
	public void btc() {
		Optional<Transaction> optional = service
				.getTransactionByHash("3130a1f6e1101deb1a5afee161eaf9ec66ee42727e706b1adab32ed68cf1ab38");

		System.out.println("交易是否存在: " + optional.isPresent());
		if (optional.isPresent()) {
			Transaction transaction = optional.get();
			System.out.println(transaction.getVcPlatform());
			System.out.println(transaction.getHash());
			System.out.println(transaction.getValue());
			System.out.println(transaction.getStatus());
			System.out.println(transaction.getTime());
		}
		optional = service.getTransactionByHash("5f20c18d94a9e511e62d6aef74ffa55d36694aabc5a67fa65cb2a161c8bc5483");

		System.out.println("交易是否存在: " + optional.isPresent());
		if (optional.isPresent()) {
			Transaction transaction = optional.get();
			System.out.println(transaction.getVcPlatform());
			System.out.println(transaction.getHash());
			System.out.println(transaction.getContract());
			System.out.println(transaction.getStatus());
			System.out.println(transaction.getBtc());
			System.out.println(JsonUtil.toJson(transaction.getBtcInfo()));
			System.out.println(transaction.getTime());
		}
	}

	@Test
	@SneakyThrows
	public void property() {
		Optional<Transaction> optional = service
				.getTransactionByHash("3130a1f6e1101deb1a5afee161eaf9ec66ee42727e706b1adab32ed68cf1ab38");

		System.out.println("交易是否存在: " + optional.isPresent());
		if (optional.isPresent()) {
			Transaction transaction = optional.get();
			System.out.println(transaction.getVcPlatform());
			System.out.println(transaction.getHash());
			System.out.println(transaction.getValue());
			System.out.println(transaction.getStatus());
			System.out.println(transaction.getTime());
		}
		optional = service.getTransactionByHash("3130a1f6e2301deb1a5afee161eaf9ec66ee42727e706b1adab32ed68cf1ab38");

		System.out.println("交易是否存在: " + optional.isPresent());
		if (optional.isPresent()) {
			Transaction transaction = optional.get();
			System.out.println(transaction.getVcPlatform());
			System.out.println(transaction.getHash());
			System.out.println(transaction.getValue());
			System.out.println(transaction.getContract());
			System.out.println(transaction.getStatus());
			System.out.println(transaction.getTime());
		}
	}

	@Test
	@SneakyThrows
	public void valid() {
		service = new BtcOmniServiceImpl(new OmniProperties().setOmniEndpoints(OmniEndpoints.MAINNET)
				.setBitcoinEndpoints(BitcoinEndpoints.TEST));
		System.out.println(service.validate("fasdgasdfasdgasdfdafgasdfsa"));
		System.out.println(service.validate("2N2vMRNmBiSJQtu3wv2onGPE2museEg9nLw"));
		System.out.println(service.validate("tb1qdhgxl3vjcg9djhd30zykmz83x63nau4rtmahmd"));
	}

}
