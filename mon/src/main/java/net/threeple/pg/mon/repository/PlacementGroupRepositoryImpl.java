package net.threeple.pg.mon.repository;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.threeple.pg.shared.config.ClusterConfig;
import net.threeple.pg.shared.util.FileUtils;

public class PlacementGroupRepositoryImpl implements PlacementGroupRepository {
	final Logger logger = LoggerFactory.getLogger(PlacementGroupRepositoryImpl.class);
	private RandomAccessFile raFile;
	private final ReentrantLock lock = new ReentrantLock();
	private int[] cache;
	
	private void load() throws FileNotFoundException {
		if(this.raFile == null) {
			try {
				lock.lock();
				if(this.raFile == null) {
					String path = FileUtils.joinPath(System.getProperty("user.home"), "photo-gallery", "pgs.pgd");
					File file = new File(path);
					if(!file.exists()) {
						file = new File("/var/local/photo-gallery/pgs.pgd");
					}
					this.raFile = new RandomAccessFile(file, "rws");
					logger.info("成功载入归置组数据文件");
				}
			} finally {
				lock.unlock();
			}
		}
	}
	
	@Override
	public int[] getPlacementGroupMap() throws IOException {
		if(cache != null) {
			return this.cache;
		} else {
			load();
			this.raFile.seek(0);
			int length = ClusterConfig.getPlacementGroupQuantity();
			int[] pgMap = new int[length];
			for(int i = 0; i < length; i++) {
				pgMap[i] = this.raFile.readInt();
			}
			this.cache = pgMap;
			return pgMap;
		}
	}
	
	@Override
	public int[] getPlacementGroupsByPsd(int psdId) throws IOException {
		int[] pgMap = getPlacementGroupMap();
		int[] pgs = new int[pgMap.length];
		int q = 0;
		for(int i = 0; i < pgMap.length; i++) {
			if(pgMap[i] == psdId) {
				pgs[q++] = i;
			}
		}
		if(q > 0) {
			pgs = Arrays.copyOfRange(pgs, 0, q);
		}
		return pgs;
	}
	
	
	public static void main(String[] args) throws Exception {
		PlacementGroupRepository pgr = new PlacementGroupRepositoryImpl();
		int[] pgMap = pgr.getPlacementGroupMap();
		System.out.println(Arrays.toString(pgMap));
		
		int[] pgs = pgr.getPlacementGroupsByPsd(0);
		System.out.println(Arrays.toString(pgs));
	}
}
