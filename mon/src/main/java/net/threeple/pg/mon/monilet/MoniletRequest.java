package net.threeple.pg.mon.monilet;

import java.util.HashMap;
import java.util.Map;

public class MoniletRequest implements IMoniletRequest {
	private String require;
	private Map<String, String> body = new HashMap<>();
	
	public String getRequire() {
		return require;
	}
	public void setRequire(String require) {
		this.require = require;
	}
	public Map<String, String> getBody() {
		return body;
	}
	public void setBody(Map<String, String> body) {
		this.body = body;
	}
	
}
