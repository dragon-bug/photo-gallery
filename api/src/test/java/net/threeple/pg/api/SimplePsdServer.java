package net.threeple.pg.api;

import net.threeple.pg.psd.PsdServer;
import net.threeple.pg.psd.persistent.Storage;
import net.threeple.pg.shared.util.FileUtils;

public class SimplePsdServer {
	public static void start() throws Exception {
		final int port = 6678;
		
		for(int i = 0; i < 2; i++) {
			final int id = i;
			Thread thread = new Thread(new Runnable() {

				@Override
				public void run() {
					Storage storage = new Storage(id, FileUtils.joinPath(SimplePsdServer.getStoragePath(), "\\psd" + id));
					
					PsdServer server = new PsdServer(storage);
					server.startup(port + id);
					
				}
				
			}, "Thread-Server-1");
			thread.setDaemon(true);
			thread.start();
		}
		Thread.sleep(300);
	}
	
	public static String getStoragePath() {
		return System.getenv("PSD_HOME");
	}
}
