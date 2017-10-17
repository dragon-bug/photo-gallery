package net.threeple.pg.api.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.threeple.pg.api.exception.ClusterUnhealthyException;
import net.threeple.pg.shared.message.AbstractCharMessageHandler;
import net.threeple.pg.shared.message.MessageReceiver;
import net.threeple.pg.shared.util.CustomInetAddressParser;

public class ClusterViewKeeper implements Runnable {
	final Logger logger = LoggerFactory.getLogger(ClusterViewKeeper.class);
	private InetSocketAddress[] addresses;
	private int[] pgs;
	private static ClusterViewKeeper instance;
	private String monitorAddrs;
	private int port;
	private AtomicBoolean initiated = new AtomicBoolean(false);
	
	static {
		instance = new ClusterViewKeeper();
	}
	
	private ClusterViewKeeper() {
		this.monitorAddrs = System.getenv("PG_MONITORS");
		Thread thread = new Thread(this, "Photo-Gallery-Synchronizer-Thread");
		thread.setDaemon(true);
		thread.start();
	}
	
	public synchronized static ClusterViewKeeper getInstance() {
		return instance;
	}
	
	public InetSocketAddress getPsdAddress(int placement) throws InterruptedException, ClusterUnhealthyException {
		int count = 0;
		while(!initiated.get()) {
			if(count > 300) {
				throw new ClusterUnhealthyException("不能正确初始化集群视图");
			}
			logger.info("同步器未完成初始化,等待10毫秒后再测试");
			Thread.sleep(10 * 1);
			count++;
		}
		
		int psdId = pgs[placement];
		if(psdId < 0) {
			repair();
			String message = String.format("集群异常,异常信息:归置组#%1d无主", placement);
			throw new ClusterUnhealthyException(message);
		}
		
		InetSocketAddress address = this.addresses[psdId];
		if(address == null) {
			repair();
			String message = String.format("集群异常,异常信息:无法获得存储节点#%1s的连接地址", psdId);
			throw new ClusterUnhealthyException(message);
		}
		
		return address;
	}
	
	public void repair() {
		initiated.compareAndSet(true, false);
		init();
	}
	
	public void start() {
		ServerSocket server = null;
		try {
			server = new ServerSocket(this.port);
			while(true) {
				Socket socket = server.accept();
				MessageReceiver receiver = new Updater(socket);
				receiver.receive();
			}
		} catch (IOException e) {
			logger.error("无法建立集群观察通道,错误信息:{}", e.getMessage());
		} finally {
			if(server != null) {
				if(!server.isClosed()) {
					try {
						server.close();
					} catch (IOException e) {
						logger.error("集群观察通道无法正常关闭,错误信息:{}", e.getMessage());
					}
				}
			}
		}
	}
	
	@Override
	public void run() {
		init();
		start();
	}
	
	private void init() {
		String mas = this.monitorAddrs;
		while(!initiated.get()) {
			try {
				if(mas == null || mas.isEmpty()) {
					logger.warn("可能未配置监视器地址或者所有的监视器都宕机了,等待3秒后重试");
					Thread.sleep(1000 * 3);
				} else {
					int index = mas.indexOf(",");
					String address = (index > 0) ? mas.substring(0, index) : mas;
					mas = (index > 0) ? mas.substring(index + 1, mas.length()) : "";
					Socket socket = new Socket();
					socket.connect(CustomInetAddressParser.parse(address), 1000 * 5);
					MessageReceiver receiver = new Initialisation(socket);
					receiver.receive();
					socket.close();
				}
				initiated.compareAndSet(false, true);
			} catch (InterruptedException e1) {
				logger.error("重新连接监视器的尝试被中断了,中断信息:{}", e1.getMessage());
			} catch (IOException e2) {
				logger.error("未能正确连接到监视器并同步地址信息,错误消息:{}", e2.getMessage());
			}
		}
	}
	
	private class Updater extends Initialisation {
		
		private Updater(Socket socket) throws IOException {
			super(socket);
		}
		
		@Override
		public void receive() throws IOException {
			String line = this.reader.readLine();
			ClusterViewKeeper.this.logger.info("集群视图发生变更,变更版本:{}", line);
			super.receive();
		}
		
	}
	
	private class Initialisation extends AbstractCharMessageHandler implements MessageReceiver {

		private Initialisation(Socket socket) throws IOException {
			this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		}

		@Override
		public void receive() throws IOException {
			// 获取存储节点的地址信息
			receivePsdAddresses();
			// 获取归置组信息
			receivePlacementGroups();
			// 注册自己
			register();
			
			this.reader.close();
			this.writer.close();
		}
		
		private void receivePsdAddresses() throws IOException {
			String line = this.reader.readLine();
			int eq = line.indexOf('=');
			if(eq > 0 && "psds".equals(line.substring(0, eq))) {
				int size = Integer.parseInt(line.substring(eq + 1, line.length()));
				ClusterViewKeeper.this.addresses = new InetSocketAddress[size];
				for(int i = 0; i < size; i++) {
					line = this.reader.readLine();
					eq = line.indexOf('=');
					int psdId = Integer.parseInt(line.substring(0, eq));
					String address = line.substring(eq + 1, line.length());
					ClusterViewKeeper.this.addresses[psdId] = CustomInetAddressParser.parse(address);
					ClusterViewKeeper.this.logger.debug("获得存储节点#{}的地址:{}", psdId, address);
				}
			}
		}
		
		private void receivePlacementGroups() throws IOException {
			String line = this.reader.readLine();
			int eq = line.indexOf('=');
			if(eq > 0 && "pgs".equals(line.substring(0, eq))) {
				int size = Integer.parseInt(line.substring(eq + 1, line.length()));
				ClusterViewKeeper.this.pgs = new int[size];
				for(int i = 0; i < size; i++) {
					line = this.reader.readLine();
					eq = line.indexOf('=');
					int pgId = Integer.parseInt(line.substring(0, eq));
					int psdId = Integer.parseInt(line.substring(eq + 1, line.length()));
					ClusterViewKeeper.this.pgs[pgId] = psdId;
					ClusterViewKeeper.this.logger.debug("获得归置组#{}的归置信息:存储节点#{}", pgId, psdId);
				}
			}
			
		}
		
		private void register() throws IOException {
			this.writer.write("register=" + ClusterViewKeeper.this.port);
			this.writer.newLine();
			this.writer.flush();
		}

	}

}
