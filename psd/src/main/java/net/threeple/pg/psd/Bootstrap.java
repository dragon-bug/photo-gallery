package net.threeple.pg.psd;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.threeple.pg.psd.client.ClientRequest;
import net.threeple.pg.psd.client.ClientRequestHandler;
import net.threeple.pg.psd.persistent.Storage;

public class Bootstrap {
	final Logger logger = LoggerFactory.getLogger(Bootstrap.class);
	private InetAddress address;
	private int port;
	private Storage storage;
	private volatile boolean stop;
	private final Executor executor = Executors.newCachedThreadPool();
	
	public void setStorage(int id, String root) {
		this.storage = new Storage(id, root);
	}
	
	public void setPort(int _port) {
		this.port = _port;
	}
	
	public void setAddress(InetAddress _address) {
		this.address = _address;
	}
	
	public void stop() {
		this.stop = true;
	}
	
	public void boot() {
		SocketAddress endpoint = new InetSocketAddress(this.address, this.port);
		ServerSocket server = null;
		try {
			try {
				server = new ServerSocket();
			} catch (IOException e) {
				logger.error("未能正常创建后台服务器，错误信息：{}", e.getMessage());
			}
			
			try {
				server.bind(endpoint);
			} catch (IOException e) {
				logger.error("未能正确绑定地址，错误信息：{}", e.getMessage());
			}
			
			logger.info("存储节点{}启动成功，监听在{}", this.storage.getId(), address + ":" + port);
			
			while(!this.stop) {
				Socket socket = null;
				try {
					socket = server.accept();
				} catch (IOException e) {
					logger.error("存储节点未能正常接收连接，错误信息：{}", e.getMessage());
				}
				
				logger.info("获得来自{}的请求", socket.getInetAddress());
				
				try {
					executor.execute(new ClientRequestHandler(new ClientRequest(socket), this.storage));
				} catch(Exception e) {
					logger.error("处理来自{}的请求失败，失败信息：{}", socket.getInetAddress(), e.getMessage());
				}
			}
		} finally {
			try {
				server.close();
			} catch (IOException e) {
				logger.error("存储节点未能正常停止", e.getMessage());
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		Option idOpt = Option.builder("id").required().hasArg().argName("id").desc("Psd ID").build();
		Option portOpt = Option.builder("port").required().hasArg().argName("port").desc("To listen this port").build();
		Option rootOpt = Option.builder("root").required().hasArg().argName("storage").desc("The root path of storage").build();
		Option addrOpt = Option.builder("addr").required().hasArg().argName("addr").desc("The IP address the API Server will advertise it's listening on. 0.0.0.0 means the default network interface's address.").build();
		
		Options options = new Options();
		options.addOption(idOpt);
		options.addOption(portOpt);
		options.addOption(rootOpt);
		options.addOption(addrOpt);
		
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = parser.parse(options, args);
		int id = Integer.parseInt(cmd.getOptionValue("id"));
		String root = cmd.getOptionValue("root");
		
		String address = cmd.getOptionValue("addr");
		InetAddress iaddr = InetAddress.getByName(address);
		int port = Integer.parseInt(cmd.getOptionValue("port"));
		
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.setStorage(id, root);
		bootstrap.setPort(port);
		bootstrap.setAddress(iaddr);
		bootstrap.boot();

	}

}
