package net.threeple.pg.mon.monilet.impl;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.threeple.pg.mon.monilet.IMoniletRequest;
import net.threeple.pg.mon.monilet.IMoniletResponse;
import net.threeple.pg.mon.monilet.Monilet;
import net.threeple.pg.mon.repository.ClusterViewRepository;
import net.threeple.pg.mon.repository.ClusterViewRepositoryFactory;

public class ClusterViewMonilet implements Monilet {
	final Logger logger = LoggerFactory.getLogger(ClusterViewMonilet.class);
	private ClusterViewRepository cvr = ClusterViewRepositoryFactory.getClusterViewRepository();
	
	@Override
	public void service(IMoniletRequest request, IMoniletResponse response) {
		BufferedWriter writer = response.getWriter();
		try {
			writer.write("Response:ClusterView");
			writer.newLine();
			
			List<String> list = cvr.getClusterView();
			if(list != null) {
				writer.write("Status:200");
				writer.newLine();
				
				for(String item : cvr.getClusterView()) {
					writer.write(item);
					writer.newLine();
				}
			} else {
				writer.write("Status: 404");
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
