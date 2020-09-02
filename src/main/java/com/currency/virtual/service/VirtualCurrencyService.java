package com.currency.virtual.service;

import com.currency.virtual.exception.TransactionException;
import com.currency.virtual.transaction.VirtualCurrencyTransaction;
import org.web3j.protocol.Web3j;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * 虚拟货币处理接口类
 *
 * @author lingting 2020-09-01 17:15
 */
public interface VirtualCurrencyService {

    /**
     * 通过交易hash获取交易信息
     *
     * @param hash 交易hash
     * @return 交易信息
     */
    Optional<VirtualCurrencyTransaction> getTransactionByHash(String hash);
}
