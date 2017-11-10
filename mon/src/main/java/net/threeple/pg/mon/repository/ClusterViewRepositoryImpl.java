package net.threeple.pg.mon.repository;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.threeple.pg.shared.util.FileUtils;

public class ClusterViewRepositoryImpl implements ClusterViewRepository {
	final Logger logger = LoggerFactory.getLogger(ClusterViewRepositoryImpl.class);
	private List<String> view;
	
	private ClusterViewRepositoryImpl() {
		
	}
	
	@Override
	public List<String> getClusterView() {
		if(view == null) {
			String path = FileUtils.joinPath(System.getProperty("user.home"), "photo-gallery", "cluster-view.cvd");
			File file = new File(path);
			if(!file.exists()) {
				file = new File("/var/local/photo-gallery/cluster-view.cvd");
				if(!file.exists()) {
					return null;
				}
			}
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			} catch (FileNotFoundException e) {
				logger.error("不可预料的错误：集群视图数据文件不存在。错误信息：{}", e.getMessage());
			}
			
			List<String> list = new ArrayList<>();
			String line = null;
			try {
				while((line = reader.readLine()) != null) {
					list.add(line);
				}
				this.view = list;
			} catch (IOException e) {
				logger.error("无法读取集群视图数据文件的内容，错误信息：{}", e.getMessage());
			}
		}
		return this.view;
	}
	
	public static void main(String[] args) {
		ClusterViewRepository cvr = ClusterViewRepositoryFactory.getClusterViewRepository();
		for(String line : cvr.getClusterView()) {
			System.out.println(line);
		}
	}

}
