package live.lingting.virtual.currency.tronscan;

import java.math.BigInteger;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import live.lingting.virtual.currency.core.model.Account;
import live.lingting.virtual.currency.core.Contract;
import live.lingting.virtual.currency.core.model.TransactionGenerate;
import live.lingting.virtual.currency.tronscan.model.Transaction;

/**
 * @author lingting 2021/2/26 15:53
 */
@Getter
@Setter
@Accessors(chain = true)
public class TronscanTransactionGenerate extends TransactionGenerate {

	private Tronscan tronscan;

	public static TronscanTransactionGenerate failed(String message) {
		TronscanTransactionGenerate generate = new TronscanTransactionGenerate();
		generate.setSuccess(false).setMessage(message);
		return generate;
	}

	public static TronscanTransactionGenerate failed(Throwable e) {
		TronscanTransactionGenerate generate = new TronscanTransactionGenerate();
		generate.setSuccess(false).setMessage(e.getMessage()).setException(e);
		return generate;
	}

	private static TronscanTransactionGenerate success(Account from, String to, BigInteger amount, Contract contract) {
		TronscanTransactionGenerate generate = new TronscanTransactionGenerate();
		generate.setSuccess(true).setContract(contract).setFrom(from).setTo(to).setAmount(amount);
		return generate;
	}

	public static TronscanTransactionGenerate success(Account from, String to, BigInteger amount, Contract contract,
			Tronscan data) {
		return success(from, to, amount, contract).setTronscan(data);
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

}
