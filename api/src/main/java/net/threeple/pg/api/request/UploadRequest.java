package net.threeple.pg.api.request;

import java.util.concurrent.Future;

public class UploadRequest implements Request {
	private String uri;
	private byte[] body;
	private SimpleFuture<Integer> future;

	@Override
	public String getUri() {
		return this.uri;
	}

	@Override
	public void setUri(String uri) {
		this.uri = uri;

	}

	@Override
	public byte[] getBody() {
		return this.body;
	}

	@Override
	public void setBody(byte[] body) {
		this.body = body;

	}

	@Override
	public Future<Integer> getFuture() {
		return this.future;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setFuture(Future<?> response) {
		this.future = (SimpleFuture<Integer>) response;
	}

}
