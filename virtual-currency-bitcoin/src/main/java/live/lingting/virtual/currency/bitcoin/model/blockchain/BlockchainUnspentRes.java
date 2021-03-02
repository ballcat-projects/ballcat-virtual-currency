package live.lingting.virtual.currency.bitcoin.model.blockchain;

import cn.hutool.core.collection.CollectionUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import live.lingting.virtual.currency.bitcoin.model.Unspent;
import live.lingting.virtual.currency.bitcoin.model.UnspentRes;

/**
 * @author lingting 2021/2/26 17:04
 */
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class BlockchainUnspentRes extends UnspentRes {

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
			list.add(new Unspent().setHash(un.txHashBigEndian).setConfirmations(un.confirmations).setOut(un.txOutputN)
					.setScript(un.script).setValue(un.value));
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
