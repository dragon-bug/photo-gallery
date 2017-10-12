package net.threeple.pg.api.impl;

import static net.threeple.pg.shared.constant.MagicNumber.UPLOAD;
import static net.threeple.pg.shared.constant.MagicNumber.DOWNLOAD;
import static net.threeple.pg.shared.constant.MagicNumber.DONE;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.threeple.pg.api.request.Request;
import net.threeple.pg.api.request.SimpleFuture;
import net.threeple.pg.shared.message.AbstractUriMessageHandler;
import net.threeple.pg.shared.util.PlacementCalculator;

public class Porter implements Runnable {
	final Logger logger = LoggerFactory.getLogger(Porter.class);
	private final static int POLL_TIMEOUT = 100;
	private BlockingQueue<Request> queue;
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
		Request request = null;
		PhotoUploadHandler uploadHandler = null;
		PhotoDownloadHandler downloadHandler = null;
		Synchronizer synchronizer = Synchronizer.getInstance();

		long start = System.currentTimeMillis();
		int quntity = 0;
		
		boolean download = true;
		try {
			logger.debug("搬运#{}工开始工作...", this.id);
			while (this.photoStorage.isWork() && 
					(request = this.queue.poll(POLL_TIMEOUT, TimeUnit.MILLISECONDS)) != null) {
				Socket socket = new Socket();
				String uri = request.getUri();
				try {

					int placement = PlacementCalculator.calculate(uri);
					InetSocketAddress address = synchronizer.getAddress(placement);
					socket.connect(address, 1000 * 5);

					if (request.getBody() == null) {
						downloadHandler = new PhotoDownloadHandler(socket, uri);
						downloadHandler.download();
						byte[] body = downloadHandler.getBody();
						@SuppressWarnings("unchecked")
						SimpleFuture<byte[]> future = (SimpleFuture<byte[]>) request.getFuture();
						future.complete(body);
					} else { // 上传
						download = false;
						uploadHandler = new PhotoUploadHandler(socket, uri, request.getBody());
						uploadHandler.upload();
						@SuppressWarnings("unchecked")
						SimpleFuture<Integer> future = (SimpleFuture<Integer>) request.getFuture();
						future.complete(0);
						
					}
				} catch (Exception e) {
					logger.error("未完成{}的传输工作, 错误信息:{}", uri, e.getMessage());
					if(!download) {
						request.setBody(null);
					}
					this.queue.offer(request, 1, TimeUnit.SECONDS);
					logger.info("已重新安排{}的传输工作", uri);
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
	
	private class PhotoUploadHandler extends AbstractUriMessageHandler {
		final Logger logger = LoggerFactory.getLogger(PhotoUploadHandler.class);
		private byte[] body;
		
		public PhotoUploadHandler(Socket _socket, String _uri, byte[] _body) throws IOException {
			InputStream _in = _socket.getInputStream();
			this.pbin = new PushbackInputStream(new BufferedInputStream(_in));
			this.in = new DataInputStream(this.pbin);
			OutputStream _out = _socket.getOutputStream();
			this.out = new DataOutputStream(new BufferedOutputStream(_out));
			this.uri = _uri;
			this.body = _body;
		}
		
		public void upload() throws IOException {
			this.out.write(UPLOAD);
			super.send();
			int length = this.body.length;
			this.out.writeInt(length);
			this.out.write(this.body);
			this.out.flush();
			
			int result = this.in.read();
			if(result != DONE) {
				throw new IOException("上传文件" + this.uri + "失败, 失败原因: 没有收到正确的响应");
			}
			logger.debug("搬运工#{}上传文件{}成功, 共上传{}字节", id, this.uri, this.body.length);
			this.out.close();
			this.in.close();
		}
		
	}
	
	private class PhotoDownloadHandler extends AbstractUriMessageHandler {
		private byte[] body;
		
		public PhotoDownloadHandler(Socket _socket, String _uri) throws IOException {
			InputStream _in = _socket.getInputStream();
			this.pbin = new PushbackInputStream(new BufferedInputStream(_in));
			this.in = new DataInputStream(this.pbin);
			OutputStream _out = _socket.getOutputStream();
			this.out = new DataOutputStream(new BufferedOutputStream(_out));
			this.uri = _uri;
		}
		
		private byte[] getBody() {
			return this.body;
		}
		
		public void download() throws IOException {
			this.out.write(DOWNLOAD);
			super.send();
			
			int remain = this.in.readInt();
			byte[] _body = new byte[0];
			byte[] buffer = new byte[1024 * 10];
			int total = 0;
			while(remain > 0) {
				total = this.in.read(buffer);
				int ol = _body.length;
				_body = Arrays.copyOf(_body, ol + total);
				System.arraycopy(buffer, 0, _body, ol, total);
				remain -= total;
			}
			this.body = _body;
			logger.debug("搬运工#{}下载文件{}成功, 共下载{}字节", id, this.uri, this.body.length);
		}
	}

}
