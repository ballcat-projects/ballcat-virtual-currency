package live.lingting.virtual.currency.core.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lingting 2020-09-02 17:12
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JacksonUtils {

	private static final ObjectMapper MAPPER = new ObjectMapper();

	static {
		MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		// 有特殊需要转移字符, 不报错
		MAPPER.enable(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature());
		// 为null不参与序列化
		MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
	}

	/**
	 * 对象转 json string
	 *
	 * @author lingting 2020-12-02 20:08
	 */
	public static String toJson(Object o) throws JsonProcessingException {
		return MAPPER.writeValueAsString(o);
	}

	/**
	 * @param json json string
	 * @param <T> 目标类型
	 * @author lingting 2020-12-02 20:12
	 */
	public static <T> T toObj(String json, Class<T> t) throws JsonProcessingException {
		return MAPPER.readValue(json, t);
	}

	/**
	 * @param json json string
	 * @param type 目标类型
	 * @author lingting 2020-12-02 20:12
	 */
	public static <T> T toObj(String json, Type type) throws JsonProcessingException {
		return MAPPER.readValue(json, MAPPER.constructType(type));
	}

	/**
	 * @param json json string
	 * @param ref 目标类型
	 * @author lingting 2020-12-02 20:12
	 */
	public static <T> T toObj(String json, TypeReference<T> ref) throws JsonProcessingException {
		return MAPPER.readValue(json, ref);
	}

	/**
	 * @param inputStream json string 输入流
	 * @param <T> 目标类型
	 * @author lingting 2020-12-02 20:12
	 */
	public static <T> T toObj(InputStream inputStream, Class<T> t) throws IOException {
		return MAPPER.readValue(inputStream, t);
	}

	/**
	 * @param inputStream json string 输入流
	 * @param type 目标类型
	 * @author lingting 2020-12-02 20:12
	 */
	public static <T> T toObj(InputStream inputStream, Type type) throws IOException {
		return MAPPER.readValue(inputStream, MAPPER.constructType(type));
	}

	/**
	 * @param inputStream json string 输入流
	 * @param ref 目标类型
	 * @author lingting 2020-12-02 20:12
	 */
	public static <T> T toObj(InputStream inputStream, TypeReference<T> ref) throws IOException {
		return MAPPER.readValue(inputStream, ref);
	}

	/**
	 * @author lingting 2020-12-02 20:18
	 */
	public static void registerSubtype(Class<?> clz, String type) {
		MAPPER.registerSubtypes(new NamedType(clz, type));
	}

	/**
	 * @author lingting 2020-12-02 20:18
	 */
	public static ObjectNode createEmptyJsonNode() {
		return new ObjectNode(MAPPER.getNodeFactory());
	}

	/**
	 * @author lingting 2020-12-02 20:18
	 */
	public static ArrayNode createEmptyArrayNode() {
		return new ArrayNode(MAPPER.getNodeFactory());
	}

	/**
	 * @author lingting 2020-12-02 20:18
	 */
	public static JsonNode transferToJsonNode(Object obj) {
		return MAPPER.valueToTree(obj);
	}

	/**
	 * @author lingting 2020-12-02 20:18
	 */
	public static JavaType constructJavaType(Type type) {
		return MAPPER.constructType(type);
	}

}
