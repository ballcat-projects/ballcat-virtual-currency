package live.lingting.virtual.currency;

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

	public Account(String address, String publicKey, String privateKey) {
		this.address = address;
		this.publicKey = publicKey;
		this.privateKey = privateKey;
	}

}
