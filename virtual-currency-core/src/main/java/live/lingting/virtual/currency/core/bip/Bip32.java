package live.lingting.virtual.currency.core.bip;

import cn.hutool.core.collection.CollectionUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;

/**
 * 树形结构中的单独节点.
 *
 * @author lingting 2021/2/7 11:18
 */
public class Bip32 {

	/**
	 * 根路径标志
	 */
	public static final String PATH_ROOT_FLAG = "m";

	/**
	 * 路径分隔符
	 */
	public static final String PATH_SPLIT_FLAG = "/";

	/**
	 * 是否需要硬化的标志后缀
	 */
	public static final String PATH_HARDENED_SUFFIX = "'";

	/**
	 * 当前节点的 key
	 */
	@Getter
	private final DeterministicKey key;

	/**
	 * 使用线程安全map存储
	 */
	private final Map<ChildNumber, Bip32> map = new ConcurrentHashMap<>();

	private Bip32(DeterministicKey key) {
		this.key = key;
	}

	public static Bip32 create(DeterministicKey key) {
		return new Bip32(key);
	}

	/**
	 * 通过 扩展 公钥或私钥 生成bip
	 * @param extKey 扩展 公钥或私钥
	 * @param np 环境
	 * @return live.lingting.virtual.currency.core.bip.Bip
	 * @author lingting 2021-02-24 17:56
	 */
	public static Bip32 create(String extKey, NetworkParameters np) {
		return create(DeterministicKey.deserializeB58(extKey, np));
	}

	/**
	 * 获取指定child对应的节点
	 * @param child child
	 * @return live.lingting.virtual.currency.core.bip.BipNode
	 * @author lingting 2021-02-07 11:27
	 */
	public Bip32 getByChildNumber(ChildNumber child) {
		if (map.containsKey(child)) {
			return map.get(child);
		}
		Bip32 node = create(HDKeyDerivation.deriveChildKey(key, child));
		map.put(child, node);
		return node;
	}

	/**
	 * 从根路径开始, 获取指定路径下的节点
	 * @param path 路径 类似于 m/44'/0'/0'/0/0 或者 0/0, 具体看 测试用例
	 * {@link live.lingting.virtual.currency.core.BipTest}
	 * @return org.bitcoinj.crypto.DeterministicKey
	 * @author lingting 2021-02-07 10:44
	 */
	public Bip32 getBipByPath(String path) {
		List<ChildNumber> list = getChildListByPath(path);

		// 没有解析出路径, 返回根路径key
		if (CollectionUtil.isEmpty(list)) {
			return this;
		}

		// 获取地址的最后一个节点
		Bip32 node = this;

		for (ChildNumber child : list) {
			node = node.getByChildNumber(child);
		}

		return node;
	}

	/**
	 * 从根路径开始, 获取指定路径下的key
	 * @param path 路径 类似于 m/44'/0'/0'/0/0 或者 0/0, 具体看 测试用例
	 * {@link live.lingting.virtual.currency.core.BipTest}
	 * @return org.bitcoinj.crypto.DeterministicKey
	 * @author lingting 2021-02-07 10:44
	 */
	public DeterministicKey getKeyByPath(String path) {
		return getBipByPath(path).getKey();
	}

	public List<ChildNumber> getChildListByPath(String path) {
		// 移除所有不可见字符
		path = path.replaceAll("\\s", "");

		String[] split = path.split(PATH_SPLIT_FLAG);

		List<ChildNumber> list = new ArrayList<>(split.length);

		for (String nodeStr : split) {
			// 根路径不生成 child Number
			if (nodeStr.equals(PATH_ROOT_FLAG)) {
				continue;
			}
			list.add(getChildByNode(nodeStr));
		}

		return list;
	}

	/**
	 * 节点 生成 ChildNumber
	 * @param nodeStr 节点字符串
	 * @return org.bitcoinj.crypto.ChildNumber
	 * @author lingting 2021-02-07 11:16
	 */
	public ChildNumber getChildByNode(String nodeStr) {
		// 是否硬化
		if (nodeStr.endsWith(PATH_HARDENED_SUFFIX)) {
			return new ChildNumber(Integer.parseInt(nodeStr.substring(0, nodeStr.length() - 1)), true);
		}
		else {
			return new ChildNumber(Integer.parseInt(nodeStr), false);
		}
	}

	/**
	 * 获取当前bip的扩展公钥 - 仅用于生成子公钥, 无法生成子私钥
	 * @param np 环境
	 * @return java.lang.String
	 * @author lingting 2021-02-24 17:54
	 */
	public String getExtPublicKey(NetworkParameters np) {
		return getKey().serializePubB58(np);
	}

	/**
	 * 获取当前bip的扩展私钥 - 可以用于生成子公钥和子私钥
	 * @param np 环境
	 * @return java.lang.String
	 * @author lingting 2021-02-24 17:55
	 */
	public String getExtPrivate(NetworkParameters np) {
		return getKey().serializePrivB58(np);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		return Objects.equals(key, ((Bip32) o).key);
	}

	@Override
	public int hashCode() {
		return Objects.hash(key);
	}

}
