package net.threeple.pg.api.model;

import java.net.Socket;
import java.util.concurrent.Future;

import net.threeple.pg.shared.constant.Operation;

public class Request implements IRequest {
	private String uri;
	private byte[] body;
	private Operation operation;
	private Future<Response> future;
	private Socket socket;
	
	@Override
	public String getUri() {
		return uri;
	}
	@Override
	public void setUri(String uri) {
		this.uri = uri;
	}
	public byte[] getBody() {
		return body;
	}
	public void setBody(byte[] body) {
		this.body = body;
	}
	@Override
	public Operation getOperation() {
		return operation;
	}
	@Override
	public void setOperation(Operation operation) {
		this.operation = operation;
	}
	@Override
	public Socket getSocket() {
		return socket;
	}
	@Override
	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	@Override
	public Future<Response> getFuture() {
		return this.future;
	}
	@Override
	public void setFuture(Future<Response> future) {
		this.future = future;
	}
	
}
