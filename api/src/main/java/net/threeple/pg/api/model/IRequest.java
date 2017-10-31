package net.threeple.pg.api.model;

import java.net.Socket;
import java.util.concurrent.Future;

import net.threeple.pg.shared.constant.Operation;

public interface IRequest {
	public String getUri();
	public void setUri(String uri);
	public byte[] getBody();
	public void setBody(byte[] body);
	public Future<Response> getFuture();
	public void setFuture(Future<Response> response);
	public Operation getOperation();
	public void setOperation(Operation operation);
	public Socket getSocket();
	public void setSocket(Socket socket);
}
