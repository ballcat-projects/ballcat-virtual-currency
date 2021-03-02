package live.lingting.virtual.currency.core;

/**
 * 节点枚举类需实现
 *
 * @author lingting 2020-09-01 17:06
 */
public interface Endpoints {

	/**
	 * 需要处理的符号
	 */
	String FLAG = "/";

	/**
	 * 获取 http 前缀
	 * @return http 前缀
	 */
	String getHttp();

	/**
	 * 生成 http 连接
	 * @param suffix 拼接后面的字符串
	 * @return 返回 http 连接
	 */
	default String getHttpUrl(String suffix) {
		String pre = getHttp();
		if (!pre.endsWith(FLAG)) {
			pre += FLAG;
		}
		if (suffix.startsWith(FLAG)) {
			suffix = suffix.substring(1);
		}
		return pre + suffix;
	}

}
