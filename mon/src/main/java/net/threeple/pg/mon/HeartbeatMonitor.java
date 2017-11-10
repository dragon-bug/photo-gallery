package net.threeple.pg.mon;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeartbeatMonitor extends Observable implements Runnable {
	final Logger logger = LoggerFactory.getLogger(HeartbeatMonitor.class);
	private Map<String, Long> map = new HashMap<>();
	
	@Override
	public void run() {
		try {
			Thread.sleep(10 * 1000);
		} catch (InterruptedException e1) {
		}
		
		while(true) {
			Set<Entry<String, Long>> set = map.entrySet();
			Iterator<Entry<String, Long>> iterator = set.iterator();
			Entry<String, Long> entry = null;
			while(iterator.hasNext()) {
				entry = iterator.next();
				if(entry.getValue() < (System.currentTimeMillis() - 1000 * 10)) {
					logger.info("The PSD#{} was dead", entry.getKey());
				}
			}
			
			try {
				Thread.sleep(5 * 1000);
			} catch (InterruptedException e) {
			}
		}
	}
	
	public void heartbeat(String id) {
		map.put(id, System.currentTimeMillis());
	}
}
