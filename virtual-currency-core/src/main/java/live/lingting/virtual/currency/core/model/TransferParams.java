package live.lingting.virtual.currency.core.model;

import java.math.BigInteger;
import lombok.Data;
import lombok.experimental.Accessors;
import org.bitcoinj.core.Coin;

/**
 * 转账操作参数, 如果设置,则直接使用已设置数据
 *
 * @author lingting 2020/12/29 10:03
 */
@Data
@Accessors(chain = true)
public class TransferParams {

	/**
	 * Etherscan转账费用配置, wei 为单位
	 */
	BigInteger gasPrice;

	/**
	 * Etherscan转账费用配置, wei 为单位
	 */
	BigInteger gasLimit;

	/**
	 * Tronscan 转账费用, 以 sun 为单位 1trx = 10^6 sun, 最大值为 10^9 sun 即 1000TRX
	 */
	BigInteger feeLimit;

	/**
	 * Tronscan 转账支付给合约的费用
	 */
	private BigInteger callValue;

	/**
	 * bitcoin 转账 字节手续费单价, 即一个字节使用多少手续费, 最后总手续费根据字节计算 单位为 聪
	 */
	private Coin fee;

	/**
	 * fee 总价, 即不管这笔交易输入输出怎样, 只付这么多手续费
	 */
	private Coin sumFee;

	public static TransferParams empty() {
		return new TransferParams();
	}

}
