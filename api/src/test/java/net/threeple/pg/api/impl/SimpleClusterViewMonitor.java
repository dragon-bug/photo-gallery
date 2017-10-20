package net.threeple.pg.api.impl;

import net.threeple.pg.mon.ClusterViewMonitor;

public class SimpleClusterViewMonitor {
	public static void start() throws Exception {
		ClusterViewMonitor monitor = new ClusterViewMonitor("mon0", 6661);
		monitor.init(System.getenv("MONITOR_DATA"));
		Thread thread = new Thread(monitor);
		thread.setDaemon(true);
		thread.start();
	
	}
}
