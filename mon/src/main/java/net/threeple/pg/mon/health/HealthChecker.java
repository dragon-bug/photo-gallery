package net.threeple.pg.mon.health;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.threeple.pg.mon.node.StorageNode;
import net.threeple.pg.mon.repository.StorageNodeRepository;
import net.threeple.pg.shared.context.ApplicationContext;

public class HealthChecker implements Runnable {
	final Logger logger = LoggerFactory.getLogger(HealthChecker.class);
	private Map<Integer, Long> beatMap = new HashMap<>();
	private StorageNodeRepository snRepository;
	
	public HealthChecker() {
		this.snRepository = (StorageNodeRepository) ApplicationContext.getBean("storageNodeRepository");
	}
	
	@Override
	public void run() {
		logger.info("健康检查器启动");
		while(true) {
			for(Entry<Integer, Long> entry : beatMap.entrySet()) {
				long interval = System.currentTimeMillis() - entry.getValue();
				if(interval > (60 * 1000)) {
					logger.warn("已经超过1分钟未收到存储节点#{}的心跳了", entry.getKey());
					List<StorageNode> nodes;
					try {
						nodes = snRepository.getAllNode();
						StorageNode node = nodes.get(entry.getKey());
						node.setDown();
					} catch (IOException e) {
						logger.error("未能获得所有的存储节点信息，错误信息：{}", e.getMessage());
					}
					
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
