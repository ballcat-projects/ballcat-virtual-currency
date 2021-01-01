package live.lingting.virtual.currency;

import java.math.BigInteger;
import lombok.Data;

/**
 * 转账操作参数, 如果设置,则直接使用已设置数据
 *
 * @author lingting 2020/12/29 10:03
 */
@Data
public class TransferParams {

	/**
	 * Etherscan转账费用配置
	 */
	BigInteger gasPrice;

	/**
	 * Etherscan转账费用配置
	 */
	BigInteger gasLimit;

	/**
	 * Tronscan 转账费用, 以 sun 为单位 1trx = 10^6 sun, 最大值为 10^sun 即 1000TRX
	 */
	BigInteger feeLimit;

	/**
	 * Tronscan 转账支付给合约的费用
	 */
	private BigInteger callValue;

}
