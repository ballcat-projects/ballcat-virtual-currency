package com.lingting.gzm.virtual.currency;

import com.lingting.gzm.virtual.currency.contract.EtherscanContract;
import com.lingting.gzm.virtual.currency.contract.TronscanContract;
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
import com.lingting.gzm.virtual.currency.util.EtherscanUtil;
import com.lingting.gzm.virtual.currency.util.TronscanUtil;
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
		VirtualCurrencyAccount account = EtherscanUtil.getAccountOfKeystore(address, pwd, keystore);

		BigDecimal bAccount = service.getNumberByAddressAndContract(account.getAddress(), EtherscanContract.ETH);
		System.out.println("account ETH余额: " + bAccount.toPlainString());
		bAccount = service.getNumberByAddressAndContract(account.getAddress(), EtherscanContract.USDT);
		System.out.println("account USDT余额: " + bAccount.toPlainString());

		String a1 = "0x9471851ad4032899a6fd45fc730dfc40f91ffd3c";
		String pb1 = "56d67ccdc455b93b46a3fd21f950c93d8be2e18c872126e584c7653e4a4796fac59b93bb3c52aa2563df6e72651e938f5a38a0fbae61c513486ee707d0542317";
		String pr1 = "f520231e9ff23a651135467028136bbc5193c6bc71465480215ccf8e16a97be";
		VirtualCurrencyAccount ac1 = EtherscanUtil.getAccountOfKey(a1, pr1);

		BigDecimal b1 = service.getNumberByAddressAndContract(a1, EtherscanContract.ETH);
		System.out.println("a1 ETH余额: " + b1.toPlainString());
		b1 = service.getNumberByAddressAndContract(a1, EtherscanContract.USDT);
		System.out.println("a1 USDT余额: " + b1.toPlainString());

		String a2 = "0x5fa7e29ffcd685f9fd31d7fe6940be5ef7cb6358";
		String pb2 = "12651006e61832fcb2ec69586e8a0d4dbc3b17e133fb425abad5978d367af2b9f43053ee160e6f4584316665ecad0029bc82568b322d6ab51b138b40967c2e00";
		String pr2 = "3a1477cd2b3f8bdb47a419760cd11d24b50ac9e2a80cc6e4f7ff902d8871978f";
		VirtualCurrencyAccount ac2 = EtherscanUtil.getAccountOfKey(a2, pr2);

		BigDecimal b2 = service.getNumberByAddressAndContract(a2, EtherscanContract.ETH);
		System.out.println("a2 ETH余额: " + b2.toPlainString());
		b2 = service.getNumberByAddressAndContract(a2, EtherscanContract.USDT);
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
		service = new TronscanServiceImpl(new TronscanProperties().setEndpoints(TronscanEndpoints.NILE));
		String a1 = "TBQa122mEvNbodaPTh5DXaEU7wFv7W1rci";
		String puk1 = "d209cbd454acd5646ecebb4260640c9f823a9ea811c1d65699717e27e8ce9a4cd9bd4c9dd21dabd55661074a98f0aeaf8c64c73630745f26154444f4e535e58c";
		String prk1 = "6784b3c8872db612adc0fac098440689f52e18b37f995eedb8db489374edb215";
		VirtualCurrencyAccount ac1 = TronscanUtil.getAccountOfKey(a1, prk1);

		BigDecimal b1 = service.getNumberByAddressAndContract(a1, TronscanContract.TRX);
		System.out.println("a1 TRX余额: " + b1.toPlainString());
		b1 = service.getNumberByAddressAndContract(a1, TronscanContract.TRZ);
		System.out.println("a1 TRZ余额: " + b1.toPlainString());
		b1 = service.getNumberByAddressAndContract(a1, TronscanContract.USDJ);
		System.out.println("a1 USDJ余额: " + b1.toPlainString());

		String a2 = "TTteWxGuyP3vSkpQLoayYYRLV1dk1pFnr8";
		String puk2 = "2c7189385fb2f685d1bd5f4d4c63bc904603fa04b6b2b72396e986ab35f361ca4effb5f70f4cf830e339b628df77b215ec99eabcb3ed7d2f11fcddfe515f616d";
		String prk2 = "cab1ba4d469a9d7168ee2c1f6627a585342f576d6129f2b7a751556e6847e1c8";
		VirtualCurrencyAccount ac2 = TronscanUtil.getAccountOfKey(a2, prk2);

		BigDecimal b2 = service.getNumberByAddressAndContract(a2, TronscanContract.TRX);
		System.out.println("a2 TRX余额: " + b2.toPlainString());
		b2 = service.getNumberByAddressAndContract(a2, TronscanContract.TRZ);
		System.out.println("a2 TRZ余额: " + b2.toPlainString());
		b2 = service.getNumberByAddressAndContract(a2, TronscanContract.USDJ);
		System.out.println("a2 USDJ余额: " + b2.toPlainString());

		System.out.println("a1 向 a2 转 1 USDJ");
		VirtualCurrencyTransferResult transfer = service.transfer(ac1, a2, TronscanContract.USDJ, new BigDecimal("1"));
		//VirtualCurrencyTransferResult transfer = new VirtualCurrencyTransferResult().setSuccess(false);

		if (!transfer.getSuccess()) {
			System.out.println("转账失败");
			return;
		}
		System.out.println("转账成功, 查询交易数据");

		b1 = service.getNumberByAddressAndContract(a1, TronscanContract.TRX);
		System.out.println("a1 TRX余额: " + b1.toPlainString());
		b1 = service.getNumberByAddressAndContract(a1, TronscanContract.TRZ);
		System.out.println("a1 TRZ余额: " + b1.toPlainString());
		b1 = service.getNumberByAddressAndContract(a1, TronscanContract.USDJ);
		System.out.println("a1 USDJ余额: " + b1.toPlainString());

		b2 = service.getNumberByAddressAndContract(a2, TronscanContract.TRX);
		System.out.println("a2 TRX余额: " + b2.toPlainString());
		b2 = service.getNumberByAddressAndContract(a2, TronscanContract.TRZ);
		System.out.println("a2 TRZ余额: " + b2.toPlainString());
		b2 = service.getNumberByAddressAndContract(a2, TronscanContract.USDJ);
		System.out.println("a2 USDJ余额: " + b2.toPlainString());
	}

}
