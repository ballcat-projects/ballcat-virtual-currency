package live.lingting.virtual.currency.etherscan.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author lingting 2021/1/5 19:58
 */
@Getter
@Setter
public abstract class BaseResponse {

	private Long code;

	private String message;

	public boolean hasError() {
		return code != null;
	}

}
