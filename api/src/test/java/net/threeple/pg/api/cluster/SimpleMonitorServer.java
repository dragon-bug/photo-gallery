package net.threeple.pg.api.cluster;

import net.threeple.pg.mon.MonitorServer;

public class SimpleMonitorServer {
	
	public static void start() throws Exception {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				MonitorServer server = new MonitorServer("mon0", "127.0.0.1", 6661);
				server.start();
			}
			
		}, "Simple-Monitor-Server-Thread");
		thread.setDaemon(true);
		thread.start();
	}
}
