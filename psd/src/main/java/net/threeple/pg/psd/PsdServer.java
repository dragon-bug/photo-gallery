package net.threeple.pg.psd;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PsdServer {
	Logger logger = LoggerFactory.getLogger(PsdServer.class);
	private final Storage storage;
	private final Executor executor;
	
	public PsdServer(Storage _storage) {
		this.storage = _storage;
		this.executor = Executors.newCachedThreadPool();
	}
	
	public void startup(int port) {
		ServerSocket server = null;
		try {
			server = new ServerSocket(port);
			logger.info("存储节点{}启动成功，监听在{}端口", this.storage.getId(), port);
			while(true) {
				Socket socket = server.accept();
				executor.execute(new Client(socket, this.storage));
			}
		} catch (IOException e) {
			logger.error("存储节点{}启动失败，失败信息：{}", this.storage.getId(), e.getMessage());
		} finally {
			if(server != null) {
				if(!server.isClosed()) {
					try {
						server.close();
						server = null;
					} catch (IOException e) {
						logger.error("存储节点{}无法正常关闭，失败信息：{}", this.storage.getId(), e.getMessage());
					}
				}
			}
		}
	}
	
}
