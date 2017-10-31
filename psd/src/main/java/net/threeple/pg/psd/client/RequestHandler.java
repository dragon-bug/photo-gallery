package net.threeple.pg.psd.client;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.threeple.pg.psd.io.FileChecker;
import net.threeple.pg.psd.io.FileReader;
import net.threeple.pg.psd.io.FileRemover;
import net.threeple.pg.psd.io.FileWriter;
import net.threeple.pg.psd.persistent.Storage;

public class RequestHandler implements Runnable {
	final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
	private Request request;
	private FileWriter writer;
	private FileReader reader;
	private FileChecker checker;
	private FileRemover remover;
	
	public RequestHandler(Request _request, Storage _storage) {
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
			logger.error("未能正确处理请求, 错误信息: {}", e.getMessage());
		}
	}
	
	public void read() {
		Response response = this.request.getResponse();
		String uri = this.request.getUri();
		try {
			if(checker.exists(uri)) {
				response.setBody(this.reader.read(uri));
			} else {
				response.sendError(Response.SC_NOT_FOUND);
			}
		} catch(IOException e) {
			response.sendError(Response.SC_INTERNAL_SERVER_ERROR);
		}
		response.setStatus(Response.SC_OK);
	}
	
	public void write() {
		Response response = this.request.getResponse();
		String uri = this.request.getUri();
		byte[] body = this.request.getBody();
		try {
			this.writer.write(uri, body);
		} catch(IOException e) {
			response.sendError(Response.SC_INTERNAL_SERVER_ERROR);
		}
		response.setStatus(Response.SC_CREATED);
	}
	
	public void delete() {
		Response response = this.request.getResponse();
		String uri = this.request.getUri();
		if(this.checker.exists(uri)) {
			boolean r = this.remover.delete(uri);
			if(r) {
				response.setStatus(Response.SC_OK);
			} else {
				response.setStatus(Response.SC_INTERNAL_SERVER_ERROR);
			}
		} else {
			response.sendError(Response.SC_NOT_FOUND);
		}
	}

}
