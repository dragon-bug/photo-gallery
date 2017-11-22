package net.threeple.pg.api.cluster;

import java.net.InetAddress;

import net.threeple.pg.mon.MonitorServer;
import net.threeple.pg.mon.health.HealthChecker;
import net.threeple.pg.mon.health.HeartbeatMonitor;

public class SimpleMonitorServer {
	
	public static void start() throws Exception {
		InetAddress address = InetAddress.getByName("127.0.0.1");
		
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				MonitorServer server = new MonitorServer("mon0", address, 6661);
				server.start();
			}
			
		}, "Simple-Monitor-Server-Thread");
		thread.setDaemon(true);
		thread.start();
		
		HealthChecker hc = new HealthChecker();
		Thread hthread = new Thread(new HeartbeatMonitor(address, hc), "Heartbeat-Monitor-Thread");
		hthread.setDaemon(true);
		hthread.start();
	}
}
