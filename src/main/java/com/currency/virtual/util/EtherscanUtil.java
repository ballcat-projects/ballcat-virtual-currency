package com.currency.virtual.util;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * @author lingting 2020-09-02 14:20
 */
public class EtherscanUtil {
    /**
     * 价格单位   1000000:1
     */
    public static final BigDecimal USDT_FLAG = new BigDecimal(1000000);
    public static final BigDecimal ETH = new BigDecimal(1000000000000000000L);

    /**
     * 解析input数据
     *
     * @author lingting 2020-09-02 14:23
     */
    public static Input resolveInput(String input) {
        return new Input()
                .setTo("0x" + input.substring(34, 74))
                .setValue(new BigDecimal(Long.parseLong(input.substring(74), 16)).divide(USDT_FLAG, MathContext.UNLIMITED))
                ;
    }

    @Data
    @Accessors(chain = true)
    public static class Input {
        private String to;
        /**
         * 单位 个
         */
        private BigDecimal value;
    }
}
