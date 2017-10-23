package net.threeple.pg.api;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Properties;
import java.util.Random;

//import org.junit.Before;
//import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.threeple.pg.api.factory.PhotoStorageFactory;
import net.threeple.pg.api.impl.AsyncPhotoStorage;
import net.threeple.pg.api.impl.SimpleClusterViewMonitor;
import net.threeple.pg.shared.util.FileUtils;
import net.threeple.pg.shared.util.PlacementCalculator;

public class AsyncUploadTest {
	final Logger logger = LoggerFactory.getLogger(AsyncUploadTest.class);
	private AsyncUploader uploader;
	
//	@Before
	public void parpare() throws Exception {
		URL url = this.getClass().getClassLoader().getResource("test.properties");
		Properties prpe = new Properties();
		prpe.load(new FileInputStream(url.getPath()));
		
		String server = prpe.getProperty("server");
		if("inner".equals(server)) {
			SimpleClusterViewMonitor.start();
			SimplePsdServer.start();
		}
		
		this.uploader = PhotoStorageFactory.getPhotoStorage(false);
		
		UploadUtils.emptyUploadDir();
	}
	
//	@Test
	public void testUpload() throws Exception {
		
		File[] pictures = UploadUtils.getPictures();
		String[] filenames = new String[pictures.length];
		
		Random random = new Random();
		int chid = random.nextInt(pictures.length);
		String od = null;
		
		for(int i = 0; i < pictures.length; i++) {
			File picture = pictures[i];
			byte[] body = FileUtils.read(picture);
			String filename = UploadUtils.createUri(picture.getName());
			filenames[i] = filename;
			
			uploader.asyncUpload(filename, body);
			
			if(i == chid) {
				od = ComparsionUtils.digest(body);
			}
			
			Thread.sleep(random.nextInt(5));
		}
		
		if(((AsyncPhotoStorage)PhotoStorageFactory.getPhotoStorage(false)).isWork()) {
			Thread.sleep(100);
		}
		
		checkFileExists(filenames);
		
		logger.debug("共上传{}个文件", pictures.length);
		
		String ud = ComparsionUtils.digest(filenames[chid]);
		logger.debug("随机对文件{}进行摘要,摘要码:{},上传后的摘要码为:{}", filenames[chid], od, ud);
		
		assertEquals("原文件的摘要与上传后的文件摘要不一致", od, ud);
		
		Thread.sleep(100);
	}
	
	private void checkFileExists(String[] filenames) throws Exception {
		for(String filename : filenames) {
			int pg = PlacementCalculator.calculate(filename);
			int psdid = pg < 4 ? 0 : 1;
			File file = new File(FileUtils.joinPath(SimplePsdServer.getStoragePath(), 
					"psd" + String.valueOf(psdid), String.valueOf(pg), filename));
			if(!file.exists()) {
				logger.debug("文件{}还未写入, 休息50毫秒再看看", filename);
				Thread.sleep(50);
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		AsyncUploadTest test = new AsyncUploadTest();
		test.parpare();
		test.testUpload();
	}
	
}
