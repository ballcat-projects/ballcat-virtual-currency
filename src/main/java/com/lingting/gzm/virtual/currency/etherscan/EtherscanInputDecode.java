package com.lingting.gzm.virtual.currency.etherscan;

import static com.lingting.gzm.virtual.currency.util.ResolveUtil.removePreZero;
import static com.lingting.gzm.virtual.currency.util.ResolveUtil.stringToArrayBy64;

import com.lingting.gzm.virtual.currency.contract.EtherscanContract;
import com.lingting.gzm.virtual.currency.exception.VirtualCurrencyException;
import java.math.BigDecimal;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.var;

/**
 * @author lingting 2020-09-02 14:20
 */
public class EtherscanInputDecode {

	public static final String START = "0x";

	/**
	 * 解析input数据
	 *
	 * @author lingting 2020-09-02 14:23
	 */
	public static Input resolveInput(String inputString) throws VirtualCurrencyException {
		// 获取方法id
		String methodId = inputString.substring(0, 10);
		Input input = new Input().setMethod(Input.MethodEnum.getById(methodId)).setData(inputString);

		if (input.getMethod() == null) {
			throw new VirtualCurrencyException("无法正确解析input data 请额外开发支持");
		}
		// 处理
		input.getMethod().handler.accept(input);
		return input;
	}

	@Data
	@Accessors(chain = true)
	public static class Input {

		/**
		 * 方法id
		 */
		private MethodEnum method;

		/**
		 * 原始数据
		 */
		private String data;

		/**
		 * 收款地址
		 */
		private String to;

		/**
		 * 单位 个
		 */
		private BigDecimal value;

		/**
		 * 合约
		 */
		private EtherscanContract contract;

		/**
		 * 合约地址
		 */
		private String contractAddress;

		public Input setTo(String to) {
			if (to.startsWith(START)) {
				this.to = to;
			}
			else {
				this.to = START + removePreZero(to);
			}
			return this;
		}

		@Getter
		@AllArgsConstructor
		public enum MethodEnum {

			/**
			 * 各种 abi 方法处理
			 */
			TRANSFER("0xa9059cbb", input -> {
				String[] array = stringToArrayBy64(input.getData().substring(10));
				input.setTo(array[0]);
				input.setValue(new BigDecimal(Long.parseLong(array[1], 16)));
			}),

			SEND_MULTI_SIG_TOKEN("0x0dcd7a6c", input -> {
				String[] array = stringToArrayBy64(input.getData().substring(10));
				input.setTo(array[0]);
				input.setValue(new BigDecimal(Long.parseLong(array[1], 16)));
				String address = START + removePreZero(array[2]);
				input.setContract(EtherscanContract.getByHash(address));
				input.setContractAddress(address);
			}),

			SEND_MULTI_SIG("0x39125215", input -> {
				String[] array = stringToArrayBy64(input.getData().substring(10));
				input.setTo(array[0]);
				input.setValue(new BigDecimal(Long.parseLong(array[1], 16)));
			}),

			;

			private final String id;

			private final Consumer<Input> handler;

			public static MethodEnum getById(String id) {
				for (var e : values()) {
					if (e.getId().equalsIgnoreCase(id)) {
						return e;
					}
				}
				return null;
			}

		}

	}

}
