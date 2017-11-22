package net.threeple.pg.mon.monilet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestDispatcher {
	final Logger logger = LoggerFactory.getLogger(RequestDispatcher.class);
	private Executor executor = Executors.newCachedThreadPool();
	private Map<String, Monilet> moniletMap = new HashMap<>();
	
	public void addMonilet(Monilet monilet) {
		String name = monilet.getClass().getSimpleName();
		logger.debug("添加一个Monilet: {}", name);
		moniletMap.put(name, monilet);
	}
	
	public void dispatch(final Socket socket) {
		executor.execute(new Runnable() {

			@Override
			public void run() {
				BufferedReader reader = null;
				BufferedWriter writer = null;
				try {
					reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				} catch (IOException e) {
					logger.error("创建读入器失败，错误信息：{}", e.getMessage());
				}
				
				IMoniletRequest request = null;
				try {
					request = RequestParser.parse(reader);
				} catch (IOException e1) {
					logger.error("无法解析请求，错误信息：{}", e1.getMessage());
				}
				
				if(request != null) {
					Socket rsocket = null;
					
					if(request.getBody().containsKey("response.port")) {
						int port = Integer.parseInt(request.getBody().get("response.port"));
						InetSocketAddress address = new InetSocketAddress(socket.getInetAddress(), port);
						rsocket = new Socket();
						try {
							rsocket.connect(address, 5 * 1000);
						} catch (IOException e) {
							logger.error("连接客户端失败，错误信息：{}", e.getMessage());
						}
						try {
							writer = new BufferedWriter(new OutputStreamWriter(rsocket.getOutputStream()));
						} catch (IOException e) {
							logger.error("创建写入器失败，错误信息：{}", e.getMessage());
						}
					} else {
						try {
							writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
						} catch (IOException e) {
							logger.error("创建写入器失败，错误信息：{}", e.getMessage());
						}
					}
					
					IMoniletResponse response = new MoniletResponse(writer);
					
					Monilet monilet = moniletMap.get(request.getRequire());
					monilet.service(request, response);
					
					try {
						Thread.sleep(5 * 1000);
						if(socket != null && socket.isConnected()) {
							socket.close();
						}
						if(rsocket != null && rsocket.isConnected()) {
							rsocket.close();
						}
					} catch (InterruptedException e) {
						logger.warn("等待5秒检查连接的操作被中断");
					} catch (IOException e) {
						logger.error("来自客户的连接未能正常关闭，错误信息：{}", e.getMessage());
					}
				} else {
					logger.error("没有正确解析请求");
				}
			}
		});
	}
}
