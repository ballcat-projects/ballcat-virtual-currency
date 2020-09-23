package com.currency.virtual;

import com.currency.virtual.endpoints.InfuraEndpoints;
import com.currency.virtual.endpoints.OmniEndpoints;
import com.currency.virtual.endpoints.TronscanEndpoints;
import com.currency.virtual.enums.Platform;
import com.currency.virtual.properties.InfuraProperties;
import com.currency.virtual.properties.OmniProperties;
import com.currency.virtual.properties.TronscanProperties;
import com.currency.virtual.properties.VirtualCurrencyProperties;
import com.currency.virtual.service.VirtualCurrencyService;
import com.currency.virtual.transaction.VirtualCurrencyTransaction;
import java.util.Optional;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * @author lingting 2020-09-01 19:56
 */
@Slf4j
public class VcTest {
    private VirtualCurrencyService service;

    @SneakyThrows
    @Test
    public void ethTest() {
        service = VirtualCurrencyFactory.getVirtualCurrencyService(
                new VirtualCurrencyProperties().setPlatform(Platform.INFURA),
                new InfuraProperties().setEndpoints(InfuraEndpoints.MAINNET).setProjectId(
                        "b6066b4cfce54e7384ea38d52f9260ac")
        );
        String txnHash = "0x438e851665668b3083220a0eecf798e5290e589d7f02824a044fea7155a1a0d1";
        Optional<VirtualCurrencyTransaction> optional = service.getTransactionByHash(txnHash.trim());

        System.out.println(optional.isPresent());
        if (optional.isPresent()) {
            VirtualCurrencyTransaction transaction = optional.get();
            // 获取系统默认时区的交易创建时间
            System.out.println(transaction.getTime());
            // 获取utc时区的交易创建时间
            System.out.println(transaction.getTimeByUtc());
        }
    }

    @Test
    public void btcTest() {
        service = VirtualCurrencyFactory.getVirtualCurrencyService(
                new VirtualCurrencyProperties().setPlatform(Platform.OMNI),
                new OmniProperties().setEndpoints(OmniEndpoints.MAINNET)
        );
        Optional<VirtualCurrencyTransaction> transaction = service.getTransactionByHash("6626088699bbcc43789c25554d3231c5106879d7ae7e96a2c77efdaeaa9b0e4d");

        System.out.println(transaction.isPresent());
    }

    @Test
    public void tronscanTest() {
        service = VirtualCurrencyFactory.getVirtualCurrencyService(
                new VirtualCurrencyProperties().setPlatform(Platform.TRONSCAN),
                new TronscanProperties().setEndpoints(TronscanEndpoints.MAINNET)
        );
        Optional<VirtualCurrencyTransaction> transaction = service.getTransactionByHash("546472bbca89b426d7c08dd4d24f7620ea743f13ad756d611aa42bccfa951a4e");

        System.out.println(transaction.isPresent());
    }
}
