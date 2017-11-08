package net.threeple.pg.mon;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.threeple.pg.mon.monilet.RequestDispatcher;
import net.threeple.pg.mon.monilet.impl.ClusterViewMonilet;
import net.threeple.pg.mon.monilet.impl.HeartbeatMonilet;

public class MonitorServer {
	final Logger logger = LoggerFactory.getLogger(MonitorServer.class);
	private String name;
	private int port;
	
	public MonitorServer(String _name, int _port) {
		this.name = _name;
		this.port = _port;
	}
	
	public void start() {
		RequestDispatcher dispatcher = new RequestDispatcher();
		dispatcher.addMonilet(new ClusterViewMonilet());
		dispatcher.addMonilet(new HeartbeatMonilet());
		
		ServerSocket server = null;
		try {
			server = new ServerSocket(this.port);
			logger.info("集群视图监控节点#{}启动成功, 监听在{}端口", this.name, this.port);
			
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
