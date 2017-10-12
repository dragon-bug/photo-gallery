package net.threeple.pg.api.request;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

public class SimpleFutureTest {
	
	@Test
	public void test() throws InterruptedException, ExecutionException, TimeoutException {
		SimpleFuture<Integer> future = new SimpleFuture<>();
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				boolean com = future.complete(1);
				System.out.println("complete:" + com);
			}
			
		}, "Test-simplefuture-thread-1");
		thread.start();
		/*future.cancel(true);
		System.out.println("r:" + future.get(15, TimeUnit.MILLISECONDS));*/
		System.out.println("r:" + future.get());
	}
}
