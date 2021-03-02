package live.lingting.virtual.currency.core;

/**
 * 代币顶级接口
 *
 * @author lingting 2020-09-02 13:59
 */
public interface Contract {

	/**
	 * 获取合约 hash
	 * @return java.lang.String
	 * @author lingting 2020-09-02 14:00
	 */
	String getHash();

	/**
	 * 获取合约精度
	 * @return java.lang.Integer
	 * @author lingting 2020-12-25 19:51
	 */
	Integer getDecimals();

}
