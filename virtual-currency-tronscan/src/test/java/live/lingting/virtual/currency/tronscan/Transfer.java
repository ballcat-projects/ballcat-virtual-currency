package live.lingting.virtual.currency.tronscan;

import java.math.BigDecimal;
import lombok.SneakyThrows;
import org.junit.Test;
import live.lingting.virtual.currency.core.Contract;
import live.lingting.virtual.currency.core.model.Account;
import live.lingting.virtual.currency.core.model.TransferResult;
import live.lingting.virtual.currency.core.util.AbiUtils;
import live.lingting.virtual.currency.core.util.AssertUtils;
import live.lingting.virtual.currency.core.util.JacksonUtils;
import live.lingting.virtual.currency.tronscan.contract.TronscanContract;
import live.lingting.virtual.currency.tronscan.endpoints.TronscanEndpoints;
import live.lingting.virtual.currency.tronscan.properties.TronscanProperties;
import live.lingting.virtual.currency.tronscan.util.TronscanUtils;

/**
 * @author lingting 2021/1/10 15:54
 */
public class Transfer {

	private static final TronscanServiceImpl service;

	private static final Contract trz;

	private static final Contract usdj;

	private static final String a1;

	private static final String pb1;

	private static final String pr1;

	private static final Account ac1;

	private static final String a2;

	private static final String pb2;

	private static final String pr2;

	private static final Account ac2;

	private static final Account ac3;

	static {
		service = new TronscanServiceImpl(new TronscanProperties().setEndpoints(TronscanEndpoints.NILE)
				.setApiKey("1dbdc72e-e69c-4909-bc27-861348b1031e"));

		// NILE 测试网的 trc10 合约
		trz = AbiUtils.createContract("1000016");

		// NILE 测试网的 trc20 合约
		usdj = AbiUtils.createContract("TLBaRhANQoJFTqre9Nf1mjuwNWjCJeYqUL");

		a1 = "TNg89VezqMAZkcdsBnMeXFzoprQDHTqEzy";
		pb1 = "5ec6a30cedc4f66c1f0af7571918a7416e6c9787b85b15e5543e1fd374b2eacff564e7bd42dadaa21219ce4872c0e3f1ab1095832719e58d58bcf873cd0ffa4e";
		pr1 = "a213096d0ecccf942f0e38ec3710756b3ce49d1a31b3de51e7e7acdc9a99f9da";
		ac1 = TronscanUtils.getAccountOfKey(a1, pr1);

		a2 = "TUWHqz3daZjodAZGyfDSrbzuSoE3RLwRUA";
		pb2 = "6d85aee68ac4ce0521fd73c4af0e86deaaa4b57b04fcad2315a8ddb1d1a719fba4b46bdf16c298fd1f4609ef229999fdb7ea36fe07306490f73474bf0b0ef45a";
		pr2 = "b1800442fe966cf8c0e5f058a4e1b7ab97b514b8983b726abe510c69b03e3905";
		ac2 = TronscanUtils.getAccountOfKey(a2, pr2);

		ac3 = TronscanUtils.getAccountOfKey("TLowwQtg4fNWsWUT5vTexpgukpwgLKg4F7",
				"6b082db1ca6b61c2e136804699a8b8f550ae4eea4bd2a6231b0e804f16bf664f");

	}

	@Test
	@SneakyThrows
	public void trx() {
		System.out.println("\n\n\n\n\n\n");
		Account from = ac1;
		Account to = ac3;

		// 可在 https://nileex.io/join/getJoinPage 中获取
		BigDecimal b1 = service.getNumberByAddressAndContract(from.getAddress(), TronscanContract.TRX);
		System.out.println("from TRX余额: " + b1.toPlainString());

		BigDecimal b2 = service.getNumberByAddressAndContract(to.getAddress(), TronscanContract.TRX);
		System.out.println("to TRX余额: " + b2.toPlainString());

		BigDecimal value = new BigDecimal("1");
		System.out.println(from.getAddress() + " 向 " + to.getAddress() + " 转 " + value.toPlainString() + " trx");
		TransferResult transfer = service.transfer(from, to.getAddress(), TronscanContract.TRX, value);

		AssertUtils.isTrue(transfer.getSuccess(), "转账失败: " + JacksonUtils.toJson(transfer));
	}

	@Test
	@SneakyThrows
	public void trc10() {
		System.out.println("\n\n\n\n\n\n");
		// 通过浏览器查看到的精度为 6
		System.out.println("trz 精度 " + service.getDecimalsByContract(trz));

		Account from = ac1;
		Account to = ac3;

		// 可在 https://nileex.io/join/getJoinPage 中获取
		BigDecimal b1 = service.getNumberByAddressAndContract(from.getAddress(), trz);
		System.out.println("from trz余额: " + b1.toPlainString());

		BigDecimal b2 = service.getNumberByAddressAndContract(to.getAddress(), trz);
		System.out.println("to trz余额: " + b2.toPlainString());

		BigDecimal value = new BigDecimal("1");
		System.out.println(from.getAddress() + " 向 " + to.getAddress() + " 转 " + value.toPlainString() + " trz");
		TransferResult transfer = service.transfer(from, to.getAddress(), trz, value);

		AssertUtils.isTrue(transfer.getSuccess(), "转账失败: " + JacksonUtils.toJson(transfer));
	}

	@Test
	@SneakyThrows
	public void trc20() {
		System.out.println("\n\n\n\n\n\n");
		// 通过浏览器查看到的精度为 18
		System.out.println("usdj 精度 " + service.getDecimalsByContract(usdj));

		Account from = ac1;
		Account to = ac3;

		// 可在 https://nileex.io/join/getJoinPage 中获取
		BigDecimal b1 = service.getNumberByAddressAndContract(from.getAddress(), usdj);
		System.out.println("from usdj余额: " + b1.toPlainString());

		BigDecimal b2 = service.getNumberByAddressAndContract(to.getAddress(), usdj);
		System.out.println("to usdj余额: " + b2.toPlainString());

		BigDecimal value = new BigDecimal("1");
		System.out.println(from.getAddress() + " 向 " + to.getAddress() + " 转 " + value.toPlainString() + " usdj");
		TransferResult transfer = service.transfer(from, to.getAddress(), usdj, value);

		AssertUtils.isTrue(transfer.getSuccess(), "转账失败: " + JacksonUtils.toJson(transfer));
	}

}
