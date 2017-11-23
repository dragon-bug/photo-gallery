package net.threeple.pg.api.impl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.threeple.pg.api.cluster.ClusterViewWatcher;
import net.threeple.pg.api.exception.ClusterUnhealthyException;
import net.threeple.pg.api.handler.DeleteHandler;
import net.threeple.pg.api.handler.DownloadHandler;
import net.threeple.pg.api.handler.Handler;
import net.threeple.pg.api.handler.UploadHandler;
import net.threeple.pg.api.model.IRequest;
import net.threeple.pg.shared.util.PlacementCalculator;

public class Porter implements Runnable {
	final Logger logger = LoggerFactory.getLogger(Porter.class);
	private final static int POLL_TIMEOUT = 100;
	private final BlockingQueue<IRequest> queue;
	private final AsyncPhotoStorage photoStorage;
	private final int id;
	private final ClusterViewWatcher watcher;
	private final int pgQuantity;
	private static AtomicInteger instanceCounter = new AtomicInteger();
	
	public Porter(AsyncPhotoStorage _photoStorage) {
		this.photoStorage = _photoStorage;
		this.queue = _photoStorage.getQueue();
		this.id = instanceCounter.getAndIncrement();
		this.watcher = _photoStorage.getWatcher();
		this.pgQuantity = watcher.getPlacementQuantity();
		logger.info("工人#{}被唤醒", this.id);
	}

	@Override
	public void run() {
		long start = System.currentTimeMillis();
		int quantity = 0;
		
		// 创建责任链
		Handler downloadHandler = new DownloadHandler();
		Handler uploadHandler = new UploadHandler();
		Handler deleteHandler = new DeleteHandler();
		downloadHandler.setNext(uploadHandler);
		uploadHandler.setNext(deleteHandler);
		
		IRequest request = null;
		try {
			logger.debug("工人#{}完成准备工作...", this.id);
			while ((request = this.queue.poll(POLL_TIMEOUT, TimeUnit.MILLISECONDS)) != null) {
				String uri = request.getUri();
				logger.info("工人#{}获得{}的{}工作,工作序号:{}", this.id, uri, request.getOperation(), quantity);
				Socket socket = new Socket();
				request.setSocket(socket);
				try {
					int placement = PlacementCalculator.calculate(uri, this.pgQuantity);
					logger.debug("获得文件{}的归置：{}", uri, placement);
					
					InetSocketAddress address = watcher.getPsdAddress(placement);
					socket.connect(address, 1000 * 5);
					logger.info("成功连接至存储节点：{}", address);
					
					downloadHandler.handle(request);
					logger.info("完成文件{}的{}工作", uri, request.getOperation());
				} catch (IOException e) {
					request.getFuture().cancel(true);
					logger.error("执行文件{}的传输工作时遇到错误，错误信息：{}", uri, e.getMessage());
				} catch (ClusterUnhealthyException e) {
					request.getFuture().cancel(true);
					logger.error("存储集群异常,暂停5秒等待集群恢复,异常信息:{}", e.getMessage());
					Thread.sleep(1000 * 5);
				} finally {
					if(request.getFuture().isCancelled()) {
						logger.error("未能完成文件{}的{}工作", uri, request.getOperation());
					}
					try {
						socket.close();
					} catch (IOException e) {
						logger.error("未能正确关闭套接字, 错误信息: {}", e.getMessage());
					}
				}
				quantity++;
				logger.debug("工人#{}完成第{}个任务", this.id, quantity);
			} // end while
		} catch (InterruptedException e) {
			if(request != null) {
				request.getFuture().cancel(true);
			}
			logger.error("工人#{}的工作被中断,错误代码:{}", this.id, e.getMessage());
		} finally {
			this.photoStorage.free();
			instanceCounter.set(0);
			logger.info("工人#{}结束此次工作, 完成任务{}个, 耗时{}毫秒", this.id, quantity, (System.currentTimeMillis() - start - POLL_TIMEOUT));
		}
	}
	
}
