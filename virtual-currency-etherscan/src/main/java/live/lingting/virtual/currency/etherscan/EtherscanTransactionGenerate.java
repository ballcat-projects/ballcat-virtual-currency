package live.lingting.virtual.currency.etherscan;

import java.math.BigInteger;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.web3j.crypto.RawTransaction;
import live.lingting.virtual.currency.core.model.Account;
import live.lingting.virtual.currency.core.Contract;
import live.lingting.virtual.currency.core.model.TransactionGenerate;

/**
 * @author lingting 2021/2/26 15:51
 */
@Getter
@Setter
@Accessors(chain = true)
public class EtherscanTransactionGenerate extends TransactionGenerate {

	private Etherscan etherscan;

	public static EtherscanTransactionGenerate failed(String message) {
		EtherscanTransactionGenerate generate = new EtherscanTransactionGenerate();
		generate.setSuccess(false).setMessage(message);
		return generate;
	}

	public static EtherscanTransactionGenerate failed(Throwable e) {
		EtherscanTransactionGenerate generate = new EtherscanTransactionGenerate();
		generate.setSuccess(false).setMessage(e.getMessage()).setException(e);
		return generate;
	}

	private static EtherscanTransactionGenerate success(Account from, String to, BigInteger amount, Contract contract) {
		EtherscanTransactionGenerate generate = new EtherscanTransactionGenerate();
		generate.setSuccess(true).setContract(contract).setFrom(from).setTo(to).setAmount(amount);
		return generate;
	}

	public static EtherscanTransactionGenerate success(Account from, String to, BigInteger amount, Contract contract,
			Etherscan data) {
		return success(from, to, amount, contract).setEtherscan(data);
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

}
