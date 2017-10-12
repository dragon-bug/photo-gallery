package net.threeple.pg.shared.util;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class CustomInetAddressParser {
	public static InetSocketAddress parse(String address) {
		int colon = address.indexOf(':');
		String host = address.substring(0, colon);
		int port = Integer.parseInt(address.substring(colon + 1, address.length()));
		InetAddress inetAddr = null;
		InetSocketAddress inetSocketAddr = null;
		try {
			inetAddr = InetAddress.getByName(host);
			new InetSocketAddress(inetAddr, port);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return inetSocketAddr;
	}
}
