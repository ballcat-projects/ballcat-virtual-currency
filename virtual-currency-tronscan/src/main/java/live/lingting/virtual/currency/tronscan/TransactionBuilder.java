package live.lingting.virtual.currency.tronscan;

import static live.lingting.virtual.currency.tronscan.enums.TransferType.TRANSFER_CONTRACT;
import static live.lingting.virtual.currency.tronscan.enums.TransferType.TRANSFER_TRC10_CONTRACT;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.google.common.primitives.Longs;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.bouncycastle.util.encoders.Hex;
import live.lingting.virtual.currency.core.Contract;
import live.lingting.virtual.currency.core.Endpoints;
import live.lingting.virtual.currency.core.enums.AbiMethod;
import live.lingting.virtual.currency.core.model.TransferParams;
import live.lingting.virtual.currency.core.util.AbiUtils;
import live.lingting.virtual.currency.tronscan.contract.TronscanContract;
import live.lingting.virtual.currency.tronscan.model.NodeInfo;
import live.lingting.virtual.currency.tronscan.model.Transaction;
import live.lingting.virtual.currency.tronscan.util.TronscanUtils;

/**
 * 不可用. 缺少 rawDataHex. 该值通过 protobuf 序列化获取. 但我不想引入 protobuf .
 *
 * @author lingting 2021/2/26 18:03
 */
@Deprecated
@SuppressWarnings("all")
public class TransactionBuilder {

	static final String COMMA = ",";
	static final String COLON = ":";

	/**
	 * 交易过期时间.单位 毫秒; 默认一分钟. 即执行 build 后一分钟, 该次build生成的交易过期
	 */
	static final long EXPIRATION_TIME = 60 * 1000;

	private final Endpoints endpoints;

	private final Transaction transaction;

	private final Transaction.RawData rawData;

	public TransactionBuilder(Endpoints endpoints) {
		this.endpoints = endpoints;
		transaction = new Transaction();
		this.rawData = new Transaction.RawData();
		transaction.setRawData(rawData);
		transaction.setVisible(true);
	}

	public TransactionBuilder refBlock(BigInteger blockNumber, String blockHash) {
		// ref block bytes
		byte[] bytes = Longs.toByteArray(blockNumber.longValue());
		rawData.setRefBlockBytes(Hex.toHexString(new byte[] { bytes[6], bytes[7] }));

		// ref block hash
		// 标准代码: byte[] refBlockHash = ArrayUtil.sub(Hex.decode(blockHash), 8, 16);
		rawData.setRefBlockHash(blockHash.substring(16, 32));
		return this;
	}

	/**
	 * 指定当前交易在什么时间过期.
	 * @param time 过期时间的时间戳, 单位: 毫秒
	 * @return live.lingting.virtual.currency.tronscan.TransactionBuilder
	 * @author lingting 2021-02-28 15:04
	 */
	public TransactionBuilder expirationAt(Long time) {
		rawData.setExpiration(time);
		return this;
	}

	/**
	 * 转账
	 * @param from 转出地址
	 * @param to 转入地址
	 * @param contract 合约
	 * @param value 转出金额
	 * @param params 转账参数
	 * @return live.lingting.virtual.currency.tronscan.TransactionBuilder
	 * @author lingting 2021-03-01 09:43
	 */
	public TransactionBuilder transfer(String from, String to, Contract contract, BigDecimal value,
			TransferParams params) {
		Assert.isTrue(value.compareTo(BigDecimal.ZERO) > 0, "转出金额必须大于0!");
		Assert.isTrue(StrUtil.isNotBlank(from), "转出地址不能为空!");
		Assert.isTrue(StrUtil.isNotBlank(to), "转入地址不能为空!");
		Assert.isTrue(contract != null, "合约不能为空!");
		Assert.isTrue(contract.getDecimals() != null, "合约精度不能为空");

		if (TronscanUtils.isHexAddress(from)) {
			from = TronscanUtils.hexToBase(from);
		}

		if (TronscanUtils.isHexAddress(to)) {
			to = TronscanUtils.hexToBase(to);
		}

		// 计算
		BigInteger amount = value.multiply(BigDecimal.TEN.pow(contract.getDecimals())).toBigInteger();

		if (contract == TronscanContract.TRX) {
			rawData.setContract(ListUtil.toList(new Transaction.RawData.Contract()
					// type
					.setType(TRANSFER_CONTRACT.getType())
					// 合约数据
					.setParameter(new Transaction.RawData.Contract.Parameter().setTypeUrl(TRANSFER_CONTRACT.getUrl())
							.setValue(new Transaction.RawData.Contract.Parameter.Value()
									// 转出地址
									.setOwnerAddress(from)
									// 转入地址
									.setToAddress(to).setAmount(amount)))));
		}
		// trc10
		else if (!TronscanUtils.isTrc20(contract.getHash())) {
			rawData.setContract(ListUtil.toList(new Transaction.RawData.Contract()
					// type
					.setType(TRANSFER_TRC10_CONTRACT.getType())
					// 合约数据
					.setParameter(
							new Transaction.RawData.Contract.Parameter().setTypeUrl(TRANSFER_TRC10_CONTRACT.getUrl())
									.setValue(new Transaction.RawData.Contract.Parameter.Value()
											// 转出地址
											.setOwnerAddress(from)
											// 转入地址
											.setToAddress(to)
											// hash
											.setAssetName(contract.getHash()).setAmount(amount)))));
		}
		// trc20
		else {
			BigInteger feeLimit = params.getFeeLimit() != null ? params.getFeeLimit() : BigInteger.TEN.pow(9);
			BigInteger callValue = params.getCallValue() != null ? params.getCallValue() : BigInteger.ZERO;
			rawData.setFeeLimit(feeLimit);
			rawData.setContract(ListUtil.toList(new Transaction.RawData.Contract()
					// type
					.setType(TRANSFER_TRC10_CONTRACT.getType())
					// 合约数据
					.setParameter(new Transaction.RawData.Contract.Parameter()
							// type url
							.setTypeUrl(TRANSFER_TRC10_CONTRACT.getUrl())
							// value
							.setValue(new Transaction.RawData.Contract.Parameter.Value()
									// 转出地址
									.setOwnerAddress(from)
									// 转入地址
									.setToAddress(to)
									// call value
									.setCallValue(callValue)
									// data
									.setData(
											// abi
											AbiMethod.TRANSFER.getMethodId() +
											// 收款人
													TronscanUtils.encodeAddressParam(to)
													// 转账金额
													+ AbiUtils.encodeUint256Params(amount))

							))));
		}

		return this;
	}

	public Transaction build() {
		if (StrUtil.isBlank(rawData.getRefBlockBytes()) || StrUtil.isBlank(rawData.getRefBlockHash())) {
			NodeInfo nodeInfo = NodeInfo.of(endpoints);
			// 切割
			String[] split = nodeInfo.getSolidityBlock().split(COMMA);
			refBlock(new BigInteger(split[0].split(COLON)[1]), split[1].split(COLON)[1]);
		}

		long timestamp = System.currentTimeMillis();
		rawData.setTimestamp(timestamp);

		if (rawData.getExpiration() == null) {
			expirationAt(timestamp + EXPIRATION_TIME);
		}

		// 生成 raw data hex

		return this.transaction;
	}

}
