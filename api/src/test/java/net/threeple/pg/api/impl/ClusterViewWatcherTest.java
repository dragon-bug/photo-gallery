package net.threeple.pg.api.impl;

import static org.junit.Assert.assertTrue;

import java.net.InetSocketAddress;

import org.junit.Before;
import org.junit.Test;

import net.threeple.pg.api.cluster.ClusterViewWatcher;

public class ClusterViewWatcherTest {
	
	@Before
	public void prepare() throws Exception {
		SimpleClusterViewMonitor.start();
	}
	
	@Test
	public void testGetPsdAddress() throws Exception {
		ClusterViewWatcher cvService = ClusterViewWatcher.getInstance();
		Thread.sleep(50);
		for(int i = 0; i < 8; i++) {
			InetSocketAddress address = cvService.getPsdAddress(i);
			if(i < 4) {
				assertTrue(address.getPort() > 0);
			} else {
				assertTrue(address.getPort() > 0);
			}
		}
	}

}
