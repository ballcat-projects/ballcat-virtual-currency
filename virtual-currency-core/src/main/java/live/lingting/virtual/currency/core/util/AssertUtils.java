package live.lingting.virtual.currency.core.util;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.sun.istack.internal.NotNull;

/**
 * @author lingting 2021/3/2 19:37
 */
public class AssertUtils extends Assert {

	public static void equals(@NotNull Object o1, Object o2) {
		equals(o1, o2, "{} 与 {} 不一致!", o1, o2);
	}

	public static void equals(Object o1, Object o2, String errorMsgTemplate, Object... params) {
		if (!o1.equals(o2)) {
			throw new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params));
		}
	}

}
