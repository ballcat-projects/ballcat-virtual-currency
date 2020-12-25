package com.lingting.gzm.virtual.currency;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.web3j.crypto.Credentials;

/**
 * 虚拟货币账号
 *
 * @author lingting 2020/12/22 16:12
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class VirtualCurrencyAccount {

	/**
	 * 地址
	 */
	private String address;

	/**
	 * 公钥
	 */
	private String publicKey;

	/**
	 * 私钥
	 */
	private String privateKey;

	/**
	 * 证书
	 */
	private Credentials credentials;

	/**
	 * keystore 内容
	 */
	private String keystore;

	public VirtualCurrencyAccount(String address, String publicKey, String privateKey) {
		this.address = address;
		this.publicKey = publicKey;
		this.privateKey = privateKey;
	}

	public static void main(String[] args) throws Exception, NoSuchAlgorithmException, NoSuchProviderException {
		String address = "TBQa122mEvNbodaPTh5DXaEU7wFv7W1rci";
		BigInteger puk = new BigInteger(
				"11000596618619890266307000516051655786791179796812746525940076457923205604394743799148631690783918389896733950100947769348937479534074020295038042277012876");
		String publicKey = "d209cbd454acd5646ecebb4260640c9f823a9ea811c1d65699717e27e8ce9a4cd9bd4c9dd21dabd55661074a98f0aeaf8c64c73630745f26154444f4e535e58c";
		BigInteger prk = new BigInteger(
				"46822688035448557556500695090962843376668094829526065091284564440306236109333");
		String privateKey = "6784b3c8872db612adc0fac098440689f52e18b37f995eedb8db489374edb215";
	}

}
