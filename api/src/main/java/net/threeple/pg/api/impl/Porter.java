package net.threeple.pg.api.impl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	private BlockingQueue<IRequest> queue;
	private AsyncPhotoStorage photoStorage;
	private static AtomicInteger instanceCounter = new AtomicInteger();
	private int id;
	
	public Porter(AsyncPhotoStorage _photoStorage) {
		this.photoStorage = _photoStorage;
		this.queue = _photoStorage.getQueue();
		this.id = instanceCounter.getAndIncrement();
		logger.info("搬运工#{}被唤醒", this.id);
	}

	@Override
	public void run() {
		ClusterViewWatcher watcher = ClusterViewWatcher.getInstance();

		long start = System.currentTimeMillis();
		int quntity = 0;
		
		Handler downloadHandler = new DownloadHandler();
		Handler uploadHandler = new UploadHandler();
		Handler deleteHandler = new DeleteHandler();
		downloadHandler.setNext(uploadHandler);
		uploadHandler.setNext(deleteHandler);
		
		IRequest request = null;
		try {
			logger.debug("搬运#{}工开始工作...", this.id);
			while (this.photoStorage.isWork() && 
					(request = this.queue.poll(POLL_TIMEOUT, TimeUnit.MILLISECONDS)) != null) {

				Socket socket = new Socket();
				request.setSocket(socket);
				String uri = request.getUri();
				try {
					int placement = PlacementCalculator.calculate(uri);
					InetSocketAddress address = watcher.getPsdAddress(placement);
					socket.connect(address, 1000 * 5);
					downloadHandler.handle(request);
				} catch (IOException e) {
					logger.error("未完成{}的传输工作, 错误信息:{}", uri, e.getMessage());
				} catch (ClusterUnhealthyException e) {
					logger.error("存储集群异常,暂停5秒等待集群恢复,异常信息:{}", e.getMessage());
					Thread.sleep(1000 * 5);
				} finally {
					try {
						socket.close();
					} catch (IOException e) {
						logger.error("未能正确关闭套接字, 错误信息: {}", e.getMessage());
					}
				}
				quntity++;
				logger.debug("搬运工#{}完成第{}个任务", this.id, quntity);
			} // end while
		} catch (InterruptedException e) {
			logger.error("搬运工#{}的工作被中断,错误代码:{}", this.id, e.getMessage());
		} finally {
			logger.info("搬运工#{}结束此次工作, 完成任务{}个, 耗时{}毫秒", this.id, quntity, (System.currentTimeMillis() - start - POLL_TIMEOUT));
			this.photoStorage.free();
		}
	}
	
}
