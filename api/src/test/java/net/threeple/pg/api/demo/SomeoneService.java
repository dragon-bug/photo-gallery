package net.threeple.pg.api.demo;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import net.threeple.pg.api.AsyncDownloader;
import net.threeple.pg.api.AsyncUploader;
import net.threeple.pg.api.factory.PhotoStorageFactory;
import net.threeple.pg.api.model.Response;
import net.threeple.pg.shared.util.FileUtils;

public class SomeoneService {
	private AsyncDownloader downloader = PhotoStorageFactory.getPhotoStorage(false);
	private AsyncUploader uploader = PhotoStorageFactory.getPhotoStorage(false);
	
	
	public void upload() throws IOException, InterruptedException, ExecutionException {
		String uri = "/path/to/someone.jpg";
		byte[] body = FileUtils.read(uri);
		Future<Response> future = uploader.asyncUpload(uri, body);
		Response r = future.get();
		if(200 >= r.getStatusCode() && 300 < r.getStatusCode()) {
			// 上传成功，接下来的业务逻辑
		} else {
			// 失败，异常处理逻辑
		}
	}
	
	public byte[] download() throws IOException, InterruptedException, ExecutionException {
		String uri = "/path/to/someone.png";
		Future<Response> future = downloader.asyncDownload(uri);
		Response r = future.get();
		if(200 >= r.getStatusCode() && 300 < r.getStatusCode()) {
			return r.getBody();
		} else {
			throw new IOException("下载失败");
		}
	}
}
