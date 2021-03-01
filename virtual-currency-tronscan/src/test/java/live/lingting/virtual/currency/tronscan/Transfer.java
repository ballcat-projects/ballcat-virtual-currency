package live.lingting.virtual.currency.tronscan;

import java.math.BigDecimal;
import java.math.BigInteger;
import lombok.SneakyThrows;
import org.junit.Test;
import live.lingting.virtual.currency.core.model.Account;
import live.lingting.virtual.currency.core.Contract;
import live.lingting.virtual.currency.core.model.TransferParams;
import live.lingting.virtual.currency.core.model.TransferResult;
import live.lingting.virtual.currency.core.util.AbiUtils;
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
		service = new TronscanServiceImpl(new TronscanProperties().setEndpoints(TronscanEndpoints.NILE));

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
	public void run() {
		String s = "{\"address\":\"TKeU9ChEmqHcXQK5uY79S1mXP9ZsZ68ocz\"," + "\"publicKey"
				+ "\":\"803d809e80150b808d2186b24ada31783e6eabf78feae3b3f4d1cae28a25a4d5152884ff68f026d7baf79183904049bdc612fe23850aed52324731a37041313a\",\"privateKey\":\"4aada735aac438abbc19794d05d3267f6e7abc64b51aeac0f97ccc606e29db48\",\"multi\":false}\n";

		String s2 = "{\"address\":\"TNXwczCYFn15rvk8YV8z5rnSN9nAmH1t1V\"," + "\"publicKey"
				+ "\":\"806248a34bcce9b438f688c800b469a017d57f8bfad290d3b24e5e1f3622b3ebc15debff747f00ac93e2d8c70c0e6ba066058487b0a24e6ce211c4372a115f5e\",\"privateKey\":\"677b8f8ba957cea8a0025de7d6e90358ea23e8bd39f34908d9e1235ff7273757\",\"multi\":false}";

		String s3 = "{\"address\":\"TFvQMGTs43oBQUauFnNkyz544J7LhdzBWq\"," + "\"publicKey"
				+ "\":\"0f33a52389b1f644f0ab1e6b2fd170a56a023d957ee70dcd32e7c7996e551342c9814719661a69e17b1c495696b4ca66c0d602b56343b94b71a6c5a1cabe1bc0\",\"privateKey\":\"2ed5328a2bd8289f103ece714133c35a56c2e8d62d117988fff44bdf114e9f6d\",\"multi\":false}\n";

		String s4 = "{\"address\":\"TKvrXAzmCJs1AFDhoLxEWZRHSrEiqzBHd9\"," + "\"publicKey"
				+ "\":\"869afd5f52e4dfa3b051b93f0a7d4b2bfe485b3cda8be6f165ed985a21592281cc936e3f033f033b16e818267830d749a73dfd39bf43795716d0f838655152e4\",\"privateKey\":\"b83850669411d9742ca9b40130e6f084046a9c222e0fadd9b7b218f95b6feeba\",\"multi\":false}";

		String s5 = "{\"address\":\"TLowwQtg4fNWsWUT5vTexpgukpwgLKg4F7\"," + "\"publicKey"
				+ "\":\"7af3fa3054dfe9a0b85708f4a7e854d214e5ddad6889b3bca0f1856b46d3b1d964a5c5806f700d77bb5675726f956c640df229cec45106ec5f7d38ad1302ab19\",\"privateKey\":\"6b082db1ca6b61c2e136804699a8b8f550ae4eea4bd2a6231b0e804f16bf664f\",\"multi\":false}";
		Account account = TronscanUtils.createAccount();
		System.out.println(JacksonUtils.toJson(account));
	}

	@Test
	@SneakyThrows
	public void trx() {
		// 可在 https://nileex.io/join/getJoinPage 中获取
		BigDecimal b1 = service.getNumberByAddressAndContract(a1, TronscanContract.TRX);
		System.out.println("a1 TRX余额: " + b1.toPlainString());

		BigDecimal b2 = service.getNumberByAddressAndContract(a2, TronscanContract.TRX);
		System.out.println("a2 TRX余额: " + b2.toPlainString());

		BigDecimal value = new BigDecimal("1");
		System.out.println("a1 向 a2 转 " + value.toPlainString() + " trx");
		TransferResult transfer = service.transfer(ac1, ac3.getAddress(), TronscanContract.TRX, value);

		if (!transfer.getSuccess()) {
			System.out.println("转账失败: " + JacksonUtils.toJson(transfer));
		}
		else {
			System.out.println("转账成功");
			System.out.println(transfer.getHash());
		}
	}

	@Test
	@SneakyThrows
	public void trc10() {
		// 通过浏览器查看到的精度为 6
		System.out.println("trz 精度 " + service.getDecimalsByContract(trz));

		// 可在 https://nileex.io/join/getJoinPage 中获取
		BigDecimal b1 = service.getNumberByAddressAndContract(a1, trz);
		System.out.println("a1 trz余额: " + b1.toPlainString());

		BigDecimal b2 = service.getNumberByAddressAndContract(a2, trz);
		System.out.println("a2 trz余额: " + b2.toPlainString());

		BigDecimal value = new BigDecimal("1");
		System.out.println("a1 向 a2 转 " + value.toPlainString() + " trz");
		TransferResult transfer = service.transfer(ac1, ac3.getAddress(), trz, value);

		if (!transfer.getSuccess()) {
			System.out.println("转账失败: " + JacksonUtils.toJson(transfer));
		}
		else {
			System.out.println("转账成功");
			System.out.println(transfer.getHash());
		}
	}

	@Test
	@SneakyThrows
	public void trc20() {
		// 通过浏览器查看到的精度为 18
		System.out.println("usdj 精度 " + service.getDecimalsByContract(usdj));

		// 可在 https://nileex.io/join/getJoinPage 中获取
		BigDecimal b1 = service.getNumberByAddressAndContract(a1, usdj);
		System.out.println("a1 usdj余额: " + b1.toPlainString());

		BigDecimal b2 = service.getNumberByAddressAndContract(a2, usdj);
		System.out.println("a2 usdj余额: " + b2.toPlainString());

		BigDecimal value = new BigDecimal("1");
		System.out.println("a1 向 a2 转 " + value.toPlainString() + " usdj");
		TransferResult transfer = service.transfer(ac1, ac3.getAddress(), usdj, value, new TransferParams().setCallValue(BigInteger.TEN));

		if (!transfer.getSuccess()) {
			System.out.println("转账失败: " + JacksonUtils.toJson(transfer));
		}
		else {
			System.out.println("转账成功");
			System.out.println(transfer.getHash());
		}
	}

}
