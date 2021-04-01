package live.lingting.virtual.currency.etherscan.model;

import java.math.BigInteger;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import live.lingting.virtual.currency.core.jsonrpc.JsonRpcException;
import live.lingting.virtual.currency.core.jsonrpc.http.HttpJsonRpc;
import live.lingting.virtual.currency.etherscan.util.EtherscanUtils;

/**
 * @author lingting 2021/1/19 16:57
 */
@Getter
@Setter
@Slf4j
public class Balance extends BaseResponse {

	private BigInteger amount;

	public static Balance of(HttpJsonRpc client, String address) {
		Balance res = new Balance();
		try {
			res.setAmount(EtherscanUtils
					.toBigInteger(client.invoke("eth_getBalance", String.class, address, BlockEnum.LATEST.getVal())));
		}
		catch (JsonRpcException e) {
			log.error("余额解析异常!", e);
			res.setMessage(e.getMessage());
			res.setCode(e.getCode());
		}
		return res;
	}

}
