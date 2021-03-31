package live.lingting.virtual.currency.etherscan.model;

import java.math.BigInteger;
import lombok.Getter;
import lombok.Setter;
import live.lingting.virtual.currency.core.jsonrpc.http.HttpJsonRpc;
import live.lingting.virtual.currency.etherscan.util.EtherscanUtils;

/**
 * @author lingting 2021/1/19 16:57
 */
@Getter
@Setter
public class Balance extends BaseResponse {

	private BigInteger amount;

	public static Balance of(HttpJsonRpc client, String address) throws Throwable {
		Balance res = new Balance();
		try {
			res.setAmount(EtherscanUtils
					.toBigInteger(client.invoke("eth_getBalance", String.class, address, BlockEnum.LATEST.getVal())));
		}
		catch (Exception e) {
			e.printStackTrace();
			res.setMessage(e.getMessage());
		}
		return res;
	}

}
