package live.lingting.virtual.currency.core.exception;

import static live.lingting.virtual.currency.core.Mnemonic.QUANTITY_MAX;

/**
 * @author lingting 2021/3/2 11:25
 */
public class MnemonicNumberException extends VirtualCurrencyException {

	public MnemonicNumberException() {
		super("助记词数量必须为3的倍数且大于11且小于等于" + QUANTITY_MAX);
	}

	public MnemonicNumberException(String message) {
		super(message);
	}

}
