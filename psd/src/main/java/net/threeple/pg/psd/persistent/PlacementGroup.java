package net.threeple.pg.psd.persistent;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.threeple.pg.psd.io.FileOperation;
import net.threeple.pg.shared.util.FileUtils;

public class PlacementGroup implements FileOperation {
	final Logger logger = LoggerFactory.getLogger(PlacementGroup.class);
	private final int id;
	private final String root;
	
	public PlacementGroup(int _id, String _root) {
		this.id = _id;
		this.root = FileUtils.joinPath(_root, String.valueOf(_id));
	}
	
	public int getId() {
		return this.id;
	}
	
	public String getRoot() {
		return this.root;
	}

	public byte[] read(String uri) throws IOException {
		String path = FileUtils.joinPath(this.root, uri);
		return FileUtils.read(path);
	}
	
	public void write(String uri, byte[] body) throws IOException {
		String path = FileUtils.joinPath(this.root, uri);
		FileUtils.write(path, body);
		logger.info("文件{}写入成功,共写入{}字节", uri, body.length);
	}

	@Override
	public boolean exists(String uri) {
		String path = FileUtils.joinPath(this.root, uri);
		return new File(path).exists();
	}

	@Override
	public boolean delete(String uri) {
		String path = FileUtils.joinPath(this.root, uri);
		return new File(path).delete();
	}
	
}
