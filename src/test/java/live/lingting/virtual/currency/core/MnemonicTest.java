package live.lingting.virtual.currency.core;

import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;
import live.lingting.virtual.currency.bip.Bip;

/**
 * @author lingting 2021/2/5 16:02
 */
public class MnemonicTest {

	String mnemonicStr = "alter shoot teach paper shock retreat easy parade couch midnight novel stable";

	byte[] seedBytes = Hex.decode(
			"1dc650be7fac7917a70dca822aef8b350376398395082ebd41390f9551018abe22aa033ca02febe9cd341427e3628c0d7740c5aeabbabb82e44006f8b101a4d1");

	long time = 1612513022;

	@Test
	public void create() {
		Mnemonic mnemonic = Mnemonic.create();
		DeterministicSeed seed = mnemonic.getSeed();
		System.out.println("助记词: " + mnemonic.getMnemonic());
		System.out.println("种子16进制: " + Hex.toHexString(mnemonic.getSeedBytes()));
		System.out.println("时间: " + seed.getCreationTimeSeconds());
	}

	@Test
	public void t1() {

		Mnemonic mnemonic = Mnemonic.of(mnemonicStr, seedBytes, "", 0);

		System.out.println("--------------------------------------");
		System.out.println("开始时间: " + System.currentTimeMillis());
		// m
		DeterministicKey masterPk = mnemonic.getMasterKey();
		System.out.println("m - pri: " + masterPk.getPrivateKeyAsHex());
		System.out.println("m - pub: " + masterPk.getPublicKeyAsHex());
		System.out.println("结束时间: " + System.currentTimeMillis());

		System.out.println("--------------------------------------");
		System.out.println("开始时间: " + System.currentTimeMillis());
		// m/44'
		DeterministicKey wa = HDKeyDerivation.deriveChildKey(masterPk, new ChildNumber(44, true));
		System.out.println("wa - pri: " + wa.getPrivateKeyAsHex());
		System.out.println("wa - pub: " + wa.getPublicKeyAsHex());
		System.out.println("结束时间: " + System.currentTimeMillis());

		System.out.println("--------------------------------------");
		System.out.println("开始时间: " + System.currentTimeMillis());
		// m/44'/0'
		DeterministicKey child0 = HDKeyDerivation.deriveChildKey(wa, new ChildNumber(0, true));
		System.out.println("child0 - pri: " + child0.getPrivateKeyAsHex());
		System.out.println("child0 - pub: " + child0.getPublicKeyAsHex());
		System.out.println("结束时间: " + System.currentTimeMillis());

		System.out.println("--------------------------------------");
		System.out.println("开始时间: " + System.currentTimeMillis());
		// m/44'/0'/0'
		DeterministicKey child00 = HDKeyDerivation.deriveChildKey(child0, new ChildNumber(0, true));
		System.out.println("child00 - pri: " + child00.getPrivateKeyAsHex());
		System.out.println("child00 - pub: " + child00.getPublicKeyAsHex());
		System.out.println("结束时间: " + System.currentTimeMillis());

		System.out.println("--------------------------------------");
		System.out.println("开始时间: " + System.currentTimeMillis());
		// m/44'/0'/0'/0/
		DeterministicKey child000 = HDKeyDerivation.deriveChildKey(child00, new ChildNumber(0, false));
		System.out.println("child000 - pri: " + child000.getPrivateKeyAsHex());
		System.out.println("child000 - pub: " + child000.getPublicKeyAsHex());
		System.out.println("结束时间: " + System.currentTimeMillis());

		System.out.println("--------------------------------------");
		System.out.println("开始时间: " + System.currentTimeMillis());
		// m/44'/0'/0'/0/0
		DeterministicKey child0000 = HDKeyDerivation.deriveChildKey(child000, new ChildNumber(0, false));
		System.out.println("child0000 - pri: " + child0000.getPrivateKeyAsHex());
		System.out.println("child0000 - pub: " + child0000.getPublicKeyAsHex());
		System.out.println("结束时间: " + System.currentTimeMillis());

		System.out.println("--------------------------------------");
		System.out.println("开始时间: " + System.currentTimeMillis());
		DeterministicKey key = mnemonic.getKeyByPathFromRoot("m/44'/0'/0'/0/0");
		System.out.println("pri: " + key.getPrivateKeyAsHex());
		System.out.println("pub: " + key.getPublicKeyAsHex());
		System.out.println("结束时间: " + System.currentTimeMillis());
	}

	/**
	 * Bip 使用演示. 包括相对路径和绝对路径生成公私钥
	 */
	@Test
	public void t2() {
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

}
