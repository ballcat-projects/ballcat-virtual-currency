package live.lingting.virtual.currency.core.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import live.lingting.virtual.currency.core.Contract;
import live.lingting.virtual.currency.core.enums.TransactionStatus;
import live.lingting.virtual.currency.core.enums.VirtualCurrencyPlatform;

/**
 * @author lingting 2020-09-02 14:02
 */
@Getter
@Setter
@Builder
@Accessors(chain = true)
public class TransactionInfo {

	/**
	 * 交易号
	 */
	private String hash;

	/**
	 * 块号
	 */
	private BigInteger block;

	/**
	 * 转账方
	 */
	private String from;

	/**
	 * 收账方
	 */
	private String to;

	/**
	 * 转账数量 单位 个
	 */
	private BigDecimal value;

	/**
	 * 交易状态
	 */
	private TransactionStatus status;

	/**
	 * 合约类型
	 */
	private Contract contract;

	/**
	 * 虚拟货币平台
	 */
	private VirtualCurrencyPlatform virtualCurrencyPlatform;

	/**
	 * 交易时间, 时区 UTC
	 */
	private LocalDateTime time;

	/**
	 * 是否为btc交易
	 */
	private Boolean btc;

	/**
	 * 获取btc交易详情
	 */
	private BtcInfo btcInfo;

	/**
	 * 是否为btc交易, 如果 btcInfo不为null 则为btc交易
	 * @author lingting 2021-01-10 18:36
	 */
	public Boolean getBtc() {
		return btcInfo != null;
	}

	/**
	 * 返回系统默认时区的交易时间
	 *
	 * @author lingting 2020-09-23 18:41
	 */
	public LocalDateTime getTime() {
		// 计算当前时区的偏移量 单位: 秒， 然后让时间偏移
		return getTimeByZone(ZoneId.systemDefault());
	}

	public TransactionInfo setTime(LocalDateTime time) {
		this.time = time;
		return this;
	}

	/**
	 * 设置时间
	 * @param time 时间戳, 单位 秒
	 */
	public TransactionInfo setTime(long time) {
		return setTime(LocalDateTime.ofEpochSecond(time, 0, ZoneOffset.UTC));
	}

	/**
	 * 返回UTC时区的交易时间
	 *
	 * @author lingting 2020-09-23 18:41
	 */
	public LocalDateTime getTimeByUtc() {
		return time;
	}

	/**
	 * 获取指定时区的交易时间
	 *
	 * @author lingting 2020-09-23 19:12
	 */
	public LocalDateTime getTimeByZone(ZoneId zoneId) {
		if (time == null) {
			return null;
		}
		return time.plusSeconds(TimeZone.getTimeZone(zoneId).getRawOffset() / 1000);
	}

	@Getter
	@Setter
	@Accessors(chain = true)
	@NoArgsConstructor
	@AllArgsConstructor
	public static class BtcInfo {

		/**
		 * 地址 -> btc个数, 单位: 个
		 */
		private Map<String, BigDecimal> in = new HashMap<>();

		/**
		 * 地址 -> btc个数, 单位: 个
		 */
		private Map<String, BigDecimal> out = new HashMap<>();

		private BigDecimal fee = BigDecimal.ZERO;

		public void addIn(String address, BigDecimal val) {
			if (in.containsKey(address)) {
				in.put(address, in.get(address).add(val));
			}
			else {
				in.put(address, val);
			}
		}

		public void addOut(String address, BigDecimal val) {
			if (out.containsKey(address)) {
				out.put(address, out.get(address).add(val));
			}
			else {
				out.put(address, val);
			}
		}

	}

}
