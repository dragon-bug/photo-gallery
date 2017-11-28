package net.threeple.pg.shared.context;

import java.util.HashMap;
import java.util.Map;

public class ApplicationContext {
	private final static Map<String, Object> beans = new HashMap<>();
	
	public static void addBean(String name, Object value) {
		beans.put(name, value);
	}

	public static Object getBean(String name) {
		return beans.get(name);
	}
}
