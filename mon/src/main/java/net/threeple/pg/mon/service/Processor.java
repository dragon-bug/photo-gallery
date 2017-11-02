package net.threeple.pg.mon.service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.threeple.pg.mon.data.RepositoryFactory;
import net.threeple.pg.mon.data.StorageNodeRepository;
import net.threeple.pg.mon.exception.CloudNotParseRequestException;
import net.threeple.pg.mon.model.PlacementGroup;
import net.threeple.pg.mon.model.StorageNode;

public class Processor implements Runnable {
	final Logger logger = LoggerFactory.getLogger(Processor.class);
	private final Request request;
	private StorageNodeRepository snRepository = RepositoryFactory.getRepository(StorageNodeRepository.class);
	
	public Processor(Request _request) {
		this.request = _request;
	}

	@Override
	public void run() {
		try {
			this.request.parse();
			Response response = this.request.getResponse();
			BufferedWriter writer = response.getOut();
			try {
				List<String> requires = this.request.getRequires();
				for(String require : requires) {
					if("AllStorageNode".equals(require)) {
						writer.write("Response:AllStorageNode");
						writer.newLine();
						
						List<StorageNode> sns = snRepository.getAllStorageNode();
						for(StorageNode sn : sns) {
							writer.write(sn.toString());
							writer.newLine();
						}
						
						writer.write("End");
						writer.newLine();
					} else if("AllPlacementGroup".equals(require)) {
						writer.write("Response:AllPlacementGroup");
						writer.newLine();
						
						List<StorageNode> sns = snRepository.getAllStorageNode();
						for(StorageNode sn : sns) {
							for(PlacementGroup pg : sn.getRoomers()) {
								writer.write(pg.toString());
								writer.newLine();
							}
						}
						
						writer.write("End");
						writer.newLine();
					}
				}
				writer.flush();
			} catch (IOException e) {
				logger.error("");
			}
		} catch(CloudNotParseRequestException e) {
			logger.error(e.getMessage());
		}
	}
}
