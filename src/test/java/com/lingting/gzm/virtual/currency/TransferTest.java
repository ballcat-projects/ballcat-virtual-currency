package com.lingting.gzm.virtual.currency;

import com.lingting.gzm.virtual.currency.contract.Etherscan;
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
import java.math.BigDecimal;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * @author lingting 2020-09-01 19:56
 */
@Slf4j
public class TransferTest {

	private VirtualCurrencyService service;

	@Test
	@SneakyThrows
	public void ethTest() {
		service = new InfuraServiceImpl(new InfuraProperties().setEndpoints(InfuraEndpoints.KOVAN)
				// infura project id
				.setProjectId("b6066b4cfce54e7384ea38d52f9260ac")
				// 自定义web3j地址
				.setUrl("http://192.168.1.206:8545/"));

		String address = "0x8825226957c7898113f6a1dddb680bf355782342";
		String keystore = "{\"address\":\"8825226957c7898113f6a1dddb680bf355782342\","
				+ "\"id\":\"ce6ea3e1-66e1-49bf-8b29-fd73e5910e34\",\"version\":3,"
				+ "\"crypto\":{\"cipher\":\"aes-128-ctr\","
				+ "\"ciphertext\":\"8b2050efb07a474cfaf0b2509799d273efcdf88f709141d946ef0ce791b302be\","
				+ "\"cipherparams\":{\"iv\":\"5286131e94674f8d880f403867d7d6d6\"},\"kdf\":\"scrypt\","
				+ "\"kdfparams\":{\"dklen\":32,\"n\":4096,\"p\":6,\"r\":8,"
				+ "\"salt\":\"a35cb2a5dbe9b8640853d93dea17c4203e0d9d08dfd6a2265621d34eb0abb0ee\"},"
				+ "\"mac\":\"7c3460c3cdf2809c7b3c25943db8b042058f2c218d07e32779f4f9c08cc18ae3\"}}";
		String pwd = "123456";
		VirtualCurrencyAccount account = VirtualCurrencyAccount.ofKeystore(address, pwd, keystore);

		BigDecimal bAccount = service.getNumberByAddressAndContract(account.getAddress(), Etherscan.ETH);
		System.out.println("account ETH余额: " + bAccount.toPlainString());
		bAccount = service.getNumberByAddressAndContract(account.getAddress(), Etherscan.USDT);
		System.out.println("account USDT余额: " + bAccount.toPlainString());

		String a1 = "0x9471851ad4032899a6fd45fc730dfc40f91ffd3c";
		String pb1 = "56d67ccdc455b93b46a3fd21f950c93d8be2e18c872126e584c7653e4a4796fac59b93bb3c52aa2563df6e72651e938f5a38a0fbae61c513486ee707d0542317";
		String pr1 = "f520231e9ff23a651135467028136bbc5193c6bc71465480215ccf8e16a97be";
		VirtualCurrencyAccount ac1 = VirtualCurrencyAccount.ofKey(a1, pr1);

		BigDecimal b1 = service.getNumberByAddressAndContract(a1, Etherscan.ETH);
		System.out.println("a1 ETH余额: " + b1.toPlainString());
		b1 = service.getNumberByAddressAndContract(a1, Etherscan.USDT);
		System.out.println("a1 USDT余额: " + b1.toPlainString());

		String a2 = "0x5fa7e29ffcd685f9fd31d7fe6940be5ef7cb6358";
		String pb2 = "12651006e61832fcb2ec69586e8a0d4dbc3b17e133fb425abad5978d367af2b9f43053ee160e6f4584316665ecad0029bc82568b322d6ab51b138b40967c2e00";
		String pr2 = "3a1477cd2b3f8bdb47a419760cd11d24b50ac9e2a80cc6e4f7ff902d8871978f";
		VirtualCurrencyAccount ac2 = VirtualCurrencyAccount.ofKey(a2, pr2);

		BigDecimal b2 = service.getNumberByAddressAndContract(a2, Etherscan.ETH);
		System.out.println("a2 ETH余额: " + b2.toPlainString());
		b2 = service.getNumberByAddressAndContract(a2, Etherscan.USDT);
		System.out.println("a2 USDT余额: " + b2.toPlainString());
	}

	@Test
	@SneakyThrows
	public void btcTest() {
		service = new OmniServiceImpl(new OmniProperties().setEndpoints(OmniEndpoints.MAINNET));
	}

	@Test
	@SneakyThrows
	public void tronscanTest() {
		service = new TronscanServiceImpl(new TronscanProperties().setEndpoints(TronscanEndpoints.MAINNET));
	}

}
