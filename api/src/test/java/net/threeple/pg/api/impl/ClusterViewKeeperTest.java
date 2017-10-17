package net.threeple.pg.api.impl;

import static org.junit.Assert.assertTrue;

import java.net.InetSocketAddress;

import org.junit.Before;
import org.junit.Test;

public class ClusterViewKeeperTest {
	
	@Before
	public void prepare() throws Exception {
		SimpleClusterViewMonitor.start();
	}
	
	@Test
	public void testGetPsdAddress() throws Exception {
		ClusterViewKeeper cvService = ClusterViewKeeper.getInstance();
		Thread.sleep(50);
		for(int i = 0; i < 8; i++) {
			InetSocketAddress address = cvService.getPsdAddress(i);
			if(i < 4) {
				assertTrue(address.getPort() == 6678);
			} else {
				assertTrue(address.getPort() == 6679);
			}
		}
	}

}
