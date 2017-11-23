package net.threeple.pg.shared.config;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.threeple.pg.shared.util.CustomInetAddressParser;

public class ClusterMoniterFactory {
	static final Logger logger = LoggerFactory.getLogger(ClusterMoniterFactory.class);
	
	private static String getAddresses() throws IOException {
		String monAddrs = System.getenv("PG_MONITORS");
		if(monAddrs == null) {
			monAddrs = ClusterConfig.getMonitorAddresses();
			logger.info("从配置文件获得监视器配置：{}", monAddrs);
		} else {
			logger.info("从环境变量获得监视器配置：{}", monAddrs);
		}
		return monAddrs;
	}
	
	public static Socket getFirstUseableConnection() throws IOException {
		Socket socket = null;
		String as = getAddresses();
		if(as == null || as.isEmpty()) {
			logger.warn("可能未配置监视器地址或者所有的监视器都宕机了,等待3秒后重试");
			try {
				Thread.sleep(1000 * 3);
			} catch (InterruptedException e) {
			}
		} else {
			boolean connected = false;
			while((as != "") && !connected) {
				int index = as.indexOf(',');
				String address = (index > 0) ? as.substring(0, index) : as;
				logger.info("解析得到监视器地址：{}", address);
				as = (index > 0) ? as.substring(index + 1, as.length()) : "";
				socket = new Socket();
				try {
					socket.connect(CustomInetAddressParser.parse(address), 1000 * 5);
					connected = true;
				} catch(SocketTimeoutException e) {
					logger.warn("连接监视器{}失败", address);
				}
				logger.info("成功连接到监视器{}", address);
			}
			
		}
		
		return socket;
	}
}
