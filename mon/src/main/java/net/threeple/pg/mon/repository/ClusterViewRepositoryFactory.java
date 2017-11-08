package net.threeple.pg.mon.repository;

import java.lang.reflect.Constructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClusterViewRepositoryFactory {
	final static Logger logger = LoggerFactory.getLogger(ClusterViewRepositoryFactory.class);
	private static ClusterViewRepository cvr;
	
	static {
		try {
			Class<?> clazz = Class.forName(ClusterViewRepositoryImpl.class.getName());
			Constructor<?> constructor = clazz.getDeclaredConstructor();
			constructor.setAccessible(true);
			cvr = (ClusterViewRepository) constructor.newInstance();
		} catch (Exception e) {
			logger.error("无法创建ClusterViewRepository实例");
		}
	}
	
	public static ClusterViewRepository getClusterViewRepository() {
		return cvr;
	}
}
