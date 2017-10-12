package net.threeple.pg.psd;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.threeple.pg.shared.util.PlacementCalculator;

public class Storage implements FileWriter, FileReader {
	final Logger logger = LoggerFactory.getLogger(Storage.class);
	private final int id;
	private final String root;
	private final PlacementGroupFactory placementGroupFactory;
	
	public Storage(int _id, String _root) {
		this.id = _id;
		this.root = _root;
		this.placementGroupFactory = new PlacementGroupFactory();
	}
	
	public int getId() {
		return this.id;
	}
	
	public String getRoot() {
		return this.root;
	}
	
	@Override
	public byte[] read(String uri) throws IOException {
		byte[] body = new byte[]{-1};
		try {
			PlacementGroup pg = this.placementGroupFactory.create(uri);
			body = pg.read(uri);
		} catch (IOException e) {
			logger.error("无法读取文件：{}", uri);
		}
		return body;
	}
	
	@Override
	public void write(String uri, byte[] body) throws IOException{
		try {
			PlacementGroup pg = this.placementGroupFactory.create(uri);
			pg.write(uri, body);
		} catch (IOException e) {
			logger.error("无法存储文件：{}", uri);
			e.printStackTrace();
		}
		
	}
	
	private class PlacementGroupFactory {
		
		public PlacementGroup create(String uri) {
			int placement = PlacementCalculator.calculate(uri);
			return new PlacementGroup(placement, root);
		}
	}
}
