package net.threeple.pg.psd.client;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.threeple.pg.psd.io.FileChecker;
import net.threeple.pg.psd.io.FileReader;
import net.threeple.pg.psd.io.FileRemover;
import net.threeple.pg.psd.io.FileWriter;
import net.threeple.pg.psd.persistent.Storage;
import net.threeple.pg.shared.exception.UnknownOperationException;

public class ClientRequestHandler implements Runnable {
	final Logger logger = LoggerFactory.getLogger(ClientRequestHandler.class);
	private ClientRequest request;
	private FileWriter writer;
	private FileReader reader;
	private FileChecker checker;
	private FileRemover remover;
	
	public ClientRequestHandler(ClientRequest _request, Storage _storage) {
		this.writer = _storage;
		this.reader = _storage;
		this.request = _request;
		this.checker = _storage;
		this.remover = _storage;
	}
	
	@Override
	public void run() {
		try {
			this.request.parse();
			switch(request.getOperation()) {
			case DOWNLOAD:
				read();
				break;
			case UPLOAD:
				write();
				break;
			case DELETE:
				delete();
				break;
				default:
					logger.error("未被支持的操作{}", request.getOperation());
			}
		} catch(IOException e) {
			logger.error("未能正确解析请求, 错误信息: {}", e.getMessage());
		} catch(UnknownOperationException e) {
			logger.error(e.getMessage());
		}
	}
	
	public void read() {
		ClientResponse response = this.request.getResponse();
		String uri = this.request.getUri();
		try {
			if(checker.exists(uri)) {
				response.setBody(this.reader.read(uri));
				logger.info("完成文件{}的下载请求", uri);
				response.setStatus(ClientResponse.SC_OK);
			} else {
				logger.warn("无法完成下载请求,原因: 文件{}不存在", uri);
				response.sendError(ClientResponse.SC_NOT_FOUND);
			}
		} catch(IOException e) {
			response.sendError(ClientResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}
	
	public void write() {
		ClientResponse response = this.request.getResponse();
		String uri = this.request.getUri();
		byte[] body = this.request.getBody();
		try {
			this.writer.write(uri, body);
			response.setStatus(ClientResponse.SC_CREATED);
			logger.info("完成文件{}的上传工作", uri);
		} catch(IOException e) {
			logger.error("文件{}写入磁盘失败, 错误信息: {}", uri, e.getMessage());
			response.sendError(ClientResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}
	
	public void delete() {
		ClientResponse response = this.request.getResponse();
		String uri = this.request.getUri();
		if(this.checker.exists(uri)) {
			boolean r = this.remover.delete(uri);
			if(r) {
				response.setStatus(ClientResponse.SC_OK);
				logger.info("完成文件{}的删除工作", uri);
			} else {
				logger.error("删除文件{}失败", uri);
				response.setStatus(ClientResponse.SC_INTERNAL_SERVER_ERROR);
			}
		} else {
			logger.warn("欲删除的文件{}不存在", uri);
			response.sendError(ClientResponse.SC_NOT_FOUND);
		}
	}

}
