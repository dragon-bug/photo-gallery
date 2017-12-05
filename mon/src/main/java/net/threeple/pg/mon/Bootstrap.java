package net.threeple.pg.mon;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.threeple.pg.mon.health.HealthChecker;
import net.threeple.pg.mon.health.HeartbeatMonitor;
import net.threeple.pg.mon.monilet.RequestDispatcher;
import net.threeple.pg.mon.monilet.impl.ClusterViewMonilet;
import net.threeple.pg.mon.repository.PlacementGroupRepositoryImpl;
import net.threeple.pg.mon.repository.StorageNodeRepositoryImpl;
import net.threeple.pg.shared.context.ApplicationContext;

public class Bootstrap {
	final Logger logger = LoggerFactory.getLogger(Bootstrap.class);
	private volatile boolean stop = false;
	private String name;
	private int port;
	private InetAddress address;
	
	public Bootstrap() {
		super();
	}
	
	public Bootstrap(String _name, InetAddress _address, int _port) {
		this.name = _name;
		this.address = _address;
		this.port = _port;
		
		ApplicationContext.addBean("placementGroupRepository", new PlacementGroupRepositoryImpl());
		ApplicationContext.addBean("storageNodeRepository", new StorageNodeRepositoryImpl());
	}
	
	public void stop() {
		this.stop = true;
	}
	
	public void boot() {
		// 启动健康检查器
		HealthChecker hc = new HealthChecker();
		Thread hcThread = new Thread(hc, "Health-Check-Thread");
		hcThread.setDaemon(true);
		hcThread.start();
		
		// 启动心跳监视仪
		Thread hmThread = new Thread(new HeartbeatMonitor(address, hc), "Heartbeat-Monitor-Thread");
		hmThread.setDaemon(true);
		hmThread.start();
		
		// 启动监视服务器
		RequestDispatcher dispatcher = new RequestDispatcher();
		dispatcher.addMonilet(new ClusterViewMonilet());
		
		SocketAddress socketAddr = new InetSocketAddress(this.address, this.port);
		
		ServerSocket server = null;
		try {
			server = new ServerSocket();
		} catch (IOException e) {
			logger.error("创建监控节点#{}服务器失败, 失败信息: {}", this.name, e.getMessage());
		}
		try {
			server.bind(socketAddr);
			logger.info("集群视图监控节点#{}启动成功, 监听在{}", this.name, this.address + ":" + this.port);
		} catch (IOException e) {
			logger.error("绑定地址{}失败，失败信息：{}", server.getInetAddress(), e.getMessage());
		}
		
		while(!this.stop) {
			Socket socket = null;
			try {
				socket = server.accept();
				logger.info("接收到来自{}的连接", socket.getInetAddress());
			} catch (IOException e) {
				logger.error("存储节点未能正常接收连接，错误信息：{}", e.getMessage());
			}
			try {
				dispatcher.dispatch(socket);
				logger.info("成功处理来自{}的请求", socket.getInetAddress());
			} catch(Exception e) {
				logger.error("未能正确处理来自{}的请求，错误信息：{}", socket.getInetAddress(), e.getMessage());
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		
		Option nameOpt = Option.builder("name").required().hasArg().argName("name").desc("The monitor's name").build();
		Option portOpt = Option.builder("port").required().hasArg().argName("port").desc("To listen this port").build();
		Option addrOpt = Option.builder("addr").required().hasArg().argName("addr").desc("The IP address the API Server will advertise it's listening on. 0.0.0.0 means the default network interface's address.").build();
		Options options = new Options();
		options.addOption(nameOpt);
		options.addOption(portOpt);
		options.addOption(addrOpt);
		
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = parser.parse(options, args);
		
		String name = cmd.getOptionValue("name");
		int port = Integer.parseInt(cmd.getOptionValue("port"));
		InetAddress address = InetAddress.getByName(cmd.getOptionValue("addr"));
		
		Bootstrap bootstrap = new Bootstrap(name, address, port);
		bootstrap.boot();
	}

}
