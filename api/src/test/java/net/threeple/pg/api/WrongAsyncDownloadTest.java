package net.threeple.pg.api;

import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.Before;
import org.junit.Test;

import net.threeple.pg.api.cluster.SimpleMonitorServer;
import net.threeple.pg.api.factory.PhotoStorageFactory;
import net.threeple.pg.api.model.Response;

public class WrongAsyncDownloadTest {
	private AsyncDownloader downloader;
	
	@Before
	public void prepare() throws Exception {
		URL url = this.getClass().getClassLoader().getResource("test.properties");
		Properties prpe = new Properties();
		prpe.load(new FileInputStream(url.getPath()));
		
		String server = prpe.getProperty("server");
		if("inner".equals(server)) {
			SimpleMonitorServer.start();
			SimplePsdServer.start();
		}
		
		this.downloader = PhotoStorageFactory.getPhotoStorage(false);
		
	}
	
	@Test
	public void testWrongDownload() throws InterruptedException, ExecutionException {
		Future<Response> future = downloader.asyncDownload("/some/not/exists/file.jpg");
		Response response = future.get();
		assertTrue(404 == response.getStatusCode());
	}
}
