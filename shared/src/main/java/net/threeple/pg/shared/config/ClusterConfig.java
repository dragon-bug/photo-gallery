package net.threeple.pg.shared.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import net.threeple.pg.shared.util.FileUtils;

public class ClusterConfig {
	public static FileInputStream getConfig() throws IOException {
		String path = FileUtils.joinPath(System.getProperty("user.home"), "pg.conf");
		File file = new File(path);
		if(!file.exists()) {
			file = new File("/etc/photo-gallery/pg.conf");
			if(!file.exists()) {
				throw new IOException("配置文件不存在");
			}
		}
		return new FileInputStream(file);
	}
}
