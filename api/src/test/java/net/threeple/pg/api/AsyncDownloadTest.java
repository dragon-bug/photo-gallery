package net.threeple.pg.api;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Future;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.threeple.pg.api.factory.PhotoStorageFactory;
import net.threeple.pg.api.impl.AsyncPhotoStorage;

public class AsyncDownloadTest {
	final Logger logger = LoggerFactory.getLogger(AsyncDownloadTest.class);
	private List<String> filenames = new ArrayList<>();
	private String year;
	
	@Before
	public void prepare() throws Exception {
		SimplePsdServer.start();
		
		Calendar cal = Calendar.getInstance();
		this.year = String.valueOf(cal.get(Calendar.YEAR));
		
		File home = new File(SimplePsdServer.getStoragePath());
		for(File psd : home.listFiles()) {
			listFiles(psd);
		}
		for(String name : filenames) {
			System.out.println(name);
		}
	}
	
	public void listFiles(File file) throws Exception {
		if(file.isFile()) {
			String path = file.getAbsolutePath();
			path = path.substring(path.indexOf(year), path.length());
			filenames.add(path);
			return;
		} else {
			for(File f : file.listFiles()) {
				listFiles(f);
			}
		}
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
		
		Future<byte[]> lastone = rs.get(rs.size() - 1);
		lastone.get();
		
		if(((AsyncPhotoStorage)PhotoStorageFactory.getPhotoStorage(false)).isWork()) {
			Thread.sleep(100);
		}
		
		Thread.sleep(10);
		
		int index = random.nextInt(rs.size());
		Future<byte[]> someone = rs.get(index);
		byte[] body = someone.get();
		String dd = ComparsionUtils.digest(body);
		String od = ComparsionUtils.digest(filenames.get(index));
		logger.debug("原文件{}的摘要码:{}, 下载的文件的摘要码:{}", filenames.get(index), od , dd);
		
		assertEquals("原文件的摘要与上传后的文件摘要不一致", od, dd);
	}

}
