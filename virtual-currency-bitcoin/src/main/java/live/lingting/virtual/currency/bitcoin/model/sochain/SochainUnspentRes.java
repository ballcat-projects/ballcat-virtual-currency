package live.lingting.virtual.currency.bitcoin.model.sochain;

import cn.hutool.core.collection.CollectionUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
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
import live.lingting.virtual.currency.bitcoin.contract.OmniContract;
import live.lingting.virtual.currency.bitcoin.model.Unspent;
import live.lingting.virtual.currency.bitcoin.model.UnspentRes;

/**
 * @author lingting 2021/2/26 17:04
 */
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class SochainUnspentRes extends UnspentRes {

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
		private List<ResData.Txs> txs;

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
