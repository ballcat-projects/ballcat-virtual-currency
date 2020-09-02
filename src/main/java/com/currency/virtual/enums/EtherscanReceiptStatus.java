package com.currency.virtual.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 以太坊交易凭据状态
 *
 * @author lingting 2020-09-02 14:34
 */
@Getter
@AllArgsConstructor
public enum EtherscanReceiptStatus {
    /**
     * 交易状态
     */
    SUCCESS("0x1"),
    ;
    private final String value;
}
