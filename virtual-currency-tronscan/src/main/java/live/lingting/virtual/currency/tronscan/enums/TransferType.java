package live.lingting.virtual.currency.tronscan.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author lingting 2021/3/1 10:18
 */
@Getter
@AllArgsConstructor
public enum TransferType {

	/**
	 * trx转账
	 */
	TRANSFER_CONTRACT("TransferContract", "type.googleapis.com/protocol.TransferContract"),
	/**
	 * trc10
	 */
	TRANSFER_TRC10_CONTRACT("TransferAssetContract", "type.googleapis.com/protocol.TransferAssetContract"),
	/**
	 * trc20
	 */
	TRANSFER_TRC20_CONTRACT("TriggerSmartContract", "type.googleapis.com/protocol.TriggerSmartContract"),

	;

	private final String type;

	private final String url;

}
