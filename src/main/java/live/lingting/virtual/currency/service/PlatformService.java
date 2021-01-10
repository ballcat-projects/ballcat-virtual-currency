package live.lingting.virtual.currency.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.Optional;
import live.lingting.virtual.currency.Account;
import live.lingting.virtual.currency.Transaction;
import live.lingting.virtual.currency.TransferParams;
import live.lingting.virtual.currency.TransferResult;
import live.lingting.virtual.currency.contract.Contract;
import live.lingting.virtual.currency.contract.EtherscanContract;
import live.lingting.virtual.currency.contract.OmniContract;
import live.lingting.virtual.currency.contract.TronscanContract;

/**
 * 虚拟货币处理接口类
 *
 * @author lingting 2020-09-01 17:15
 */
public interface PlatformService {

	/**
	 * 通过交易hash获取交易信息
	 * @param hash 交易hash
	 * @return 交易信息
	 * @exception Throwable 可能抛出的异常
	 */
	Optional<Transaction> getTransactionByHash(String hash) throws Throwable;

	/**
	 * 获取合约的精度, 请注意不同平台不同api的返回结果不一致
	 * @param contract 合约
	 * @return 精度
	 * @author lingting 2020-12-11 16:09
	 * @exception Throwable 可能抛出的异常
	 */
	Integer getDecimalsByContract(Contract contract) throws Throwable;

	/**
	 * 查询指定地址, 指定合约余额. 不同平台请使用不同平台对应的 contract . eth {@link EtherscanContract}. omni
	 * {@link OmniContract}. tronscan {@link TronscanContract}.
	 * @param address 账号地址
	 * @param contract 合约
	 * @return java.math.BigDecimal
	 * @author lingting 2020-12-10 23:56
	 * @exception Throwable 可能抛出的异常
	 */
	BigInteger getBalanceByAddressAndContract(String address, Contract contract) throws Throwable;

	/**
	 * 获取指定地址, 指定合约余额 (单位: 个)
	 * @param address 地址
	 * @param contract 余额
	 * @return java.math.BigDecimal
	 * @author lingting 2020-12-11 19:47
	 * @exception Throwable 可能抛出的异常
	 */
	default BigDecimal getNumberByAddressAndContract(String address, Contract contract) throws Throwable {
		return getNumberByBalanceAndContract(getBalanceByAddressAndContract(address, contract), contract);
	}

	/**
	 * 通过
	 * {@link PlatformService#getBalanceByAddressAndContract(java.lang.String, Contract)}
	 * 方法的余额单位不一定是个, 会附带小数, 可执行此方法转换为以个为单位
	 * @param balance getBalanceByAddressAndContract 方法返回值
	 * @param contract 合约地址
	 * @return java.math.BigDecimal
	 * @author lingting 2020-12-11 17:20
	 * @exception Throwable 可能抛出的异常
	 */
	default BigDecimal getNumberByBalanceAndContract(BigInteger balance, Contract contract) throws Throwable {
		return getNumberByBalanceAndContract(balance, contract, MathContext.UNLIMITED);
	}

	/**
	 * 通过
	 * {@link PlatformService#getBalanceByAddressAndContract(java.lang.String, Contract)}
	 * 方法的余额单位不一定是个, 会附带小数, 可执行此方法转换为以个为单位
	 * @param balance getBalanceByAddressAndContract 方法返回值
	 * @param contract 合约地址
	 * @param mathContext 精度要求
	 * @return java.math.BigDecimal
	 * @author lingting 2020-12-11 17:20
	 * @exception Throwable 可能抛出的异常
	 */
	BigDecimal getNumberByBalanceAndContract(BigInteger balance, Contract contract, MathContext mathContext)
			throws Throwable;

	/**
	 * 指定合约金额(单位 个)转为数量
	 * @param value 金额
	 * @param contract 合约
	 * @return 返回数量
	 * @throws Throwable 异常
	 */
	default BigInteger valueToBalanceByContract(BigDecimal value, Contract contract) throws Throwable {
		return value.multiply(BigDecimal.TEN.pow(getDecimalsByContract(contract))).toBigInteger();
	}

	/**
	 * 转账
	 * @param from 转出账号
	 * @param to 转入地址
	 * @param contract 合约
	 * @param value 转账金额, 单位 个
	 * @exception Throwable 异常
	 * @return boolean
	 * @author lingting 2020-12-22 16:14
	 */
	default TransferResult transfer(Account from, String to, Contract contract, BigDecimal value) throws Throwable {
		return transfer(from, to, contract, value, new TransferParams());
	}

	/**
	 * 转账
	 * @param from 转出账号
	 * @param to 转入地址
	 * @param contract 合约
	 * @param value 转账金额, 单位 个
	 * @param params 转账参数
	 * @exception Throwable 异常
	 * @return boolean
	 * @author lingting 2020-12-22 16:14
	 */
	TransferResult transfer(Account from, String to, Contract contract, BigDecimal value, TransferParams params)
			throws Throwable;

}
