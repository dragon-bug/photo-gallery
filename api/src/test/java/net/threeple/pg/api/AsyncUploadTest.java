package net.threeple.pg.api;

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

public class AsyncUploadTest {
	final Logger logger = LoggerFactory.getLogger(AsyncUploadTest.class);
	
	@Before
	public void parpare() throws Exception {
		File home = new File("D:\\psds");
		File[] psds = home.listFiles();
		for(File psd : psds) {
			FileUtils.emptyDir(psd);
		}
		
		SimplePsdServer.start();
		
	}
	
	@Test
	public void testUpload() throws Exception {
		AsyncUploader uploader = PhotoStorageFactory.getPhotoStorage(false);
		
		Calendar cal = Calendar.getInstance();
		String year = String.valueOf(cal.get(Calendar.YEAR));
		String month = String.valueOf(cal.get(Calendar.MONTH) + 1);
		String day = String.valueOf(cal.get(Calendar.DATE));
		String dir = FileUtils.joinPath(year, month, day);
		
		File[] pictures = getPictures();
		String[] filenames = new String[pictures.length];
		Random random = new Random();
		for(int i = 0; i < pictures.length; i++) {
			File picture = pictures[i];
			byte[] body = FileUtils.read(picture);
			
			String filename = picture.getName();
			String ext = filename.substring(filename.lastIndexOf('.'), filename.length());
			UUID uuid = UUID.randomUUID();
			String rfilename = FileUtils.joinPath(dir, uuid.toString() + ext);
			logger.debug("本地文件{}改名为{},共{}字节", filename, rfilename, body.length);
			uploader.asyncUpload(rfilename, body);
			filenames[i] = rfilename;
			Thread.sleep(random.nextInt(5));
		}
		
		if(((AsyncPhotoStorage)PhotoStorageFactory.getPhotoStorage(false)).isWork()) {
			Thread.sleep(100);
		}
		Thread.sleep(500);
		logger.debug("共上传{}个文件", pictures.length);
		
		File file = new File("C:\\Users\\Hamilton\\Documents\\filenames.txt");
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
