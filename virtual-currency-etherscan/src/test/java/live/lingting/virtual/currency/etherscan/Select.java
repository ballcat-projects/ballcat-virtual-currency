package live.lingting.virtual.currency.etherscan;

import java.math.BigDecimal;
import java.util.Optional;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import live.lingting.virtual.currency.core.enums.TransactionStatus;
import live.lingting.virtual.currency.core.model.TransactionInfo;
import live.lingting.virtual.currency.core.util.AssertUtils;
import live.lingting.virtual.currency.etherscan.endpoints.EtherscanEndpoints;
import live.lingting.virtual.currency.etherscan.properties.EtherscanProperties;

/**
 * @author lingting 2021/1/10 15:54
 */
@Slf4j
public class Select {

	private static final EtherscanServiceImpl service;

	static {
		service = new EtherscanServiceImpl(new EtherscanProperties()
				// 节点
				.setEndpoints(EtherscanEndpoints.MAINNET)
				// project Id
				.setProjectId("9d0efcd0739f4d2f8dae3da3a49051f0"));
	}

	@Test
	@SneakyThrows
	public void etc() {
		Optional<TransactionInfo> optional = service
				.getTransactionByHash("0x17dfd34a127eab2be3c17e9567d42ab39fde92907b23bb190071744eff2be9e0");
		AssertUtils.isFalse(optional.isPresent());

		optional = service.getTransactionByHash("0x17dfd34a3e7eab2be3c17e9567d42ab39fde92907b23bb190071744eff2be9e0");

		TransactionInfo info = optional.get();
		AssertUtils.equals("0x17dfd34a3e7eab2be3c17e9567d42ab39fde92907b23bb190071744eff2be9e0", info.getHash());
		AssertUtils.equals(TransactionStatus.SUCCESS, info.getStatus());
		AssertUtils.equals(new BigDecimal("0.27"), info.getValue());
	}

	@Test
	@SneakyThrows
	public void erc20() {
		Optional<TransactionInfo> optional = service
				.getTransactionByHash("0x17dfd34a127eab2be3c17e9567d42ab39fde92907b23bb190071744eff2be9e0");

		AssertUtils.isFalse(optional.isPresent());
		optional = service.getTransactionByHash("0xf07f075c1fad8e87bdaa095c07ae3ccab5a0a1f0d8c0460769326d10d5a07e4b");

		TransactionInfo info = optional.get();
		AssertUtils.equals("0xf07f075c1fad8e87bdaa095c07ae3ccab5a0a1f0d8c0460769326d10d5a07e4b", info.getHash());
		AssertUtils.equals(TransactionStatus.SUCCESS, info.getStatus());
		AssertUtils.equals(new BigDecimal("2000"), info.getValue());
	}

	@Test
	@SneakyThrows
	public void valid() {
		AssertUtils.isFalse(service.validate("2131231231"));
		AssertUtils.isTrue(service.validate("0x41c559f4f785664371ffe3ff489029126488849c"));
	}

}
