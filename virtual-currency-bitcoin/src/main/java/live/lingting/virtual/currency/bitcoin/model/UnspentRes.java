package live.lingting.virtual.currency.bitcoin.model;

import cn.hutool.http.HttpRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import live.lingting.virtual.currency.bitcoin.endpoints.BitcoinEndpoints;
import live.lingting.virtual.currency.bitcoin.endpoints.BitcoinSochainEndpoints;
import live.lingting.virtual.currency.bitcoin.endpoints.BlockchainEndpoints;
import live.lingting.virtual.currency.bitcoin.model.blockchain.BlockchainUnspentRes;
import live.lingting.virtual.currency.bitcoin.model.sochain.SochainUnspentRes;
import live.lingting.virtual.currency.core.Endpoints;
import live.lingting.virtual.currency.core.util.JacksonUtils;

/**
 * @author lingting 2021/1/7 11:19
 */
@NoArgsConstructor
@Accessors(chain = true)
public abstract class UnspentRes {

	/**
	 * 获取指定地址未使用utxo
	 * @param bitcoinEndpoints 节点
	 * @param min 最小确认数, 部分节点, 此参数无效
	 * @param address 地址
	 * @return live.lingting.virtual.currency.bitcoin.model.UnspentRes
	 * @author lingting 2021-01-08 18:40
	 */
	public static UnspentRes of(BitcoinEndpoints bitcoinEndpoints, int min, String address)
			throws JsonProcessingException {
		// 测试节点使用 sochain
		boolean isSochain = bitcoinEndpoints == BitcoinEndpoints.TEST;

		Endpoints endpoints = isSochain ? BlockchainEndpoints.MAINNET : BitcoinSochainEndpoints.TEST;

		HttpRequest request;
		// sochain 节点处理
		if (isSochain) {
			request = HttpRequest.get(endpoints.getHttpUrl("v2/get_tx_unspent/"
					// 网络 BTC
					+ ("BTCTEST/")
					// 地址
					+ address

			));
		}
		else {
			request = HttpRequest.get(endpoints.getHttpUrl("unspent?confirmations=" + min + "&active=" + address));
		}

		String response = request.execute().body();

		// sochain 节点处理
		if (isSochain) {
			if (response.contains(SochainUnspentRes.FAIL)) {
				return new SochainUnspentRes();
			}
			return JacksonUtils.toObj(response, SochainUnspentRes.class);
		}

		if (response.equals(BlockchainUnspentRes.ERROR)) {
			return new BlockchainUnspentRes().setUnspentList(Collections.emptyList());
		}
		return JacksonUtils.toObj(response, BlockchainUnspentRes.class);
	}

	public abstract List<Unspent> toUnspentList();

}
