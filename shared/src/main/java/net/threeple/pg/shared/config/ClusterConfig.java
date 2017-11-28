package net.threeple.pg.shared.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.threeple.pg.shared.util.FileUtils;

public class ClusterConfig {
	final static Logger logger = LoggerFactory.getLogger(ClusterConfig.class);
	private static Properties prop = new Properties();
	
	static {
		String path = FileUtils.joinPath(System.getProperty("user.home"), "pg.conf");
		File file = new File(path);
		if(!file.exists()) {
			file = new File("/etc/photo-gallery/pg.conf");
		}
		if(file.exists()) {
			try {
				prop.load(new FileInputStream(file));
			} catch (IOException e) {
				logger.error("无法装载配置文件pg.conf，错误信息：{}", e.getMessage());
			}
		} else {
			logger.error("配置文件pg.conf不存在");
		}
	}
	
	public static String getMonitorAddresses() {
		return prop.getProperty("monitors");
	}
	
	public static int getPlacementGroupQuantity() {
		return Integer.parseInt(prop.getProperty("placement_group_quantity"));
	}
	
	public static int getDuplicates() {
		return Integer.parseInt(prop.getProperty("duplicates"));
	}
	
}
