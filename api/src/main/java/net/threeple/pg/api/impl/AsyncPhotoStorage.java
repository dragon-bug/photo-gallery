package net.threeple.pg.api.impl;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.threeple.pg.api.async.SimpleFuture;
import net.threeple.pg.api.model.IRequest;
import net.threeple.pg.api.model.FileRequest;
import net.threeple.pg.api.model.Response;
import net.threeple.pg.shared.constant.Operation;

public class AsyncPhotoStorage extends AbstractPhotoStorage {
	private final static int MAX_JOBS_QUANTITY = 1000;
	private final static int DEFAULT_BUSY_THRESHOLD = 100;
	private final static int STEP = 30;
	final Logger logger = LoggerFactory.getLogger(AsyncPhotoStorage.class);
	private final BlockingQueue<IRequest> queue;
	private Executor executor;
	private AtomicInteger busyThreshold;
	private AtomicInteger workerCount;
	private final ReentrantLock lock = new ReentrantLock();
	
	private AsyncPhotoStorage() {
		this.queue = new ArrayBlockingQueue<>(MAX_JOBS_QUANTITY);
		this.executor = Executors.newCachedThreadPool();
		this.busyThreshold = new AtomicInteger(DEFAULT_BUSY_THRESHOLD);
		this.workerCount = new AtomicInteger(0);
	}
	
	@Override
	public BlockingQueue<IRequest> getQueue() {
		return this.queue;
	}

	@Override
	public Future<Response> asyncDownload(String uri) {
		IRequest request = new FileRequest();
		request.setOperation(Operation.DOWNLOAD);
		request.setUri(uri);
		Future<Response> future = new SimpleFuture();
		request.setFuture(future);
		logger.info("完成组装文件{}的下载请求，准备放入工作队列", uri);
		try {
			lock.lock();
			this.queue.offer(request, 1, TimeUnit.SECONDS);
			ring();
			logger.info("成功将文件{}的下载请求放入工作队列，准备唤醒工人工作", uri);
		} catch (InterruptedException e) {
			future.cancel(true);
			logger.error("派遣下载文件{}的工作被中断, 中断信息: {}", uri, e.getMessage());
		} finally {
			lock.unlock();
		}
		
		return future;
	}

	@Override
	public Future<Response> asyncUpload(String uri, byte[] body) {
		IRequest request = new FileRequest();
		request.setOperation(Operation.UPLOAD);
		request.setUri(uri);
		request.setBody(body);
		Future<Response> future = new SimpleFuture();
		request.setFuture(future);
		logger.info("完成组装文件{}的上传请求，准备放入工作队列", uri);
		try {
			lock.lock();
			this.queue.offer(request, 1, TimeUnit.SECONDS);
			ring();
			logger.info("成功将文件{}的上传请求放入工作队列，准备唤醒工人工作", uri);
		} catch (InterruptedException e) {
			future.cancel(true);
			logger.error("派遣上传文件{}的工作被中断, 中断信息: {}", uri, e.getMessage());
		} finally {
			lock.unlock();
		}
		
		return future;
	}
	
	@Override
	public Future<Response> asyncDelete(String uri) {
		IRequest request = new FileRequest();
		request.setOperation(Operation.DELETE);
		request.setUri(uri);
		Future<Response> future = new SimpleFuture();
		request.setFuture(future);
		logger.info("完成组装文件{}的删除请求，准备放入工作队列", uri);
		try {
			lock.lock();
			this.queue.offer(request, 1, TimeUnit.SECONDS);
			ring();
			logger.info("成功将文件{}的删除请求放入工作队列，准备唤醒工人工作", uri);
		} catch (InterruptedException e) {
			future.cancel(true);
			logger.error("派遣删除文件{}的工作被中断, 中断信息: {}", uri, e.getMessage());
		} finally {
			lock.unlock();
		}
		return future;
	}

	private void ring() {
		if(this.workerCount.get() > 0) {
			if (this.queue.size() > this.busyThreshold.get()) {
				logger.info("工作太忙,添个工人帮忙...");
				this.busyThreshold.getAndAdd(STEP);
				createWorker();
			}
		} else { // 没有正在工作的工人
			logger.info("缺少工作的工人，请一个...");
			createWorker();
		}
	}
	
	private void createWorker() {
		this.executor.execute(new Porter(this));
		int count = this.workerCount.incrementAndGet();
		logger.info("当前有{}个工人正在工作", count);
	}
	
	public void free() {
		try {
			lock.lock();
			this.workerCount.decrementAndGet();
			if(this.busyThreshold.get() > DEFAULT_BUSY_THRESHOLD) {
				this.busyThreshold.set(DEFAULT_BUSY_THRESHOLD);
			}
			logger.info("工作都完成了，恢复工作现场");
		} finally {
			lock.unlock();
		}
	}
	
}
