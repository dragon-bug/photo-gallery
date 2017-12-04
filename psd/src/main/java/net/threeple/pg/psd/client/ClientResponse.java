package net.threeple.pg.psd.client;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.threeple.pg.shared.constant.Operation;

public class ClientResponse {
	public final static int SC_NOT_FOUND = 404;
	public final static int SC_INTERNAL_SERVER_ERROR  = 500;
	public final static int SC_BAD_REQUEST = 400;
	public final static int SC_OK = 200;
	public final static int SC_CREATED  = 201;
	final Logger logger = LoggerFactory.getLogger(ClientResponse.class);
	private byte[] body;
	private Socket socket;
	private ClientRequest request;
	
	public ClientResponse() {
	}
	
	public void setRequest(ClientRequest request) {
		this.request = request;
	}
	
	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	
	public void sendError(int statusCode) {
		setStatus(statusCode);
	}
	
	public void setBody(byte[] body) {
		this.body = body;
	}
	
	public void setStatus(int statusCode) {
		DataOutputStream out = null;
		try {
			out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
			out.writeInt(statusCode);
			if(this.body != null 
					&& Operation.DOWNLOAD.equals(this.request.getOperation())) {
				out.writeInt(this.body.length);
				out.write(this.body);
			}
			out.flush();
		} catch (IOException e) {
			logger.error("响应客户{}请求失败, 错误信息: {}", this.request.getOperation(), e.getMessage());
		} finally {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			if(this.socket != null) {
				if(!this.socket.isClosed()) {
					try {
						this.socket.close();
					} catch (IOException e) {
						logger.error("套接字未正常关闭");
					}
					this.socket = null;
					logger.info("套接字已经正常关闭");
				}
			}
		}
	}
}
