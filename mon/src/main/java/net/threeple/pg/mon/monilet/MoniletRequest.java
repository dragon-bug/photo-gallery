package net.threeple.pg.mon.monilet;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;

public class MoniletRequest implements IMoniletRequest {
	private String require;
	private BufferedReader reader;
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
	public BufferedReader getReader() {
		return reader;
	}
	public void setReader(BufferedReader reader) {
		this.reader = reader;
	}
	
}
