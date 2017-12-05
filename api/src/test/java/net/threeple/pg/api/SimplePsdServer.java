package net.threeple.pg.api;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.threeple.pg.psd.Bootstrap;
import net.threeple.pg.shared.util.FileUtils;

public class SimplePsdServer {
	final Logger logger = LoggerFactory.getLogger(SimplePsdServer.class);
	public static void start() throws Exception {
		final int port = 6678;
		
		for(int i = 0; i < 2; i++) {
			final int id = i;
			Thread thread = new Thread(new Runnable() {

				@Override
				public void run() {
					Bootstrap bootstrap = new Bootstrap();
					bootstrap.setStorage(id, FileUtils.joinPath(SimplePsdServer.getStoragePath(), "\\psd" + id));
					bootstrap.setPort(port + id);
					try {
						bootstrap.setAddress(InetAddress.getByName("127.0.0.1"));
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					bootstrap.boot();
					
				}
				
			}, "Thread-Server-1");
			thread.setDaemon(true);
			thread.start();
		}
		Thread.sleep(300);
	}
	
	public static String getStoragePath() {
		return System.getenv("PSD_HOME");
	}
}
