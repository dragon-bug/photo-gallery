package net.threeple.pg.mon.monilet.impl;

import java.io.BufferedReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.threeple.pg.mon.HeartbeatMonitor;
import net.threeple.pg.mon.monilet.IMoniletRequest;
import net.threeple.pg.mon.monilet.IMoniletResponse;
import net.threeple.pg.mon.monilet.Monilet;

public class HeartbeatMonilet implements Monilet {
	final Logger logger = LoggerFactory.getLogger(HeartbeatMonilet.class);
	private final HeartbeatMonitor monitor;
	
	public HeartbeatMonilet() {
		monitor = new HeartbeatMonitor();
		Thread thread = new Thread(monitor, "Heartbeat-Monitor-Thread");
		thread.setDaemon(true);
		thread.start();
	}
	
	@Override
	public void service(IMoniletRequest request, IMoniletResponse response) {
		String id = request.getBody().get("id");
		BufferedReader reader = request.getReader();
		String line = null;
		try {
			while(true) {
				if((line = reader.readLine()) != null) {
					logger.debug("监听到来自存储节点#{}的心跳：{}", id, line);
					monitor.heartbeat(id);
				}
				Thread.sleep(5 * 1000);
			}
		} catch (Exception e) {
			logger.error("监听存储节点#{}心跳失败，错误信息：{}", id, e.getMessage());
		} 
	}

}
