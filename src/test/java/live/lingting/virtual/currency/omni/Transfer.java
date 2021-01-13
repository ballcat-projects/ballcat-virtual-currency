package live.lingting.virtual.currency.omni;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.convert.Convert;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.params.TestNet3Params;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;
import live.lingting.virtual.currency.Account;
import live.lingting.virtual.currency.TransferResult;
import live.lingting.virtual.currency.contract.OmniContract;
import live.lingting.virtual.currency.core.JsonRpcClient;
import live.lingting.virtual.currency.endpoints.BitcoinEndpoints;
import live.lingting.virtual.currency.endpoints.OmniEndpoints;
import live.lingting.virtual.currency.properties.OmniProperties;
import live.lingting.virtual.currency.service.PlatformService;
import live.lingting.virtual.currency.service.impl.BtcOmniServiceImpl;
import live.lingting.virtual.currency.util.BitcoinUtil;
import live.lingting.virtual.currency.util.JsonUtil;

/**
 * @author lingting 2021/1/10 15:54
 */
public class Transfer {

	private static PlatformService service;

	private static JsonRpcClient client;

	/**
	 * 配置网络环境为测试服
	 */
	private static final TestNet3Params np = TestNet3Params.get();

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

	private static final String a6;

	private static final String pb6;

	private static final String pr6;

	private static final Account ac6;

	private static final String a7;

	private static final String pb7;

	private static final String pr7;

	private static final Account ac7;

	private static final Account ac8;

	private static final Account ac9;

	static {
		a1 = "miBEA6o6nZcaLZebR1dsDv4AMHRwJk1mbi";
		pb1 = "03a5be350852bb09e24edc83f8e02070a74597a8b775af46e8efbef394ae4fa98e";
		pr1 = "27b5bf6853c1730c20c152d67d9f4f85b20b674267f621bd1c88d177b2d56d83";
		ac1 = BitcoinUtil.getAccountOfKey(a1, pr1);

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
		ac5 = BitcoinUtil.getAccountOfKey(a5, pr5);

		a6 = "2NGKaZCkwpUMW8yhA9gHzCBfXJba5WZwRMS";
		pb6 = "025ff09849923024c7d67e66a69a61df6bc6e0dbb98a916a292b306a3b6bc49cb6";
		pr6 = "b078396071e5c344adb1143b2ab43349782c41f34dba1a16610dd9ce551134ea";
		ac6 = BitcoinUtil.getAccountOfKey(a6, pr6);

		a7 = "miV1sbmLM9fptRfWD5o8hs5L6ucUeJzmp5";
		pb7 = "03ca65ac405e0ea1b514c61b797035674a27457fea63a8972cd473eef058712474";
		pr7 = "899ca5442f546a0281bb40aa97ca6bbfd119449747574555357af9ed00cbb75a";
		ac7 = BitcoinUtil.getAccountOfKey(a7, pr7);

		ac8 = BitcoinUtil.getMultiAccountOfKey("2N2vMRNmBiSJQtu3wv2onGPE2museEg9nLw", 1,
				ListUtil.toList("020f36d7b035992140d1aec93cdbfd1780c5cb70a138394d42e296e94734a8846a",
						"03a7167a1b607ae82b9935c28d1795150668a60a41899acfdd2028282d63edb9dc",
						"0376ad68f86690a1f2c2e7aca2f5722c245fadceb9d9889e5ababde3534127d93b"),
				// 假设只拥有第三个公钥对应的私钥
				ListUtil.toList("", "", "7102ee9535259e129c392ee061b0e97ecd86d248fd99425a1aa4285ccaf651cc"));

		ac9 = BitcoinUtil.getAccountOfKey("tb1qdhgxl3vjcg9djhd30zykmz83x63nau4rtmahmd",
				"02e16db98d2012dc17dec062ccb898fb1c7afe6d278763eefdc2d5eae721e8b2df",
				"8aee29b56cab5c2fbb48a2e9e54382804f33ef7dbca039eb8a3dcab97ad2e47e");
	}

	@Test
	public void createMultiAddress() throws Throwable {
		init();
		List<ECKey> keys = ListUtil.toList(
				// 020f36d7b035992140d1aec93cdbfd1780c5cb70a138394d42e296e94734a8846a
				ECKey.fromPrivate(Hex.decode("395222b75a102bdabaeb3f6d616ad7972642e5bfe529e3052814ef340e931fb3")),
				// 03a7167a1b607ae82b9935c28d1795150668a60a41899acfdd2028282d63edb9dc
				ECKey.fromPrivate(Hex.decode("998059439fa9a6591cae0e0ba863c1544384aefe4c90c1e98eec670ab55bfc8c")),
				// 0376ad68f86690a1f2c2e7aca2f5722c245fadceb9d9889e5ababde3534127d93b
				ECKey.fromPrivate(Hex.decode("7102ee9535259e129c392ee061b0e97ecd86d248fd99425a1aa4285ccaf651cc")));
		Account multiAddress = BitcoinUtil.createMultiAddress(TestNet3Params.get(), 1, keys);
		System.out.println(multiAddress.getAddress());
		System.out.println("------------------");
		multiAddress.getPublicKeyArray().forEach(System.out::println);
		System.out.println("------------------");
		multiAddress.getPrivateKeyArray().forEach(System.out::println);

		System.out.println("--------------------");
		Account segwitAddress = BitcoinUtil.createSegwitAddress(TestNet3Params.get());
		System.out.println(segwitAddress.getAddress());
		System.out.println(segwitAddress.getPublicKey());
		System.out.println(segwitAddress.getPrivateKey());
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
		service = new BtcOmniServiceImpl(new OmniProperties()
				// 广播交易
				.setBroadcastTransaction((raw, endpoints) -> {
					try {
						// 使用测试服节点进行广播, 没有找到其他api有测试服广播接口. 如果您知道, 可以的话,请告诉我, 谢谢
						String hash = client.invoke("sendrawtransaction", String.class, raw);
						return PushTx.success(hash);
						// return PushTx.success("");
					}
					catch (Throwable throwable) {
						return new PushTx(throwable);
					}
				})
				// 网络
				.setNp(TestNet3Params.get())
				// omni 节点
				.setOmniEndpoints(OmniEndpoints.MAINNET)
				// 比特 节点
				.setBitcoinEndpoints(BitcoinEndpoints.TEST));
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
		System.out.println("a6: " + a6);
		System.out.println("a7: " + a7);
		System.out.println("a8: " + ac8.getAddress());
		System.out.println("a9: " + ac9.getAddress());
		// TransferResult transfer = service.transfer(ac1, a7, OmniContract.BTC, value);
		// TransferResult transfer = service.transfer(ac1, ac8.getAddress(),
		// OmniContract.BTC, value);
		// TransferResult transfer = service.transfer(ac6, a1, OmniContract.BTC, value);
		// TransferResult transfer = service.transfer(ac8, a1, OmniContract.BTC, value);
		// TransferResult transfer = service.transfer(ac1, a6, OmniContract.BTC, value);
		// TransferResult transfer = service.transfer(ac5, a1, OmniContract.BTC, value);
		// TransferResult transfer = service.transfer(ac7, a3, OmniContract.BTC, value);
		// TransferResult transfer = service.transfer(ac1, ac9.getAddress(),
		// OmniContract.BTC, value);
		//TransferResult transfer = service.transfer(ac8, ac9.getAddress(), OmniContract.BTC, value);
		TransferResult transfer = service.transfer(ac9, a3, OmniContract.BTC, value);

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
		TransferResult transfer = service.transfer(ac1, a5, OmniContract.OMNI, value);

		if (!transfer.getSuccess()) {
			System.out.println("转账失败: " + JsonUtil.toJson(transfer));
		}
		else {
			System.out.println("转账成功");
			System.out.println(transfer.getHash());
			System.out.println(client.invokeObj("omni_gettransaction", transfer.getHash()));
		}
	}

}
