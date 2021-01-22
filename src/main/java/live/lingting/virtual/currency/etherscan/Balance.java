package live.lingting.virtual.currency.etherscan;

import java.math.BigInteger;
import lombok.Getter;
import lombok.Setter;
import live.lingting.virtual.currency.core.JsonRpcClient;
import live.lingting.virtual.currency.util.EtherscanUtil;

/**
 * @author lingting 2021/1/19 16:57
 */
@Getter
@Setter
public class Balance extends BaseResponse {

	public static Balance of(JsonRpcClient client, String address) throws Throwable {
		Balance res = new Balance();
		try {
			res.setAmount(EtherscanUtil
					.toBigInteger(client.invoke("eth_getBalance", String.class, address, BlockEnum.LATEST.getVal())));
		}
		catch (Exception e) {
			e.printStackTrace();
			res.setMessage(e.getMessage());
		}
		return res;
	}

	private BigInteger amount;

}
