package net.threeple.pg.shared.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.threeple.pg.shared.config.ClusterConfig;

public class PlacementCalculator {
	final static Logger logger = LoggerFactory.getLogger(PlacementCalculator.class);
	private static int pgQuantity;
	
	static {
		pgQuantity = ClusterConfig.getPlacementGroupQuantity();
		logger.info("从配置文件读取到归置组数量：{}", pgQuantity);
	}
	
	public static int calculate(String uri) {
		return Math.abs(uri.hashCode() % pgQuantity);
	}
	
	public static int getPgQuantity() {
		return pgQuantity;
	}
}
