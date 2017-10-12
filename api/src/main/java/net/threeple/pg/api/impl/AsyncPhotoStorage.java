package net.threeple.pg.api.impl;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.threeple.pg.api.request.Request;
import net.threeple.pg.api.request.RequestFactory;
import net.threeple.pg.api.request.SimpleFuture;

public class AsyncPhotoStorage extends AbstractPhotoStorage {
	private final static int MAX_JOBS_QUANTITY = 1000;
	private final static int DEFAULT_BUSY_THRESHOLD = 100;
	private final static int STEP = 30;
	final Logger logger = LoggerFactory.getLogger(AsyncPhotoStorage.class);
	private final BlockingQueue<Request> queue;
	private Executor executor;
	private AtomicInteger busyThreshold;
	private final ReentrantLock lock = new ReentrantLock();
	private AtomicBoolean work = new AtomicBoolean(false);
	
	private AsyncPhotoStorage() {
		this.queue = new ArrayBlockingQueue<>(MAX_JOBS_QUANTITY);
		this.executor = Executors.newCachedThreadPool();
		this.busyThreshold = new AtomicInteger(DEFAULT_BUSY_THRESHOLD);
	}
	
	@Override
	public BlockingQueue<Request> getQueue() {
		return this.queue;
	}

	@Override
	public Future<byte[]> asyncDownload(String uri) {
		Request request = RequestFactory.Download.create();
		request.setUri(uri);
		Future<byte[]> response = new SimpleFuture<>();
		request.setFuture(response);
		
		try {
			this.queue.offer(request, 1, TimeUnit.SECONDS);
			getUp();
		} catch (InterruptedException e) {
			logger.error("派遣下载文件{}的工作被中断, 中断信息: {}", uri, e.getMessage());
		}
		
		return response;
	}

	@Override
	public Future<Integer> asyncUpload(String uri, byte[] body) {
		Request request = RequestFactory.Upload.create();
		request.setUri(uri);
		request.setBody(body);
		Future<Integer> response = new SimpleFuture<>();
		request.setFuture(response);
		
		try {
			this.queue.offer(request, 1, TimeUnit.SECONDS);
			getUp();
		} catch (InterruptedException e) {
			logger.error("派遣上传文件{}的工作被中断, 中断信息: {}", uri, e.getMessage());
		}
		
		return response;
	}
	
	private void getUp() {
		logger.debug("当前有{}个待处理工作", this.queue.size());
		if (this.work.get()) {
			if (this.queue.size() > this.busyThreshold.get()) {
				logger.info("工作太忙,添个人帮忙...");
				this.busyThreshold.getAndAdd(STEP);
				this.executor.execute(new PhotoPorter(this));
			}
		} else {
			this.executor.execute(new PhotoPorter(this));
			this.work.compareAndSet(false, true);
		}
	}
	
	public boolean isWork() {
		return this.work.get();
	}
	
	public void free() {
		try {
			lock.lock();
			this.busyThreshold.set(DEFAULT_BUSY_THRESHOLD);
			this.work.compareAndSet(true, false);
		} finally {
			lock.unlock();
		}
		
	}
	
}
