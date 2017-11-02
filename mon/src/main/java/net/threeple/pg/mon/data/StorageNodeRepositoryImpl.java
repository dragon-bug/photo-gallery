package net.threeple.pg.mon.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.threeple.pg.mon.model.PlacementGroup;
import net.threeple.pg.mon.model.StorageNode;
import net.threeple.pg.shared.util.FileUtils;

public class StorageNodeRepositoryImpl implements StorageNodeRepository {
	final Logger logger = LoggerFactory.getLogger(StorageNodeRepositoryImpl.class);
	
	@Override
	public List<StorageNode> getAllStorageNode() throws IOException {
		String path = FileUtils.joinPath(System.getProperty("user.home"), "pg-cluster.data");
		File file = new File(path);
		if(!file.exists()) {
			file = new File("/var/local/photo-gallery/pg-cluster.data");
			if(!file.exists()) {
				throw new IOException("集群文件不存在");
			}
		}
		Properties prope = new Properties();
		List<StorageNode> list = null;
		prope.load(new FileInputStream(file));
		list = new ArrayList<>();
		
		for(int i = 0; i < 2; i++) {
			StorageNode sn = new StorageNode();
			sn.setId(i);
			String t = prope.getProperty("ns" + i);
			int s = t.indexOf('<');
			String address = t.substring(0, s);
			sn.setAddress(address);
			logger.debug("获得存储节点#{}的地址：{}", i, address);
			
			t = t.substring(s + 1);
			
			List<PlacementGroup> roomers = new ArrayList<>();
			while((s = t.indexOf(',')) > 0) {
				int id = Integer.parseInt(t.substring(0, s));
				PlacementGroup pg = new PlacementGroup(id, sn);
				roomers.add(pg);
				t = t.substring(s + 1);
				logger.debug("获得存储节点#{}的归置组#{}", i, id);
			}
			int id = Integer.parseInt(t);
			PlacementGroup pg = new PlacementGroup(id, sn);
			roomers.add(pg);
			logger.debug("获得存储节点#{}的归置组#{}", i, id);
			sn.setRoomers(roomers);
			
			list.add(sn);
		}
		return list;
	}

	
	public static void main(String[] args) {
	}
}
