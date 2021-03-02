package live.lingting.virtual.currency.core.exception;

import java.math.BigDecimal;
import lombok.Getter;
import org.bitcoinj.core.Coin;

/**
 * 账户余额不足
 *
 * @author lingting 2021/3/2 11:16
 */
public class InsufficientBalanceException extends VirtualCurrencyException {

	@Getter
	private final BigDecimal amount;

	public InsufficientBalanceException(Coin coin) {
		super("账户余额不足: " + coin.toPlainString() + ";执行失败!");
		amount = new BigDecimal(coin.toPlainString());
	}

}
