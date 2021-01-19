package live.lingting.virtual.currency.tronscan;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.math.BigDecimal;
import lombok.SneakyThrows;
import org.junit.Test;
import live.lingting.virtual.currency.Account;
import live.lingting.virtual.currency.TransferResult;
import live.lingting.virtual.currency.contract.Contract;
import live.lingting.virtual.currency.contract.TronscanContract;
import live.lingting.virtual.currency.endpoints.TronscanEndpoints;
import live.lingting.virtual.currency.properties.TronscanProperties;
import live.lingting.virtual.currency.service.PlatformService;
import live.lingting.virtual.currency.service.impl.TronscanServiceImpl;
import live.lingting.virtual.currency.util.AbiUtil;
import live.lingting.virtual.currency.util.JsonUtil;
import live.lingting.virtual.currency.util.TronscanUtil;

/**
 * @author lingting 2021/1/10 15:54
 */
public class Transfer {

	private static final PlatformService service;

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

	static {
		service = new TronscanServiceImpl(new TronscanProperties().setEndpoints(TronscanEndpoints.NILE));

		// NILE 测试网的 trc10 合约
		trz = AbiUtil.createContract("1000016");

		// NILE 测试网的 trc20 合约
		usdj = AbiUtil.createContract("TLBaRhANQoJFTqre9Nf1mjuwNWjCJeYqUL");

		a1 = "TNg89VezqMAZkcdsBnMeXFzoprQDHTqEzy";
		pb1 = "5ec6a30cedc4f66c1f0af7571918a7416e6c9787b85b15e5543e1fd374b2eacff564e7bd42dadaa21219ce4872c0e3f1ab1095832719e58d58bcf873cd0ffa4e";
		pr1 = "a213096d0ecccf942f0e38ec3710756b3ce49d1a31b3de51e7e7acdc9a99f9da";
		ac1 = TronscanUtil.getAccountOfKey(a1, pr1);

		a2 = "TUWHqz3daZjodAZGyfDSrbzuSoE3RLwRUA";
		pb2 = "6d85aee68ac4ce0521fd73c4af0e86deaaa4b57b04fcad2315a8ddb1d1a719fba4b46bdf16c298fd1f4609ef229999fdb7ea36fe07306490f73474bf0b0ef45a";
		pr2 = "b1800442fe966cf8c0e5f058a4e1b7ab97b514b8983b726abe510c69b03e3905";
		ac2 = TronscanUtil.getAccountOfKey(a2, pr2);

	}

	public static void main(String[] args) throws JsonProcessingException {
		String s = "{\"address\":\"TKeU9ChEmqHcXQK5uY79S1mXP9ZsZ68ocz\"," + "\"publicKey"
				+ "\":\"803d809e80150b808d2186b24ada31783e6eabf78feae3b3f4d1cae28a25a4d5152884ff68f026d7baf79183904049bdc612fe23850aed52324731a37041313a\",\"privateKey\":\"4aada735aac438abbc19794d05d3267f6e7abc64b51aeac0f97ccc606e29db48\",\"multi\":false}\n";
		Account account = TronscanUtil.create();
		System.out.println(JsonUtil.toJson(account));
	}

	@Test
	@SneakyThrows
	public void trx() {
		// 可在 https://nileex.io/join/getJoinPage 中获取
		BigDecimal b1 = service.getNumberByAddressAndContract(a1, TronscanContract.TRX);
		System.out.println("a1 TRX余额: " + b1.toPlainString());

		BigDecimal b2 = service.getNumberByAddressAndContract(a2, TronscanContract.TRX);
		System.out.println("a2 TRX余额: " + b2.toPlainString());

		BigDecimal value = new BigDecimal("100");
		System.out.println("a1 向 a2 转 " + value.toPlainString() + " trx");
		TransferResult transfer = service.transfer(ac1, a2, TronscanContract.TRX, value);

		if (!transfer.getSuccess()) {
			System.out.println("转账失败: " + JsonUtil.toJson(transfer));
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
		TransferResult transfer = service.transfer(ac1, a2, trz, value);

		if (!transfer.getSuccess()) {
			System.out.println("转账失败: " + JsonUtil.toJson(transfer));
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

		BigDecimal value = new BigDecimal("10");
		System.out.println("a1 向 a2 转 " + value.toPlainString() + " usdj");
		TransferResult transfer = service.transfer(ac1, a2, usdj, value);

		if (!transfer.getSuccess()) {
			System.out.println("转账失败: " + JsonUtil.toJson(transfer));
		}
		else {
			System.out.println("转账成功");
			System.out.println(transfer.getHash());
		}
	}

}
