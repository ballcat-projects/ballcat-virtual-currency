package live.lingting.virtual.currency.bitcoin;

import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.LegacyAddress;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.params.MainNetParams;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;
import live.lingting.virtual.currency.bitcoin.util.BitcoinUtils;
import live.lingting.virtual.currency.core.Mnemonic;
import live.lingting.virtual.currency.core.bip.Bip32;
import live.lingting.virtual.currency.core.bip.Bip44Constant;
import live.lingting.virtual.currency.core.bip.Bip49Constant;
import live.lingting.virtual.currency.core.model.Account;
import live.lingting.virtual.currency.core.util.AssertUtils;

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
		NetworkParameters np = MainNetParams.get();
		Mnemonic m = Mnemonic.of(mnemonicStr, seedBytes, "", 0);
		Bip32 rootBip = m.getBip();
		String path = "m/44'/0'/0'/0";
		Bip32 extBip = rootBip.getBipByPath(path);
		AssertUtils.equals(extBip.getExtPublic(np),
				"xpub6DuDpBVqf46m5wKrGC6SpSKdfd8YVMb2VbDmxEJ81Mr4sKPgfsWAyKtDkDTyWqSFsfGNe9ga7coxPs3EMW5feGDZZsTuE3FTJjD7ALGMnBz",
				"扩展公钥异常!");

		AssertUtils.equals(extBip.getExtPrivate(np),
				"xprv9zusQfxwpgYTsTFPAAZSTJNu7bJ45tsB8NJB9qtWT2K5zX4Y8LBvRXZjtwiA3CNZ1XzfiqrPckZk4Q6ByPdFhgQP9Q59zb9wkxP1JZb5p3h",
				"扩展私钥异常!");

		DeterministicKey key1 = extBip.getKeyByPath("0");
		Account account1 = BitcoinUtils.createLegacyAddress(np, key1);
		AssertUtils.equals(account1.getAddress(), "1HofJpZzDLJieZKTii3jmWxmQN7Dp2Bzvr", "通过bip生成的地址异常!");

		Bip32 bip = Bip32.create(
				"xpub6DuDpBVqf46m5wKrGC6SpSKdfd8YVMb2VbDmxEJ81Mr4sKPgfsWAyKtDkDTyWqSFsfGNe9ga7coxPs3EMW5feGDZZsTuE3FTJjD7ALGMnBz",
				np);
		DeterministicKey key2 = bip.getKeyByPath("0");
		AssertUtils.equals(LegacyAddress.fromKey(np, key2).toString(), "1HofJpZzDLJieZKTii3jmWxmQN7Dp2Bzvr",
				"通过扩展公钥生成的地址异常!");
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
		AssertUtils.equals(legacy.getPublicKeyAsHex(),
				"020c9e69a5371a1b2fc3b4b8db4d304f3f2ab12336a534ed9ca5f0c365c014401d", "生成的公钥异常!");

		AssertUtils.equals(legacy.getPrivateKeyAsHex(),
				"85fe00f4af7012efba31b3cee15beee6eba9c902ddae77856aa43a6b1fb0b703", "生成的私钥异常!");

		AssertUtils.equals(BitcoinUtils.hexToWif(np, Hex.decode(legacy.getPrivateKeyAsHex()), false),
				"5JqJESJJhrHHsqnocEzeYGa7eAMBKoq6mEX8fo9LeSyoAGsLrrt", "生成的私钥WIF异常!");

		AssertUtils.equals(BitcoinUtils.hexToWif(np, Hex.decode(legacy.getPrivateKeyAsHex()), true),
				"L1iB4JfP9JWuVQMAiYmSh1jAK2G4a1hJSEfzrEfoiHfgji4iq6p5", "生成的私钥WIF压缩异常!");

		AssertUtils.equals(BitcoinUtils.createLegacyAddress(np, legacy).getAddress(),
				"1HofJpZzDLJieZKTii3jmWxmQN7Dp2Bzvr", "生成的普通地址异常!");

		// bitcoin 隔离见证地址
		DeterministicKey segwit = mnemonic.getKeyByPathAndIndexFromRoot(Bip49Constant.PATH_BITCOIN, 0);
		AssertUtils.equals(segwit.getPublicKeyAsHex(),
				"02e2a2f76ac33eb1dedac01bf93fb504d2833c0d710018898a7e7bac068d01004e", "生成的公钥异常!");

		AssertUtils.equals(segwit.getPrivateKeyAsHex(),
				"edebf5b7b9be0793d2d71410110b50adeadc1ba49417fb0c557d14d687255d16", "生成的私钥异常!");

		AssertUtils.equals(BitcoinUtils.hexToWif(np, Hex.decode(segwit.getPrivateKeyAsHex()), false),
				"5Kd4yMeuVDxtyhDKtNL7S2HRv2TqprYU6q5R5gDkVSzkCbCMt5o", "生成的私钥WIF异常!");

		AssertUtils.equals(BitcoinUtils.hexToWif(np, Hex.decode(segwit.getPrivateKeyAsHex()), true),
				"L5CCXkAJfaDiREVQMj9MPSfWy3TUWDftcjCVHe7GxZjsg8okmdL2", "生成的私钥WIF压缩异常!");

		AssertUtils.equals(BitcoinUtils.createSegwitAddress(np, segwit).getAddress(),
				"bc1q253ethfzvvm5kh720cym3ttl0jxsxvjq4rfg4s", "生成的隔离见证地址异常!");

		AssertUtils.equals(BitcoinUtils.createMultiSegwitAddress(np, segwit).getAddress(),
				"35AcdTs5DNM7oaWxED2vvvDLxk47AV5xUi", "生成的兼容性隔离见证地址异常!");

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

		AssertUtils.equals(bitcoin.getPublicKeyAsHex(),
				"0392b294ca3877d6d36dfe55a16b5b2215b4e3089594b2bcace1766b60a5ca004f", "生成的公钥异常!");

		AssertUtils.equals(bitcoin.getPrivateKeyAsHex(),
				"f2c3e9ce05ceed8e47011c269b45bd1f1e4c1cf3faa7fea7e82a8304bc8f1a27", "生成的私钥异常!");

		AssertUtils.equals(BitcoinUtils.hexToWif(np, Hex.decode(bitcoin.getPrivateKeyAsHex()), false),
				"5KfChFyUXVRWRGpBz7PvikZhCYfHUFX78SsjzghcQTXfsiyRBME", "生成的私钥WIF异常!");

		AssertUtils.equals(BitcoinUtils.hexToWif(np, Hex.decode(bitcoin.getPrivateKeyAsHex()), true),
				"L5Mcch6GVaTAr8oHV5EC7HHJRxjyhH5t3sCxmcEFGY6NaZeVPQZS", "生成的私钥WIF压缩异常!");

		AssertUtils.equals(BitcoinUtils.createLegacyAddress(np, bitcoin).getAddress(),
				"1BocwNhPKCxxQUwEbhaCftNoveX3q4Bo3o", "生成的普通地址异常!");

		AssertUtils.equals(BitcoinUtils.createSegwitAddress(np, bitcoin).getAddress(),
				"bc1qw6pxgfrsf2ulsg74xtg5dw9wgzh7k70n32ka2n", "生成的隔离见证地址异常!");

		AssertUtils.equals(BitcoinUtils.createMultiSegwitAddress(np, bitcoin).getAddress(),
				"3GKLaZrZUTsE28cqNzuVyd3FrdYW8giWou", "生成的兼容性隔离见证地址异常!");

	}

}
