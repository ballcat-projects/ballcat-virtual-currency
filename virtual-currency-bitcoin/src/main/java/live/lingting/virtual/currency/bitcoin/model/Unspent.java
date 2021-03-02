package live.lingting.virtual.currency.bitcoin.model;

import java.math.BigInteger;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author lingting 2021/1/13 16:32
 */
@Data
@Accessors(chain = true)
public class Unspent {

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
