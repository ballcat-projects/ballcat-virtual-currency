package com.currency.virtual.contract;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 波场 合约地址
 *
 * @author lingting 2020-09-02 13:40
 */
@Getter
@AllArgsConstructor
public enum Tronscan implements Contract {
    /**
     * 代币名称
     */
    USDT("31"),
    /*
    token转账
     */
    TOKEN("2"),
    ;
    /**
     * 比特合约地址, 仅提供正式服
     */
    private final String hash;

    /**
     * 通过合约hash获取属于哪个 比特合约
     *
     * @author lingting 2020-09-02 13:44
     */
    public static Tronscan getByHash(String hash) {
        if (StrUtil.isEmpty(hash)) {
            return null;
        }
        for (Tronscan c : values()) {
            if (c.hash.equalsIgnoreCase(hash)) {
                return c;
            }
        }
        return null;
    }
}
