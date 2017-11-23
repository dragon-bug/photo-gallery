package net.threeple.adm;

import java.net.InetAddress;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.threeple.pg.mon.health.HealthChecker;
import net.threeple.pg.mon.health.HeartbeatMonitor;
import net.threeple.pg.psd.health.Heartbeat;

public class HeartbeatTest {
	final Logger logger = LoggerFactory.getLogger(HeartbeatTest.class);
	
	@Test
	public void testHeartbeat() throws Exception {
		Heartbeat heartbeat = new Heartbeat(2);
		Thread thread = new Thread(heartbeat, "Send-Heartbeat-Thread");
		thread.setDaemon(true);
		thread.start();
		
		HealthChecker hc = new HealthChecker();
		
		InetAddress address = InetAddress.getByName("localhost");
		Thread mthread = new Thread(new HeartbeatMonitor(address, hc), "Heartbeat-Monitor-Thread");
		mthread.setDaemon(true);
		mthread.start();
		
		Thread.sleep(10 * 1000);
	}
}
