package net.threeple.pg.api;

import java.util.concurrent.Future;

import net.threeple.pg.api.model.Response;

public interface AsyncDownloader {
	public Future<Response> asyncDownload(String uri);
}
