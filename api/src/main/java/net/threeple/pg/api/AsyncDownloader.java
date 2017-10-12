package net.threeple.pg.api;

import java.util.concurrent.Future;

public interface AsyncDownloader {
	public Future<byte[]> asyncDownload(String uri);
}
