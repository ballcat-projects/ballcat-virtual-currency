package com.lingting.gzm.virtual.currency.service;

import com.lingting.gzm.virtual.currency.VirtualCurrencyAccount;
import com.lingting.gzm.virtual.currency.VirtualCurrencyTransaction;
import com.lingting.gzm.virtual.currency.contract.Contract;
import com.lingting.gzm.virtual.currency.exception.VirtualCurrencyException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * 虚拟货币处理接口类
 *
 * @author lingting 2020-09-01 17:15
 */
public interface VirtualCurrencyService {

	/**
	 * 通过交易hash获取交易信息
	 * @param hash 交易hash
	 * @return 交易信息
	 */
	Optional<VirtualCurrencyTransaction> getTransactionByHash(String hash) throws Exception;

	/**
	 * 获取合约的精度, 请注意不同平台不同api的返回结果不一致
	 * @param contract 合约
	 * @return 精度
	 * @author lingting 2020-12-11 16:09
	 */
	Integer getDecimalsByContract(Contract contract) throws Exception;

	/**
	 * 查询指定地址, 指定合约余额. 不同平台请使用不同平台对应的 contract . eth
	 * {@link com.lingting.gzm.virtual.currency.contract.Etherscan}. omni
	 * {@link com.lingting.gzm.virtual.currency.contract.Omni}. tronscan
	 * {@link com.lingting.gzm.virtual.currency.contract.Tronscan}.
	 * @param address 账号地址
	 * @param contract 合约
	 * @return java.math.BigDecimal
	 * @author lingting 2020-12-10 23:56
	 */
	BigDecimal getBalanceByAddressAndContract(String address, Contract contract) throws Exception;

	/**
	 * 获取指定地址, 指定合约余额 (单位: 个)
	 * @param address 地址
	 * @param contract 余额
	 * @return java.math.BigDecimal
	 * @author lingting 2020-12-11 19:47
	 */
	default BigDecimal getNumberByAddressAndContract(String address, Contract contract) throws Exception {
		return getNumberByBalanceAndContract(getBalanceByAddressAndContract(address, contract), contract);
	}

	/**
	 * 通过
	 * {@link VirtualCurrencyService#getBalanceByAddressAndContract(java.lang.String, com.lingting.gzm.virtual.currency.contract.Contract)}
	 * 方法的余额单位不一定是个, 会附带小数, 可执行此方法转换为以个为单位
	 * @param balance getBalanceByAddressAndContract 方法返回值
	 * @param contract 合约地址
	 * @return java.math.BigDecimal
	 * @author lingting 2020-12-11 17:20
	 */
	default BigDecimal getNumberByBalanceAndContract(BigDecimal balance, Contract contract) throws Exception {
		return getNumberByBalanceAndContract(balance, contract, MathContext.UNLIMITED);
	}

	/**
	 * 通过
	 * {@link VirtualCurrencyService#getBalanceByAddressAndContract(java.lang.String, com.lingting.gzm.virtual.currency.contract.Contract)}
	 * 方法的余额单位不一定是个, 会附带小数, 可执行此方法转换为以个为单位
	 * @param balance getBalanceByAddressAndContract 方法返回值
	 * @param contract 合约地址
	 * @param mathContext 精度要求
	 * @return java.math.BigDecimal
	 * @author lingting 2020-12-11 17:20
	 */
	BigDecimal getNumberByBalanceAndContract(BigDecimal balance, Contract contract, MathContext mathContext)
			throws Exception;

	/**
	 * 转账
	 * @param from 转出账号
	 * @param to 转入地址
	 * @param contract 合约
	 * @param value 转账金额, 单位 个
	 * @return boolean
	 * @author lingting 2020-12-22 16:14
	 */
	boolean transfer(VirtualCurrencyAccount from, String to, Contract contract, BigDecimal value)
			throws Exception, InterruptedException, ExecutionException;

}
