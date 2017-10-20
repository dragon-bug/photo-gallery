package net.threeple.pg.mon;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

import java.util.Random;

import org.junit.Test;

public class ClusterViewMonitorTest {
	
	@Test
	public void testInit() {
		ClusterViewMonitor monitor = new ClusterViewMonitor("mon0", 6661);
		monitor.init("D:\\mons\\mon0");
		
		PlacementGroup[] pgs = monitor.getPgs();
		PsdNode[] psds = monitor.getPsdNodes();
		
		assertTrue(pgs.length == 8);
		assertTrue(psds.length == 2);
		
		Random random = new Random();
		assertNotNull(pgs[random.nextInt(8)]);
	}
}
