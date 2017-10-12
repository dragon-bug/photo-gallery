package net.threeple.pg.shared.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.StringJoiner;


public class FileUtils {
	public static byte[] read(String uri) throws IOException {
		return read(new FileInputStream(uri));
	}
	
	public static byte[] read(File file) throws IOException {
		return read(new FileInputStream(file));
	}
	
	private static byte[] read(FileInputStream in) throws IOException {
		BufferedInputStream bufIn = new BufferedInputStream(in);
		
		byte[] result = new byte[0];
		byte[] buffer = new byte[1024 * 10];
		int total = 0;
		while((total = bufIn.read(buffer)) > 0) {
			int ol = result.length;
			result = Arrays.copyOf(result, ol + total);
			System.arraycopy(buffer, 0, result, ol, total);
		}
		bufIn.close();
		return  result;
	}
	
	public static void write(String uri, byte[] body) throws IOException {
		if(body.length <= 0) {
			throw new IOException("图片信息为空");
		}
		File file = new File(uri);
		
		File parent = file.getParentFile();
		if(!parent.exists()) {
			parent.setReadable(true);
			parent.setWritable(true, true);
			parent.setExecutable(true);
			parent.mkdirs();
		} else if(!parent.isDirectory()){
			throw new IOException("预计为目录,实际是文件");
		}
		
		file.setReadable(true);
		file.setWritable(true, true);
		FileOutputStream fileOut = new FileOutputStream(file);
		BufferedOutputStream bufOut = new BufferedOutputStream(fileOut);
		bufOut.write(body);
		bufOut.flush();
		bufOut.close();
	}
	
	public static String joinPath(String...strings) {
		StringJoiner joiner = new StringJoiner(System.getProperty("file.separator"));
		for(int i = 0; i < strings.length; i++) {
			String path = strings[i];
			// 保留最后的separator，删除其它以separator结尾的路径片断
			if((i < (strings.length - 1))
					&& (path.endsWith("/") || path.endsWith("\\"))) {
				path = path.substring(0, path.length() - 1);
			}
			// 保留最开始的separator，删除其它以separator开始的路径片断
			if((i > 0) 
					&& (path.startsWith("/") || path.startsWith("\\"))) {
				path = path.substring(1, path.length());
			}
			joiner.add(path);
		}
		return joiner.toString();
	}
	
	public static boolean emptyDir(File dir) throws IOException {
		if(!dir.isDirectory()) {
			return false;
		}
		File[] children = dir.listFiles();
		for(int i = 0; i < children.length; i++) {
			deepDelete(children[i]);
		}
		return dir.listFiles().length == 0;
	}
	
	public static boolean deepDelete(File file) throws IOException {
		if(!file.exists()) {
			return true;
		}
		
		if(file.isDirectory()) {
			File[] children = file.listFiles();
			if(children.length > 0) {
				for(int i = 0; i < children.length; i++) {
					deepDelete(children[i]);
				}
			}
		}
		
		return file.delete();
	}
	
}
