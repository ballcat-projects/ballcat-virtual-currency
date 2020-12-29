package com.lingting.gzm.virtual.currency.util;

import static com.lingting.gzm.virtual.currency.util.ResolveUtil.removePreZero;
import static com.lingting.gzm.virtual.currency.util.ResolveUtil.stringToArrayBy64;

import cn.hutool.core.util.ArrayUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.lingting.gzm.virtual.currency.AbiMethod;
import com.lingting.gzm.virtual.currency.VirtualCurrencyAccount;
import com.lingting.gzm.virtual.currency.contract.Contract;
import com.lingting.gzm.virtual.currency.contract.TronscanContract;
import com.lingting.gzm.virtual.currency.endpoints.Endpoints;
import com.lingting.gzm.virtual.currency.exception.VirtualCurrencyException;
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
import org.bouncycastle.jcajce.provider.digest.Keccak;
import org.bouncycastle.util.encoders.Hex;
import org.tron.tronj.crypto.SECP256K1;
import org.tron.tronj.utils.Base58Check;

/**
 * @author lingting 2020/12/23 20:37
 */
public class TronscanUtil {

	public static final String HEX_ADDRESS_PREFIX = "41";

	public static final Map<String, Function<String, Trc20Data>> METHOD_HANDLER;

	private static final Pattern NUMBER_PATTERN = Pattern.compile("^\\d+$");

	static {
		// 合约函数数据解析方法
		METHOD_HANDLER = new ConcurrentHashMap<>(AbiMethod.values().length);
		METHOD_HANDLER.put(AbiMethod.TRANSFER.getMethodId(), str -> {
			if (str.startsWith(AbiMethod.TRANSFER.getMethodId())) {
				str = str.substring(AbiMethod.TRANSFER.getMethodId().length());
			}
			String[] array = stringToArrayBy64(str);
			// 收款人
			String to = decodeAddressParam(array[0]);
			// 数量
			BigDecimal amount = new BigDecimal(new BigInteger(removePreZero(array[1]), 16));
			return new Trc20Data().setAmount(amount).setTo(to);
		});

		METHOD_HANDLER.put(AbiMethod.SEND_MULTI_SIG_TOKEN.getMethodId(), str -> {
			if (str.startsWith(AbiMethod.SEND_MULTI_SIG_TOKEN.getMethodId())) {
				str = str.substring((AbiMethod.SEND_MULTI_SIG_TOKEN.getMethodId()).length());
			}
			String[] array = stringToArrayBy64(str);
			String contractHash = removePreZero(array[2]);

			Contract contract = TronscanContract.getByHash(contractHash);
			if (contract == null) {
				contract = new Contract() {
					@Override
					public String getHash() {
						return contractHash;
					}

					@Override
					public Integer getDecimals() {
						return null;
					}
				};
			}
			return new Trc20Data()
					// 收款人
					.setTo(decodeAddressParam(array[0]))
					// 数量
					.setAmount(new BigDecimal(Long.parseLong(array[1], 16)))
					// 合约地址
					.setContract(contract);
		});

		METHOD_HANDLER.put(AbiMethod.SEND_MULTI_SIG.getMethodId(), str -> {
			if (str.startsWith(AbiMethod.SEND_MULTI_SIG.getMethodId())) {
				str = str.substring((AbiMethod.SEND_MULTI_SIG.getMethodId()).length());
			}
			String[] array = stringToArrayBy64(str);
			return new Trc20Data().setTo(decodeAddressParam(array[0]))
					.setAmount(new BigDecimal(Long.parseLong(array[1], 16)));
		});

		METHOD_HANDLER.put(AbiMethod.TRANSFER_FROM.getMethodId(), str -> {
			if (str.startsWith(AbiMethod.TRANSFER_FROM.getMethodId())) {
				str = str.substring((AbiMethod.TRANSFER_FROM.getMethodId()).length());
			}
			String[] array = stringToArrayBy64(str);
			return new Trc20Data()
					// 转账人
					.setFrom(decodeAddressParam(array[0]))
					// 收款人
					.setTo(decodeAddressParam(array[1]))
					// 数量
					.setAmount(new BigDecimal(Long.parseLong(array[2], 16)));
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
	 * 将地址转换为abi方法的参数
	 * @author lingting 2020-12-25 22:04
	 */
	public static String encodeAddressParam(String address) {
		// 是否为16进制地址
		if (address.startsWith(HEX_ADDRESS_PREFIX)) {
			// 移除 开头的41
			address = address.substring(HEX_ADDRESS_PREFIX.length());
		}
		// base58编码地址
		else {
			// base58反序列化
			byte[] bytes = Base58.decode(address);
			// 需要 移除后4个字节 以及 第一个字节
			byte[] initAddress = new byte[bytes.length - 5];
			// 复制除 后4个字节 以及 第一个字节 以外的字符
			System.arraycopy(bytes, 1, initAddress, 0, initAddress.length);
			// 转十六进制
			address = Hex.toHexString(initAddress);
		}
		return AbiUtil.addZeroTo64InPre(address);
	}

	/**
	 * 判断合约是否为 trc20, hash 纯数字为trc10
	 *
	 * @author lingting 2020-12-13 15:19
	 */
	public static boolean isTrc20(String hash) {
		return !NUMBER_PATTERN.matcher(hash).find();
	}

	/**
	 * 新增账号
	 * @author lingting 2020-12-25 20:35
	 */
	public static VirtualCurrencyAccount create()
			throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
		// 生成密钥对
		SECP256K1.KeyPair keyPair = SECP256K1.KeyPair.generate();

		// 公钥
		SECP256K1.PublicKey publicKey = keyPair.getPublicKey();
		String pubKey = publicKey.toString().substring(2);
		// 私钥
		SECP256K1.PrivateKey privateKey = keyPair.getPrivateKey();
		String priKey = privateKey.toString().substring(2);

		Keccak.Digest256 digest = new Keccak.Digest256();
		// 对公钥进行hash
		byte[] hash = digest.digest(publicKey.getEncoded());
		// 提取最后20个字节
		byte[] hash20 = ArrayUtil.sub(hash, hash.length - 20, hash.length);
		// 初识地址
		byte[] initAddress = new byte[21];
		// 将 0x41 添加到字节数组开头
		initAddress[0] = 0x41;
		// 添加其他数据
		System.arraycopy(hash20, 0, initAddress, 1, 20);
		// 生成地址
		String address = Base58Check.bytesToBase58(initAddress);
		return new VirtualCurrencyAccount(address, pubKey, priKey);
	}

	/**
	 * 根据私钥获取账户
	 * @param address 地址
	 * @param privateKey 私钥
	 * @return com.lingting.gzm.virtual.currency.VirtualCurrencyAccount
	 * @author lingting 2020-12-23 14:04
	 */
	public static VirtualCurrencyAccount getAccountOfKey(String address, String privateKey) {
		return getAccountOfKey(address, null, privateKey);
	}

	/**
	 * 根据公私钥获取账户
	 * @param address 地址
	 * @param publicKey 公钥, 忘记了可以留空
	 * @param privateKey 私钥
	 * @return com.lingting.gzm.virtual.currency.VirtualCurrencyAccount
	 * @author lingting 2020-12-23 14:05
	 */
	public static VirtualCurrencyAccount getAccountOfKey(String address, String publicKey, String privateKey) {
		return EtherscanUtil.getAccountOfKey(address, publicKey, privateKey);
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
		TriggerResult.Trc20DecimalsResult result = TriggerRequest.decimals(endpoints, contract).exec();
		return Integer.valueOf(result.getConstantResult().get(0), 16);
	}

}
