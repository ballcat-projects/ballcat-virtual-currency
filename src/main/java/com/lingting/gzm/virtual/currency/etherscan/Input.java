package com.lingting.gzm.virtual.currency.etherscan;

import static com.lingting.gzm.virtual.currency.util.EtherscanUtil.START;
import static com.lingting.gzm.virtual.currency.util.ResolveUtil.removePreZero;

import com.lingting.gzm.virtual.currency.AbiMethod;
import com.lingting.gzm.virtual.currency.contract.EtherscanContract;
import com.lingting.gzm.virtual.currency.exception.VirtualCurrencyException;
import java.math.BigDecimal;
import lombok.Data;
import lombok.experimental.Accessors;

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
	 * 单位 个
	 */
	private BigDecimal value;

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
			this.to = START + removePreZero(to);
		}
		return this;
	}

}
