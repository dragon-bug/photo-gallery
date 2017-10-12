package net.threeple.pg.api;

import java.util.concurrent.BlockingQueue;

import net.threeple.pg.api.request.Request;

public interface PhotoStorage extends Downloader, Uploader, AsyncDownloader, AsyncUploader {
	public BlockingQueue<Request> getQueue();
}
