package net.threeple.pg.mon.monilet;

import java.util.Map;

public interface IMoniletRequest {
	public String getRequire();
	public void setRequire(String require);
	public Map<String, String> getBody();
	public void setBody(Map<String, String> body);
}
