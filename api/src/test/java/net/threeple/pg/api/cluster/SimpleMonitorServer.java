package net.threeple.pg.api.cluster;

import java.net.InetAddress;
import java.net.UnknownHostException;

import net.threeple.pg.mon.Bootstrap;

public class SimpleMonitorServer {
	
	public static void start() throws Exception {
		
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				InetAddress address = null;
				try {
					address = InetAddress.getByName("127.0.0.1");
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
				Bootstrap bootstrap = new Bootstrap("mon0", address, 6661);
				bootstrap.boot();
				
			}
			
		}, "Simple-Monitor-Thread");
		thread.setDaemon(true);
		thread.start();
	}
}
