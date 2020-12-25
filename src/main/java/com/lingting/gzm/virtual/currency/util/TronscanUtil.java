package com.lingting.gzm.virtual.currency.util;

import static com.lingting.gzm.virtual.currency.util.KeyUtil.keyDeserialization;
import static com.lingting.gzm.virtual.currency.util.ResolveUtil.removePreZero;
import static com.lingting.gzm.virtual.currency.util.ResolveUtil.stringToArrayBy64;

import cn.hutool.core.util.ArrayUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.lingting.gzm.virtual.currency.VirtualCurrencyAccount;
import com.lingting.gzm.virtual.currency.contract.Contract;
import com.lingting.gzm.virtual.currency.endpoints.Endpoints;
import com.lingting.gzm.virtual.currency.exception.VirtualCurrencyException;
import com.lingting.gzm.virtual.currency.tronscan.Method;
import com.lingting.gzm.virtual.currency.tronscan.Trc20Data;
import com.lingting.gzm.virtual.currency.tronscan.TriggerRequest;
import com.lingting.gzm.virtual.currency.tronscan.TriggerResult;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.regex.Pattern;
import lombok.SneakyThrows;
import org.bitcoinj.core.Base58;
import org.bouncycastle.util.encoders.Hex;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;

/**
 * @author lingting 2020/12/23 20:37
 */
public class TronscanUtil {

	public static final String HEX_ADDRESS_PREFIX = "41";

	private static final Pattern NUMBER_PATTERN = Pattern.compile("^\\d+$");

	public static final Map<String, Function<String, Trc20Data>> METHOD_HANDLER;

	static {
		// 合约函数数据解析方法
		METHOD_HANDLER = new ConcurrentHashMap<>(Method.values().length);
		METHOD_HANDLER.put(Method.TRANSFER.getMethodId(), str -> {
			if (str.startsWith(Method.TRANSFER.getMethodId())) {
				str = str.substring(Method.TRANSFER.getMethodId().length());
			}
			String[] array = stringToArrayBy64(str);
			// 收款人
			String to = decodeAddressParam(array[0]);
			// 数量
			BigDecimal amount = new BigDecimal(new BigInteger(removePreZero(array[1]), 16));
			return new Trc20Data().setAmount(amount).setTo(to);
		});
	}

	/**
	 * 解析数据 为 trc20 Data
	 * @author lingting 2020-12-25 19:08
	 */
	public static Trc20Data resolve(String rawData) throws VirtualCurrencyException {
		for (String s : METHOD_HANDLER.keySet()) {
			if (rawData.startsWith(s)) {
				Trc20Data data = METHOD_HANDLER.get(s).apply(rawData);
				// 保存原始数据
				return data.setRawData(rawData);
			}
		}

		throw new VirtualCurrencyException("未支持此方法, 请手动处理");
	}

	/**
	 * 解析地址类型参数
	 * @author lingting 2020-12-25 17:00
	 */
	public static String decodeAddressParam(String param) {
		String str = removePreZero(param);
		if (!str.startsWith(HEX_ADDRESS_PREFIX)) {
			str = HEX_ADDRESS_PREFIX + str;
		}
		return TronscanUtil.hexToString(str);
	}

	/**
	 * 判断合约是否为 trc20, hash 纯数字为trc10
	 *
	 * @author lingting 2020-12-13 15:19
	 */
	public static boolean isTrc20(String hash) {
		return !NUMBER_PATTERN.matcher(hash).find();
	}

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
		// 生成地址
		String address = addressByteToString(initAddress);

		return new VirtualCurrencyAccount(address, keyDeserialization(publicKey),
				keyDeserialization(keyPair.getPrivateKey()));
	}

	/**
	 * 十六进制地址转为字符串地址
	 * @author lingting 2020-12-25 11:40
	 */
	@SneakyThrows
	public static String hexToString(String hex) {
		return addressByteToString(Hex.decode(hex));
	}

	/**
	 * 字节地址转为字符串地址
	 * @author lingting 2020-12-25 11:40
	 */
	public static String addressByteToString(byte[] address) throws NoSuchAlgorithmException {
		// 使用sha256对地址进行两次hash
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] addressHash1 = digest.digest(address);
		byte[] addressHash2 = digest.digest(addressHash1);
		// 将前4个字节作为验证码
		byte[] code = { addressHash2[0], addressHash2[1], addressHash2[2], addressHash2[3] };
		// 将验证码添加到 初始地址 的末尾
		byte[] addressByte = ArrayUtil.addAll(address, code);
		// 通过base58获得 base58check编码
		return Base58.encode(addressByte);
	}

	/**
	 * 获取trc20合约精度
	 * @author lingting 2020-12-25 19:36
	 */
	public static Integer getDecimalByTrc20(Endpoints endpoints, Contract contract) throws JsonProcessingException {
		TriggerResult result = TriggerRequest.decimals(endpoints, contract).exec();
		return Integer.valueOf(result.getConstantResult().get(0), 16);
	}

}
