package live.lingting.virtual.currency.tronscan;

import java.util.Optional;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import live.lingting.virtual.currency.Transaction;
import live.lingting.virtual.currency.endpoints.TronscanEndpoints;
import live.lingting.virtual.currency.properties.TronscanProperties;
import live.lingting.virtual.currency.service.PlatformService;
import live.lingting.virtual.currency.service.impl.TronscanServiceImpl;

/**
 * @author lingting 2021/1/10 15:54
 */
@Slf4j
public class Select {

	private static final PlatformService service;

	static {
		service = new TronscanServiceImpl(new TronscanProperties().setEndpoints(TronscanEndpoints.MAINNET));
	}

	@Test
	@SneakyThrows
	public void trx() {
		Optional<Transaction> optional = service
				.getTransactionByHash("96b57737c419bf5fde63119be4c116e57fb2fb0607d3cc257f7664a655af345d");

		System.out.println("交易是否存在: " + optional.isPresent());
		if (optional.isPresent()) {
			Transaction transaction = optional.get();
			System.out.println(transaction.getVcPlatform());
			System.out.println(transaction.getHash());
			System.out.println(transaction.getValue());
			System.out.println(transaction.getStatus());
			System.out.println(transaction.getTime());
		}
		// 由于api没有返回确认数, 所以这里使用的固化块api, 如果交易确认不到19, 无法通过本api查询到数据
		optional = service.getTransactionByHash("96b57737c419bf5fde63129be4c116e57fb2fb0607d3cc257f7664a655af345d");

		System.out.println("交易是否存在: " + optional.isPresent());
		if (optional.isPresent()) {
			Transaction transaction = optional.get();
			System.out.println(transaction.getVcPlatform());
			System.out.println(transaction.getHash());
			System.out.println(transaction.getValue());
			System.out.println(transaction.getContract());
			System.out.println(transaction.getStatus());
			System.out.println(transaction.getTime());
		}
	}

	@Test
	@SneakyThrows
	public void trc10() {
		Optional<Transaction> optional = service
				.getTransactionByHash("96b57737c419bf5fde63119be4c116e57fb2fb0607d3cc257f7664a655af345d");

		System.out.println("交易是否存在: " + optional.isPresent());
		if (optional.isPresent()) {
			Transaction transaction = optional.get();
			System.out.println(transaction.getVcPlatform());
			System.out.println(transaction.getHash());
			System.out.println(transaction.getValue());
			System.out.println(transaction.getStatus());
			System.out.println(transaction.getTime());
		}
		optional = service.getTransactionByHash("0f4fee870b87988aaf31cce1e8abeec2897efa0186b953b0a0e0738dc4aef361");

		System.out.println("交易是否存在: " + optional.isPresent());
		if (optional.isPresent()) {
			Transaction transaction = optional.get();
			System.out.println(transaction.getVcPlatform());
			System.out.println(transaction.getHash());
			System.out.println(transaction.getValue());
			System.out.println(transaction.getContract());
			System.out.println(transaction.getStatus());
			System.out.println(transaction.getTime());
		}
	}

	@Test
	@SneakyThrows
	public void trc20() {
		Optional<Transaction> optional = service
				.getTransactionByHash("96b57737c419bf5fde63119be4c116e57fb2fb0607d3cc257f7664a655af345d");

		System.out.println("交易是否存在: " + optional.isPresent());
		if (optional.isPresent()) {
			Transaction transaction = optional.get();
			System.out.println(transaction.getVcPlatform());
			System.out.println(transaction.getHash());
			System.out.println(transaction.getValue());
			System.out.println(transaction.getStatus());
			System.out.println(transaction.getTime());
		}
		optional = service.getTransactionByHash("19535f41c136ae8bd2a5b36ed7383b0ab4c1cacaacc71d0bf4ea3881e5d3e91b");

		System.out.println("交易是否存在: " + optional.isPresent());
		if (optional.isPresent()) {
			Transaction transaction = optional.get();
			System.out.println(transaction.getVcPlatform());
			System.out.println(transaction.getHash());
			System.out.println(transaction.getValue());
			System.out.println(transaction.getContract());
			System.out.println(transaction.getStatus());
			System.out.println(transaction.getTime());
		}
	}

}
