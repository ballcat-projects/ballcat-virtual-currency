package com.lingting.gzm.virtual.currency.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.lingting.gzm.virtual.currency.VirtualCurrencyAccount;
import com.lingting.gzm.virtual.currency.VirtualCurrencyTransaction;
import com.lingting.gzm.virtual.currency.VirtualCurrencyTransferResult;
import com.lingting.gzm.virtual.currency.contract.Contract;
import com.lingting.gzm.virtual.currency.contract.Tronscan;
import com.lingting.gzm.virtual.currency.endpoints.Endpoints;
import com.lingting.gzm.virtual.currency.enums.TransactionStatus;
import com.lingting.gzm.virtual.currency.enums.VcPlatform;
import com.lingting.gzm.virtual.currency.properties.TronscanProperties;
import com.lingting.gzm.virtual.currency.service.VirtualCurrencyService;
import com.lingting.gzm.virtual.currency.tronscan.Account;
import com.lingting.gzm.virtual.currency.tronscan.TokenTrc10;
import com.lingting.gzm.virtual.currency.tronscan.TokenTrc20;
import com.lingting.gzm.virtual.currency.tronscan.TransactionByHash;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lingting 2020-09-01 17:16
 */
@Slf4j
public class TronscanServiceImpl implements VirtualCurrencyService {

	private final TronscanProperties properties;

	private final Endpoints endpoints;

	private final HttpRequest transactionByHashRequest;

	private final HttpRequest tokenRequest;

	private final HttpRequest accountRequest;

	@Getter
	private static final Map<com.lingting.gzm.virtual.currency.contract.Contract, Integer> CONTRACT_DECIMAL_CACHE;

	static {
		CONTRACT_DECIMAL_CACHE = new ConcurrentHashMap<>(Tronscan.values().length + 1);
		CONTRACT_DECIMAL_CACHE.put(Tronscan.TRX, 6);
	}

	public TronscanServiceImpl(TronscanProperties properties) {
		this.properties = properties;
		this.endpoints = properties.getEndpoints();
		transactionByHashRequest = HttpRequest.get(endpoints.getHttp());
		tokenRequest = HttpRequest.get(endpoints.getHttp());
		accountRequest = HttpRequest.get(endpoints.getHttp());
	}

	@Override
	public Optional<VirtualCurrencyTransaction> getTransactionByHash(String hash) throws Exception {
		// 交易成功时的返回结果
		String success = "SUCCESS";
		TransactionByHash transactionByHash = TransactionByHash.of(transactionByHashRequest, endpoints, hash);

		VirtualCurrencyTransaction transaction = new VirtualCurrencyTransaction().setBlock(transactionByHash.getBlock())
				.setHash(hash).setVcPlatform(VcPlatform.TRONSCAN);
		com.lingting.gzm.virtual.currency.contract.Contract contract;
		BigDecimal value = BigDecimal.ZERO;
		// 金额为空, 其他合约交易
		TransactionByHash.ContractData contractData = transactionByHash.getContractData();
		if (transactionByHash.getContractType() != 1 || contractData.getAmount() == null) {
			Integer decimals;
			// trc10 token 转账
			if (transactionByHash.getContractType() == 2) {
				TransactionByHash.ContractData.TokenInfo tokenInfo = contractData.getTokenInfo();
				contract = Tronscan.getByHash(tokenInfo.getTokenId());
				decimals = tokenInfo.getTokenDecimal();
				value = contractData.getAmount();
				transaction.setFrom(contractData.getOwnerAddress()).setTo(contractData.getToAddress())
						.setContractAddress(tokenInfo.getTokenId());
			}
			// trc20 token 转账
			else {
				String type = "Transfer";
				TransactionByHash.TokenTransferInfo transferInfo = transactionByHash.getTokenTransferInfo();
				contract = Tronscan.getByHash(transferInfo.getContractAddress());
				transaction.setFrom(transferInfo.getFromAddress()).setTo(transferInfo.getToAddress())
						.setContractAddress(transferInfo.getContractAddress());
				decimals = transferInfo.getDecimals();

				// 转账交易
				if (type.equals(transferInfo.getType())) {
					value = new BigDecimal(transferInfo.getAmountStr());
				}
			}

			// 合约不为空, 且目前未缓存当前合约精度 则进行精度缓存
			if (contract != null && !CONTRACT_DECIMAL_CACHE.containsKey(contract)) {
				CONTRACT_DECIMAL_CACHE.put(contract, decimals);
			}
		}
		// trx 转账
		else {
			contract = Tronscan.TRX;
			value = contractData.getAmount();
			transaction.setFrom(transactionByHash.getOwnerAddress()).setTo(transactionByHash.getToAddress());
		}

		// 交易成功 且 已确认
		if (success.equals(transactionByHash.getContractRet()) && transactionByHash.getConfirmed()) {
			transaction.setStatus(TransactionStatus.SUCCESS);
		}
		// 未确认或交易失败
		else {
			// 交易未确认则等待, 交易失败则失败
			transaction.setStatus(success.equals(transactionByHash.getContractRet()) ? TransactionStatus.WAIT
					: TransactionStatus.FAIL);
		}

		transaction.setValue(getNumberByBalanceAndContract(value, contract)).setContract(contract);

		// 这里返回的时间单位是 毫秒，需要转为秒
		return Optional.of(transaction.setTime(transactionByHash.getTimestamp() / 1000));
	}

	@Override
	public Integer getDecimalsByContract(com.lingting.gzm.virtual.currency.contract.Contract contract)
			throws JsonProcessingException {
		if (contract == null) {
			return 0;
		}
		if (CONTRACT_DECIMAL_CACHE.containsKey(contract)) {
			return CONTRACT_DECIMAL_CACHE.get(contract);
		}
		Integer decimals = 0;

		// trc20 查询
		if (isTrc20(contract.getHash())) {
			List<TokenTrc20.Trc20Token> tokens = TokenTrc20.of(tokenRequest, endpoints, contract.getHash())
					.getTrc20Tokens();
			if (!CollectionUtil.isEmpty(tokens)) {
				decimals = tokens.get(0).getDecimals();
			}
		}
		// trc10 查询
		else {
			List<TokenTrc10.TokenData> list = TokenTrc10.of(tokenRequest, endpoints, contract.getHash()).getData();
			if (!CollectionUtil.isEmpty(list)) {
				decimals = list.get(0).getPrecision();
			}
		}

		CONTRACT_DECIMAL_CACHE.put(contract, decimals);
		return decimals;
	}

	@Override
	public BigDecimal getBalanceByAddressAndContract(String address,
			com.lingting.gzm.virtual.currency.contract.Contract contract) throws JsonProcessingException {
		Account account = Account.of(accountRequest, endpoints, address);
		// 搜索拥有的token
		for (Account.Tokens token : account.getTokens()) {
			// 如果存在 ownerAddress, 对比 ownerAddress, 如果指定合约的 hash 与 当前 token.ownerAddress 相同
			if (StrUtil.isNotBlank(token.getOwnerAddress())
					&& contract.getHash().equalsIgnoreCase(token.getOwnerAddress())) {
				return token.getBalance();
			}

			// 如果 token.token_id 与 指定合约的 hash 相同
			if (token.getTokenId().equalsIgnoreCase(contract.getHash())) {
				return token.getBalance();
			}
		}
		// 未找到合约, 返回 0
		return BigDecimal.ZERO;
	}

	@Override
	public BigDecimal getNumberByBalanceAndContract(BigDecimal balance,
			com.lingting.gzm.virtual.currency.contract.Contract contract, MathContext mathContext)
			throws JsonProcessingException {
		// 合约为null 返回原值
		if (contract == null) {
			return balance;
		}
		// 计算返回值
		return balance.divide(BigDecimal.TEN.pow(getDecimalsByContract(contract)), mathContext);
	}

	@Override
	public VirtualCurrencyTransferResult transfer(VirtualCurrencyAccount from, String to, Contract contract,
			BigDecimal value) {
		return null;
	}

	public static final Pattern NUMBER_PATTERN = Pattern.compile("^\\d+$");

	/**
	 * 判断合约是否为 trc20, hash 纯数字为trc10
	 *
	 * @author lingting 2020-12-13 15:19
	 */
	private boolean isTrc20(String hash) {
		return !NUMBER_PATTERN.matcher(hash).find();
	}

}
