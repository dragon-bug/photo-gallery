package net.threeple.pg.mon.health;

public class HealthCheckerTest {
	/*@Before
	public void prepare() throws Exception {
		ApplicationContext.addBean("placementGroupRepository", new PlacementGroupRepositoryImpl());
		ApplicationContext.addBean("storageNodeRepository", new StorageNodeRepositoryImpl());
		
		// 启动健康检查器
		HealthChecker hc = new HealthChecker();
		hc.setTimeout(10 * 1000);
		Thread hcThread = new Thread(hc, "Health-Check-Thread");
		hcThread.setDaemon(true);
		hcThread.start();
		
		// 启动心跳监视仪
		Thread hmThread = new Thread(new HeartbeatMonitor(InetAddress.getLocalHost(), hc), "Heartbeat-Monitor-Thread");
		hmThread.setDaemon(true);
		hmThread.start();
	}
	
	@Test
	public void testNotification() throws Exception {
		Thread.sleep(20 * 1000);
	}*/
}
