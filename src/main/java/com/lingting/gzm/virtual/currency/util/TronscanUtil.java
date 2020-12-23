package com.lingting.gzm.virtual.currency.util;

import static com.lingting.gzm.virtual.currency.util.KeyUtil.keyDeserialization;

import cn.hutool.core.util.ArrayUtil;
import com.lingting.gzm.virtual.currency.VirtualCurrencyAccount;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import org.bitcoinj.core.Base58;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;

/**
 * @author lingting 2020/12/23 20:37
 */
public class TronscanUtil {

	public static VirtualCurrencyAccount create()
			throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
		// 生成密钥对
		ECKeyPair keyPair = Keys.createEcKeyPair();
		// 公钥
		BigInteger publicKey = keyPair.getPublicKey();
		// 获取公钥 byte[]
		byte[] publicKeyByte = publicKey.toByteArray();

		// 获取 sha3-256
		MessageDigest digest = MessageDigest.getInstance("SHA3-256");
		// 对公钥进行hash
		byte[] hash = digest.digest(publicKeyByte);
		// 提取结果的最后20个字节
		byte[] hash20 = ArrayUtil.sub(hash, hash.length - 20, hash.length);
		// 开始初始化地址
		byte[] initAddress = new byte[21];
		// 将 0x41 添加到字节数组开头
		initAddress[0] = 0x41;
		// 添加其他数据
		System.arraycopy(hash20, 0, initAddress, 1, hash20.length);
		// 使用sha256对地址进行两次hash
		digest = MessageDigest.getInstance("SHA-256");
		byte[] addressHash1 = digest.digest(initAddress);
		byte[] addressHash2 = digest.digest(addressHash1);
		// 将前4个字节作为验证码
		byte[] code = { addressHash2[0], addressHash2[1], addressHash2[2], addressHash2[3] };
		// 将验证码添加到 初始地址 的末尾
		byte[] addressByte = ArrayUtil.addAll(initAddress, code);
		// 通过base58获得 base58check编码
		String addressBase58 = Base58.encode(addressByte);

		return new VirtualCurrencyAccount(addressBase58, keyDeserialization(publicKey),
				keyDeserialization(keyPair.getPrivateKey()));
	}

}
