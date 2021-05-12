package live.lingting.virtual.currency.etherscan.model;

import static live.lingting.virtual.currency.etherscan.util.EtherscanUtils.START;

import java.math.BigInteger;
import lombok.Data;
import lombok.experimental.Accessors;
import live.lingting.virtual.currency.core.enums.AbiMethod;
import live.lingting.virtual.currency.etherscan.contract.EtherscanContract;
import live.lingting.virtual.currency.etherscan.util.EtherscanUtils;

/**
 * @author lingting 2020-09-02 14:20
 */
@Data
@Accessors(chain = true)
public class Input {

	/**
	 * 方法id
	 */
	private AbiMethod method;

	/**
	 * 原始数据
	 */
	private String data;

	/**
	 * 收款地址
	 */
	private String to;

	/**
	 * 转账人
	 */
	private String from;

	/**
	 * 数量
	 */
	private BigInteger value;

	/**
	 * 合约
	 */
	private EtherscanContract contract;

	/**
	 * 合约地址
	 */
	private String contractAddress;

	public Input setTo(String to) {
		if (to.startsWith(START)) {
			this.to = to;
		}
		else {
			this.to = EtherscanUtils.decodeAddressParam(to);
		}
		return this;
	}

	public Input setFrom(String from) {
		if (from.startsWith(START)) {
			this.from = from;
		}
		else {
			this.from = EtherscanUtils.decodeAddressParam(to);
		}
		return this;
	}

}
