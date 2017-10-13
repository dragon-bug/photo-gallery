package net.threeple.pg.mon;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.threeple.pg.shared.message.AbstractCharMessageHandler;
import net.threeple.pg.shared.message.MessageSender;

public class Client implements Observer, Runnable {
	final Logger logger = LoggerFactory.getLogger(Client.class);
	private Socket socket;
	private final ClusterViewMonitor cluterView;
	private int port;
	private final InetAddress address;
	
	public Client(Socket _socket, ClusterViewMonitor _clusterView) {
		this.cluterView = _clusterView;
		this.socket = _socket;
		this.address = _socket.getInetAddress();
	}
	
	@Override
	public void update(Observable o, Object arg) {
		Socket _socket = new Socket();
		InetSocketAddress socketAddress = new InetSocketAddress(this.address, this.port); 
		try {
			_socket.connect(socketAddress);
			MessageSender sender = new Updater(_socket);
			sender.send();
		} catch (IOException e) {
			logger.error("无法向客户更新集群视图, 错误信息: {}", e.getMessage());
		} finally {
			if(_socket.isConnected()) {
				try {
					_socket.close();
				} catch (IOException e) {
					logger.warn("用于更新集群视图的套接字没有正确关闭, 错误信息: {}", e.getMessage());
				}
			}
		}
	}

	@Override
	public void run() {
		try {
			MessageSender sender = new Initialisation(this.socket);
			sender.send();
		} catch (IOException e) {
			logger.error("无法向客户发送集群视图, 错误信息: {}", e.getMessage());
		} finally {
			if(this.socket.isConnected()) {
				try {
					this.socket.close();
					this.socket = null;
				} catch (IOException e) {
					logger.warn("用于更新集群视图的套接字没有正确关闭, 错误信息: {}", e.getMessage());
				}
			}
		}
	}
	
	private class Updater extends Initialisation {

		public Updater(Socket _socket) {
			super(_socket);
		}

		@Override
		public void send() throws IOException {
			int version = Client.this.cluterView.getVersion();
			this.writer.write(String.valueOf(version));
			this.writer.newLine();
			this.writer.flush();
			super.send();
		}
		
		
	}
	
	private class Initialisation extends AbstractCharMessageHandler implements MessageSender {
		
		public Initialisation(Socket _socket) {
			try {
				this.reader = new BufferedReader(new InputStreamReader(_socket.getInputStream()));
				this.writer = new BufferedWriter(new OutputStreamWriter(_socket.getOutputStream()));
			} catch (IOException e) {
				logger.error("创建读写器时失败, 失败信息: {}", e.getMessage());
			}
		}
		
		@Override
		public void send() throws IOException {
			sendPsdAddresses();
			sendPlacementGroups();
			register();
			this.reader.close();
			this.writer.close();
			
		}
		
		private void sendPsdAddresses() throws IOException {
			PsdNode[] nodes = Client.this.cluterView.getPsdNodes();
			this.writer.write("psds=" + nodes.length);
			this.writer.newLine();
			for(int i = 0; i < nodes.length; i++) {
				PsdNode node = nodes[i];
				StringBuilder sb = new StringBuilder();
				sb.append(node.getId());
				sb.append('=');
				sb.append(node.getAddress().getHostAddress());
				sb.append(':');
				sb.append(node.getPort());
				this.writer.write(sb.toString());
				this.writer.newLine();
				logger.debug("写入存储节点地址信息: {}", sb.toString());
			}
			this.writer.flush();
		}
		
		private void sendPlacementGroups() throws IOException {
			int pgs = Client.this.cluterView.getPgTotal();
			this.writer.write("pgs=" + pgs);
			this.writer.newLine();
			PsdNode[] nodes = Client.this.cluterView.getPsdNodes();
			for(int i = 0; i < nodes.length; i++) {
				PsdNode node = nodes[i];
				for(PlacementGroup pg : node.getPlacementGroups()) {
					StringBuilder sb = new StringBuilder();
					sb.append(pg.getId());
					sb.append('=');
					sb.append(node.getId());
					this.writer.write(sb.toString());
					this.writer.newLine();
					logger.debug("写入归置组信息: {}", sb.toString());
				}
			}
			this.writer.flush();
		}
		
		private void register() throws IOException {
			String line = this.reader.readLine();
			int eq = line.indexOf('=');
			if("register".equals(line.substring(0, eq))) {
				Client.this.port = Integer.parseInt(line.substring(eq + 1, line.length()));
				Client.this.cluterView.addObserver(Client.this);
			} else {
				Client.this.logger.warn("客户端注册失败");
			}
		}
		
	}
}
