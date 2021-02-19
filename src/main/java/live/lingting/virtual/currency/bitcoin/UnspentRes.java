package live.lingting.virtual.currency.bitcoin;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.http.HttpRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import live.lingting.virtual.currency.contract.OmniContract;
import live.lingting.virtual.currency.endpoints.BitcoinEndpoints;
import live.lingting.virtual.currency.endpoints.Endpoints;
import live.lingting.virtual.currency.util.JsonUtil;

/**
 * @author lingting 2021/1/7 11:19
 */
@NoArgsConstructor
@Accessors(chain = true)
public abstract class UnspentRes {

	public abstract List<Unspent> toUnspentList();

	/**
	 * 获取指定地址未使用utxo
	 * @param endpoints 节点
	 * @param min 最小确认数, 部分节点, 此参数无效
	 * @param address 地址
	 * @return live.lingting.virtual.currency.bitcoin.UnspentRes
	 * @author lingting 2021-01-08 18:40
	 */
	public static UnspentRes of(Endpoints endpoints, int min, String address) throws JsonProcessingException {
		boolean isSochain = endpoints.getHttp().contains("sochain.com");
		HttpRequest request;
		// sochain 节点处理
		if (isSochain) {
			request = HttpRequest.get(endpoints.getHttpUrl("v2/get_tx_unspent/"
					// 网络
					+ (endpoints == BitcoinEndpoints.SOCHAIN_MAINNET ? "BTC/" : "BTCTEST/")
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
			if (response.contains(Sochain.FAIL)) {
				return new Sochain();
			}
			return JsonUtil.toObj(response, Sochain.class);
		}

		if (response.equals(Blockchain.ERROR)) {
			return new Blockchain().setUnspentList(new ArrayList<>());
		}
		return JsonUtil.toObj(response, Blockchain.class);
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@Accessors(chain = true)
	public static class Sochain extends UnspentRes {

		public static final String FAIL = "fail";

		private String status;

		private ResData data;

		@Override
		public List<Unspent> toUnspentList() {
			if (data == null || CollectionUtil.isEmpty(data.txs)) {
				return Collections.emptyList();
			}
			List<Unspent> list = new ArrayList<>(data.txs.size());

			for (ResData.Txs tx : data.txs) {
				list.add(new Unspent()
						// 金额 处理成 btc 数量
						.setValue(tx.value.multiply(BigDecimal.TEN.pow(OmniContract.BTC.getDecimals())).toBigInteger())
						// 脚本hash
						.setScript(tx.getScriptHex()).setHash(tx.txid).setOut(tx.outputNo)
						.setConfirmations(tx.confirmations));
			}

			return list;
		}

		@NoArgsConstructor
		@Data
		public static class ResData {

			@JsonProperty("network")
			private String network;

			@JsonProperty("address")
			private String address;

			@JsonProperty("txs")
			private List<Txs> txs;

			@NoArgsConstructor
			@Data
			public static class Txs {

				@JsonProperty("txid")
				private String txid;

				@JsonProperty("output_no")
				private Long outputNo;

				@JsonProperty("script_asm")
				private String scriptAsm;

				@JsonProperty("script_hex")
				private String scriptHex;

				@JsonProperty("value")
				private BigDecimal value;

				@JsonProperty("confirmations")
				private BigInteger confirmations;

				@JsonProperty("time")
				private Long time;

			}

		}

	}

	@Getter
	@Setter
	@NoArgsConstructor
	@Accessors(chain = true)
	public static class Blockchain extends UnspentRes {

		public static final String ERROR = "No free outputs to spend";

		@JsonProperty("notice")
		private String notice;

		@JsonProperty("unspent_outputs")
		private List<BlockchainUnspent> unspentList;

		@Override
		public List<Unspent> toUnspentList() {
			if (CollectionUtil.isEmpty(unspentList)) {
				return Collections.emptyList();
			}
			List<Unspent> list = new ArrayList<>(unspentList.size());

			for (BlockchainUnspent un : unspentList) {
				list.add(new Unspent().setHash(un.txHashBigEndian).setConfirmations(un.confirmations)
						.setOut(un.txOutputN).setScript(un.script).setValue(un.value));
			}

			return list;
		}

		@NoArgsConstructor
		@Data
		public static class BlockchainUnspent {

			@JsonProperty("tx_hash")
			private String txHash;

			/**
			 * tx id
			 */
			@JsonProperty("tx_hash_big_endian")
			private String txHashBigEndian;

			@JsonProperty("tx_output_n")
			private Long txOutputN;

			@JsonProperty("script")
			private String script;

			/**
			 * 交易数量
			 */
			@JsonProperty("value")
			private BigInteger value;

			@JsonProperty("value_hex")
			private String valueHex;

			@JsonProperty("confirmations")
			private BigInteger confirmations;

			@JsonProperty("tx_index")
			private BigInteger txIndex;

			public String getTxId() {
				return txHashBigEndian;
			}

		}

	}

}
