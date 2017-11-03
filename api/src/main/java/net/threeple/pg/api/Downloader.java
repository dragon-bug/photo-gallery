package net.threeple.pg.api;

import net.threeple.pg.api.model.Response;

public interface Downloader {
	public Response download(String uri);
}
