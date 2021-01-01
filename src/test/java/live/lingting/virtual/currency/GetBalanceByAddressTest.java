package live.lingting.virtual.currency;

import live.lingting.virtual.currency.contract.EtherscanContract;
import live.lingting.virtual.currency.contract.OmniContract;
import live.lingting.virtual.currency.contract.TronscanContract;
import live.lingting.virtual.currency.endpoints.InfuraEndpoints;
import live.lingting.virtual.currency.endpoints.OmniEndpoints;
import live.lingting.virtual.currency.endpoints.TronscanEndpoints;
import live.lingting.virtual.currency.properties.InfuraProperties;
import live.lingting.virtual.currency.properties.OmniProperties;
import live.lingting.virtual.currency.properties.TronscanProperties;
import live.lingting.virtual.currency.service.VirtualCurrencyService;
import live.lingting.virtual.currency.service.impl.InfuraServiceImpl;
import live.lingting.virtual.currency.service.impl.OmniServiceImpl;
import live.lingting.virtual.currency.service.impl.TronscanServiceImpl;
import java.math.BigDecimal;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * @author lingting 2020-09-01 19:56
 */
@Slf4j
public class GetBalanceByAddressTest {

	private VirtualCurrencyService service;

	@Test
	@SneakyThrows
	public void ethTest() {

		service = new InfuraServiceImpl(new InfuraProperties().setEndpoints(InfuraEndpoints.MAINNET)
				.setProjectId("b6066b4cfce54e7384ea38d52f9260ac"));
		String address = "0xb501E624f65683Ab6ceA24d3Eb82FB1187d690aD";

		BigDecimal decimal = service.getNumberByAddressAndContract(address, EtherscanContract.ETH);
		System.out.println(decimal);
	}

	@Test
	@SneakyThrows
	public void btcTest() {
		service = new OmniServiceImpl(new OmniProperties().setEndpoints(OmniEndpoints.MAINNET));
		BigDecimal decimal = service.getNumberByAddressAndContract("1KN4mnqdUhtERmrZdfke1VUkBQUMcicNHE",
				OmniContract.MAID_SAFE_COIN);
		System.out.println(decimal);
	}

	@Test
	@SneakyThrows
	public void tronscanTest() {
		service = new TronscanServiceImpl(new TronscanProperties().setEndpoints(TronscanEndpoints.MAINNET));

		String address = "TBAbX6ezwTRLEoYW2DUhQQjQ2aA3KWxFkp";
		BigDecimal value = service.getNumberByAddressAndContract(address, TronscanContract.TRX);
		System.out.println(value);

	}

}
