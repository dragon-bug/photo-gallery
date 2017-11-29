package net.threeple.pg.shared.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.threeple.pg.shared.util.FileUtils;

public class ApplicationConfig {
	final static Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);
	private static Properties props = new Properties();
	private static long loadTime;
	
	private static void load() {
		String path = FileUtils.joinPath(System.getProperty("user.home"), "pg.conf");
		File file = new File(path);
		if(!file.exists()) {
			file = new File("/etc/photo-gallery/pg.conf");
		}
		if(loadTime < file.lastModified()) {
			if(file.exists()) {
				try {
					props.load(new FileInputStream(file));
				} catch (IOException e) {
					logger.error("无法装载配置文件pg.conf，错误信息：{}", e.getMessage());
				}
				loadTime = file.lastModified();
			} else {
				logger.error("配置文件pg.conf不存在");
			}
		}
	}
	
	public static String getMonitorAddresses() {
		load();
		return props.getProperty("monitors");
	}
	
	public static int getPlacementGroupQuantity() {
		load();
		return Integer.parseInt(props.getProperty("placement_group_quantity"));
	}
	
	public static int getDuplicates() {
		load();
		return Integer.parseInt(props.getProperty("duplicates"));
	}
	
	public static String getSmtpHost() {
		load();
		return props.getProperty("smtp_host");
	}
	
	public static String getSmtpUsername() {
		load();
		return props.getProperty("smtp_username");
	}
	
	public static String getSmtpPassword() {
		load();
		return props.getProperty("smtp_password");
	}
	
	public static String getAdminEmail() {
		load();
		return props.getProperty("admin_email");
	}
	
}
