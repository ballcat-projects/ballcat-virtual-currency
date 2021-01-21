package live.lingting.virtual.currency;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author lingting 2020/12/22 17:23
 */
@Data
@Accessors(chain = true)
public class TransferResult {

	/**
	 * 是否成功
	 */
	private Boolean success;

	/**
	 * 交易hash
	 */
	private String hash;

	/**
	 * 如果失败, 错误码, 可能为空
	 */
	private String code;

	/**
	 * 如果失败, 错误信息
	 */
	private String message;

	/**
	 * 对应异常数据, 可能为 null
	 */
	private Throwable exception;

	public static TransferResult success(String hash) {
		return new TransferResult()
				// 成功
				.setSuccess(true)
				// hash
				.setHash(hash);
	}

	public static TransferResult failed(String message) {
		return failed(message, null);
	}

	public static TransferResult failed(Throwable e) {
		return failed(e.getMessage(), e);
	}

	public static TransferResult failed(String message, Throwable e) {
		return new TransferResult()
				// 成功
				.setSuccess(false)
				// message
				.setMessage(message)
				// Exception
				.setException(e);
	}

	public static TransferResult failed(TransactionGenerate generate) {
		return failed(generate.getMessage(), generate.getException());
	}

}
