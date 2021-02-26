package live.lingting.virtual.currency.bitcoin;

import java.util.Optional;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import live.lingting.virtual.currency.bitcoin.endpoints.BitcoinEndpoints;
import live.lingting.virtual.currency.bitcoin.properties.BitcoinProperties;
import live.lingting.virtual.currency.core.model.TransactionInfo;
import live.lingting.virtual.currency.core.util.JacksonUtils;

/**
 * @author lingting 2021/1/10 15:54
 */
@Slf4j
public class Select {

	private static BitcoinServiceImpl service;

	static {
		service = new BitcoinServiceImpl(new BitcoinProperties().setEndpoints(BitcoinEndpoints.MAINNET));
	}

	@Test
	@SneakyThrows
	public void btc() {
		Optional<TransactionInfo> optional = service
				.getTransactionByHash("3130a1f6e1101deb1a5afee161eaf9ec66ee42727e706b1adab32ed68cf1ab38");

		System.out.println("交易是否存在: " + optional.isPresent());
		if (optional.isPresent()) {
			TransactionInfo transactionInfo = optional.get();
			System.out.println(transactionInfo.getVirtualCurrencyPlatform());
			System.out.println(transactionInfo.getHash());
			System.out.println(transactionInfo.getValue());
			System.out.println(transactionInfo.getStatus());
			System.out.println(transactionInfo.getTime());
		}
		optional = service.getTransactionByHash("5f20c18d94a9e511e62d6aef74ffa55d36694aabc5a67fa65cb2a161c8bc5483");

		System.out.println("交易是否存在: " + optional.isPresent());
		if (optional.isPresent()) {
			TransactionInfo transactionInfo = optional.get();
			System.out.println(transactionInfo.getVirtualCurrencyPlatform());
			System.out.println(transactionInfo.getHash());
			System.out.println(transactionInfo.getContract());
			System.out.println(transactionInfo.getStatus());
			System.out.println(transactionInfo.getBtc());
			System.out.println(JacksonUtils.toJson(transactionInfo.getBtcInfo()));
			System.out.println(transactionInfo.getTime());
		}
	}

	@Test
	@SneakyThrows
	public void property() {
		Optional<TransactionInfo> optional = service
				.getTransactionByHash("3130a1f6e1101deb1a5afee161eaf9ec66ee42727e706b1adab32ed68cf1ab38");

		System.out.println("交易是否存在: " + optional.isPresent());
		if (optional.isPresent()) {
			TransactionInfo transactionInfo = optional.get();
			System.out.println(transactionInfo.getVirtualCurrencyPlatform());
			System.out.println(transactionInfo.getHash());
			System.out.println(transactionInfo.getValue());
			System.out.println(transactionInfo.getStatus());
			System.out.println(transactionInfo.getTime());
		}
		optional = service.getTransactionByHash("3130a1f6e2301deb1a5afee161eaf9ec66ee42727e706b1adab32ed68cf1ab38");

		System.out.println("交易是否存在: " + optional.isPresent());
		if (optional.isPresent()) {
			TransactionInfo transactionInfo = optional.get();
			System.out.println(transactionInfo.getVirtualCurrencyPlatform());
			System.out.println(transactionInfo.getHash());
			System.out.println(transactionInfo.getValue());
			System.out.println(transactionInfo.getContract());
			System.out.println(transactionInfo.getStatus());
			System.out.println(transactionInfo.getTime());
		}
	}

	@Test
	@SneakyThrows
	public void valid() {
		service = new BitcoinServiceImpl(new BitcoinProperties().setEndpoints(BitcoinEndpoints.TEST));
		System.out.println(service.validate("fasdgasdfasdgasdfdafgasdfsa"));
		System.out.println(service.validate("2N2vMRNmBiSJQtu3wv2onGPE2museEg9nLw"));
		System.out.println(service.validate("tb1qdhgxl3vjcg9djhd30zykmz83x63nau4rtmahmd"));
	}

}
