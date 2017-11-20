package net.threeple.pg.shared.util;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.threeple.pg.shared.config.ClusterConfig;

public class PlacementCalculator {
	final static Logger logger = LoggerFactory.getLogger(PlacementCalculator.class);
	private static int pgQuantity;
	static {
		try {
			Properties prope = new Properties();
			prope.load(ClusterConfig.getConfig());
			String pgs = prope.getProperty("placement_group_quantity");
			pgQuantity = Integer.parseInt(pgs);
			logger.info("从配置文件读取到归置组数量：{}", pgQuantity);
		} catch(IOException e) {
			logger.error("无法从配置文件中读取归置组数量");
		}
	}
	
	public static int calculate(String uri) {
		return Math.abs(uri.hashCode() % pgQuantity);
	}
	
	public static int getPgQuantity() {
		return pgQuantity;
	}
}
