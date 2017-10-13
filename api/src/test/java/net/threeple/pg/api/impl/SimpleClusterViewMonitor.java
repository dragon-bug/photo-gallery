package net.threeple.pg.api.impl;

import java.net.InetAddress;
import java.util.Arrays;

import net.threeple.pg.mon.ClusterViewMonitor;
import net.threeple.pg.mon.PlacementGroup;
import net.threeple.pg.mon.PsdNode;

public class SimpleClusterViewMonitor {
	public static void start() throws Exception {
		ClusterViewMonitor monitor = new ClusterViewMonitor(6661);
		monitor.setPgTotal(8);
		InetAddress address = InetAddress.getByName("localhost");
		PsdNode psd0 = new PsdNode(0, address, 6678);
		PsdNode psd1 = new PsdNode(1, address, 6679);
		PlacementGroup[] pgs = new PlacementGroup[8];
		for(int i = 0; i < 8; i++) {
			PlacementGroup pg = new PlacementGroup(i);
			if(i < 4) {
				pg.setPsd(psd0);
			} else {
				pg.setPsd(psd1);
			}
			pgs[i] = pg;
		}
		
		psd0.setPlacementGroups(Arrays.copyOfRange(pgs, 0, 4));
		psd1.setPlacementGroups(Arrays.copyOfRange(pgs, 4, 8));
		
		monitor.setPsdNodes(new PsdNode[]{psd0, psd1});
		Thread thread = new Thread(monitor);
		thread.setDaemon(true);
		thread.start();
	
	}
}
