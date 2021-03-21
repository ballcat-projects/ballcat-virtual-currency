package live.lingting.virtual.currency.bitcoin;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.convert.Convert;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.TestNet3Params;
import org.junit.Test;
import live.lingting.virtual.currency.bitcoin.contract.OmniContract;
import live.lingting.virtual.currency.bitcoin.endpoints.BitcoinEndpoints;
import live.lingting.virtual.currency.bitcoin.model.Unspent;
import live.lingting.virtual.currency.bitcoin.model.UnspentRes;
import live.lingting.virtual.currency.bitcoin.model.omni.PushTx;
import live.lingting.virtual.currency.bitcoin.properties.BitcoinProperties;
import live.lingting.virtual.currency.bitcoin.util.BitcoinUtils;
import live.lingting.virtual.currency.core.JsonRpcClient;
import live.lingting.virtual.currency.core.model.Account;
import live.lingting.virtual.currency.core.model.TransferParams;
import live.lingting.virtual.currency.core.model.TransferResult;
import live.lingting.virtual.currency.core.util.AssertUtils;
import live.lingting.virtual.currency.core.util.JacksonUtils;

/**
 * @author lingting 2021/1/10 15:54
 */
public class Transfer {

	private static final String a1;

	private static final String pb1;

	private static final String pr1;

	private static final Account ac1;

	private static final String a2;

	private static final String a3;

	private static final String a4;

	private static final String a5;

	private static final String pb5;

	private static final String pr5;

	private static final Account ac5;

	private static final String a7;

	private static final String pb7;

	private static final String pr7;

	private static final Account ac7;

	private static final Account ac8;

	private static final Account ac9;

	private static final Account ac10;

	private static final Account ac11;

	private static BitcoinServiceImpl service;

	private static JsonRpcClient client;

	/**
	 * 配置网络环境为测试服
	 */
	private static NetworkParameters np = TestNet3Params.get();

	static {
		a1 = "miBEA6o6nZcaLZebR1dsDv4AMHRwJk1mbi";
		pb1 = "03a5be350852bb09e24edc83f8e02070a74597a8b775af46e8efbef394ae4fa98e";
		pr1 = "27b5bf6853c1730c20c152d67d9f4f85b20b674267f621bd1c88d177b2d56d83";
		ac1 = BitcoinUtils.getAccountOfKey(a1, pr1);

		// 这个地址是给我发测试币的地址, 直接转回去
		a2 = "2MsX74Kyreue6qg8okRtjhnd2yz8zAnLcNi";
		// 测试服 btc 回收地址
		a3 = "mv4rnyY3Su5gjcDNzbMLKBQkBicCtHUtFB";
		// 0.001 转btc 收到 omni 测试币
		a4 = "moneyqMan7uh8FqdCA2BV5yZ8qVrc9ikLP";
		// yusuo, omni 测试币转
		a5 = "2Mw3TeWtsJwJ6C7WE8cpjMhS2X4cWk87NLC";
		pb5 = "03fb005387502bd1e07f6202ef804ffde302f3c6f062e15ad634d1d06c95d08f96";
		pr5 = "29234b418f8fd27dad7ee537cf8677224c4cb32caab55ff008af393d20a43a71";
		ac5 = BitcoinUtils.getAccountOfKey(a5, pr5);

		a7 = "miV1sbmLM9fptRfWD5o8hs5L6ucUeJzmp5";
		pb7 = "03ca65ac405e0ea1b514c61b797035674a27457fea63a8972cd473eef058712474";
		pr7 = "899ca5442f546a0281bb40aa97ca6bbfd119449747574555357af9ed00cbb75a";
		ac7 = BitcoinUtils.getAccountOfKey(a7, pr7);

		ac8 = BitcoinUtils.getMultiAccountOfKey("2N2vMRNmBiSJQtu3wv2onGPE2museEg9nLw", 1,
				ListUtil.toList("020f36d7b035992140d1aec93cdbfd1780c5cb70a138394d42e296e94734a8846a",
						"03a7167a1b607ae82b9935c28d1795150668a60a41899acfdd2028282d63edb9dc",
						"0376ad68f86690a1f2c2e7aca2f5722c245fadceb9d9889e5ababde3534127d93b"),
				// 假设只拥有第三个公钥对应的私钥
				ListUtil.toList("", "998059439fa9a6591cae0e0ba863c1544384aefe4c90c1e98eec670ab55bfc8c",
						"7102ee9535259e129c392ee061b0e97ecd86d248fd99425a1aa4285ccaf651cc"));

		ac9 = BitcoinUtils.getAccountOfKey("tb1qdhgxl3vjcg9djhd30zykmz83x63nau4rtmahmd",
				"02e16db98d2012dc17dec062ccb898fb1c7afe6d278763eefdc2d5eae721e8b2df",
				"8aee29b56cab5c2fbb48a2e9e54382804f33ef7dbca039eb8a3dcab97ad2e47e");

		ac10 = BitcoinUtils.getMultiAccountOfKey("2Mv6KiSdZhde22KsFkRMGiVVebdwupS4UX1", 2,
				ListUtil.toList("0266ec44c371aefc0a0f42a0accfd34f005d0921ca02f0e1711747b801c28fada1",
						"0355b6efa12c7fcf479ab16578cb3bca07bdad3e224f38b3aec59874721dd44c5c",
						"03103cabc278c7f6ddcb72a95362cf43b1e660d4eafe0ec3ba89aa5ee914bdd88a"),
				ListUtil.toList("1dd474829a4ac9d3baad15637c67a9d79887031271ec0fd6067d5543837d0946",
						"b4dfe7d37e99962bc21678961940373e592fa7691d5e3a176ca26e06156b18fc", ""));

		ac11 = BitcoinUtils.getAccountOfKey("2N2dP9FtENAB8Dfh7TXyBK1znDGt5BE9BAT",
				"899ca5442f546a0281bb40aa97ca6bbfd119449747574555357af9ed00cbb75a");
	}

	/**
	 * 初始化 rpc节点
	 * @author lingting 2021-01-10 19:43
	 */
	@SneakyThrows
	static void init() {
		Map<String, String> headers = new HashMap<>();

		headers.put("Authorization", "Basic " + Base64.encode("omnicorerpc" + ":" + "5hMTZI9iBGFqKxsWfOUF"));

		// 这里使用的同事搭建的测试服节点
		client = JsonRpcClient.of("http://192.168.1.206:18332", headers);
		service = new BitcoinServiceImpl(new BitcoinProperties()
				// 广播交易
				.setBroadcastTransaction((raw, endpoints) -> {
					try {
						// 使用测试服节点进行广播, 没有找到其他api有测试服广播接口. 如果您知道, 可以的话,请告诉我, 谢谢
						// String hash = client.invoke("sendrawtransaction", String.class,
						// raw);
						// return PushTx.success(hash);
						System.out.println(raw);
						return PushTx.success("");
					}
					catch (Throwable throwable) {
						return new PushTx(throwable);
					}
				})
				// 获取未花费输出
				.setUnspent((address, endpoints) -> {
					try {
						return UnspentRes.of(endpoints, 6, address).toUnspentList();
					}
					catch (Exception e) {
						return Collections.emptyList();
					}
				})
				// 比特 节点
				.setEndpoints(BitcoinEndpoints.TEST));
	}

	public static List<Unspent> listUnspent(JsonRpcClient client, String address) {
		try {
			List<Map<String, Object>> list = client.invoke("listunspent", List.class, 6, 9999999,
					new String[] { address });

			List<Unspent> unspents = new ArrayList<>(list.size());

			for (Map<String, Object> m : list) {
				unspents.add(new Unspent()
						.setValue(new BigDecimal(Convert.toStr(m.get("amount")))
								.multiply(BigDecimal.TEN.pow(OmniContract.BTC.getDecimals())).toBigInteger())

						.setConfirmations(new BigInteger(Convert.toStr(m.get("confirmations"))))

						.setHash(Convert.toStr(m.get("txid")))

						.setOut(Convert.toLong(m.get("vout")))

						.setScript(Convert.toStr(m.get("scriptPubKey"))));
			}

			return unspents;
		}
		catch (Throwable throwable) {
			return Collections.emptyList();
		}
	}

	/**
	 * 本测试用例的交易已经在测试链广播成功, 可通过txId查询比对数据
	 * @author lingting 2021-03-19 16:45
	 */
	@Test
	@SneakyThrows
	public void btcMulti() {
		service = new BitcoinServiceImpl(new BitcoinProperties().setBroadcastTransaction((raw, endpoints) -> {
			try {
				// 不进行交易的广播
				return PushTx.success("");
			}
			catch (Throwable throwable) {
				return new PushTx(throwable);
			}
			// 获取未花费输出
		}).setUnspent((address, endpoints) -> {
			try {
				// 出于测试需要, 使用指定的未花费输出
				return new ArrayList<Unspent>() {
					{
						add(new Unspent().setValue(new BigInteger("1029719"))
								.setHash("2bde430e919273f2777b11a7cae9228ae8710129a6ee2ca8b46a6fd5e2f2591a").setOut(1L)
								.setConfirmations(new BigInteger("35688"))
								.setScript("a9141f396a0c4994cae1820224bb249a21de3ad4e73287"));
					}
				};
			}
			catch (Exception e) {
				return Collections.emptyList();
			}
			// 测试节点
		}).setEndpoints(BitcoinEndpoints.TEST));

		// 模拟两个人分别拥有私钥, 进行签名使用
		Account u1 = BitcoinUtils.getMultiAccountOfKey("2Mv6KiSdZhde22KsFkRMGiVVebdwupS4UX1", 2,
				ListUtil.toList("0266ec44c371aefc0a0f42a0accfd34f005d0921ca02f0e1711747b801c28fada1",
						"0355b6efa12c7fcf479ab16578cb3bca07bdad3e224f38b3aec59874721dd44c5c",
						"03103cabc278c7f6ddcb72a95362cf43b1e660d4eafe0ec3ba89aa5ee914bdd88a"),
				ListUtil.toList("1dd474829a4ac9d3baad15637c67a9d79887031271ec0fd6067d5543837d0946", "", ""));
		Account u2 = BitcoinUtils.getMultiAccountOfKey("2Mv6KiSdZhde22KsFkRMGiVVebdwupS4UX1", 2,
				ListUtil.toList("0266ec44c371aefc0a0f42a0accfd34f005d0921ca02f0e1711747b801c28fada1",
						"0355b6efa12c7fcf479ab16578cb3bca07bdad3e224f38b3aec59874721dd44c5c",
						"03103cabc278c7f6ddcb72a95362cf43b1e660d4eafe0ec3ba89aa5ee914bdd88a"),
				ListUtil.toList("", "b4dfe7d37e99962bc21678961940373e592fa7691d5e3a176ca26e06156b18fc", ""));
		/*
		 * 如上例所示, u1 u2 分别拥有不同的私钥
		 */

		BigDecimal value = new BigDecimal("0.001");
		// 1. u1 生成交易
		BitcoinTransactionGenerate ug1 = service.transactionGenerate(u1, a1, OmniContract.BTC, value,
				new TransferParams().setFee(Coin.valueOf(88)));

		AssertUtils.equals(ug1.getBitcoin().getTransaction().getTxId().toString(),
				"00179375fdc07017409ddfc0b67e19877325cf93b51f720971d5f76c1c2e163b", "u1生成的txId异常!");

		// 2. u1 签名交易
		ug1 = service.transactionSign(ug1);

		AssertUtils.equals(ug1.getBitcoin().getTransaction().getTxId().toString(),
				"2d1222330211c202f1502248af266141bf7a53b778d0f15c96ce4c08d6160df0", "u1签名后的txId异常!");

		AssertUtils.equals(ug1.getSignHex(),
				"01000000011a59f2e2d56f6ab4a82ceea6290171e88a22e9caa7117b77f27392910e43de2b01000000b40047304402204a4cd012d1f55ce2e67eea1c4ee79d02c9ce8bc4dcdc368c21020c746771b3d402206ca103eef6aa61798a3faa5f317f5ab83a36284b85ea89b6f5880ba4c31df7e1014c6952210266ec44c371aefc0a0f42a0accfd34f005d0921ca02f0e1711747b801c28fada1210355b6efa12c7fcf479ab16578cb3bca07bdad3e224f38b3aec59874721dd44c5c2103103cabc278c7f6ddcb72a95362cf43b1e660d4eafe0ec3ba89aa5ee914bdd88a53aeffffffff02a0860100000000001976a9141d2d4cbe20c9d587f52d49becc755157cdfc041788ac07e20d000000000017a9141f396a0c4994cae1820224bb249a21de3ad4e7328700000000",
				"u1签名异常!");

		// 3. u1 将 原始数据 发送给 u2
		String rawHex = ug1.getSignHex();
		// u2 生成交易
		BitcoinTransactionGenerate ug2 = BitcoinTransactionGenerate.ofBitcoinRaw(u2, np, rawHex);

		AssertUtils.equals(ug2.getBitcoin().getTransaction().getTxId().toString(),
				ug1.getBitcoin().getTransaction().getTxId().toString(), "根据rawHex生成的交易信息异常!");

		// u2 签名
		ug2 = service.transactionSign(ug2);

		AssertUtils.equals(ug2.getBitcoin().getTransaction().getTxId().toString(),
				"72344a82f9b99597a4d9bdb6bd1c54871e98dabff59fa15241b6c926f4fd03ca", "u2签名后的txId异常!");

		AssertUtils.equals(ug2.getSignHex(),
				"01000000011a59f2e2d56f6ab4a82ceea6290171e88a22e9caa7117b77f27392910e43de2b01000000fc0047304402204a4cd012d1f55ce2e67eea1c4ee79d02c9ce8bc4dcdc368c21020c746771b3d402206ca103eef6aa61798a3faa5f317f5ab83a36284b85ea89b6f5880ba4c31df7e1014730440220328e213dcb90dac79e1d72f2a13d9f6cb0ffb05dc96fd22870e8cc7554acb92502200296437ed6bbd84ed67f8a7761fe5b99ada7b2dd7f53172c46be57eecec9fd16014c6952210266ec44c371aefc0a0f42a0accfd34f005d0921ca02f0e1711747b801c28fada1210355b6efa12c7fcf479ab16578cb3bca07bdad3e224f38b3aec59874721dd44c5c2103103cabc278c7f6ddcb72a95362cf43b1e660d4eafe0ec3ba89aa5ee914bdd88a53aeffffffff02a0860100000000001976a9141d2d4cbe20c9d587f52d49becc755157cdfc041788ac07e20d000000000017a9141f396a0c4994cae1820224bb249a21de3ad4e7328700000000",
				"u2签名异常!");
	}

	@Test
	public void p2sh_p2wpkh() {

	}

	@Test
	@SneakyThrows
	public void btc() {
		init();

		// 使用浏览器查询余额, 节点的查询有问题, 没有找到测试类余额查询api
		System.out.println("a1 BTC 余额 0.06779068");
		System.out.println("a6 BTC 余额 0.03899058");
		System.out.println("a7 BTC 余额 0");

		BigDecimal value = new BigDecimal("0.001");
		// System.out.println("a1 向 a7 转 " + value.toPlainString() + " BTC");
		System.out.println("a1: " + a1);
		System.out.println("a5: " + a5);
		System.out.println("a7: " + a7);
		System.out.println("a8: " + ac8.getAddress());
		System.out.println("a9: " + ac9.getAddress());
		TransferParams params = new TransferParams().setSumFee(Coin.valueOf(546));
		TransferResult transfer = service.transfer(ac11, ac10.getAddress(), OmniContract.BTC, value);
		// TransferResult transfer = service.transfer(ac10, ac11.getAddress(),
		// OmniContract.BTC, value);
		// TransferResult transfer = service.transfer(ac1, ac8.getAddress(),
		// OmniContract.BTC, value);
		// TransferResult transfer = service.transfer(ac7, a1, OmniContract.BTC, value,
		// params);
		// TransferResult transfer = service.transfer(ac8, a1, OmniContract.BTC, value,
		// params);
		// TransferResult transfer = service.transfer(ac10, a1, OmniContract.BTC, value,
		// params);
		// TransferResult transfer = service.transfer(ac1, a6, OmniContract.BTC, value);
		// TransferResult transfer = service.transfer(ac5, a1, OmniContract.BTC, value,
		// params);
		// TransferResult transfer = service.transfer(ac7, a3, OmniContract.BTC, value,
		// params);
		// TransferResult transfer = service.transfer(ac1, ac9.getAddress(),
		// OmniContract.BTC, value , params);
		// TransferResult transfer = service.transfer(ac8, ac9.getAddress(),
		// OmniContract.BTC, value);
		// TransferResult transfer = service.transfer(ac9, ac1.getAddress(),
		// OmniContract.BTC, value, params);
		// TransferResult transfer = service.transfer(ac10, ac1.getAddress(),
		// OmniContract.BTC, value, params);
		// client.invokeObj("listunspent", 6, 9999999, new String[]{ac9.getAddress()})
		if (!transfer.getSuccess()) {
			System.out.println("转账失败");
			if (transfer.getException() != null) {
				transfer.getException().printStackTrace();
			}
			else {
				System.out.println("异常信息: " + transfer.getMessage());
			}
		}
		else {
			System.out.println("转账成功");
			System.out.println(transfer.getHash());
		}
	}

	@Test
	@SneakyThrows
	public void property() {
		init();
		// 通过资料查询到的的精度为 8
		System.out.println("omni 精度 " + service.getDecimalsByContract(OmniContract.OMNI));

		// 可通过 向 moneyqMan7uh8FqdCA2BV5yZ8qVrc9ikLP 转入 btc 来获取 omni 测试币
		// 没找到 Omni 的测试网接口, 所以使用 rpc 查询
		System.out.println(
				"a1 OMNI 余额: " + client.invokeObj("omni_getbalance", a1, Convert.toInt(OmniContract.OMNI.getHash())));

		System.out.println(
				"a5 OMNI 余额: " + client.invokeObj("omni_getbalance", a5, Convert.toInt(OmniContract.OMNI.getHash())));

		BigDecimal value = new BigDecimal("0.01");
		System.out.println("a1 向 a5 转 " + value.toPlainString() + " OMNI");
		TransferResult transfer = service.transfer(ac1, ac10.getAddress(), OmniContract.OMNI, value);

		if (!transfer.getSuccess()) {
			System.out.println("转账失败: " + JacksonUtils.toJson(transfer));
		}
		else {
			System.out.println("转账成功");
			System.out.println(transfer.getHash());
			System.out.println(client.invokeObj("omni_gettransaction", transfer.getHash()));
			System.out.println(client.invokeObj("omni_gettrade", transfer.getHash()));
		}
	}

}
