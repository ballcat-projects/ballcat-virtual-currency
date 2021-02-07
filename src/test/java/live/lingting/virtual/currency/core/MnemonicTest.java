package live.lingting.virtual.currency.core;

import org.bitcoinj.wallet.DeterministicSeed;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

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
	public void of() {
		Mnemonic mnemonic = Mnemonic.of(mnemonicStr, seedBytes, "", 0);
		DeterministicSeed seed = mnemonic.getSeed();
		System.out.println("助记词: " + mnemonic.getMnemonic());
		System.out.println("种子16进制: " + Hex.toHexString(mnemonic.getSeedBytes()));
		System.out.println("时间: " + seed.getCreationTimeSeconds());
	}

}
