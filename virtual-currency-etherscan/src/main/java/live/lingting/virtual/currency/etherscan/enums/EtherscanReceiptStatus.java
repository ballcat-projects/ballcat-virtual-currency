package live.lingting.virtual.currency.etherscan.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 以太坊交易凭据状态
 *
 * @author lingting 2020-09-02 14:34
 */
@Getter
@AllArgsConstructor
public enum EtherscanReceiptStatus {

	/**
	 * 交易状态
	 */
	SUCCESS("0x1"),

	;

	private final String value;

	@JsonCreator
	public static EtherscanReceiptStatus of(String str) {
		EtherscanReceiptStatus[] enums = EtherscanReceiptStatus.values();
		for (EtherscanReceiptStatus e : enums) {
			if (e.toString().equals(str)) {
				return e;
			}
		}
		return null;
	}

}
