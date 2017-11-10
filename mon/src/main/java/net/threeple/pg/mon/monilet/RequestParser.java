package net.threeple.pg.mon.monilet;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestParser {
	public static IMoniletRequest parse(BufferedReader reader) throws IOException {
		String line = reader.readLine();
		IMoniletRequest request = null;
		if(line != null && line.startsWith("Require")) {
			int n = line.indexOf(':');
			String require = line.substring(n + 1).trim() + "Monilet";
			request = new MoniletRequest();
			request.setReader(reader);
			request.setRequire(require);
			
			Map<String, String> body = new HashMap<>();
			while(!"End".equals((line = reader.readLine()))) {
				n = line.indexOf('=');
				String name = line.substring(0, n).trim();
				String value = line.substring(n + 1).trim();
				body.put(name, value);
			}
			request.setBody(body);
		}
		return request;
	}
}
