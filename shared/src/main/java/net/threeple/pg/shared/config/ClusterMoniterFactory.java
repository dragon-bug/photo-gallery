package net.threeple.pg.shared.config;

import java.io.IOException;
import java.net.Socket;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.threeple.pg.shared.util.CustomInetAddressParser;

public class ClusterMoniterFactory {
	static final Logger logger = LoggerFactory.getLogger(ClusterMoniterFactory.class);
	
	private static String getConfig() throws IOException {
		String monAddrs = System.getenv("PG_MONITORS");
		if(monAddrs == null) {
			logger.info("环境变量PG_MONITORS不存在，准备从配置文件读取");
			
			Properties prope = new Properties();
			prope.load(ClusterConfig.getConfig());
			monAddrs = prope.getProperty("monitors");
			logger.info("从配置文件获得监视器地址：{}", monAddrs);
		} else {
			logger.info("从环境变量获得监视器地址：{}", monAddrs);
		}
		return monAddrs;
	}
	
	public static Socket getFirstUseableMonitor() throws IOException {
		Socket socket = null;
		String as = getConfig();
		if(as == null || as.isEmpty()) {
			logger.warn("可能未配置监视器地址或者所有的监视器都宕机了,等待3秒后重试");
			try {
				Thread.sleep(1000 * 3);
			} catch (InterruptedException e) {
			}
		} else {
			int index = as.indexOf(",");
			String address = (index > 0) ? as.substring(0, index) : as;
			logger.info("获得监视器地址：{}", address);
			as = (index > 0) ? as.substring(index + 1, as.length()) : "";
			socket = new Socket();
			logger.info("准备连接到监视器{}", address);
			socket.connect(CustomInetAddressParser.parse(address), 1000 * 5);
			logger.info("成功连接到监视器{}", address);
		}
		
		return socket;
	}
}
