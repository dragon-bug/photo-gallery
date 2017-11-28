package net.threeple.pg.mon.monilet.impl;

import java.io.BufferedWriter;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.threeple.pg.mon.monilet.IMoniletRequest;
import net.threeple.pg.mon.monilet.IMoniletResponse;
import net.threeple.pg.mon.monilet.Monilet;
import net.threeple.pg.mon.node.StorageNode;
import net.threeple.pg.mon.repository.StorageNodeRepository;
import net.threeple.pg.shared.context.ApplicationContext;

public class ClusterViewMonilet implements Monilet {
	final Logger logger = LoggerFactory.getLogger(ClusterViewMonilet.class);
	private final StorageNodeRepository snRepository;
	
	public ClusterViewMonilet() {
		this.snRepository = (StorageNodeRepository) ApplicationContext.getBean("storageNodeRepository");
	}
	
	@Override
	public void service(IMoniletRequest request, IMoniletResponse response) {
		BufferedWriter writer = response.getWriter();
		try {
			writer.write("Response:ClusterView");
			writer.newLine();
			
			writer.write("Status:200");
			writer.newLine();
			for(StorageNode node : this.snRepository.getAllNode()) {
				writer.write(node.toString());
				writer.newLine();
			}
			
			writer.write("End");
			writer.newLine();
			
			writer.flush();
		} catch (IOException e) {
			logger.error("发送集群视图数据失败，错误信息：{}", e.getMessage());
		}
	}

}
