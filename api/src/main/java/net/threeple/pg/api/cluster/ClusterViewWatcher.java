package net.threeple.pg.api.cluster;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.threeple.pg.api.exception.ClusterUnhealthyException;
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
			InetSocketAddress addr = CustomInetAddressParser.parse("127.0.0.1:0");
			server.bind(addr);
			this.port = server.getLocalPort();
			logger.info("哨兵启动成功，监听在{}端口", this.port);
			require(getFirstUseableMonitor()); // 向集群监视器申请集群视图信息
			while(true) {
				Socket socket = server.accept();
				try {
					lock.lock();
					response(socket); // 接收集群监视器发回的集群视图信息
					logger.info("哨兵完成初始化");
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
	
	private Socket getFirstUseableMonitor() {
		Socket socket = null;
		String as = getClusterMonitersConfig();
		try {
			if(as == null || as.isEmpty()) {
				logger.warn("可能未配置监视器地址或者所有的监视器都宕机了,等待3秒后重试");
				Thread.sleep(1000 * 3);
			} else {
				int index = as.indexOf(",");
				String address = (index > 0) ? as.substring(0, index) : as;
				logger.info("获得监视器地址：{}", address);
				as = (index > 0) ? as.substring(index + 1, as.length()) : "";
				socket = new Socket();
				logger.info("准备连接到监视器{}", address);
				socket.connect(CustomInetAddressParser.parse(address), 1000 * 5);
				logger.info("成功连接到监视器{}", address);
			}
			
			logger.info("监视器哨兵完成初始化工作");
		} catch (InterruptedException e1) {
			logger.error("重新连接监视器的尝试被中断了,中断信息:{}", e1.getMessage());
		} catch (IOException e2) {
			logger.error("未能正确连接到监视器并同步地址信息,错误消息:{}", e2.getMessage());
		}
		return socket;
	}
	
	private String getClusterMonitersConfig() {
		String monAddrs = System.getenv("PG_MONITORS");
		if(monAddrs == null) {
			logger.info("环境变量PG_MONITORS不存在，准备从配置文件读取");
			URL url = this.getClass().getClassLoader().getResource("pg.conf");
			Properties prpe = new Properties();
			try {
				prpe.load(new FileInputStream(url.getPath()));
				monAddrs = prpe.getProperty("monitors");
				logger.info("从配置文件获得监视器地址：{}", monAddrs);
			} catch (FileNotFoundException e) {
				logger.error("监视器配置文件不存在, 错误信息: {}", e.getMessage());
			} catch (IOException e) {
				logger.error("无法读取监视器配置文件, 错误信息: {}", e.getMessage());
			}
		} else {
			logger.info("从环境变量获得监视器地址：{}", monAddrs);
		}
		return monAddrs;
	}
	
	private void require(Socket socket) throws IOException {
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		
		writer.write("Response:port=" + this.port);
		writer.newLine();
		writer.write("Require:AllStorageNode");
		writer.newLine();
		writer.write("Require:AllPlacementGroup");
		writer.newLine();
		writer.write("End");
		writer.newLine();
		writer.flush();
		
		socket.close();
	}
	
	private void response(Socket socket) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
		String t = null;
		while((t = reader.readLine()) != null) {
			if("Response:AllStorageNode".equals(t)) {
				String v = null;
				List<InetSocketAddress> list = new ArrayList<>();
				while(!"End".equals((v = reader.readLine()))) {
					int es = v.indexOf('=');
					if(es > 0) {
						int id = Integer.parseInt(v.substring(0, es));
						v = v.substring(es + 1);
						InetSocketAddress addr = CustomInetAddressParser.parse(v);
						list.add(id, addr);
						logger.debug("获得存储节点#{}的地址：{}", id, v);
					}
				}
				this.psdAddrs = list.toArray(new InetSocketAddress[0]);
			} else if("Response:AllPlacementGroup".equals(t)) {
				String v = null;
				List<Integer> list = new ArrayList<>();
				while(!"End".equals((v = reader.readLine()))) {
					int gt = v.indexOf('>');
					if(gt > 0) {
						int id = Integer.parseInt(v.substring(0, gt));
						int pid = Integer.parseInt(v.substring(gt + 1));
						list.add(id, pid);
						logger.debug("获得归置组#{}的归置：{}", id, pid);
					}
				}
				this.placements = list.toArray(new Integer[0]);
			}
		}
		socket.close();
	}
	
}
