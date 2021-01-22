package live.lingting.virtual.currency.omni;

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
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.LegacyAddress;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.SegwitAddress;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;
import live.lingting.virtual.currency.Account;
import live.lingting.virtual.currency.TransactionGenerate;
import live.lingting.virtual.currency.TransferParams;
import live.lingting.virtual.currency.TransferResult;
import live.lingting.virtual.currency.bitcoin.Unspent;
import live.lingting.virtual.currency.bitcoin.UnspentRes;
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
	private static NetworkParameters np = TestNet3Params.get();

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

		a7 = "miV1sbmLM9fptRfWD5o8hs5L6ucUeJzmp5";
		pb7 = "03ca65ac405e0ea1b514c61b797035674a27457fea63a8972cd473eef058712474";
		pr7 = "899ca5442f546a0281bb40aa97ca6bbfd119449747574555357af9ed00cbb75a";
		ac7 = BitcoinUtil.getAccountOfKey(a7, pr7);

		ac8 = BitcoinUtil.getMultiAccountOfKey("2N2vMRNmBiSJQtu3wv2onGPE2museEg9nLw", 1,
				ListUtil.toList("020f36d7b035992140d1aec93cdbfd1780c5cb70a138394d42e296e94734a8846a",
						"03a7167a1b607ae82b9935c28d1795150668a60a41899acfdd2028282d63edb9dc",
						"0376ad68f86690a1f2c2e7aca2f5722c245fadceb9d9889e5ababde3534127d93b"),
				// 假设只拥有第三个公钥对应的私钥
				ListUtil.toList("", "998059439fa9a6591cae0e0ba863c1544384aefe4c90c1e98eec670ab55bfc8c",
						"7102ee9535259e129c392ee061b0e97ecd86d248fd99425a1aa4285ccaf651cc"));

		ac9 = BitcoinUtil.getAccountOfKey("tb1qdhgxl3vjcg9djhd30zykmz83x63nau4rtmahmd",
				"02e16db98d2012dc17dec062ccb898fb1c7afe6d278763eefdc2d5eae721e8b2df",
				"8aee29b56cab5c2fbb48a2e9e54382804f33ef7dbca039eb8a3dcab97ad2e47e");

		ac10 = BitcoinUtil.getMultiAccountOfKey("2Mv6KiSdZhde22KsFkRMGiVVebdwupS4UX1", 2,
				ListUtil.toList("0266ec44c371aefc0a0f42a0accfd34f005d0921ca02f0e1711747b801c28fada1",
						"0355b6efa12c7fcf479ab16578cb3bca07bdad3e224f38b3aec59874721dd44c5c",
						"03103cabc278c7f6ddcb72a95362cf43b1e660d4eafe0ec3ba89aa5ee914bdd88a"),
				ListUtil.toList("1dd474829a4ac9d3baad15637c67a9d79887031271ec0fd6067d5543837d0946",
						"b4dfe7d37e99962bc21678961940373e592fa7691d5e3a176ca26e06156b18fc", ""));

		ac11 = BitcoinUtil.getAccountOfKey("2N2dP9FtENAB8Dfh7TXyBK1znDGt5BE9BAT",
				"899ca5442f546a0281bb40aa97ca6bbfd119449747574555357af9ed00cbb75a");
	}

	/**
	 * 创建兼容性的隔离见证地址
	 */
	@Test
	@SneakyThrows
	public void createMultiSegwitAddress() {
		init();
		ECKey ecKey = ECKey.fromPrivate(Hex.decode(ac7.getPrivateKey()));
		System.out.println("公钥: " + ecKey.getPublicKeyAsHex());
		System.out.println("私钥: " + ecKey.getPrivateKeyAsHex());
		System.out.println("私钥-WIF: " + ecKey.getPrivateKeyAsWiF(np));

		LegacyAddress legacyAddress = LegacyAddress.fromKey(np, ecKey);
		System.out.println("普通地址: " + legacyAddress.toString());
		SegwitAddress segwitAddress = SegwitAddress.fromKey(np, ecKey);
		System.out.println("隔离见证地址: " + segwitAddress.toString());

		System.out.println("兼容性隔离见证地址: " + BitcoinUtil.createMultiSegwitAddress(np, ecKey));
		System.out.println("-------------------");
		ecKey = ECKey.fromPrivate(Hex.decode("9a9a6539856be209b8ea2adbd155c0919646d108515b60b7b13d6a79f1ae5174"));
		System.out.println("兼容性隔离见证地址: " + BitcoinUtil.createMultiSegwitAddress(np, ecKey));
	}

	@Test
	public void createMultiAddress() throws Throwable {
		init();
		client.invokeObj("omni_getallbalancesforid", 1);
		System.out.println("----------------------------------------------");
		System.out.println("2Mv6KiSdZhde22KsFkRMGiVVebdwupS4UX1");
		System.out.println(client.invokeObj("omni_getbalance", "2Mv6KiSdZhde22KsFkRMGiVVebdwupS4UX1", 1));
		System.out.println(client.invokeObj("omni_getbalance", "2Mv6KiSdZhde22KsFkRMGiVVebdwupS4UX1", 2));
		System.out.println("----------------------------------------------");
		System.out.println("tb1qdhgxl3vjcg9djhd30zykmz83x63nau4rtmahmd");
		System.out.println(client.invokeObj("omni_getbalance", "tb1qdhgxl3vjcg9djhd30zykmz83x63nau4rtmahmd", 1));
		System.out.println(client.invokeObj("omni_getbalance", "tb1qdhgxl3vjcg9djhd30zykmz83x63nau4rtmahmd", 2));
		System.out.println("----------------------------------------------");
		System.out.println("2Mw3TeWtsJwJ6C7WE8cpjMhS2X4cWk87NLC");
		System.out.println(client.invokeObj("omni_getbalance", "2Mw3TeWtsJwJ6C7WE8cpjMhS2X4cWk87NLC", 1));
		System.out.println(client.invokeObj("omni_getbalance", "2Mw3TeWtsJwJ6C7WE8cpjMhS2X4cWk87NLC", 2));

		System.out.println("----------------------------------------------");
		System.out.println("miBEA6o6nZcaLZebR1dsDv4AMHRwJk1mbi");
		System.out.println(client.invokeObj("omni_getbalance", "miBEA6o6nZcaLZebR1dsDv4AMHRwJk1mbi", 1));
		System.out.println(client.invokeObj("omni_getbalance", "miBEA6o6nZcaLZebR1dsDv4AMHRwJk1mbi", 2));

		System.out.println("----------------------------------------------");
		System.out.println("2N2vMRNmBiSJQtu3wv2onGPE2museEg9nLw");
		System.out.println(client.invokeObj("omni_getbalance", "2N2vMRNmBiSJQtu3wv2onGPE2museEg9nLw", 1));
		System.out.println(client.invokeObj("omni_getbalance", "2N2vMRNmBiSJQtu3wv2onGPE2museEg9nLw", 2));

		client.invokeObj("omni_gettradehistoryforaddress", a1);
		client.invokeObj("omni_gettrade", "6c424490ddf78903c79117aec800556d4a2daf7b7b4a936210902804fff4ab70");
		client.invokeObj("omni_gettrade", "def1ea75c0a263c8d8fbc9a1e67a20c25f71a89cc0273ce93267d55a64d0ba93");
		client.invokeObj("omni_gettrade", "f884404c0ebd199152b727c720e39bf4756027ced7a066f22e6d40cc46654572");
		client.invokeObj("omni_gettrade", "790d15d045e5712ba2f203b1e55bf35ac6cbb9ad2521e1140c95db804cfb7559");
		client.invokeObj("omni_gettrade", "5ba9d08a06eb2c0e98c01d34a4517ab711058e8bafee71b9f938a7f0d23df33a");
		client.invokeObj("omni_gettrade", "0da0ca259b5ddb3da6b9ccde5dd4a0fc36fe9a9d54f1b8472502a137cf0044a8");

		List<ECKey> keys = ListUtil.toList(
				// 020f36d7b035992140d1aec93cdbfd1780c5cb70a138394d42e296e94734a8846a
				ECKey.fromPrivate(Hex.decode("395222b75a102bdabaeb3f6d616ad7972642e5bfe529e3052814ef340e931fb3")),
				// 03a7167a1b607ae82b9935c28d1795150668a60a41899acfdd2028282d63edb9dc
				ECKey.fromPrivate(Hex.decode("998059439fa9a6591cae0e0ba863c1544384aefe4c90c1e98eec670ab55bfc8c")),
				// 0376ad68f86690a1f2c2e7aca2f5722c245fadceb9d9889e5ababde3534127d93b
				ECKey.fromPrivate(Hex.decode("7102ee9535259e129c392ee061b0e97ecd86d248fd99425a1aa4285ccaf651cc")));
		Account multiAddress = BitcoinUtil.createMultiAddress(MainNetParams.get(), 1, keys);
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

		System.out.println("-------------------");
		keys = ListUtil.toList(
				// 0266ec44c371aefc0a0f42a0accfd34f005d0921ca02f0e1711747b801c28fada1
				ECKey.fromPrivate(Hex.decode("1dd474829a4ac9d3baad15637c67a9d79887031271ec0fd6067d5543837d0946")),
				// 0355b6efa12c7fcf479ab16578cb3bca07bdad3e224f38b3aec59874721dd44c5c
				ECKey.fromPrivate(Hex.decode("b4dfe7d37e99962bc21678961940373e592fa7691d5e3a176ca26e06156b18fc")),
				// 03103cabc278c7f6ddcb72a95362cf43b1e660d4eafe0ec3ba89aa5ee914bdd88a
				ECKey.fromPrivate(Hex.decode("84f5a909a1d7ec9bde9f1930ca4ab8b8cb551920377ce940a6e50d1b3f906d17")));
		multiAddress = BitcoinUtil.createMultiAddress(TestNet3Params.get(), 2, keys);
		// 2Mv6KiSdZhde22KsFkRMGiVVebdwupS4UX1
		System.out.println(multiAddress.getAddress());
		System.out.println("------------------");
		multiAddress.getPublicKeyArray().forEach(System.out::println);
		System.out.println("------------------");
		multiAddress.getPrivateKeyArray().forEach(System.out::println);
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
					if (address.startsWith("t")) {
						return listUnspent(client, address);
					}

					try {
						return Unspent.of(UnspentRes.of(endpoints, 6, address));
					}
					catch (Exception e) {
						return Collections.emptyList();
					}
				})
				// 网络
				.setNp(TestNet3Params.get())
				// omni 节点
				.setOmniEndpoints(OmniEndpoints.MAINNET)
				// 比特 节点
				.setBitcoinEndpoints(BitcoinEndpoints.TEST));
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

	@Test
	@SneakyThrows
	public void btcMulti() {
		init();
		// 模拟两个人分别拥有私钥, 进行签名使用
		Account u1 = BitcoinUtil.getMultiAccountOfKey("2Mv6KiSdZhde22KsFkRMGiVVebdwupS4UX1", 2,
				ListUtil.toList("0266ec44c371aefc0a0f42a0accfd34f005d0921ca02f0e1711747b801c28fada1",
						"0355b6efa12c7fcf479ab16578cb3bca07bdad3e224f38b3aec59874721dd44c5c",
						"03103cabc278c7f6ddcb72a95362cf43b1e660d4eafe0ec3ba89aa5ee914bdd88a"),
				ListUtil.toList("1dd474829a4ac9d3baad15637c67a9d79887031271ec0fd6067d5543837d0946", "", ""));
		Account u2 = BitcoinUtil.getMultiAccountOfKey("2Mv6KiSdZhde22KsFkRMGiVVebdwupS4UX1", 2,
				ListUtil.toList("0266ec44c371aefc0a0f42a0accfd34f005d0921ca02f0e1711747b801c28fada1",
						"0355b6efa12c7fcf479ab16578cb3bca07bdad3e224f38b3aec59874721dd44c5c",
						"03103cabc278c7f6ddcb72a95362cf43b1e660d4eafe0ec3ba89aa5ee914bdd88a"),
				ListUtil.toList("", "b4dfe7d37e99962bc21678961940373e592fa7691d5e3a176ca26e06156b18fc", ""));
		/*
		 * 如上例所示, u1 u2 分别拥有不同的私钥
		 */

		BigDecimal value = new BigDecimal("0.001");
		// 1. u1 生成交易
		TransactionGenerate ug1 = service.transactionGenerate(u1, a1, OmniContract.BTC, value, TransferParams.empty());
		// 2. u1 签名交易
		ug1 = service.transactionSign(ug1);
		// 3. u1 将 原始数据 发送给 u2
		String rawHex = ug1.getSignHex();
		// u2 生成交易
		TransactionGenerate ug2 = TransactionGenerate.ofBitcoinRaw(u2, np, rawHex);
		// u2 签名
		ug2 = service.transactionSign(ug2);
		// 广播
		System.out.println(ug2.getSignHex());
	}

	@Test
	@SneakyThrows
	public void testP2shP2wpkh() {
		String raw = "0100000001db6b1b20aa0fd7b23880be2ecbd4a98130974cf4748fb66092ac4d3ceb1a54770100000000feffffff02b8b4eb0b000000001976a914a457b684d7f0d539a46a45bbc043f35b6d7d7a7ca4ea7ca4ea7a4a7a4a4a7a4a1a1a1a1a1a8a1a8a1a1a1a1a8a8a1a1a1a1a1a0b3b4a6a8a8a7a8a7b1a7b3b3b1b1b1b1b1b0b1b0b0b0b0b0b0b0b0b0b1b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0";

		Transaction tx = new Transaction(np, Hex.decode(raw));

		System.out.println(tx.getTxId().toString());
	}

	@Test
	@SneakyThrows
	public void btc() {
		init();

		// 使用浏览器查询余额, 节点的查询有问题, 没有找到测试类余额查询api
		System.out.println("a1 BTC 余额 0.06779068");
		System.out.println("a6 BTC 余额 0.03899058");
		System.out.println("a7 BTC 余额 0");

		BigDecimal value = new BigDecimal("0.01");
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
			System.out.println("转账失败: " + JsonUtil.toJson(transfer));
		}
		else {
			System.out.println("转账成功");
			System.out.println(transfer.getHash());
			System.out.println(client.invokeObj("omni_gettransaction", transfer.getHash()));
			System.out.println(client.invokeObj("omni_gettrade", transfer.getHash()));
		}
	}

}
