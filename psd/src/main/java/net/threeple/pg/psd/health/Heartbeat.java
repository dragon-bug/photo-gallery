package net.threeple.pg.psd.health;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.threeple.pg.shared.config.ClusterConfig;
import net.threeple.pg.shared.util.NumberUtils;

public class Heartbeat implements Runnable {
	final Logger logger = LoggerFactory.getLogger(Heartbeat.class);
	private final int id;
	
	public Heartbeat(int _id) {
		this.id = _id;
	}
	
	private InetAddress getMonitorAddress() throws UnknownHostException {
		InetAddress addr = null;
		String as = ClusterConfig.getMonitorAddresses();
		int index = as.indexOf(',');
		as = (index > 0) ? as.substring(0, index) : as;
		index = as.indexOf(':');
		as = as.substring(0, index);
		addr = InetAddress.getByName(as);
		return addr;
	}
	
	@Override
	public void run() {
		logger.info("存储节点#{}心跳包线程启动", this.id);
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket();
			InetAddress addr = getMonitorAddress();
			byte[] buf = NumberUtils.intToByte4(this.id);
			DatagramPacket packet = new DatagramPacket(buf, 4, addr, 7766);
			
			while(true) {
				try {
					socket.send(packet);
				} catch (IOException e) {
					logger.warn("发送心跳包失败");
				}
				try {
					Thread.sleep(5 * 1000);
				} catch (InterruptedException e) {
				}
			}
		} catch (SocketException e1) {
			logger.error("无法创建数据报套接字，错误信息：{}", e1.getMessage());
		} catch (UnknownHostException e1) {
			logger.error("无法解析主机名，错误信息：{}", e1.getMessage());
		} finally {
			socket.close();
		}
	}

}
