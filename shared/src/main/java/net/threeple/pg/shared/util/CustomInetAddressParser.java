package net.threeple.pg.shared.util;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomInetAddressParser {
	final static Logger logger = LoggerFactory.getLogger(CustomInetAddressParser.class);
	public static InetSocketAddress parse(String address) {
		int colon = address.indexOf(':');
		String host = address.substring(0, colon);
		int port = Integer.parseInt(address.substring(colon + 1, address.length()));
		InetAddress inetAddr = null;
		InetSocketAddress inetSocketAddr = null;
		try {
			inetAddr = InetAddress.getByName(host);
			inetSocketAddr = new InetSocketAddress(inetAddr, port);
		} catch (UnknownHostException e) {
			logger.error("无法解析地址：{}，错误信息：{}", address, e.getMessage());
		}
		return inetSocketAddr;
	}
}
