package net.threeple.pg.api.impl;

import net.threeple.pg.mon.ClusterViewMonitor;

public class SimpleClusterViewMonitor {
	public static void start() throws Exception {
		ClusterViewMonitor monitor = new ClusterViewMonitor(6661);
		monitor.init("D:\\mons\\mon0");
		Thread thread = new Thread(monitor);
		thread.setDaemon(true);
		thread.start();
	
	}
}
