package live.lingting.virtual.currency.etherscan.model;

import static live.lingting.virtual.currency.etherscan.util.EtherscanUtils.addStart;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigInteger;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author lingting 2021/1/5 20:41
 */
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EtherscanTransaction {

	/**
	 * 交易发起人
	 *
	 * transaction sender
	 */
	private String from;

	/**
	 * 交易接受者, 如果部署合约则为 null
	 *
	 * transaction recipient or null if deploying a contract
	 */
	private String to;

	/**
	 * 为交易执行提供的天然气数量
	 *
	 * gas provided for transaction execution
	 */
	private String gas;

	/**
	 * 每种使用的气体的单位为单位的价格
	 *
	 *
	 * price in wei of each gas used
	 */
	private String gasPrice;

	/**
	 * 与此交易一起发送的wei值
	 *
	 * value in wei sent with this transaction
	 */
	private String value;

	/**
	 * 合约代码或带有编码参数的哈希方法调用
	 *
	 * contract code or a hashed method call with encoded args
	 */
	private String data;

	/**
	 * nonce field is not present on eth_call/eth_estimateGas
	 */
	private String nonce;

	private String gasPremium;

	private String feeCap;

	public static EtherscanTransaction of(String from, String to, String data) {
		return new EtherscanTransaction().setFrom(from).setTo(to).setData(data);
	}

	public static EtherscanTransaction of(String from, BigInteger nonce, BigInteger gasPrice, BigInteger gasLimit,
			String to, BigInteger value, String data) {
		return new EtherscanTransaction().setFrom(from).setTo(to).setNonce(nonce.toString())
				.setGas(addStart(gasLimit.toString())).setGasPrice(addStart(gasPrice.toString()))
				.setValue(addStart(value.toString(16))).setData(data);
	}

}
