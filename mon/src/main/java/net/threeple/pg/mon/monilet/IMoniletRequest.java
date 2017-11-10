package net.threeple.pg.mon.monilet;

import java.io.BufferedReader;
import java.util.Map;

public interface IMoniletRequest {
	public String getRequire();
	public void setRequire(String require);
	public Map<String, String> getBody();
	public void setBody(Map<String, String> body);
	public BufferedReader getReader();
	public void setReader(BufferedReader reader);
}
