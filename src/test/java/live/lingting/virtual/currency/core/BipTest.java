package live.lingting.virtual.currency.core;

import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.params.MainNetParams;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;
import live.lingting.virtual.currency.Account;
import live.lingting.virtual.currency.bip.Bip;
import live.lingting.virtual.currency.bip.Bip44Constant;
import live.lingting.virtual.currency.util.BitcoinUtil;
import live.lingting.virtual.currency.util.EtherscanUtil;
import live.lingting.virtual.currency.util.TronscanUtil;

/**
 * @author lingting 2021/2/7 13:38
 */
public class BipTest {

	String mnemonicStr = "alter shoot teach paper shock retreat easy parade couch midnight novel stable";

	byte[] seedBytes = Hex.decode(
			"1dc650be7fac7917a70dca822aef8b350376398395082ebd41390f9551018abe22aa033ca02febe9cd341427e3628c0d7740c5aeabbabb82e44006f8b101a4d1");

	long time = 1612513022;

	/**
	 * Bip 使用演示. 包括相对路径和绝对路径生成公私钥
	 */
	@Test
	public void path() {
		Mnemonic 老板拥有的助记词 = Mnemonic.of(mnemonicStr, seedBytes, "", 0);
		Bip 老板节点 = 老板拥有的助记词.getBip();

		Bip 研发节点 = 老板节点.getBipByPath("m/44'/0'/0'");
		Bip 研发1节点 = 研发节点.getBipByPath("0");
		Bip 研发2节点 = 研发节点.getBipByPath("1");

		System.out.println("研发1第一个地址公钥: " + 研发1节点.getKeyByPath("0").getPublicKeyAsHex());
		System.out.println("研发1第一个地址私钥: " + 研发1节点.getKeyByPath("0").getPrivateKeyAsHex());
		System.out.println("研发2第一个地址公钥: " + 研发2节点.getKeyByPath("0").getPublicKeyAsHex());
		System.out.println("研发2第一个地址私钥: " + 研发2节点.getKeyByPath("0").getPrivateKeyAsHex());

		Bip 运营节点 = 老板节点.getBipByPath("m/44'/0'/1'");
		Bip 运营1节点 = 运营节点.getBipByPath("0");
		Bip 运营2节点 = 运营节点.getBipByPath("1");

		System.out.println("运营1第一个地址公钥: " + 运营1节点.getKeyByPath("0").getPublicKeyAsHex());
		System.out.println("运营1第一个地址私钥: " + 运营1节点.getKeyByPath("0").getPrivateKeyAsHex());
		System.out.println("运营2第一个地址公钥: " + 运营2节点.getKeyByPath("0").getPublicKeyAsHex());
		System.out.println("运营2第一个地址私钥: " + 运营2节点.getKeyByPath("0").getPrivateKeyAsHex());

		System.out.println("运营1叛变了, 老板要把钱全部拿出来");
		Bip 老板生成的运营1节点 = 老板节点.getBipByPath("m/44'/0'/1'/0");
		System.out.println("老板获取到的运营1第一个地址公钥: " + 老板生成的运营1节点.getKeyByPath("0").getPublicKeyAsHex());
		System.out.println("老板获取到的运营1第一个地址私钥: " + 老板生成的运营1节点.getKeyByPath("0").getPrivateKeyAsHex());
	}

	@Test
	public void standard() {
		Mnemonic mnemonic = Mnemonic.of(mnemonicStr, seedBytes, "", 0);
		/*
		 * 使用标准路径生成地址的优缺点
		 *
		 * 优点1: 不用担心忘记路径
		 *
		 * 优点2: 可以很方便的使用主流app, 网站 通过助记词管理地址
		 *
		 * 缺点1: 助记词泄露万事皆休
		 */

		MainNetParams np = MainNetParams.get();
		// bitcoin 地址
		DeterministicKey legacy = mnemonic.getKeyByPathAndIndexFromRoot(Bip44Constant.PATH_BITCOIN, 0);
		System.out.println("-----------bitcoin普通地址-----------");
		System.out.println("公钥: " + legacy.getPublicKeyAsHex());
		System.out.println("私钥: " + legacy.getPrivateKeyAsHex());
		System.out.println("私钥WIF: " + BitcoinUtil.hexToWif(np, Hex.decode(legacy.getPrivateKeyAsHex()), false));
		System.out.println("私钥WIF压缩: " + BitcoinUtil.hexToWif(np, Hex.decode(legacy.getPrivateKeyAsHex()), true));
		System.out.println("普通地址: " + BitcoinUtil.createLegacyAddress(np, legacy).getAddress());

		DeterministicKey segwit = mnemonic.getKeyByPathAndIndexFromRoot(Bip44Constant.PATH_BITCOIN_IMTOKEN, 0);
		System.out.println("-----------bitcoin隔离见证地址-----------");
		System.out.println("公钥: " + segwit.getPublicKeyAsHex());
		System.out.println("私钥: " + segwit.getPrivateKeyAsHex());
		System.out.println("私钥WIF: " + BitcoinUtil.hexToWif(np, Hex.decode(segwit.getPrivateKeyAsHex()), false));
		System.out.println("私钥WIF压缩: " + BitcoinUtil.hexToWif(np, Hex.decode(segwit.getPrivateKeyAsHex()), true));
		System.out.println("隔离见证地址: " + BitcoinUtil.createSegwitAddress(np, segwit).getAddress());
		System.out.println("兼容性隔离见证地址: " + BitcoinUtil.createMultiSegwitAddress(np, segwit).getAddress());

		// etherscan 地址
		DeterministicKey etherscan = mnemonic.getKeyByPathAndIndexFromRoot(Bip44Constant.PATH_ETHERSCAN, 0);
		System.out.println("-----------etherscan地址-----------");
		System.out.println("公钥: " + etherscan.getPublicKeyAsHex());
		System.out.println("私钥: " + etherscan.getPrivateKeyAsHex());
		Account etherscanAccount = EtherscanUtil.createAccount(etherscan);
		System.out.println("地址: " + etherscanAccount.getAddress());

		// tronscan 地址
		DeterministicKey tronscan = mnemonic.getKeyByPathAndIndexFromRoot(Bip44Constant.PATH_TRONSCAN, 0);
		System.out.println("-----------tronscan地址-----------");
		System.out.println("公钥: " + tronscan.getPublicKeyAsHex());
		System.out.println("私钥: " + tronscan.getPrivateKeyAsHex());
		Account tronscanAccount = TronscanUtil.createAccount(tronscan);
		System.out.println("地址: " + tronscanAccount.getAddress());

	}

	@Test
	public void nonStandard() {
		Mnemonic mnemonic = Mnemonic.of(mnemonicStr, seedBytes, "", 0);
		/*
		 * 使用非标准路径生成地址的优缺点
		 *
		 * 优点1: 即便助记词泄露, 也不会立刻万事皆休, 一个一个路径的测试需要时间, 这段时间已经可以让你转移资产(如果你意识到了助记词泄露的话)
		 *
		 *
		 * 缺点1: 忘记路径的话, 需要一个一个试, 比较麻烦
		 *
		 * 缺点2: 如果目前使用的 app. 网站 不支持 自定义路径的话, 那么管理地址会比较麻烦
		 */
		MainNetParams np = MainNetParams.get();
		// 自定义路径. 隔离见证地址和普通地址使用同一个路径, 也可以使用不同的, 这里不举例
		String bitcoinPath = "m/92'/55'/0'/0";
		// bitcoin 地址
		DeterministicKey bitcoin = mnemonic.getKeyByPathAndIndexFromRoot(bitcoinPath, 0);
		System.out.println("-----------bitcoin普通地址-----------");
		System.out.println("公钥: " + bitcoin.getPublicKeyAsHex());
		System.out.println("私钥: " + bitcoin.getPrivateKeyAsHex());
		System.out.println("私钥WIF: " + BitcoinUtil.hexToWif(np, Hex.decode(bitcoin.getPrivateKeyAsHex()), false));
		System.out.println("私钥WIF压缩: " + BitcoinUtil.hexToWif(np, Hex.decode(bitcoin.getPrivateKeyAsHex()), true));
		System.out.println("普通地址: " + BitcoinUtil.createLegacyAddress(np, bitcoin).getAddress());
		System.out.println("隔离见证地址: " + BitcoinUtil.createSegwitAddress(np, bitcoin).getAddress());
		System.out.println("兼容性隔离见证地址: " + BitcoinUtil.createMultiSegwitAddress(np, bitcoin).getAddress());

		// 自定义路径
		String etherscanPath = "m/32'/233'/0'/0";
		// etherscan 地址
		DeterministicKey etherscan = mnemonic.getKeyByPathAndIndexFromRoot(etherscanPath, 0);
		System.out.println("-----------etherscan地址-----------");
		System.out.println("公钥: " + etherscan.getPublicKeyAsHex());
		System.out.println("私钥: " + etherscan.getPrivateKeyAsHex());
		Account etherscanAccount = EtherscanUtil.createAccount(etherscan);
		System.out.println("地址: " + etherscanAccount.getAddress());

		// 自定义路径
		String tronscanPath = "m/25'/2233'/0'/0";
		// tronscan 地址
		DeterministicKey tronscan = mnemonic.getKeyByPathAndIndexFromRoot(tronscanPath, 0);
		System.out.println("-----------tronscan地址-----------");
		System.out.println("公钥: " + tronscan.getPublicKeyAsHex());
		System.out.println("私钥: " + tronscan.getPrivateKeyAsHex());
		Account tronscanAccount = TronscanUtil.createAccount(tronscan);
		System.out.println("地址: " + tronscanAccount.getAddress());
	}

}
