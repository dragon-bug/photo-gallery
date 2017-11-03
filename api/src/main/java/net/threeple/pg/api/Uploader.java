package net.threeple.pg.api;

import net.threeple.pg.api.model.Response;

public interface Uploader {
	public Response upload(String uri,byte[] body);
}
