package net.threeple.pg.mon;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClusterViewMonitor extends Observable implements Observer, Runnable {
	final Logger logger = LoggerFactory.getLogger(ClusterViewMonitor.class);
	private int port;
	private PsdNode[] psdNodes;
	private int pgTotal;
	private final Executor executor;
	private final AtomicInteger version;

	public ClusterViewMonitor(int _port) {
		this.executor = Executors.newCachedThreadPool();
		this.version = new AtomicInteger(0);
		this.port = _port;
	}

	@Override
	public void run() {
		ServerSocket server = null;
		try {
			server = new ServerSocket(this.port);
			logger.info("集群视图监控节点启动成功, 监听在{}端口", this.port);
			while(true) {
				Socket socket = server.accept();
				Client client = new Client(socket, this);
				executor.execute(client);
			}
		} catch (IOException e) {
			logger.error("集群视图监控节点启动失败, 失败信息: {}", e.getMessage());
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		
	}

	public PsdNode[] getPsdNodes() {
		return psdNodes;
	}

	public int getPgTotal() {
		return pgTotal;
	}
	
	public int getVersion() {
		return this.version.get();
	}

	public void setPsdNodes(PsdNode[] psdNodes) {
		this.psdNodes = psdNodes;
	}

	public void setPgTotal(int pgTotal) {
		this.pgTotal = pgTotal;
	}
	
}
