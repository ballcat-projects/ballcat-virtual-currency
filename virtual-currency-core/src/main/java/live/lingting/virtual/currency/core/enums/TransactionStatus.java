package live.lingting.virtual.currency.core.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 交易状态
 *
 * @author lingting 2020-09-02 13:53
 */
@Getter
@AllArgsConstructor
public enum TransactionStatus {

	/**
	 * 交易状态
	 */
	SUCCESS(1, "交易成功"),

	WAIT(2, "等待处理"),

	FAIL(3, "交易失败"),

	;

	private final Integer value;

	private final String desc;

	@JsonCreator
	public static TransactionStatus of(String str) {
		TransactionStatus[] enums = TransactionStatus.values();
		for (TransactionStatus e : enums) {
			if (e.toString().equals(str)) {
				return e;
			}
		}
		return null;
	}

}
