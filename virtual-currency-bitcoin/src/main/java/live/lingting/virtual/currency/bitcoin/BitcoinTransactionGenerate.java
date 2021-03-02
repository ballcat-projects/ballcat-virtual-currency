package live.lingting.virtual.currency.bitcoin;

import java.math.BigInteger;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.NetworkParameters;
import org.bouncycastle.util.encoders.Hex;
import live.lingting.virtual.currency.core.model.Account;
import live.lingting.virtual.currency.core.Contract;
import live.lingting.virtual.currency.core.model.TransactionGenerate;

/**
 * @author lingting 2021/2/26 15:43
 */
@Getter
@Setter
@Accessors(chain = true)
public class BitcoinTransactionGenerate extends TransactionGenerate {

	private Bitcoin bitcoin;

	public static BitcoinTransactionGenerate failed(String message) {
		BitcoinTransactionGenerate generate = new BitcoinTransactionGenerate();
		generate.setSuccess(false).setMessage(message);
		return generate;
	}

	public static BitcoinTransactionGenerate failed(Throwable e) {
		BitcoinTransactionGenerate generate = new BitcoinTransactionGenerate();
		generate.setSuccess(false).setMessage(e.getMessage()).setException(e);
		return generate;
	}

	private static BitcoinTransactionGenerate success(Account from, String to, BigInteger amount, Contract contract) {
		BitcoinTransactionGenerate generate = new BitcoinTransactionGenerate();
		generate.setSuccess(true).setContract(contract).setFrom(from).setTo(to).setAmount(amount);
		return generate;
	}

	public static BitcoinTransactionGenerate success(Account from, String to, BigInteger amount, Contract contract,
			Bitcoin data) {
		return success(from, to, amount, contract).setBitcoin(data);
	}

	/**
	 * 生成用于二次签名
	 * @param from 发送账号
	 * @param raw 交易原始数据
	 * @return live.lingting.virtual.currency.core.BitcoinTransactionGenerate
	 * @author lingting 2021-01-20 21:37
	 */
	public static BitcoinTransactionGenerate ofBitcoinRaw(Account from, NetworkParameters np, String raw) {
		return ofBitcoinRaw(from, new org.bitcoinj.core.Transaction(np, Hex.decode(raw)));
	}

	/**
	 * 生成用于p2sh二次签名(指非第一次签名, 不一定是二次签名). 如果是第一次签名, 请手动修改返回值中 .bitcoin.firstSign 属性值为 true
	 * @param from 发送账号
	 * @param transaction 交易
	 * @return live.lingting.virtual.currency.core.BitcoinTransactionGenerate
	 * @author lingting 2021-01-20 21:37
	 */
	public static BitcoinTransactionGenerate ofBitcoinRaw(Account from, org.bitcoinj.core.Transaction transaction) {
		BitcoinTransactionGenerate generate = new BitcoinTransactionGenerate();
		generate.setFrom(from).setSuccess(true);
		return generate.setBitcoin(new Bitcoin().setTransaction(transaction).setFirstSign(false));
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
