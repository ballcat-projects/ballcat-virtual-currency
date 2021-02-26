package live.lingting.virtual.currency.core;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;
import live.lingting.virtual.currency.core.bip.Bip32;

/**
 * @author lingting 2021/2/7 13:38
 */
@Slf4j
public class Bip32Test {

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
		Bip32 老板节点 = 老板拥有的助记词.getBip();

		Bip32 研发节点 = 老板节点.getBipByPath("m/44'/0'/0'");
		Bip32 研发1节点 = 研发节点.getBipByPath("0");
		Bip32 研发2节点 = 研发节点.getBipByPath("1");

		System.out.println("研发1第一个地址公钥: " + 研发1节点.getKeyByPath("0").getPublicKeyAsHex());
		System.out.println("研发1第一个地址私钥: " + 研发1节点.getKeyByPath("0").getPrivateKeyAsHex());
		System.out.println("研发2第一个地址公钥: " + 研发2节点.getKeyByPath("0").getPublicKeyAsHex());
		System.out.println("研发2第一个地址私钥: " + 研发2节点.getKeyByPath("0").getPrivateKeyAsHex());

		Bip32 运营节点 = 老板节点.getBipByPath("m/44'/0'/1'");
		Bip32 运营1节点 = 运营节点.getBipByPath("0");
		Bip32 运营2节点 = 运营节点.getBipByPath("1");

		System.out.println("运营1第一个地址公钥: " + 运营1节点.getKeyByPath("0").getPublicKeyAsHex());
		System.out.println("运营1第一个地址私钥: " + 运营1节点.getKeyByPath("0").getPrivateKeyAsHex());
		System.out.println("运营2第一个地址公钥: " + 运营2节点.getKeyByPath("0").getPublicKeyAsHex());
		System.out.println("运营2第一个地址私钥: " + 运营2节点.getKeyByPath("0").getPrivateKeyAsHex());

		System.out.println("运营1叛变了, 老板要把钱全部拿出来");
		Bip32 老板生成的运营1节点 = 老板节点.getBipByPath("m/44'/0'/1'/0");
		System.out.println("老板获取到的运营1第一个地址公钥: " + 老板生成的运营1节点.getKeyByPath("0").getPublicKeyAsHex());
		System.out.println("老板获取到的运营1第一个地址私钥: " + 老板生成的运营1节点.getKeyByPath("0").getPrivateKeyAsHex());
	}

}
