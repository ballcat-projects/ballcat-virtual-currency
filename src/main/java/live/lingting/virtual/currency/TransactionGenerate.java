package live.lingting.virtual.currency;

import java.math.BigInteger;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.NetworkParameters;
import org.bouncycastle.util.encoders.Hex;
import org.web3j.crypto.RawTransaction;
import live.lingting.virtual.currency.contract.Contract;
import live.lingting.virtual.currency.tronscan.Transaction;

/**
 * @author lingting 2021/1/20 16:37
 */
@Data
@Accessors(chain = true)
public class TransactionGenerate {

	private String message;

	private Boolean success;

	/**
	 * 对应异常数据, 可能为 null
	 */
	private Throwable exception;

	private Contract contract;

	private Account from;

	private String to;

	private BigInteger amount;

	/**
	 * 签名后用于广播的原始交易数据
	 */
	private String signHex;

	private Bitcoin bitcoin;

	private Etherscan etherscan;

	private Tronscan tronscan;

	public static TransactionGenerate failed(String message) {
		return new TransactionGenerate().setSuccess(false).setMessage(message);
	}

	public static TransactionGenerate failed(Throwable e) {
		return new TransactionGenerate().setSuccess(false).setMessage(e.getMessage()).setException(e);
	}

	private static TransactionGenerate success(Account from, String to, BigInteger amount, Contract contract) {
		return new TransactionGenerate().setSuccess(true).setContract(contract).setFrom(from).setTo(to)
				.setAmount(amount);
	}

	public static TransactionGenerate success(Account from, String to, BigInteger amount, Contract contract,
			Tronscan data) {
		return success(from, to, amount, contract).setTronscan(data);
	}

	public static TransactionGenerate success(Account from, String to, BigInteger amount, Contract contract,
			Etherscan data) {
		return success(from, to, amount, contract).setEtherscan(data);
	}

	public static TransactionGenerate success(Account from, String to, BigInteger amount, Contract contract,
			Bitcoin data) {
		return success(from, to, amount, contract).setBitcoin(data);
	}

	/**
	 * 生成用于二次签名
	 * @param from 发送账号
	 * @param raw 交易原始数据
	 * @return live.lingting.virtual.currency.TransactionGenerate
	 * @author lingting 2021-01-20 21:37
	 */
	public static TransactionGenerate ofBitcoinRaw(Account from, NetworkParameters np, String raw) {
		return ofBitcoinRaw(from, new org.bitcoinj.core.Transaction(np, Hex.decode(raw)));
	}

	/**
	 * 生成用于p2sh二次签名(指非第一次签名, 不一定是二次签名). 如果是第一次签名, 请手动修改返回值中 .bitcoin.firstSign 属性值为 true
	 * @param from 发送账号
	 * @param transaction 交易
	 * @return live.lingting.virtual.currency.TransactionGenerate
	 * @author lingting 2021-01-20 21:37
	 */
	public static TransactionGenerate ofBitcoinRaw(Account from, org.bitcoinj.core.Transaction transaction) {
		return new TransactionGenerate().setFrom(from)
				.setBitcoin(new Bitcoin().setTransaction(transaction).setFirstSign(false)).setSuccess(true);
	}

	@Data
	@Accessors(chain = true)
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Etherscan {

		BigInteger nonce;

		BigInteger gasPrice;

		BigInteger gasLimit;

		RawTransaction rawTransaction;

	}

	@Data
	@Accessors(chain = true)
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Tronscan {

		String txId;

		Transaction.RawData rawData;

		String rawDataHex;

		BigInteger feeLimit;

		BigInteger callValue;

	}

	@Data
	@Accessors(chain = true)
	@NoArgsConstructor
	public static class Bitcoin {

		org.bitcoinj.core.Transaction transaction;

		Coin sumFee;

		/**
		 * 是否为第一次签名, 主要针对 p2sh 脚本, 非第一次签名
		 */
		Boolean firstSign = true;

		public Bitcoin(org.bitcoinj.core.Transaction transaction, Coin sumFee) {
			this.transaction = transaction;
			this.sumFee = sumFee;
		}

	}

}
