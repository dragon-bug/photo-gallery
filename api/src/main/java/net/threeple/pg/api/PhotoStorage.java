package net.threeple.pg.api;

import java.util.concurrent.BlockingQueue;

import net.threeple.pg.api.model.IRequest;

public interface PhotoStorage extends Downloader, Uploader, AsyncDownloader, AsyncUploader, AsyncRemover {
	public BlockingQueue<IRequest> getQueue();
}
