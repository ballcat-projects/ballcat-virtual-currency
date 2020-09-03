package com.currency.virtual.service.impl;

import cn.hutool.http.HttpUtil;
import com.currency.virtual.contract.Btc;
import com.currency.virtual.enums.Protocol;
import com.currency.virtual.enums.TransactionStatus;
import com.currency.virtual.properties.OmniProperties;
import com.currency.virtual.service.VirtualCurrencyService;
import com.currency.virtual.transaction.OmniTransaction;
import com.currency.virtual.transaction.VirtualCurrencyTransaction;
import com.currency.virtual.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * @author lingting 2020-09-01 17:16
 */
@Slf4j
@RequiredArgsConstructor
public class OmniServiceImpl implements VirtualCurrencyService {
    /**
     * 至少多少个确认数， 认得交易成功
     */
    private static final int SUCCESS_CONFIRMATIONS_MIN = 6;
    private final OmniProperties properties;

    @Override
    @SneakyThrows
    public Optional<VirtualCurrencyTransaction> getTransactionByHash(String hash) {
        OmniTransaction omniTransaction = JsonUtil.readValue(HttpUtil.get(properties.getTransactionUrlByHash(hash)), OmniTransaction.class);

        // 返回值为null 或者 转账方为null
        if (omniTransaction == null || omniTransaction.getFrom() == null) {
            return Optional.empty();
        }

        return Optional.of(new VirtualCurrencyTransaction()
                .setBlock(omniTransaction.getBlock())
                .setBlockHash(omniTransaction.getBlockHash())
                .setContract(Btc.getByHash(omniTransaction.getPropertyId().toString()))
                .setFrom(omniTransaction.getFrom())
                .setTo(omniTransaction.getTo())
                .setValue(new BigDecimal(omniTransaction.getAmount()))
                .setProtocol(Protocol.BTC)
                // 如果已确数小于 SUCCESS_CONFIRMATIONS_MIN 值，则不算交易成功
                .setStatus(omniTransaction.getConfirmations() < SUCCESS_CONFIRMATIONS_MIN ? TransactionStatus.FAIL : TransactionStatus.SUCCESS)
                .setHash(hash));
    }
}
