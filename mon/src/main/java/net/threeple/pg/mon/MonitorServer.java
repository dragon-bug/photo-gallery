package net.threeple.pg.mon;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.threeple.pg.mon.monilet.RequestDispatcher;
import net.threeple.pg.mon.monilet.impl.ClusterViewMonilet;

public class MonitorServer {
	final Logger logger = LoggerFactory.getLogger(MonitorServer.class);
	private String name;
	private int port;
	private InetAddress address;
	
	public MonitorServer(String _name, InetAddress _address, int _port) {
		this.name = _name;
		this.address = _address;
		this.port = _port;
	}
	
	public void start() {
		RequestDispatcher dispatcher = new RequestDispatcher();
		dispatcher.addMonilet(new ClusterViewMonilet());
		
		ServerSocket server = null;
		try {
			SocketAddress socketAddr = new InetSocketAddress(this.address, this.port);
			server = new ServerSocket();
			server.bind(socketAddr);
			logger.info("集群视图监控节点#{}启动成功, 监听在{}", this.name, this.address + ":" + this.port);
			
			while(true) {
				Socket socket = server.accept();
				logger.info("接收到来自{}的连接", socket.getInetAddress());
				dispatcher.dispatch(socket);
				logger.info("成功处理来自{}的请求", socket.getInetAddress());
			}
		} catch (IOException e) {
			logger.error("集群视图监控节点#{}启动失败, 失败信息: {}", this.name, e.getMessage());
		}
	}
	
}
