package net.threeple.pg.api.request;

import java.util.concurrent.Future;

public interface Request {
	public String getUri();
	public void setUri(String uri);
	public byte[] getBody();
	public void setBody(byte[] body);
	public Future<?> getFuture();
	public void setFuture(Future<?> response);
}
