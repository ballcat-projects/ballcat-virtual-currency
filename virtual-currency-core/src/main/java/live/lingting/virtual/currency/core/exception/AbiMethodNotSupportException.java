package live.lingting.virtual.currency.core.exception;

/**
 * 不支持此abi方法
 *
 * @author lingting 2021/3/2 11:20
 */
public class AbiMethodNotSupportException extends VirtualCurrencyException {

	public AbiMethodNotSupportException(String rawData) {
		super("暂不支持此ABI方法: " + rawData.substring(0, 8));
	}

}
