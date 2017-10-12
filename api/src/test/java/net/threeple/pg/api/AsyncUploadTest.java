package net.threeple.pg.api;

import static org.junit.Assert.assertEquals;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Random;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.threeple.pg.api.factory.PhotoStorageFactory;
import net.threeple.pg.api.impl.AsyncPhotoStorage;
import net.threeple.pg.shared.util.FileUtils;
import net.threeple.pg.shared.util.PlacementCalculator;

public class AsyncUploadTest {
	final Logger logger = LoggerFactory.getLogger(AsyncUploadTest.class);
	private String path;
	
	@Before
	public void parpare() throws Exception {
		File home = new File(SimplePsdServer.getStoragePath());
		File[] psds = home.listFiles();
		for(File psd : psds) {
			FileUtils.emptyDir(psd);
		}
		
		SimplePsdServer.start();
		
		Calendar cal = Calendar.getInstance();
		String year = String.valueOf(cal.get(Calendar.YEAR));
		String month = String.valueOf(cal.get(Calendar.MONTH) + 1);
		String day = String.valueOf(cal.get(Calendar.DATE));
		this.path = FileUtils.joinPath(year, month, day);
		
	}
	
	@Test
	public void testUpload() throws Exception {
		AsyncUploader uploader = PhotoStorageFactory.getPhotoStorage(false);
		
		File[] pictures = getPictures();
		String[] filenames = new String[pictures.length];
		
		Random random = new Random();
		int chid = random.nextInt(pictures.length);
		String od = null;
		
		for(int i = 0; i < pictures.length; i++) {
			File picture = pictures[i];
			byte[] body = FileUtils.read(picture);
			String filename = changeFilename(picture.getName());
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
		logger.debug("随机文件{}进行摘要,摘要码:{},上传后的摘要码为:{}", filenames[chid], od, ud);
		
		assertEquals("原文件的摘要与上传后的文件摘要不一致", od, ud);
		
		Thread.sleep(100);
		writeFilenamesToFile(filenames);
	}
	
	private String changeFilename(String filename) {
		String ext = filename.substring(filename.lastIndexOf('.'), filename.length());
		UUID uuid = UUID.randomUUID();
		String rfilename = FileUtils.joinPath(this.path, uuid.toString() + ext);
		logger.debug("本地文件{}改名为{}", filename, rfilename);
		return rfilename;
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
	
	private void writeFilenamesToFile(String[] filenames) throws IOException {
		String userHome = System.getProperty("user.home");
		File file = new File(FileUtils.joinPath(userHome, "Documents\\filenames.txt"));
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		
		for(int i = 0; i < filenames.length; i++) {
			writer.write(filenames[i] + "\n");
		}
		writer.close();
	}
	
	private File[] getPictures() throws IOException {
		String userHome = System.getProperty("user.home");
		File picturesHome = new File(FileUtils.joinPath(userHome, "Pictures"));
		return picturesHome.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				String name = pathname.getName();
				return pathname.isFile() && (name.endsWith(".jpg") || name.endsWith(".png"));
			}
			
		});
	}
	
}
