package net.threeple.pg.api.handler;

import static net.threeple.pg.shared.constant.MagicNumber.UPLOAD;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import net.threeple.pg.api.async.SimpleFuture;
import net.threeple.pg.api.model.IRequest;
import net.threeple.pg.api.model.Response;
import net.threeple.pg.shared.constant.Operation;

public class UploadHandler extends Handler {
	private Charset utf8 = StandardCharsets.UTF_8;
	
	public UploadHandler() {
		super(Operation.UPLOAD);
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
			
			out.write(UPLOAD);
			// 发送文件URI到存储节点
			byte[] buf = request.getUri().getBytes(utf8);
			out.writeInt(buf.length);
			out.write(buf);
			out.flush();
			
			// 发送文件内容到存储节点
			byte[] body = request.getBody();
			int length = body.length;
			out.writeInt(length);
			out.write(body);
			out.flush();
			
			Integer statusCode = in.readInt();
			SimpleFuture future = (SimpleFuture) request.getFuture();
			Response response = new Response(statusCode);
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
