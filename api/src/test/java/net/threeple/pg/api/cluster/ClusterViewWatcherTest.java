package net.threeple.pg.api.cluster;

import static org.junit.Assert.assertTrue;

import java.net.InetSocketAddress;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.threeple.pg.api.cluster.ClusterViewWatcher;

public class ClusterViewWatcherTest {
	final Logger logger = LoggerFactory.getLogger(ClusterViewWatcherTest.class);
	
	@Before
	public void prepare() throws Exception {
		SimpleMonitorServer.start();
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
