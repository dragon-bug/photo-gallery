package net.threeple.pg.mon.repository;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.threeple.pg.mon.node.StorageNode;
import net.threeple.pg.shared.context.ApplicationContext;
import net.threeple.pg.shared.util.FileUtils;

public class StorageNodeRepositoryImpl implements StorageNodeRepository {
	final Logger logger = LoggerFactory.getLogger(StorageNodeRepositoryImpl.class);
	private RandomAccessFile raFile;
	private final ReentrantLock lock = new ReentrantLock();
	private final PlacementGroupRepository pgRepository;
	private List<StorageNode> cache;
	
	public StorageNodeRepositoryImpl() {
		this.pgRepository = (PlacementGroupRepository) ApplicationContext.getBean("placementGroupRepository");
	}
	
	public StorageNodeRepositoryImpl(PlacementGroupRepository _pgRepository) {
		this.pgRepository = _pgRepository;
	}
	
	private void load() throws FileNotFoundException {
		if(this.raFile == null) {
			try {
				lock.lock();
				if(this.raFile == null) {
					String path = FileUtils.joinPath(System.getProperty("user.home"), "photo-gallery", "psd.pgd");
					File file = new File(path);
					if(!file.exists()) {
						file = new File("/var/local/photo-gallery/psd.pgd");
					}
					this.raFile = new RandomAccessFile(file, "rws");
					logger.info("成功载入存储节点数据文件");
				}
			} finally {
				lock.unlock();
			}
		}
	}
	
	@Override
	public List<StorageNode> getAllNode() throws IOException {
		if(this.cache != null) {
			return this.cache;
		} else {
			load();
			this.raFile.seek(0);
			long len = this.raFile.length();
			int size = (int) len / 50;
			byte[] buf = null;
			List<StorageNode> list = new ArrayList<>(size);
			for(int i = 0; i < size; i++) {
				int pos = i * 50;
				this.raFile.seek(pos);
				pos = this.raFile.readInt();
				buf = new byte[pos];
				this.raFile.read(buf);
				StorageNode node = new StorageNode(i, new String(buf));
				node.setPgs(pgRepository.getPlacementGroupsByPsd(i));
				list.add(node);
			}
			this.cache = list;
			return list;
		}
	}
	
	@Override
	public StorageNode getNodeById(int id) throws IOException {
		return getAllNode().get(id);
	}
	
}
