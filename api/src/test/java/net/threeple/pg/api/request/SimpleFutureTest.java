package net.threeple.pg.api.request;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Before;
import org.junit.Test;

import net.threeple.pg.api.async.SimpleFuture;
import net.threeple.pg.api.model.Response;

public class SimpleFutureTest {
	private SimpleFuture future = new SimpleFuture();
	
	@Before
	public void prepare() {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				boolean com = future.complete(new Response(200));
				assertTrue("赋值失败", com);
			}
			
		}, "Test-simplefuture-thread-1");
		thread.start();
	}
	
	@Test
	public void testGet() throws Exception {
		assertSame("未得到正确的值", Integer.valueOf(200), future.get().getStatusCode());
	}
	
	@Test
	public void testTimeoutGet() throws Exception {
		assertSame("超时仍未得到正确的值", Integer.valueOf(200), future.get(15, TimeUnit.MILLISECONDS).getStatusCode());
	}
	
	@Test(expected=TimeoutException.class)
	public void testTimeout() throws Exception {
		future.get(5, TimeUnit.MILLISECONDS);
	}
	
}
