package net.threeple.pg.mon.health;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HealthChecker implements Runnable {
	final Logger logger = LoggerFactory.getLogger(HealthChecker.class);
	private Map<Integer, Long> beatMap = new HashMap<>();
	
	@Override
	public void run() {
		logger.info("健康检查器启动");
		while(true) {
			for(Entry<Integer, Long> entry : beatMap.entrySet()) {
				long interval = System.currentTimeMillis() - entry.getValue();
				if(interval > (12 * 1000)) {
					logger.warn("已经超过12秒未收到存储节点#{}的心跳了", entry.getKey());
				}
			}
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
			}
		}
	}
	
	public void beat(int id) {
		beatMap.put(id, System.currentTimeMillis());
	}
}
