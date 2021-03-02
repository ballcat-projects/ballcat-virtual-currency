package live.lingting.virtual.currency.etherscan;

import java.math.BigDecimal;
import lombok.SneakyThrows;
import org.junit.Test;
import live.lingting.virtual.currency.core.model.Account;
import live.lingting.virtual.currency.core.Contract;
import live.lingting.virtual.currency.core.model.TransferResult;
import live.lingting.virtual.currency.core.PlatformService;
import live.lingting.virtual.currency.core.util.AbiUtils;
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
	public void run() {
		String s = "{\"address\":\"0xb84286ea170661e25d8ef5f52cc18066e175b5ba\"," + "\"publicKey"
				+ "\":\"58b75ffb6bdb54ca16a94c4eb2ddd603c9b61e8df1b8eab2366a106eb587b1f311f512aade1cb5ffc8baa433a679ed29274a677d8ef40b54e90ce0572fe75e64\",\"privateKey\":\"dc8b697056d14dbe7c7ed431f9e9c2dd34ee93663425750bbd3907c549107d7a\",\"multi\":false}\n";

		String s2 = "{\"address\":\"0x9060a6c61e998ab0c4b6855f68af30f5a4354cc2\"," + "\"publicKey"
				+ "\":\"7ca5e0694f53b968ac3ac6f2e56ad8afd9e07f2e7764adc7a53a1eb571136342ba3df1080347077fe65662bb38ac0e08578df3bdabdfcccae14d0cf91af33401\",\"privateKey\":\"37b1eeca602a68620b00ee56dcf25b1c88bf599b60e4c9130068ff6d9debfc15\",\"multi\":false}";

		String s3 = "{\"address\":\"0xfc2be74e45af57049fa5cc8ea1923fc9e3927577\"," + "\"publicKey"
				+ "\":\"bbf6f69003aaf2db4f8f8bed14656a0ab982c46506009de5dacefa0da0b04d9ef9886cf8dfc1d9a87c8872fd3fe2569cc8c367e19163ced038a8aefefce8d3e6\",\"privateKey\":\"e921173570bb99a11639bcb028ce45fc051ab63d3aa6e9b3a5617dd5ecdea50d\",\"multi\":false}";

		String s4 = "{\"address\":\"0xf7ac254d7a558c87925d89bd05273426a58244b7\"," + "\"publicKey"
				+ "\":\"6e831e54ba2a76c0dff3bbbdfe42fc714078cddf6fa24d2c1592ef133d44469cd2b6a75efe3a71427df1e6c4f15e67c5112be8d11f54df400232b0be8004ed82\",\"privateKey\":\"ff450f315d0942d27b2fa447a9e987e44b5e970aeb114e1459b523a383c23b0a\",\"multi\":false}\n";
		System.out.println(JacksonUtils.toJson(EtherscanUtils.createAccount()));
	}

	@Test
	@SneakyThrows
	public void eth() {

		// 可在 https://faucet.rinkeby.io/ 中获取
		BigDecimal b1 = service.getNumberByAddressAndContract(a1, EtherscanContract.ETH);
		System.out.println("a1 ETH余额: " + b1.toPlainString());

		BigDecimal b2 = service.getNumberByAddressAndContract(a2, EtherscanContract.ETH);
		System.out.println("a2 ETH余额: " + b2.toPlainString());

		BigDecimal value = new BigDecimal("1");
		System.out.println("a1 向 a2 转 " + value.toPlainString() + " eth");
		TransferResult transfer = service.transfer(ac2, ac3.getAddress(), EtherscanContract.ETH, value);

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
	public void erc20() {
		// 通过浏览器查看到的精度为 18
		System.out.println("cl 精度 " + service.getDecimalsByContract(cl));

		// 可在 https://rinkeby.chain.link/ 中获取
		BigDecimal b1 = service.getNumberByAddressAndContract(a1, cl);
		System.out.println("a1 cl余额: " + b1.toPlainString());

		BigDecimal b2 = service.getNumberByAddressAndContract(a2, cl);
		System.out.println("a2 cl余额: " + b2.toPlainString());

		BigDecimal value = new BigDecimal("1");
		System.out.println("a1 向 a2 转 " + value.toPlainString() + " cl");
		// TransferResult transfer = service.transfer(ac1, a2, cl, value);
		TransferResult transfer = service.transfer(ac1, ac3.getAddress(), cl, value);

		if (!transfer.getSuccess()) {
			System.out.println("转账失败: " + JacksonUtils.toJson(transfer));
		}
		else {
			System.out.println("转账成功");
			System.out.println(transfer.getHash());
		}
	}

}
