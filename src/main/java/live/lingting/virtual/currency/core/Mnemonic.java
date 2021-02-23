package live.lingting.virtual.currency.core;

import cn.hutool.core.util.ArrayUtil;
import com.google.common.base.Splitter;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.List;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.crypto.HDUtils;
import org.bitcoinj.wallet.DeterministicSeed;
import live.lingting.virtual.currency.bip.Bip;
import live.lingting.virtual.currency.exception.VirtualCurrencyException;

/**
 * 助记词.
 *
 * 目前只生成英文的助记词. 中文等其他语言好像没有app支持, 参考 https://iancoleman.io/bip39/#chinese_simplified
 *
 * 助记词列原始数据使用 https://github.com/bitcoin/bips/blob/master/bip-0039/bip-0039-wordlists.md
 *
 * @author lingting 2021/2/5 11:41
 */
public class Mnemonic {

	/**
	 * 助记词数量最小值
	 */
	public static final int QUANTITY_MIN = 11;

	/**
	 * 助记词数量要求
	 */
	public static final int QUANTITY_3 = 3;

	/**
	 * 默认助记词数量最大值
	 */
	public static final int QUANTITY_MAX = 24;

	/**
	 * 默认助记词数量
	 */
	public static final int QUANTITY_DEFAULT = 12;

	/**
	 * 默认密码
	 */
	public static final String PASSWORD_DEFAULT = "";

	/**
	 * 间隔标志
	 */
	public static final String FLAG = " ";

	@Getter
	private final DeterministicSeed seed;

	@Getter
	private final Bip bip;

	private Mnemonic(DeterministicSeed seed) {
		this.seed = seed;
		this.bip = Bip.create(generateMasterKey());
	}

	/**
	 * 生成助记词
	 * @return live.lingting.virtual.currency.core.Mnemonic
	 * @author lingting 2021-02-05 13:56
	 */
	@SneakyThrows
	public static Mnemonic create() {
		return create(QUANTITY_DEFAULT, PASSWORD_DEFAULT);
	}

	/**
	 * 生成助记词
	 * @param quantity 助记词数量. 必须为3的倍数,大于11; 小于25 不建议过少
	 * @param password 密码, 不要密码则使用 {@link Mnemonic#PASSWORD_DEFAULT}
	 * @return live.lingting.virtual.currency.core.Mnemonic
	 * @author lingting 2021-02-05 13:56
	 */
	@SneakyThrows
	public static Mnemonic create(int quantity, String password) {
		if (quantity <= QUANTITY_MIN || quantity % QUANTITY_3 != 0 || quantity > QUANTITY_MAX) {
			throw new VirtualCurrencyException("助记词数量必须为3的倍数且大于11且小于等于" + QUANTITY_MAX);
		}

		// 生成助记词信息
		return new Mnemonic(new DeterministicSeed(new SecureRandom(), quantity / QUANTITY_3 * 32, password));
	}

	/**
	 * 通过助记词内容, 获取助记词类
	 * @param mnemonics 助记词内容用空格间隔
	 * @param seed 种子字节, 为null 则 通过密码和助记词 进行推导. 推导过程较慢
	 * @param password 密码. 没有密码使用 {@link Mnemonic#PASSWORD_DEFAULT}
	 * @param creationTimeSeconds 生成时的时间戳- 不记得随便填, 实测生成Bitcoin地址没问题
	 * @return live.lingting.virtual.currency.core.Mnemonic
	 * @author lingting 2021-02-05 14:46
	 */
	public static Mnemonic of(String mnemonics, byte[] seed, String password, long creationTimeSeconds) {
		return of(Splitter.on(FLAG).splitToList(mnemonics), seed, password, creationTimeSeconds);
	}

	/**
	 * 通过助记词内容, 获取助记词类
	 * @param mnemonics 助记词内容
	 * @param seed 种子字节, 为null 则 通过密码和助记词 进行推导. 推导过程较慢
	 * @param password 密码. 没有密码使用 {@link Mnemonic#PASSWORD_DEFAULT}
	 * @param creationTimeSeconds 生成时的时间戳
	 * @return live.lingting.virtual.currency.core.Mnemonic
	 * @author lingting 2021-02-05 14:46
	 */
	public static Mnemonic of(List<String> mnemonics, byte[] seed, String password, long creationTimeSeconds) {
		return of(new DeterministicSeed(mnemonics, seed, password, creationTimeSeconds));
	}

	/**
	 * 通过 包装种子类 生成助记词
	 * @param seed 包装种子类
	 * @return live.lingting.virtual.currency.core.Mnemonic
	 * @author lingting 2021-02-05 14:45
	 */
	public static Mnemonic of(DeterministicSeed seed) {
		return new Mnemonic(seed);
	}

	/**
	 * 获取助记词
	 * @return java.util.List<java.lang.String>
	 * @author lingting 2021-02-05 14:38
	 */
	public List<String> getMnemonics() {
		return seed.getMnemonicCode();
	}

	/**
	 * 获取助记词. 已用空格间隔
	 * @return java.lang.String
	 * @author lingting 2021-02-05 14:38
	 */
	public String getMnemonic() {
		return String.join(FLAG, getMnemonics());
	}

	/**
	 * 获取种子字节码
	 * @return byte[]
	 * @author lingting 2021-02-05 16:04
	 */
	public byte[] getSeedBytes() {
		return seed.getSeedBytes();
	}

	/**
	 * 生成 master key
	 */
	public DeterministicKey generateMasterKey() {
		// hmac sha512
		byte[] hmacSha512 = HDUtils.hmacSha512("Bitcoin seed".getBytes(StandardCharsets.UTF_8), getSeedBytes());
		// 分成两段
		byte[][] split = ArrayUtil.split(hmacSha512, 32);
		// 主密码 字节
		byte[] il = split[0];
		// 主代码 字节
		byte[] ir = split[1];
		return HDKeyDerivation.createMasterPrivKeyFromBytes(il, ir);
	}

	/**
	 * 获取助记词主key
	 * https://github.com/bitcoin/bips/blob/master/bip-0032.mediawiki#Master_key_generation
	 *
	 * 验证工具 https://iancoleman.io/bip39/#english
	 * @return org.bitcoinj.crypto.DeterministicKey
	 * @author lingting 2021-02-05 20:08
	 */
	public DeterministicKey getMasterKey() {
		return bip.getKey();
	}

	/**
	 * 注意, 本方法传入的地址参数从根(m)开始
	 *
	 * 地址参考
	 * https://github.com/bitcoin/bips/blob/master/bip-0032.mediawiki#Master_key_generation
	 * @return org.bitcoinj.crypto.DeterministicKey
	 * @author lingting 2021-02-05 20:31
	 */
	public DeterministicKey getKeyByPathFromRoot(String path) {
		return bip.getKeyByPath(path);
	}

	/**
	 * 提供地址前部分 和 下一级的 索引. 返回对应的key
	 * @param path 地址前部分, 参考 测试用例
	 * {@link live.lingting.virtual.currency.core.BipTest#standard()}
	 * @param index 下一级索引
	 * @return org.bitcoinj.crypto.DeterministicKey
	 * @author lingting 2021-02-07 14:11
	 */
	public DeterministicKey getKeyByPathAndIndexFromRoot(String path, int index) {
		if (!path.endsWith(Bip.PATH_SPLIT_FLAG)) {
			path = path + Bip.PATH_SPLIT_FLAG;
		}
		return getKeyByPathFromRoot(path + index);
	}

}
