package net.threeple.pg.api.handler;

import static net.threeple.pg.shared.constant.MagicNumber.DOWNLOAD;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.threeple.pg.api.async.SimpleFuture;
import net.threeple.pg.api.model.IRequest;
import net.threeple.pg.api.model.Response;
import net.threeple.pg.shared.constant.Operation;

public class DownloadHandler extends Handler {
	final Logger logger = LoggerFactory.getLogger(DownloadHandler.class);
	private Charset utf8 = StandardCharsets.UTF_8;

	public DownloadHandler() {
		super(Operation.DOWNLOAD);
	}

	@Override
	protected void response(IRequest request) throws IOException {
		Socket socket = null;
		DataInputStream in = null;
		DataOutputStream out = null;
		try {
			socket = request.getSocket();
			in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
			out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
			
			out.write(DOWNLOAD);
			// 发送文件URI到存储节点
			byte[] buf = request.getUri().getBytes(utf8);
			out.writeInt(buf.length);
			out.write(buf);
			out.flush();
			
			int statusCode = in.readInt();
			
			byte[] body = new byte[0];
			if(statusCode >= 200 && statusCode < 300) {
				// 接收文件
				int remain = in.readInt();
				byte[] buffer = new byte[1024 * 10];
				int total = 0;
				while(remain > 0) {
					total = in.read(buffer);
					int ol = body.length;
					body = Arrays.copyOf(body, ol + total);
					System.arraycopy(buffer, 0, body, ol, total);
					remain -= total;
				}
			} else {
				logger.warn("文件{}下载失败，错误码：{}", request.getUri(), statusCode);
			}
			
			SimpleFuture future = (SimpleFuture) request.getFuture();
			Response response = new Response(statusCode);
			response.setBody(body);
			future.complete(response);
		} finally {
			if(in != null) {
				in.close();
			}
			if(out != null) {
				out.close();
			}
			if(socket != null) {
				socket.close();
			}
		}
	}

}
