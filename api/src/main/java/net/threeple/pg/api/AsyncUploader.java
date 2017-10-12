package net.threeple.pg.api;

import java.util.concurrent.Future;

public interface AsyncUploader {
	public Future<Integer> asyncUpload(String uri, byte[] body);
}
