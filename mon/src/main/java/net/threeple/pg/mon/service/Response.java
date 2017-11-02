package net.threeple.pg.mon.service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Response {
	final Logger logger = LoggerFactory.getLogger(Response.class);
	private Socket socket;
	private Request request;
	private BufferedWriter out;
	
	public Socket getSocket() {
		return socket;
	}
	public void setSocket(Socket socket) {
		this.socket = socket;
		try {
			this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		} catch (IOException e) {
			logger.error("无法与{}建立写通道", socket.getInetAddress());
		}
	}
	public Request getRequest() {
		return request;
	}
	public void setRequest(Request request) {
		this.request = request;
	}
	public BufferedWriter getOut() {
		return this.out;
	}
}
