package live.lingting.virtual.currency.bitcoin;

import cn.hutool.core.collection.CollectionUtil;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author lingting 2021/1/13 16:32
 */
@Data
@Accessors(chain = true)
public class Unspent {

	public static List<Unspent> of(UnspentRes res) {
		if (res == null || CollectionUtil.isEmpty(res.getUnspentList())) {
			return Collections.emptyList();
		}
		List<Unspent> list = new ArrayList<>(res.getUnspentList().size());

		for (UnspentRes.Unspent un : res.getUnspentList()) {
			list.add(of(un));
		}

		return list;
	}

	public static Unspent of(UnspentRes.Unspent un) {
		return new Unspent().setHash(un.getTxHashBigEndian()).setConfirmations(un.getConfirmations())
				.setOut(un.getTxOutputN()).setScript(un.getScript()).setValue(new BigInteger(un.getValue()));
	}

	/**
	 * 交易hash
	 */
	private String hash;

	/**
	 * 交易数量
	 */
	private BigInteger value;

	/**
	 * 输出索引
	 */
	private Long out;

	/**
	 * script
	 */
	private String script;

	/**
	 * 确认数
	 */
	private BigInteger confirmations;

}
