package live.lingting.virtual.currency.tronscan;

import java.math.BigInteger;
import lombok.experimental.Accessors;
import live.lingting.virtual.currency.AbiMethod;
import live.lingting.virtual.currency.contract.Contract;

/**
 * @author lingting 2020/12/25 16:06
 */
@lombok.Data
@Accessors(chain = true)
public class Trc20Data {

	/**
	 * 原始数据
	 */
	private String rawData;

	/**
	 * 调用方法
	 */
	private AbiMethod method;

	/**
	 * 来源
	 */
	private String from;

	/**
	 * 目标
	 */
	private String to;

	/**
	 * 合约
	 */
	private Contract contract;

	/**
	 * 数量
	 */
	private BigInteger amount;

}
