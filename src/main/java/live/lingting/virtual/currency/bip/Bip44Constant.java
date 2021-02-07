package live.lingting.virtual.currency.bip;

/**
 * bip44 常量
 *
 * 参考文档 https://github.com/bitcoin/bips/blob/master/bip-0044.mediawiki
 *
 * 参考文档 https://github.com/satoshilabs/slips/blob/master/slip-0044.md
 *
 * @author lingting 2021/2/7 13:58
 */
public class Bip44Constant {

	/**
	 * 助记词生成 bitcoin 密钥对(用于生成普通地址, 隔离见证地址在 {@link Bip49Constant#PATH_BITCOIN}) 标准路径
	 */
	public static final String PATH_BITCOIN = "m/44'/0'/0'/0";

	/**
	 * 助记词生成 bitcoin 密钥对 标准路径 . 这个路径是 imtoken 通过助记词生成 兼容性隔离见证地址(就是imToken中的隔离见证地址选项) 默认路径
	 */
	public static final String PATH_BITCOIN_IMTOKEN = "m/49'/0'/0'/0";

	/**
	 * 助记词生成 etherscan 密钥对 标准路径
	 */
	public static final String PATH_ETHERSCAN = "m/44'/60'/0'/0";

	/**
	 * 助记词生成 tronscan 密钥对 标准路径
	 */
	public static final String PATH_TRONSCAN = "m/44'/195'/0'/0";

}
