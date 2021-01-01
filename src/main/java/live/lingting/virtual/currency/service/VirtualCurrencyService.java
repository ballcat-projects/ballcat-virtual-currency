package live.lingting.virtual.currency.service;

import live.lingting.virtual.currency.TransferParams;
import live.lingting.virtual.currency.VirtualCurrencyAccount;
import live.lingting.virtual.currency.VirtualCurrencyTransaction;
import live.lingting.virtual.currency.VirtualCurrencyTransferResult;
import live.lingting.virtual.currency.contract.Contract;
import live.lingting.virtual.currency.contract.EtherscanContract;
import live.lingting.virtual.currency.contract.OmniContract;
import live.lingting.virtual.currency.contract.TronscanContract;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Optional;

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
	 * @exception Exception 可能抛出的异常
	 */
	Optional<VirtualCurrencyTransaction> getTransactionByHash(String hash) throws Exception;

	/**
	 * 获取合约的精度, 请注意不同平台不同api的返回结果不一致
	 * @param contract 合约
	 * @return 精度
	 * @author lingting 2020-12-11 16:09
	 * @exception Exception 可能抛出的异常
	 */
	Integer getDecimalsByContract(Contract contract) throws Exception;

	/**
	 * 查询指定地址, 指定合约余额. 不同平台请使用不同平台对应的 contract . eth {@link EtherscanContract}. omni
	 * {@link OmniContract}. tronscan {@link TronscanContract}.
	 * @param address 账号地址
	 * @param contract 合约
	 * @return java.math.BigDecimal
	 * @author lingting 2020-12-10 23:56
	 * @exception Exception 可能抛出的异常
	 */
	BigDecimal getBalanceByAddressAndContract(String address, Contract contract) throws Exception;

	/**
	 * 获取指定地址, 指定合约余额 (单位: 个)
	 * @param address 地址
	 * @param contract 余额
	 * @return java.math.BigDecimal
	 * @author lingting 2020-12-11 19:47
	 * @exception Exception 可能抛出的异常
	 */
	default BigDecimal getNumberByAddressAndContract(String address, Contract contract) throws Exception {
		return getNumberByBalanceAndContract(getBalanceByAddressAndContract(address, contract), contract);
	}

	/**
	 * 通过
	 * {@link VirtualCurrencyService#getBalanceByAddressAndContract(java.lang.String, Contract)}
	 * 方法的余额单位不一定是个, 会附带小数, 可执行此方法转换为以个为单位
	 * @param balance getBalanceByAddressAndContract 方法返回值
	 * @param contract 合约地址
	 * @return java.math.BigDecimal
	 * @author lingting 2020-12-11 17:20
	 * @exception Exception 可能抛出的异常
	 */
	default BigDecimal getNumberByBalanceAndContract(BigDecimal balance, Contract contract) throws Exception {
		return getNumberByBalanceAndContract(balance, contract, MathContext.UNLIMITED);
	}

	/**
	 * 通过
	 * {@link VirtualCurrencyService#getBalanceByAddressAndContract(java.lang.String, Contract)}
	 * 方法的余额单位不一定是个, 会附带小数, 可执行此方法转换为以个为单位
	 * @param balance getBalanceByAddressAndContract 方法返回值
	 * @param contract 合约地址
	 * @param mathContext 精度要求
	 * @return java.math.BigDecimal
	 * @author lingting 2020-12-11 17:20
	 * @exception Exception 可能抛出的异常
	 */
	BigDecimal getNumberByBalanceAndContract(BigDecimal balance, Contract contract, MathContext mathContext)
			throws Exception;

	/**
	 * 转账
	 * @param from 转出账号
	 * @param to 转入地址
	 * @param contract 合约
	 * @param value 转账金额, 单位 个
	 * @exception Exception 异常
	 * @return boolean
	 * @author lingting 2020-12-22 16:14
	 */
	default VirtualCurrencyTransferResult transfer(VirtualCurrencyAccount from, String to, Contract contract,
			BigDecimal value) throws Exception {
		return transfer(from, to, contract, value, new TransferParams());
	}

	/**
	 * 转账
	 * @param from 转出账号
	 * @param to 转入地址
	 * @param contract 合约
	 * @param value 转账金额, 单位 个
	 * @param params 转账参数
	 * @exception Exception 异常
	 * @return boolean
	 * @author lingting 2020-12-22 16:14
	 */
	VirtualCurrencyTransferResult transfer(VirtualCurrencyAccount from, String to, Contract contract, BigDecimal value,
			TransferParams params) throws Exception;

}
