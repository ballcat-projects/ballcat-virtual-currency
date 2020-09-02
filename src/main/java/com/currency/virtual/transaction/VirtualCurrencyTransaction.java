package com.currency.virtual.transaction;

import com.currency.virtual.contract.Contract;
import com.currency.virtual.enums.Protocol;
import com.currency.virtual.enums.TransactionStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.math.BigInteger;

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
     * 块hash
     */
    private String blockHash;

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
     * 合约类型，可能为空
     */
    private Contract contract;

    /**
     * 协议
     */
    private Protocol protocol;
}
