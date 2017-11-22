package net.threeple.pg.mon.health;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Observable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.threeple.pg.shared.util.NumberUtils;

public class HeartbeatMonitor extends Observable implements Runnable {
	final Logger logger = LoggerFactory.getLogger(HeartbeatMonitor.class);
	private InetAddress address;
	private final int port = 7766;
	private HealthChecker healthChecker;
	
	public HeartbeatMonitor(InetAddress _address, HealthChecker _healthChecker) {
		this.address = _address;
		this.healthChecker = _healthChecker;
	}
	
	@Override
	public void run() {
		logger.info("监听存储节点心跳包的线程启动");
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket(this.port, this.address);
			byte[] buf = new byte[4];
			int id = -1;
			while(true) {
				DatagramPacket packet = new DatagramPacket(buf, 4);
				try {
					socket.receive(packet);
				} catch (IOException e) {
					logger.error("未能正确接收心跳包，错误信息：{}", e.getMessage());
				}
				id = NumberUtils.byte4ToInt(buf, 0);
				this.healthChecker.beat(id);
				logger.debug("接收到来自存储节点#{}的心跳包", id);
			}
		} catch (SocketException e) {
			logger.error("无法创建数据报套接字，错误信息：{}", e.getMessage());
		} finally {
			socket.close();
		}
	}
	
}
