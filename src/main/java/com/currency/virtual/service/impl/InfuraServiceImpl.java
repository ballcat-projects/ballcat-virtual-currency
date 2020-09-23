package com.currency.virtual.service.impl;

import cn.hutool.core.convert.Convert;
import com.currency.virtual.contract.Etherscan;
import com.currency.virtual.enums.EtherscanReceiptStatus;
import com.currency.virtual.enums.Protocol;
import com.currency.virtual.enums.TransactionStatus;
import com.currency.virtual.exception.TransactionException;
import com.currency.virtual.properties.InfuraProperties;
import com.currency.virtual.service.VirtualCurrencyService;
import com.currency.virtual.transaction.VirtualCurrencyTransaction;
import com.currency.virtual.util.EtherscanUtil;
import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

/**
 * @author lingting 2020-09-01 17:16
 */
@Slf4j
public class InfuraServiceImpl implements VirtualCurrencyService {
    private static final String INPUT_EMPTY = "0x";
    @Getter
    private final Web3j web3j;

    public InfuraServiceImpl(InfuraProperties properties) {
        // 使用web3j连接infura客户端
        web3j = Web3j.build(properties.getHttpService());
    }

    @Override
    @SneakyThrows
    public Optional<VirtualCurrencyTransaction> getTransactionByHash(String hash) {
        EthTransaction ethTransaction = web3j.ethGetTransactionByHash(hash).sendAsync().get();

        if (ethTransaction.hasError()) {
            // 订单出错
            throw new TransactionException(ethTransaction.getError().getMessage());
        }

        /* 订单信息为空
            如果交易还没有被打包，就查询不到交易信息
         */
        if (!ethTransaction.getTransaction().isPresent()) {
            return Optional.empty();
        }
        Transaction transaction = ethTransaction.getTransaction().get();

        // 获取合约代币
        Etherscan contract = Etherscan.getByHash(transaction.getTo());
        // 解析input数据
        EtherscanUtil.Input input;
        if (INPUT_EMPTY.equals(transaction.getInput())) {
            // 不是使用代币交易，而是直接使用eth交易
            if (contract != null) {
                // 如果合约代币不为null
                throw new TransactionException("合约代币应该为null，但是解析出来的不为null，请检查. 交易hash: " + hash);
            }
            input = new EtherscanUtil.Input().setTo(transaction.getTo()).setValue(new BigDecimal(transaction.getValue()).divide(EtherscanUtil.ETH, MathContext.UNLIMITED));
        } else {
            input = EtherscanUtil.resolveInput(transaction.getInput());
        }

        VirtualCurrencyTransaction virtualCurrencyTransaction = new VirtualCurrencyTransaction()
                .setProtocol(Protocol.ETHERSCAN)
                .setBlockHash(transaction.getBlockHash())
                .setBlock(transaction.getBlockNumber())
                .setHash(transaction.getHash())
                .setFrom(transaction.getFrom())
                .setTo(input.getTo())
                // 设置代币类型
                .setContract(contract)
                .setValue(input.getValue());

        // 获取交易状态
        Optional<TransactionReceipt> receiptOptional =
                web3j.ethGetTransactionReceipt(hash).send().getTransactionReceipt();
        if (receiptOptional.isPresent() && receiptOptional.get().getStatus().equals(EtherscanReceiptStatus.SUCCESS.getValue())) {
            // 交易成功
            virtualCurrencyTransaction.setStatus(TransactionStatus.SUCCESS);
        } else {
            virtualCurrencyTransaction.setStatus(TransactionStatus.FAIL);
        }

        // 获取交易时间
        EthBlock block = web3j.ethGetBlockByHash(transaction.getBlockHash(), false).sendAsync().get();

        // 从平台获取的交易是属于 UTC 时区的
        return Optional.of(virtualCurrencyTransaction.setTime(LocalDateTime.ofEpochSecond(
                Convert.toLong(block.getBlock().getTimestamp()),
                0,
                ZoneOffset.UTC
        )));
    }
}
