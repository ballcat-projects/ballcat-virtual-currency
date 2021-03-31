package live.lingting.virtual.currency.core.jsonrpc.http.model;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import live.lingting.virtual.currency.core.util.JacksonUtils;

/**
 * @author lingting 2021/3/31 17:18
 */
@Getter
@Setter
public abstract class AbstractJsonRpc {

	/**
	 * 便于依赖自行修改实例
	 */
	public static final Snowflake SNOWFLAKE = IdUtil.createSnowflake(1, 1);

	private long id;

	@JsonProperty("jsonrpc")
	private String jsonRpc = "2.0";

	protected AbstractJsonRpc() {
	}

	/**
	 * 生成id
	 * @author lingting 2021-03-31 18:59
	 */
	protected static long generateId() {
		return SNOWFLAKE.nextId();
	}

	@SneakyThrows
	@Override
	public String toString() {
		return JacksonUtils.toJson(this);
	}

}
