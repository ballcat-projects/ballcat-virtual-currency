package live.lingting.virtual.currency.core.model;

import java.math.BigInteger;
import lombok.Data;
import lombok.experimental.Accessors;
import live.lingting.virtual.currency.core.Contract;

/**
 * @author lingting 2021/1/20 16:37
 */
@Data
@Accessors(chain = true)
public class TransactionGenerate {

	protected String message;

	protected Boolean success;

	/**
	 * 对应异常数据, 可能为 null
	 */
	protected Throwable exception;

	protected Contract contract;

	protected Account from;

	protected String to;

	protected BigInteger amount;

	/**
	 * 签名后用于广播的原始交易数据
	 */
	protected String signHex;

}
