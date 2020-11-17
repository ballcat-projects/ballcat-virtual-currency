package com.lingting.gzm.virtual.currency.util;

import cn.hutool.core.util.StrUtil;
import com.lingting.gzm.virtual.currency.contract.Contract;
import com.lingting.gzm.virtual.currency.contract.Etherscan;
import com.lingting.gzm.virtual.currency.enums.Protocol;
import com.lingting.gzm.virtual.currency.exception.TransactionException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.var;

/**
 * @author lingting 2020-09-02 14:20
 */
public class EtherscanUtil {

	public static final String REMOVE_ZERO_REG_STR = "^(0+)";

	/**
	 * 价格单位 1000000:1
	 */
	public static final BigDecimal USDT_FLAG = new BigDecimal(1000000);

	public static final BigDecimal ETH = new BigDecimal(1000000000000000000L);

	/**
	 * 传入删除 methodId的 input data
	 *
	 * @author lingting 2020-11-17 21:40
	 */
	public static String[] stringToArray(String str) {
		return StrUtil.cut(str, 64);
	}

	/**
	 * 移除字符串前的0
	 *
	 * @author lingting 2020-11-17 21:44
	 */
	public static String removePreZero(String str) {
		return str.replaceAll(REMOVE_ZERO_REG_STR, "");
	}

	/**
	 * 解析input数据
	 *
	 * @author lingting 2020-09-02 14:23
	 */
	public static Input resolveInput(String inputString) throws TransactionException {
		// 获取方法id
		String methodId = inputString.substring(0, 10);
		Input input = new Input().setMethod(Input.MethodEnum.getById(methodId)).setData(inputString);

		if (input.getMethod() == null) {
			throw new TransactionException("无法正确解析input data 请额外开发支持");
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
		private Contract contract;

		public Input setTo(String to) {
			if (to.startsWith(Protocol.ETHERSCAN.getStart())) {
				this.to = to;
			}
			else {
				this.to = Protocol.ETHERSCAN.getStart() + removePreZero(to);
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
				String[] array = stringToArray(input.getData().substring(10));
				input.setTo(array[0]);
				input.setValue(new BigDecimal(Long.parseLong(array[1], 16)).divide(USDT_FLAG, MathContext.UNLIMITED));
			}),

			SEND_MULTI_SIG_TOKEN("0x0dcd7a6c", input -> {
				String[] array = stringToArray(input.getData().substring(10));
				input.setTo(array[0]);
				input.setValue(new BigDecimal(Long.parseLong(array[1], 16)).divide(USDT_FLAG, MathContext.UNLIMITED));
				input.setContract(Etherscan.getByHash(Protocol.ETHERSCAN.getStart() + removePreZero(array[2])));
			}),;

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
