package net.threeple.pg.api.model;

public class Response {
	private int statusCode;
	private byte[] body;
	
	public Response() {
		super();
	}
	
	public Response(int _statusCode) {
		this.statusCode = _statusCode;
	}
	
	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	public byte[] getBody() {
		return body;
	}
	public void setBody(byte[] body) {
		this.body = body;
	}
	
	
	
}
