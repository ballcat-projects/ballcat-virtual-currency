package live.lingting.virtual.currency.properties;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import live.lingting.virtual.currency.bitcoin.Unspent;
import live.lingting.virtual.currency.bitcoin.UnspentRes;
import live.lingting.virtual.currency.endpoints.Endpoints;
import live.lingting.virtual.currency.omni.PushTx;
import live.lingting.virtual.currency.util.BitcoinUtil;

/**
 * omni平台配置
 *
 * @author lingting 2020-09-01 16:53
 */
@Data
@Accessors(chain = true)
@Slf4j
public class OmniProperties implements PlatformProperties {

	/**
	 * 返回每个字节使用多少手续费, 单位 聪
	 */
	public Supplier<Coin> feeByByte = BitcoinUtil::getSlowFeeByByte;

	/**
	 * 广播交易, 暴露这个函数主要用于测试网络广播交易使用
	 */
	public BiFunction<String, Endpoints, PushTx> broadcastTransaction = (raw, endpoints) -> {
		try {
			return PushTx.of(endpoints, raw);
		}
		catch (Exception e) {
			log.error("广播交易异常!", e);
			return new PushTx(e);
		}
	};

	/**
	 * 获取未花费输出
	 */
	public BiFunction<String, Endpoints, List<Unspent>> unspent = (address, endpoints) -> {
		try {
			return UnspentRes.of(endpoints, getConfirmationsMin(), address).toUnspentList();
		}
		catch (Exception e) {
			log.error("获取未花费输出异常!", e);
			return Collections.emptyList();
		}
	};

	/**
	 * omni节点
	 */
	private Endpoints omniEndpoints;

	/**
	 * 比特节点, 用于操作比特数据
	 */
	private Endpoints bitcoinEndpoints;

	/**
	 * 网络环境
	 * @see MainNetParams#get() 主网
	 * @see TestNet3Params#get() 测试网
	 */
	private NetworkParameters np = MainNetParams.get();

	/**
	 * 获取锁, 成功则允许发送请求, 手动实现限制, 限制请求在 5-10s 一次
	 */
	private Supplier<Boolean> lock = () -> true;

	/**
	 * 释放锁, 请求完成后执行, 返回值无效, 返回false也不会继续尝试释放锁
	 */
	private Supplier<Boolean> unlock = () -> true;

	private Integer confirmationsMin = 6;

	/**
	 * 自定义 omni 节点url, 可为空
	 */
	private String omniUrl;

	/**
	 * 自定义 bitcoin 节点url, 可为空
	 */
	private String bitcoinUrl;

	/**
	 * rpc 地址
	 */
	private String rpcUrl;

	/**
	 * rpc 请求时的请求头
	 */
	private Map<String, String> headers;

}
