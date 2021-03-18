package live.lingting.virtual.currency.tronscan;

import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.params.MainNetParams;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;
import live.lingting.virtual.currency.core.Mnemonic;
import live.lingting.virtual.currency.core.bip.Bip32;
import live.lingting.virtual.currency.core.bip.Bip44Constant;
import live.lingting.virtual.currency.core.model.Account;
import live.lingting.virtual.currency.core.util.AssertUtils;
import live.lingting.virtual.currency.tronscan.util.TronscanUtils;

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
		String extPath = "m/44'/0'/0'/0";
		Bip32 extBip = rootBip.getBipByPath(extPath);

		AssertUtils.equals(extBip.getExtPublicKey(np),
				"xpub6DuDpBVqf46m5wKrGC6SpSKdfd8YVMb2VbDmxEJ81Mr4sKPgfsWAyKtDkDTyWqSFsfGNe9ga7coxPs3EMW5feGDZZsTuE3FTJjD7ALGMnBz");
		AssertUtils.equals(extBip.getExtPrivate(np),
				"xprv9zusQfxwpgYTsTFPAAZSTJNu7bJ45tsB8NJB9qtWT2K5zX4Y8LBvRXZjtwiA3CNZ1XzfiqrPckZk4Q6ByPdFhgQP9Q59zb9wkxP1JZb5p3h");

		// 通过助记词生成的地址
		DeterministicKey key1 = extBip.getKeyByPath("0");
		String a1 = TronscanUtils.getBaseAddressByPublicKey(key1.getPublicKeyAsHex());
		AssertUtils.equals(a1, "TAPoDb3ybMuB4ApqLQ5KPdvnXdLqGeT734");

		// 通过扩展公钥生成地址
		Bip32 bip32 = Bip32.create(
				"xpub6DuDpBVqf46m5wKrGC6SpSKdfd8YVMb2VbDmxEJ81Mr4sKPgfsWAyKtDkDTyWqSFsfGNe9ga7coxPs3EMW5feGDZZsTuE3FTJjD7ALGMnBz",
				np);

		DeterministicKey key2 = bip32.getKeyByPath("0");
		String a2 = TronscanUtils.getBaseAddressByPublicKey(key2.getPublicKeyAsHex());
		AssertUtils.equals(a2, "TAPoDb3ybMuB4ApqLQ5KPdvnXdLqGeT734");
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

		// tronscan 地址
		DeterministicKey tronscan = mnemonic.getKeyByPathAndIndexFromRoot(Bip44Constant.PATH_TRONSCAN, 0);
		System.out.println("-----------tronscan地址-----------");
		System.out.println("公钥: " + tronscan.getPublicKeyAsHex());
		System.out.println("私钥: " + tronscan.getPrivateKeyAsHex());
		Account tronscanAccount = TronscanUtils.createAccount(tronscan);
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

		// 自定义路径
		String tronscanPath = "m/25'/2233'/0'/0";
		// tronscan 地址
		DeterministicKey tronscan = mnemonic.getKeyByPathAndIndexFromRoot(tronscanPath, 0);
		System.out.println("-----------tronscan地址-----------");
		System.out.println("公钥: " + tronscan.getPublicKeyAsHex());
		System.out.println("私钥: " + tronscan.getPrivateKeyAsHex());
		Account tronscanAccount = TronscanUtils.createAccount(tronscan);
		System.out.println("地址: " + tronscanAccount.getAddress());
	}

}
