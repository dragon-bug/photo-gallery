package net.threeple.pg.api.impl;

import net.threeple.pg.mon.ClusterMonitor;

public class SimpleClusterViewMonitor {
	public static void start() throws Exception {
		ClusterMonitor monitor = new ClusterMonitor("mon0", 6661);
		monitor.init(System.getenv("MONITOR_DATA"));
		Thread thread = new Thread(monitor);
		thread.setDaemon(true);
		thread.start();
	
	}
}
