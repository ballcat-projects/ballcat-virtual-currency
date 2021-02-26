package live.lingting.virtual.currency.etherscan.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 * @author lingting 2021/1/6 11:07
 */
@Getter
@AllArgsConstructor
public enum BlockEnum {

	/**
	 *
	 */
	EARLIEST("earliest"),
	/**
	 * 最后一个块
	 */
	LATEST("latest"),
	/**
	 * 正在打包的块
	 */
	PENDING("pending"),

	;

	private final String val;

}
