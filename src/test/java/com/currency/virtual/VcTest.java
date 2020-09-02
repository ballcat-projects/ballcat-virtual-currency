package com.currency.virtual;

import com.currency.virtual.endpoints.InfuraEndpoints;
import com.currency.virtual.enums.Platform;
import com.currency.virtual.properties.InfuraProperties;
import com.currency.virtual.properties.VirtualCurrencyProperties;
import com.currency.virtual.service.VirtualCurrencyService;
import com.currency.virtual.transaction.VirtualCurrencyTransaction;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Optional;

/**
 * @author lingting 2020-09-01 19:56
 */
@Slf4j
public class VcTest {
    private final VirtualCurrencyService service = VirtualCurrencyFactory.getVirtualCurrencyService(
            new VirtualCurrencyProperties().setPlatform(Platform.INFURA),
            new InfuraProperties().setEndpoints(InfuraEndpoints.MAINNET).setProjectId("b6066b4cfce54e7384ea38d52f9260ac")
    );

    @SneakyThrows
    @Test
    public void getOrderByHash() {
        String txnHash = "0x45cb0c87e66979f4d837e199ee9cbfbf262eaf777c5629e5ae8fccab7358ab30";
        Optional<VirtualCurrencyTransaction> transaction = service.getTransactionByHash(txnHash.trim());

        System.out.println(transaction.isEmpty());
    }
}
