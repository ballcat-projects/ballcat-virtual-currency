package live.lingting.virtual.currency.etherscan;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.math.BigDecimal;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import lombok.SneakyThrows;
import org.junit.Test;
import live.lingting.virtual.currency.Account;
import live.lingting.virtual.currency.TransferResult;
import live.lingting.virtual.currency.contract.Contract;
import live.lingting.virtual.currency.contract.EtherscanContract;
import live.lingting.virtual.currency.endpoints.InfuraEndpoints;
import live.lingting.virtual.currency.properties.InfuraProperties;
import live.lingting.virtual.currency.service.PlatformService;
import live.lingting.virtual.currency.service.impl.InfuraServiceImpl;
import live.lingting.virtual.currency.util.AbiUtil;
import live.lingting.virtual.currency.util.EtherscanUtil;
import live.lingting.virtual.currency.util.JsonUtil;

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

	static {
		service = new InfuraServiceImpl(new InfuraProperties()
				// 节点
				.setEndpoints(InfuraEndpoints.RINKEBY)
				// project Id
				.setProjectId("b6066b4cfce54e7384ea38d52f9260ac"));

		// ChainLink 测试网的erc20合约
		cl = AbiUtil.createContract("0x01BE23585060835E02B77ef475b0Cc51aA1e0709");

		a1 = "0x8825226957c7898113f6a1dddb680bf355782342";
		pb1 = "df154f4660d9fb2645bdbf24db3a0875be6f84295323c873c6d2b3ba9e41bc255c5c7c5547997a81013fc674d0ee69c8d10f1aa295e48e994a383647072b7b17";
		pr1 = "a2aef487fce63769f65689fcea175c79aa6faeb5aca9ac8a57052a8d898a9c9";
		ac1 = EtherscanUtil.getAccountOfKey(a1, pr1);

		a2 = "0x5fa7e29ffcd685f9fd31d7fe6940be5ef7cb6358";
		pb2 = "12651006e61832fcb2ec69586e8a0d4dbc3b17e133fb425abad5978d367af2b9f43053ee160e6f4584316665ecad0029bc82568b322d6ab51b138b40967c2e00";
		pr2 = "3a1477cd2b3f8bdb47a419760cd11d24b50ac9e2a80cc6e4f7ff902d8871978f";
		ac2 = EtherscanUtil.getAccountOfKey(a2, pr2);

	}

	public static void main(String[] args) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException,
			NoSuchProviderException, JsonProcessingException {
		String s = "{\"address\":\"0xb84286ea170661e25d8ef5f52cc18066e175b5ba\"," + "\"publicKey"
				+ "\":\"58b75ffb6bdb54ca16a94c4eb2ddd603c9b61e8df1b8eab2366a106eb587b1f311f512aade1cb5ffc8baa433a679ed29274a677d8ef40b54e90ce0572fe75e64\",\"privateKey\":\"dc8b697056d14dbe7c7ed431f9e9c2dd34ee93663425750bbd3907c549107d7a\",\"multi\":false}\n";
		System.out.println(JsonUtil.toJson(EtherscanUtil.createAccount()));
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
		TransferResult transfer = service.transfer(ac1, a2, EtherscanContract.ETH, value);

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
	public void erc20() {
		// 通过浏览器查看到的精度为 18
		System.out.println("cl 精度 " + service.getDecimalsByContract(cl));

		// 可在 https://rinkeby.chain.link/ 中获取
		BigDecimal b1 = service.getNumberByAddressAndContract(a1, cl);
		System.out.println("a1 cl余额: " + b1.toPlainString());

		BigDecimal b2 = service.getNumberByAddressAndContract(a2, cl);
		System.out.println("a2 cl余额: " + b2.toPlainString());

		BigDecimal value = new BigDecimal("3");
		System.out.println("a1 向 a2 转 " + value.toPlainString() + " cl");
		//TransferResult transfer = service.transfer(ac1, a2, cl, value);
		TransferResult transfer = service.transfer(ac2, "0xb84286Ea170661E25d8eF5F52Cc18066e175B5BA", cl, value);

		if (!transfer.getSuccess()) {
			System.out.println("转账失败: " + JsonUtil.toJson(transfer));
		}
		else {
			System.out.println("转账成功");
			System.out.println(transfer.getHash());
		}
	}

}
