package net.threeple.pg.mon.health;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.threeple.pg.mon.node.StorageNode;
import net.threeple.pg.mon.repository.StorageNodeRepository;
import net.threeple.pg.shared.config.ApplicationConfig;
import net.threeple.pg.shared.context.ApplicationContext;
import net.threeple.pg.shared.notification.EmailNotification;

public class HealthChecker implements Runnable {
	final Logger logger = LoggerFactory.getLogger(HealthChecker.class);
	private Map<Integer, Long> beatMap = new HashMap<>();
	private StorageNodeRepository snRepository;
	private Map<Integer, Long> notifiMap = new HashMap<>();
	private int timeout = 60 * 1000;
	
	public HealthChecker() {
		this.snRepository = (StorageNodeRepository) ApplicationContext.getBean("storageNodeRepository");
		try {
			for(StorageNode node : this.snRepository.getAllNode()) {
				beatMap.put(node.getId(), System.currentTimeMillis());
				notifiMap.put(node.getId(), 0L);
			}
		} catch(IOException e) {
			logger.error("获取所有的存储节点信息失败，错误信息：{}", e.getMessage());
		}
		
	}
	
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	
	@Override
	public void run() {
		logger.info("健康检查器启动");
		while(true) {
			for(Entry<Integer, Long> entry : beatMap.entrySet()) {
				long interval = System.currentTimeMillis() - entry.getValue();
				if(interval > this.timeout) {
					try {
						StorageNode node = snRepository.getNodeById(entry.getKey());
						node.setDown();
					} catch (IOException e) {
						logger.error("未能获得所有的存储节点信息，错误信息：{}", e.getMessage());
					}
					
					long diff = System.currentTimeMillis() - this.notifiMap.get(entry.getKey());
					if(diff > (60 * 1000 * 10)) {
						logger.warn("已经超过{}分钟未收到存储节点#{}的心跳了", (interval / (60 * 1000)), entry.getKey());
						EmailNotification en = new EmailNotification();
						String subject = "图片存储集群" + ApplicationConfig.getClusterName() + "异常通知";
						String text = "紧急通知：心跳监视器已经超过%d分钟未收到存储节点#%d的心跳包了，请尽快恢复。";
						en.send(subject, String.format(text, (interval / (60 * 1000)), entry.getKey()));
						logger.debug("图片存储集群心跳包异常通知已经发出");
						this.notifiMap.put(entry.getKey(), System.currentTimeMillis());
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
