package live.lingting.virtual.currency.core;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.List;
import java.util.Optional;
import live.lingting.virtual.currency.core.model.Account;
import live.lingting.virtual.currency.core.model.TransactionInfo;
import live.lingting.virtual.currency.core.model.TransferParams;
import live.lingting.virtual.currency.core.model.TransferResult;

/**
 * 虚拟货币处理接口类
 *
 * @author lingting 2020-09-01 17:15
 */
public interface PlatformService<G, Q> {

	/**
	 * 通过交易hash获取交易信息
	 * @param hash 交易hash
	 * @return 交易信息
	 * @exception Throwable 可能抛出的异常
	 */
	Optional<TransactionInfo> getTransactionByHash(String hash) throws Throwable;

	/**
	 * 获取合约的精度, 请注意不同平台不同api的返回结果不一致
	 * @param contract 合约
	 * @return 精度
	 * @author lingting 2020-12-11 16:09
	 * @exception Throwable 可能抛出的异常
	 */
	Integer getDecimalsByContract(Contract contract) throws Throwable;

	/**
	 * 查询指定地址, 指定合约余额. 不同平台请使用不同平台对应的 contract
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
	 * 转账生成
	 * @param from 转出账号
	 * @param to 转入地址
	 * @param contract 合约
	 * @param value 转账金额, 单位 个
	 * @param params 转账参数
	 * @exception Throwable 异常
	 * @return boolean
	 * @author lingting 2020-12-22 16:14
	 */
	G transactionGenerate(Account from, String to, Contract contract, BigDecimal value, TransferParams params)
			throws Throwable;

	/**
	 * 对交易进行签名
	 * @param generate 通过 transactionGenerate 方法生成的数据
	 * @return live.lingting.virtual.currency.core.TransactionGenerate
	 * @author lingting 2021-01-20 17:07
	 * @exception Throwable 异常
	 */
	G transactionSign(G generate) throws Throwable;

	/**
	 * 广播交易
	 * @param generate 通过 transactionSign 方法生成的数据
	 * @return live.lingting.virtual.currency.core.TransferResult
	 * @author lingting 2021-01-20 17:10
	 * @exception Throwable 异常
	 */
	TransferResult transactionBroadcast(G generate) throws Throwable;

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
	default TransferResult transfer(Account from, String to, Contract contract, BigDecimal value, TransferParams params)
			throws Throwable {
		return transactionBroadcast(transactionSign(transactionGenerate(from, to, contract, value, params)));
	}

	/**
	 * 校验地址是否正确
	 * @param address 地址
	 * @return boolean true 正确地址
	 * @author lingting 2021-01-19 15:41
	 */
	boolean validate(String address) throws Throwable;

	/**
	 * 查询指定地址的历史交易记录
	 * @param query 查询条件- 各个平台实现都不一致
	 * @throws Throwable 抛出异常
	 * @return java.util.List<live.lingting.virtual.currency.core.model.TransactionInfo>
	 * @author lingting 2021-03-15 11:13
	 */
	List<TransactionInfo> listHistoryByAddress(Q query) throws Throwable;

}
