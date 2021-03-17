package live.lingting.virtual.currency.bitcoin;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.junit.Test;
import live.lingting.virtual.currency.bitcoin.endpoints.BitcoinEndpoints;
import live.lingting.virtual.currency.bitcoin.properties.BitcoinProperties;
import live.lingting.virtual.currency.core.enums.TransactionStatus;
import live.lingting.virtual.currency.core.model.TransactionInfo;
import live.lingting.virtual.currency.core.util.AssertUtils;

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

		AssertUtils.isFalse(optional.isPresent());
		optional = service.getTransactionByHash("5f20c18d94a9e511e62d6aef74ffa55d36694aabc5a67fa65cb2a161c8bc5483");
		TransactionInfo info = optional.get();
		AssertUtils.equals("5f20c18d94a9e511e62d6aef74ffa55d36694aabc5a67fa65cb2a161c8bc5483", info.getHash());
		AssertUtils.equals(TransactionStatus.SUCCESS, info.getStatus());
		AssertUtils.isTrue(info.getBtc());
	}

	@Test
	@SneakyThrows
	public void property() {
		Optional<TransactionInfo> optional = service
				.getTransactionByHash("3130a1f6e1101deb1a5afee161eaf9ec66ee42727e706b1adab32ed68cf1ab38");

		AssertUtils.isFalse(optional.isPresent());

		optional = service.getTransactionByHash("3130a1f6e2301deb1a5afee161eaf9ec66ee42727e706b1adab32ed68cf1ab38");

		TransactionInfo info = optional.get();
		AssertUtils.equals("3130a1f6e2301deb1a5afee161eaf9ec66ee42727e706b1adab32ed68cf1ab38", info.getHash());
		AssertUtils.equals(TransactionStatus.SUCCESS, info.getStatus());
		AssertUtils.equals(new BigDecimal("2425.00000000"), info.getValue());
		AssertUtils.isFalse(info.getBtc());
	}

	@Test
	@SneakyThrows
	public void valid() {
		service = new BitcoinServiceImpl(new BitcoinProperties().setEndpoints(BitcoinEndpoints.TEST));
		AssertUtils.isFalse(service.validate("fasdgasdfasdgasdfdafgasdfsa"));
		AssertUtils.isTrue(service.validate("2N2vMRNmBiSJQtu3wv2onGPE2museEg9nLw"));
		AssertUtils.isTrue(service.validate("tb1qdhgxl3vjcg9djhd30zykmz83x63nau4rtmahmd"));
	}

	@Test
	@SneakyThrows
	public void history() {
		List<TransactionInfo> list;
		int i = 0;
		int page = 0;

		// 预计有两个交易要查询
		while (i < 2) {
			page++;
			list = service.listHistoryByAddress(new BitcoinHistoryQueryParams()
					//
					.setAddress("3BPAodH6WkNQC8TUrnwFKhETiFacNUi68X")
					// 查询方案一
					.setOnlyOmni(false).setPageIndex(page));

			if (list.isEmpty()) {
				throw new IllegalArgumentException("请手动检查地址是否拥有交易");
			}

			for (var info : list) {
				if (info.getHash().equals("7845a0012b1fabd7065287a51913731a17158aaacd83204db48dabecdbcbea19")
						|| info.getHash().equals("ec0c39afbc25181d50a35fae5a5a4a157640eb5448709e91f46b1bdc6f7120d6")) {
					i++;

					if (info.getHash().equals("7845a0012b1fabd7065287a51913731a17158aaacd83204db48dabecdbcbea19")) {
						AssertUtils.equals(info.getFrom(), "32rHHtPae37urNavtgqkHkB5womt8f2S9t");
						AssertUtils.equals(info.getTo(), "3BPAodH6WkNQC8TUrnwFKhETiFacNUi68X");
						AssertUtils.equals(info.getBlock(), new BigInteger("674827"));
					}
					else {
						AssertUtils.equals(info.getFrom(), "1CUCsHmhDBE4j7dnEGwqsnqP1Ji9vceBc2");
						AssertUtils.equals(info.getTo(), "3BPAodH6WkNQC8TUrnwFKhETiFacNUi68X");
						AssertUtils.equals(info.getBlock(), new BigInteger("674827"));
					}
				}
			}
		}

		i = 0;
		// 预计有两个交易要查询
		while (i < 2) {
			page++;
			list = service.listHistoryByAddress(new BitcoinHistoryQueryParams()
					//
					.setAddress("3BPAodH6WkNQC8TUrnwFKhETiFacNUi68X")
					// 查询方案二
					.setOnlyOmni(true).setPageIndex(page));

			if (list.isEmpty()) {
				throw new IllegalArgumentException("请手动检查地址是否拥有交易");
			}

			for (var info : list) {
				if (info.getHash().equals("7845a0012b1fabd7065287a51913731a17158aaacd83204db48dabecdbcbea19")
						|| info.getHash().equals("ec0c39afbc25181d50a35fae5a5a4a157640eb5448709e91f46b1bdc6f7120d6")) {
					i++;

					if (info.getHash().equals("7845a0012b1fabd7065287a51913731a17158aaacd83204db48dabecdbcbea19")) {
						AssertUtils.equals(info.getFrom(), "32rHHtPae37urNavtgqkHkB5womt8f2S9t");
						AssertUtils.equals(info.getTo(), "3BPAodH6WkNQC8TUrnwFKhETiFacNUi68X");
						AssertUtils.equals(info.getBlock(), new BigInteger("674827"));
					}
					else {
						AssertUtils.equals(info.getFrom(), "1CUCsHmhDBE4j7dnEGwqsnqP1Ji9vceBc2");
						AssertUtils.equals(info.getTo(), "3BPAodH6WkNQC8TUrnwFKhETiFacNUi68X");
						AssertUtils.equals(info.getBlock(), new BigInteger("674827"));
					}
				}
			}
		}

	}

}
