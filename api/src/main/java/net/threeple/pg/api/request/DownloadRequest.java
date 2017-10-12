package net.threeple.pg.api.request;

import java.util.concurrent.Future;

public class DownloadRequest implements Request {
	private String uri;
	private SimpleFuture<byte[]> future;
	
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
		return null;
	}

	@Override
	public void setBody(byte[] body) {
		
	}

	@Override
	public Future<?> getFuture() {
		return this.future;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setFuture(Future<?> response) {
		this.future = (SimpleFuture<byte[]>) response;
	}

}
