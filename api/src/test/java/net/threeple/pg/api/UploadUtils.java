package net.threeple.pg.api;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Calendar;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.threeple.pg.shared.util.FileUtils;

public class UploadUtils {
	static final Logger logger = LoggerFactory.getLogger(UploadUtils.class);
	private static String path;
	
	static {
		Calendar cal = Calendar.getInstance();
		String year = String.valueOf(cal.get(Calendar.YEAR));
		String month = String.valueOf(cal.get(Calendar.MONTH) + 1);
		String day = String.valueOf(cal.get(Calendar.DATE));
		path = FileUtils.joinPath(year, month, day);
	}
	
	public static void emptyUploadDir() throws IOException {
		File home = new File(SimplePsdServer.getStoragePath());
		File[] psds = home.listFiles();
		for(File psd : psds) {
			FileUtils.emptyDir(psd);
		}
	}
	
	public static File[] getPictures() throws IOException {
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
	
	public static String createUri(String filename) {
		String ext = filename.substring(filename.lastIndexOf('.'), filename.length());
		UUID uuid = UUID.randomUUID();
		String uri = FileUtils.joinPath(path, uuid.toString() + ext);
		logger.debug("本地文件{}改名为{}", filename, uri);
		return uri;
	}
	
	public static void main(String[] args) {
	}
}
