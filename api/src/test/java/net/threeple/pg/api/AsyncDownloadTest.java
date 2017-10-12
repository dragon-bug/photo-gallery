package net.threeple.pg.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Future;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.threeple.pg.api.factory.PhotoStorageFactory;

public class AsyncDownloadTest {
	final Logger logger = LoggerFactory.getLogger(AsyncDownloadTest.class);
	private List<String> filenames = new ArrayList<>();
	
	@Before
	public void prepare() throws Exception {
		SimplePsdServer.start();
		
		File f = new File("C:\\Users\\Hamilton\\Documents\\filenames.txt");
		BufferedReader reader = new BufferedReader(new FileReader(f));
		String line = null;
		while((line = reader.readLine()) != null) {
			filenames.add(line);
		}
		reader.close();
	}

	@Test
	public void testDownload() throws Exception {
		AsyncDownloader downloader = PhotoStorageFactory.getPhotoStorage(false);
		List<Future<byte[]>> rs = new ArrayList<>();
		Random random = new Random();
		for(String filename : filenames) {
			Thread.sleep(random.nextInt(5));
			Future<byte[]> result = downloader.asyncDownload(filename);
			rs.add(result);
		}
		Thread.sleep(200);
		int index = random.nextInt(rs.size());
		Future<byte[]> someone = rs.get(index);
		byte[] body = someone.get();
		System.out.println(filenames.get(index) + ": " + body.length);
	}
}
