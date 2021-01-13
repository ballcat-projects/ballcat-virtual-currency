package live.lingting.virtual.currency;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 虚拟货币账号
 *
 * @author lingting 2020/12/22 16:12
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class Account {

	/**
	 * 地址
	 */
	private String address;

	/**
	 * 公钥
	 */
	private String publicKey;

	/**
	 * 私钥
	 */
	private String privateKey;

	/**
	 * keystore 内容
	 */
	private String keystore;

	/**
	 * 是否多签地址
	 */
	private Boolean multi = false;

	/**
	 * 最少签名个数, 多签地址要求最少使用多少个密钥进行签名
	 */
	private Integer multiNum;

	/**
	 * 公钥组
	 */
	private List<String> publicKeyArray;

	/**
	 * 私钥组
	 */
	private List<String> privateKeyArray;

	public Account(String address, String publicKey, String privateKey) {
		this.address = address;
		this.publicKey = publicKey;
		this.privateKey = privateKey;
	}

	/**
	 * 请注意多签账号公私钥顺序, 顺序不正确可能导致交易广播失败
	 * @author lingting 2021-01-12 17:13
	 */
	public Account(String address, int multiNum, List<String> publicKeyArray, List<String> privateKeyArray) {
		this.address = address;
		this.multi = true;
		this.multiNum = multiNum;
		this.publicKeyArray = publicKeyArray;
		this.privateKeyArray = privateKeyArray;
	}

}
