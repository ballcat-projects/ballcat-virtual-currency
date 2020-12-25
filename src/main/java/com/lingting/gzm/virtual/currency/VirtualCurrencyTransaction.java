package com.lingting.gzm.virtual.currency;

import com.lingting.gzm.virtual.currency.contract.Contract;
import com.lingting.gzm.virtual.currency.enums.TransactionStatus;
import com.lingting.gzm.virtual.currency.enums.VcPlatform;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.TimeZone;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author lingting 2020-09-02 14:02
 */
@Getter
@Setter
@Accessors(chain = true)
public class VirtualCurrencyTransaction {

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
	private VcPlatform vcPlatform;

	/**
	 * 交易时间, 时区 UTC
	 */
	private LocalDateTime time;

	public VirtualCurrencyTransaction setTime(LocalDateTime time) {
		this.time = time;
		return this;
	}

	public VirtualCurrencyTransaction setTime(long time) {
		return setTime(LocalDateTime.ofEpochSecond(time, 0, ZoneOffset.UTC));
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

}
