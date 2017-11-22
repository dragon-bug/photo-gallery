package net.threeple.pg.api.cluster;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.threeple.pg.api.exception.ClusterUnhealthyException;
import net.threeple.pg.shared.config.ClusterMoniterFactory;
import net.threeple.pg.shared.util.CustomInetAddressParser;

public class ClusterViewWatcher implements Runnable {
	final Logger logger = LoggerFactory.getLogger(ClusterViewWatcher.class);
	private InetSocketAddress[] psdAddrs;
	private Integer[] placements;
	private static ClusterViewWatcher instance;
	private int port = 6655;
	private final ReentrantLock lock = new ReentrantLock();
	
	static {
		instance = new ClusterViewWatcher();
		Thread thread = new Thread(instance, "Cluster-View-Watcher-Thread");
		thread.setDaemon(true);
		thread.start();
	}
	
	public synchronized static ClusterViewWatcher getInstance() {
		return instance;
	}
	
	public InetSocketAddress getPsdAddress(int placement) throws InterruptedException, ClusterUnhealthyException {
		int count = 0;
		while(((placements == null) || (placements.length == 0)) 
				&& (count < 100)) {
			logger.info("尚未获得集群视图，请稍等片刻");
			Thread.sleep(10);
			count++;
		}
		int pid = placements[placement];
		InetSocketAddress address = this.psdAddrs[pid];
		if(address == null) {
			String message = String.format("集群异常,异常信息:无法获得存储节点#%1s的连接地址", pid);
			throw new ClusterUnhealthyException(message);
		}
		logger.debug("获得归置组#{}寄宿的存储节点地址：{}", placement, address);
		return address;
		
	}
	
	public Socket getPsdConnection(int placement) 
			throws InterruptedException, ClusterUnhealthyException, IOException {
		Socket socket = new Socket();
		InetSocketAddress address = getPsdAddress(placement);
		socket.connect(address, 5 * 1000);
		logger.info("成功连接到存储节点：{}", address);
		return socket;
	}
	
	@Override
	public void run() {
		ServerSocket server = null;
		try {
			server = new ServerSocket();
			String envPort = System.getenv("PG_WATCHER_PORT");
			InetSocketAddress addr = null;
			if(envPort == null) {
				addr = CustomInetAddressParser.parse("0.0.0.0:0");
			} else {
				addr = CustomInetAddressParser.parse("0.0.0.0:" + envPort);
			}
			server.bind(addr);
			this.port = server.getLocalPort();
			logger.info("哨兵启动成功，监听在{}端口", this.port);
			require(ClusterMoniterFactory.getFirstUseableConnection()); // 向集群监视器申请集群视图信息
			while(true) {
				Socket socket = server.accept();
				try {
					lock.lock();
					response(socket); // 接收集群监视器发回的集群视图信息
					logger.info("哨兵完成同步集群视图的工作");
				} finally {
					lock.unlock();
				}
				
			}
		} catch (IOException e) {
			logger.error("哨兵启动失败，错误信息:{}", e.getMessage());
		} finally {
			if(server != null) {
				if(!server.isClosed()) {
					try {
						server.close();
					} catch (IOException e) {
						logger.error("哨兵退出失败，错误信息:{}", e.getMessage());
					}
				}
			}
		}
	}
	
	private void require(Socket socket) throws IOException {
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

		writer.write("Require:ClusterView");
		writer.newLine();
		
		writer.write("response.port=" + this.port);
		writer.newLine();
		
		writer.write("End");
		writer.newLine();
		
		writer.flush();
		
		socket.close();
	}
	
	private void response(Socket socket) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
		String t = reader.readLine();
		if("Response:ClusterView".equals(t) 
				&& "Status:200".equals((t = reader.readLine()))) {
			
			List<InetSocketAddress> addrList = new ArrayList<>();
			List<Integer> pgList = new ArrayList<>();
			
			int n = -1;
			while(!"End".equals((t = reader.readLine()))) {
				n = t.indexOf('=');
				if(n > 0) {
					int psdId = Integer.parseInt(t.substring(0, n));
					t = t.substring(n + 1);
					n = t.indexOf('<');
					
					if(n > 0) {
						String address = t.substring(0, n);
						InetSocketAddress isAddr = CustomInetAddressParser.parse(address);
						addrList.add(psdId, isAddr);
						t = t.substring(n + 1);
						
						while((n = t.indexOf(',')) > 0) {
							int pgId = Integer.parseInt(t.substring(0, n));
							pgList.add(pgId, psdId);
							t = t.substring(n + 1);
						}
						
						if(t.length() > 0) {
							int pgId = Integer.parseInt(t);
							pgList.add(pgId, psdId);
						}
					}
				}
			}
			this.psdAddrs = addrList.toArray(new InetSocketAddress[0]);
			this.placements = pgList.toArray(new Integer[0]);
		} else {
			logger.error("没有收到正确的响应");
		}
		socket.close();
	}
	
}
