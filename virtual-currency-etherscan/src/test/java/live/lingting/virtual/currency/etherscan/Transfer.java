package live.lingting.virtual.currency.etherscan;

import java.math.BigDecimal;
import lombok.SneakyThrows;
import org.junit.Test;
import live.lingting.virtual.currency.core.Contract;
import live.lingting.virtual.currency.core.PlatformService;
import live.lingting.virtual.currency.core.model.Account;
import live.lingting.virtual.currency.core.model.TransferResult;
import live.lingting.virtual.currency.core.util.AbiUtils;
import live.lingting.virtual.currency.core.util.AssertUtils;
import live.lingting.virtual.currency.core.util.JacksonUtils;
import live.lingting.virtual.currency.etherscan.contract.EtherscanContract;
import live.lingting.virtual.currency.etherscan.endpoints.EtherscanEndpoints;
import live.lingting.virtual.currency.etherscan.properties.EtherscanProperties;
import live.lingting.virtual.currency.etherscan.util.EtherscanUtils;

/**
 * @author lingting 2021/1/10 15:54
 */
public class Transfer {

	private static final PlatformService service;

	private static final Contract cl;

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
		service = new EtherscanServiceImpl(new EtherscanProperties()
				// 节点
				.setEndpoints(EtherscanEndpoints.RINKEBY)
				// project Id
				.setProjectId("b6066b4cfce54e7384ea38d52f9260ac"));

		// ChainLink 测试网的erc20合约
		cl = AbiUtils.createContract("0x01BE23585060835E02B77ef475b0Cc51aA1e0709");

		a1 = "0x8825226957c7898113f6a1dddb680bf355782342";
		pb1 = "df154f4660d9fb2645bdbf24db3a0875be6f84295323c873c6d2b3ba9e41bc255c5c7c5547997a81013fc674d0ee69c8d10f1aa295e48e994a383647072b7b17";
		pr1 = "a2aef487fce63769f65689fcea175c79aa6faeb5aca9ac8a57052a8d898a9c9";
		ac1 = EtherscanUtils.getAccountOfKey(a1, pr1);

		a2 = "0x5fa7e29ffcd685f9fd31d7fe6940be5ef7cb6358";
		pb2 = "12651006e61832fcb2ec69586e8a0d4dbc3b17e133fb425abad5978d367af2b9f43053ee160e6f4584316665ecad0029bc82568b322d6ab51b138b40967c2e00";
		pr2 = "3a1477cd2b3f8bdb47a419760cd11d24b50ac9e2a80cc6e4f7ff902d8871978f";
		ac2 = EtherscanUtils.getAccountOfKey(a2, pr2);

		ac3 = EtherscanUtils.getAccountOfKey("0xf7ac254d7a558c87925d89bd05273426a58244b7",
				"ff450f315d0942d27b2fa447a9e987e44b5e970aeb114e1459b523a383c23b0a");
	}

	@Test
	@SneakyThrows
	public void eth() {
		System.out.println("\n\n\n\n\n\n");
		Account from = ac2;
		Account to = ac3;

		// 可在 https://faucet.rinkeby.io/ 中获取
		BigDecimal b1 = service.getNumberByAddressAndContract(from.getAddress(), EtherscanContract.ETH);
		System.out.println("from ETH 余额: " + b1.toPlainString());

		BigDecimal b2 = service.getNumberByAddressAndContract(to.getAddress(), EtherscanContract.ETH);
		System.out.println("to ETH 余额: " + b2.toPlainString());

		BigDecimal value = new BigDecimal("1");
		System.out.println(from.getAddress() + " 向 " + to.getAddress() + " 转 " + value.toPlainString() + " eth");
		TransferResult transfer = service.transfer(from, to.getAddress(), EtherscanContract.ETH, value);

		AssertUtils.isTrue(transfer.getSuccess(), "转账失败: " + JacksonUtils.toJson(transfer));
	}

	@Test
	@SneakyThrows
	public void erc20() {
		System.out.println("\n\n\n\n\n\n");
		// 通过浏览器查看到的精度为 18
		System.out.println("cl 精度 " + service.getDecimalsByContract(cl));

		Account from = ac2;
		Account to = ac3;

		// 可在 https://faucet.rinkeby.io/ 中获取
		BigDecimal b1 = service.getNumberByAddressAndContract(from.getAddress(), cl);
		System.out.println("from cl 余额: " + b1.toPlainString());

		BigDecimal b2 = service.getNumberByAddressAndContract(to.getAddress(), cl);
		System.out.println("to cl 余额: " + b2.toPlainString());

		BigDecimal value = new BigDecimal("1");
		System.out.println(from.getAddress() + " 向 " + to.getAddress() + " 转 " + value.toPlainString() + " cl");
		TransferResult transfer = service.transfer(from, to.getAddress(), cl, value);

		AssertUtils.isTrue(transfer.getSuccess(), "转账失败: " + JacksonUtils.toJson(transfer));
	}

}
