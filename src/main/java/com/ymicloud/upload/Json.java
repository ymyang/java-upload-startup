package com.ymicloud.upload;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

/**
 * json
 * 
 * @author yang
 * 
 */
public final class Json {

	private static final ObjectMapper mapper = new ObjectMapper();

	private Json() {

	}

	/**
	 * to json
	 * @param obj
	 * @return
	 * @throws IOException 
	 */
	public static String toJson(Object obj) throws IOException {
		return mapper.writeValueAsString(obj);
	}

	/**
	 * parse json as Object
	 * @param json
	 * @param clazz
	 * @return
	 * @throws IOException 
	 */
	public static <T> T parse(String json, Class<T> clazz) throws IOException {
		return mapper.readValue(json, clazz);
	}

	/**
	 * parse json use TypeReference
	 * @param json
	 * @param typeRef
	 * @return
	 * @throws IOException
	 */
	public static <T> T parse(String json, TypeReference<T> typeRef) throws IOException {
		return mapper.readValue(json, typeRef);
	}

	/**
	 * parse json as array
	 * @param json
	 * @param typeRef
	 * @return
	 * @throws IOException 
	 * @deprecated use parse
	 */
	public static <T> T parseArray(String json, TypeReference<T> typeRef) throws IOException {
		return parse(json, typeRef);
	}

}
