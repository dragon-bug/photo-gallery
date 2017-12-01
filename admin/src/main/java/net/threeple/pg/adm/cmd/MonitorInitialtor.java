package net.threeple.pg.adm.cmd;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.threeple.pg.shared.util.FileUtils;

public class MonitorInitialtor {
	final Logger logger = LoggerFactory.getLogger(MonitorInitialtor.class);
	
	public void init(String filename) throws Exception {
		filename = FileUtils.joinPath(System.getProperty("user.dir"), filename);
		File cfile = new File(filename);
		if(!cfile.exists()) {
			logger.error("Not found the file: {}", filename);
			return;
		}
		logger.info("准备从文件{}读取数据", filename);
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(cfile)));
		String line = reader.readLine();
		String[] ss = line.split(" ");
		int[] pgMap = new int[ss.length];
		for(int i = 0; i < ss.length; i++) {
			pgMap[i] = Integer.parseInt(ss[i]);
		}
		
		List<String> list = new ArrayList<>();
		int index = 0;
		while((line = reader.readLine()) != null) {
			list.add(index++, line);
		}
		reader.close();
		logger.info("完成数据读取，准备初始化...");
		
		filename = FileUtils.joinPath(System.getProperty("user.dir"), "pgs.pgd");
		DataOutputStream dos = new DataOutputStream(new FileOutputStream(filename));
		for(int p : pgMap) {
			dos.writeInt(p);
		}
		dos.close();
		
		filename = FileUtils.joinPath(System.getProperty("user.dir"), "psd.pgd");
		dos = new DataOutputStream(new FileOutputStream(filename));
		for(int i = 0; i < list.size(); i++) {
			String addr = list.get(i);
			byte[] buf = addr.getBytes();
			int len = buf.length;
			dos.writeInt(len);
			buf = Arrays.copyOf(buf, 50 - 4);
			dos.write(buf);
		}
		dos.close();
		logger.info("初始化完成！");
	}
}
