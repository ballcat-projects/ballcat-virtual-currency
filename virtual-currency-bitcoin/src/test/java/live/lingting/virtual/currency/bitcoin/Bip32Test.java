package live.lingting.virtual.currency.bitcoin;

import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.LegacyAddress;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.params.MainNetParams;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;
import live.lingting.virtual.currency.bitcoin.util.BitcoinUtils;
import live.lingting.virtual.currency.core.model.Account;
import live.lingting.virtual.currency.core.Mnemonic;
import live.lingting.virtual.currency.core.bip.Bip32;
import live.lingting.virtual.currency.core.bip.Bip44Constant;
import live.lingting.virtual.currency.core.bip.Bip49Constant;

/**
 * @author lingting 2021/2/7 13:38
 */
@Slf4j
public class Bip32Test {

	String mnemonicStr = "alter shoot teach paper shock retreat easy parade couch midnight novel stable";

	byte[] seedBytes = Hex.decode(
			"1dc650be7fac7917a70dca822aef8b350376398395082ebd41390f9551018abe22aa033ca02febe9cd341427e3628c0d7740c5aeabbabb82e44006f8b101a4d1");

	long time = 1612513022;

	@Test
	public void extPub() {
		MainNetParams np = MainNetParams.get();
		Mnemonic m = Mnemonic.of(mnemonicStr, seedBytes, "", 0);
		Bip32 rootBip = m.getBip();
		String path = "m/44'/0'/0'/0";
		Bip32 extBip = rootBip.getBipByPath(path);
		DeterministicKey extKey = extBip.getKey();
		System.out.println(extKey.getPublicKeyAsHex());
		System.out.println(extKey.getPrivateKeyAsHex());
		System.out.println(extBip.getExtPrivate(np));
		System.out.println(extBip.getExtPublicKey(np));
		System.out.println("-----------------------------");
		DeterministicKey path1 = extBip.getKeyByPath("0");
		Account account1 = BitcoinUtils.createLegacyAddress(np, path1);
		System.out.println(account1.getAddress());

		System.out.println("-----------------------------");
		// Bip bip =
		// Bip.create("xprv9zusQfxwpgYTsTFPAAZSTJNu7bJ45tsB8NJB9qtWT2K5zX4Y8LBvRXZjtwiA3CNZ1XzfiqrPckZk4Q6ByPdFhgQP9Q59zb9wkxP1JZb5p3h",
		// np);
		Bip32 bip = Bip32.create(
				"xpub6DuDpBVqf46m5wKrGC6SpSKdfd8YVMb2VbDmxEJ81Mr4sKPgfsWAyKtDkDTyWqSFsfGNe9ga7coxPs3EMW5feGDZZsTuE3FTJjD7ALGMnBz",
				np);
		DeterministicKey path2 = bip.getKeyByPath("0");
		// Account account2 = BitcoinUtil.createLegacyAddress(np, path2);
		System.out.println(LegacyAddress.fromKey(np, path2).toString());
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
		System.out.println("私钥WIF: " + BitcoinUtils.hexToWif(np, Hex.decode(legacy.getPrivateKeyAsHex()), false));
		System.out.println("私钥WIF压缩: " + BitcoinUtils.hexToWif(np, Hex.decode(legacy.getPrivateKeyAsHex()), true));
		System.out.println("普通地址: " + BitcoinUtils.createLegacyAddress(np, legacy).getAddress());

		DeterministicKey segwit = mnemonic.getKeyByPathAndIndexFromRoot(Bip49Constant.PATH_BITCOIN, 0);
		System.out.println("-----------bitcoin隔离见证地址-----------");
		System.out.println("公钥: " + segwit.getPublicKeyAsHex());
		System.out.println("私钥: " + segwit.getPrivateKeyAsHex());
		System.out.println("私钥WIF: " + BitcoinUtils.hexToWif(np, Hex.decode(segwit.getPrivateKeyAsHex()), false));
		System.out.println("私钥WIF压缩: " + BitcoinUtils.hexToWif(np, Hex.decode(segwit.getPrivateKeyAsHex()), true));
		System.out.println("隔离见证地址: " + BitcoinUtils.createSegwitAddress(np, segwit).getAddress());
		System.out.println("兼容性隔离见证地址: " + BitcoinUtils.createMultiSegwitAddress(np, segwit).getAddress());

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
		System.out.println("私钥WIF: " + BitcoinUtils.hexToWif(np, Hex.decode(bitcoin.getPrivateKeyAsHex()), false));
		System.out.println("私钥WIF压缩: " + BitcoinUtils.hexToWif(np, Hex.decode(bitcoin.getPrivateKeyAsHex()), true));
		System.out.println("普通地址: " + BitcoinUtils.createLegacyAddress(np, bitcoin).getAddress());
		System.out.println("隔离见证地址: " + BitcoinUtils.createSegwitAddress(np, bitcoin).getAddress());
		System.out.println("兼容性隔离见证地址: " + BitcoinUtils.createMultiSegwitAddress(np, bitcoin).getAddress());

	}

}
