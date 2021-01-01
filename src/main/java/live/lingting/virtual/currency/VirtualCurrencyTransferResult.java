package live.lingting.virtual.currency;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author lingting 2020/12/22 17:23
 */
@Data
@Accessors(chain = true)
public class VirtualCurrencyTransferResult {

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
	private Exception exception;

}
