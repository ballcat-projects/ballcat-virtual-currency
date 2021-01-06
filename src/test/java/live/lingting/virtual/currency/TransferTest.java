package live.lingting.virtual.currency;

import static live.lingting.virtual.currency.contract.TronscanContract.TRX;

import java.math.BigDecimal;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import live.lingting.virtual.currency.contract.Contract;
import live.lingting.virtual.currency.contract.EtherscanContract;
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
import live.lingting.virtual.currency.util.AbiUtil;
import live.lingting.virtual.currency.util.EtherscanUtil;
import live.lingting.virtual.currency.util.TronscanUtil;

/**
 * @author lingting 2020-09-01 19:56
 */
@Slf4j
public class TransferTest {

	private VirtualCurrencyService service;

	@Test
	@SneakyThrows
	public void ethTest() {
		service = new InfuraServiceImpl(new InfuraProperties()
				// 节点
				.setEndpoints(InfuraEndpoints.ROPSTEN)
				// 自定义web3j地址
				.setUrl("http://192.168.1.206:8545/")
				// infura project id
				.setProjectId("b6066b4cfce54e7384ea38d52f9260ac"));

		Contract contract = AbiUtil.createContract("0x9bae6500cd511f1546f7c1e1a9baf34b3afd5d01");

		// String a1 = "0x9471851ad4032899a6fd45fc730dfc40f91ffd3c";
		String a1 = "0x8825226957c7898113f6a1dddb680bf355782342";
		// String pb1 =
		// "56d67ccdc455b93b46a3fd21f950c93d8be2e18c872126e584c7653e4a4796fac59b93bb3c52aa2563df6e72651e938f5a38a0fbae61c513486ee707d0542317";
		String pb1 = "df154f4660d9fb2645bdbf24db3a0875be6f84295323c873c6d2b3ba9e41bc255c5c7c5547997a81013fc674d0ee69c8d10f1aa295e48e994a383647072b7b17";
		// String pr1 = "f520231e9ff23a651135467028136bbc5193c6bc71465480215ccf8e16a97be";
		String pr1 = "a2aef487fce63769f65689fcea175c79aa6faeb5aca9ac8a57052a8d898a9c9";
		Account ac1 = EtherscanUtil.getAccountOfKey(a1, pr1);

		BigDecimal b1 = service.getNumberByAddressAndContract(a1, EtherscanContract.ETH);
		System.out.println("a1 ETH余额: " + b1.toPlainString());
		b1 = service.getNumberByAddressAndContract(a1, EtherscanContract.USDT);
		System.out.println("a1 USDT余额: " + b1.toPlainString());
		b1 = service.getNumberByAddressAndContract(a1, contract);
		System.out.println("a1 contract余额: " + b1.toPlainString());

		String a2 = "0x5fa7e29ffcd685f9fd31d7fe6940be5ef7cb6358";
		String pb2 = "12651006e61832fcb2ec69586e8a0d4dbc3b17e133fb425abad5978d367af2b9f43053ee160e6f4584316665ecad0029bc82568b322d6ab51b138b40967c2e00";
		String pr2 = "3a1477cd2b3f8bdb47a419760cd11d24b50ac9e2a80cc6e4f7ff902d8871978f";
		Account ac2 = EtherscanUtil.getAccountOfKey(a2, pr2);

		BigDecimal b2 = service.getNumberByAddressAndContract(a2, EtherscanContract.ETH);
		System.out.println("a2 ETH余额: " + b2.toPlainString());
		b2 = service.getNumberByAddressAndContract(a2, EtherscanContract.USDT);
		System.out.println("a2 USDT余额: " + b2.toPlainString());
		b2 = service.getNumberByAddressAndContract(a2, contract);
		System.out.println("a2 contract余额: " + b2.toPlainString());

		// TransferResult transfer = new TransferResult();
		// TransferResult transfer = service.transfer(ac1, a2, contract, new
		// BigDecimal("0.1"));
		// TransferResult transfer = service.transfer(ac1, a2, EtherscanContract.ETH, new
		// BigDecimal("0.001"));
		// System.out.println(transfer.getSuccess());
		// System.out.println(transfer.getHash());
	}

	@Test
	@SneakyThrows
	public void btcTest() {
		service = new OmniServiceImpl(new OmniProperties().setEndpoints(OmniEndpoints.MAINNET));
	}

	@Test
	@SneakyThrows
	public void tronscanTest() {
		Contract USDJ = AbiUtil.createContract("TLBaRhANQoJFTqre9Nf1mjuwNWjCJeYqUL", 18);

		// Contract TRZ = AbiUtil.createContract("1000016", 6);
		Contract TRZ = AbiUtil.createContract("1000016", null);

		service = new TronscanServiceImpl(new TronscanProperties().setEndpoints(TronscanEndpoints.NILE));
		String a1 = "TNg89VezqMAZkcdsBnMeXFzoprQDHTqEzy";
		String puk1 = "5ec6a30cedc4f66c1f0af7571918a7416e6c9787b85b15e5543e1fd374b2eacff564e7bd42dadaa21219ce4872c0e3f1ab1095832719e58d58bcf873cd0ffa4e";
		String prk1 = "a213096d0ecccf942f0e38ec3710756b3ce49d1a31b3de51e7e7acdc9a99f9da";
		Account ac1 = TronscanUtil.getAccountOfKey(a1, prk1);

		BigDecimal b1 = service.getNumberByAddressAndContract(a1, TRX);
		System.out.println("a1 TRX余额: " + b1.toPlainString());
		b1 = service.getNumberByAddressAndContract(a1, TRZ);
		System.out.println("a1 TRZ余额: " + b1.toPlainString());
		b1 = service.getNumberByAddressAndContract(a1, USDJ);
		System.out.println("a1 USDJ余额: " + b1.toPlainString());

		String a2 = "TUWHqz3daZjodAZGyfDSrbzuSoE3RLwRUA";
		String puk2 = "6d85aee68ac4ce0521fd73c4af0e86deaaa4b57b04fcad2315a8ddb1d1a719fba4b46bdf16c298fd1f4609ef229999fdb7ea36fe07306490f73474bf0b0ef45a";
		String prk2 = "b1800442fe966cf8c0e5f058a4e1b7ab97b514b8983b726abe510c69b03e3905";
		Account ac2 = TronscanUtil.getAccountOfKey(a2, prk2);

		BigDecimal b2 = service.getNumberByAddressAndContract(a2, TRX);
		System.out.println("a2 TRX余额: " + b2.toPlainString());
		b2 = service.getNumberByAddressAndContract(a2, TRZ);
		System.out.println("a2 TRZ余额: " + b2.toPlainString());
		b2 = service.getNumberByAddressAndContract(a2, USDJ);
		System.out.println("a2 USDJ余额: " + b2.toPlainString());

		BigDecimal value = new BigDecimal("0.01");
		// System.out.println("a1 向 a2 转 " + value.toPlainString() + " USDJ");
		// TransferResult transfer = service.transfer(ac1, a2, USDJ, value);
		// System.out.println("a1 向 a2 转 1 TRZ");
		// TransferResult transfer = service.transfer(ac1, a2, TRZ, value);
		System.out.println("a1 向 a2 转 1 TRX");
		TransferResult transfer = service.transfer(ac1, a2, TRX, value);

		if (!transfer.getSuccess()) {
			System.out.println("转账失败, msg: " + transfer.getMessage());
			return;
		}
		System.out.println("转账成功, txId: " + transfer.getHash());

		b1 = service.getNumberByAddressAndContract(a1, TRX);
		System.out.println("a1 TRX余额: " + b1.toPlainString());
		b1 = service.getNumberByAddressAndContract(a1, TRZ);
		System.out.println("a1 TRZ余额: " + b1.toPlainString());
		b1 = service.getNumberByAddressAndContract(a1, USDJ);
		System.out.println("a1 USDJ余额: " + b1.toPlainString());

		b2 = service.getNumberByAddressAndContract(a2, TRX);
		System.out.println("a2 TRX余额: " + b2.toPlainString());
		b2 = service.getNumberByAddressAndContract(a2, TRZ);
		System.out.println("a2 TRZ余额: " + b2.toPlainString());
		b2 = service.getNumberByAddressAndContract(a2, USDJ);
		System.out.println("a2 USDJ余额: " + b2.toPlainString());
	}

}
