package net.threeple.pg.psd.persistent;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.threeple.pg.psd.health.Heartbeat;
import net.threeple.pg.psd.io.FileOperation;
import net.threeple.pg.shared.config.ApplicationConfig;
import net.threeple.pg.shared.util.PlacementCalculator;

public class Storage implements FileOperation {
	final Logger logger = LoggerFactory.getLogger(Storage.class);
	private final int id;
	private final String root;
	private final PlacementGroupFactory placementGroupFactory;
	
	public Storage(int _id, String _root) {
		this.id = _id;
		this.root = _root;
		this.placementGroupFactory = new PlacementGroupFactory();
		Heartbeat heartbeat = new Heartbeat(_id);
		Thread thread = new Thread(heartbeat, "Send-Heartbeat-Thread");
		thread.setDaemon(true);
		thread.start();
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
		PlacementGroup pg = this.placementGroupFactory.create(uri);
		body = pg.read(uri);
		logger.info("成功从归置组{}读取文件{}", pg.getId(), uri);
		return body;
	}
	
	@Override
	public void write(String uri, byte[] body) throws IOException{
		PlacementGroup pg = this.placementGroupFactory.create(uri);
		pg.write(uri, body);
		logger.info("成功将文件写入归置组{},写入{}字节数据,文件路径{}", pg.getId(), body.length, uri);
	}
	
	@Override
	public boolean exists(String uri) {
		PlacementGroup pg = this.placementGroupFactory.create(uri);
		return pg.exists(uri);
	}

	@Override
	public boolean delete(String uri) {
		PlacementGroup pg = this.placementGroupFactory.create(uri);
		logger.info("准备从归置组{}删除文件{}", pg.getId(), uri);
		return pg.delete(uri);
	}

	private class PlacementGroupFactory {
		private int pgQuantity = ApplicationConfig.getPlacementGroupQuantity();
		
		public PlacementGroup create(String uri) {
			int placement = PlacementCalculator.calculate(uri, this.pgQuantity);
			return new PlacementGroup(placement, root);
		}
	}
}
