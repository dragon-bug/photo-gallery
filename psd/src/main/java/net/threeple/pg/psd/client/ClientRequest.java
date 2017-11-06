package net.threeple.pg.psd.client;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.threeple.pg.shared.constant.MagicNumber;
import net.threeple.pg.shared.constant.Operation;
import net.threeple.pg.shared.exception.UnknownOperationException;

public class ClientRequest {
	final Logger logger = LoggerFactory.getLogger(ClientRequest.class);
	private Socket socket;
	private Operation operation;
	private String uri;
	private byte[] body;
	private ClientResponse response;
	private Charset utf8 = StandardCharsets.UTF_8;
	
	public ClientRequest(Socket _socket) {
		this.socket = _socket;
	}
	
	public void parse() throws IOException {
		DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
		
		this.response = new ClientResponse();
		this.response.setSocket(socket);
		this.response.setRequest(this);
		
		int magicNum = in.read();
		switch(magicNum) {
		case MagicNumber.DOWNLOAD:
			this.operation = Operation.DOWNLOAD;
			break;
		case MagicNumber.UPLOAD:
			this.operation = Operation.UPLOAD;
			break;
		case MagicNumber.DELETE:
			this.operation = Operation.DELETE;
			break;
			default:
				throw new UnknownOperationException("未知的操作数" + magicNum);
		}
		
		int length = in.readInt();
		byte[] buf = new byte[length];
		in.read(buf);
		this.uri = new String(buf, utf8);
		logger.debug("成功从客户端接收到文件URI: {}", uri);
		
		if(Operation.UPLOAD.equals(this.operation)) {
			int remain = in.readInt();
			byte[] _body = new byte[0];
			byte[] buffer = new byte[1024 * 10];
			int total = 0;
			while(remain > 0) {
				total = in.read(buffer);
				int ol = _body.length;
				_body = Arrays.copyOf(_body, ol + total);
				System.arraycopy(buffer, 0, _body, ol, total);
				remain -= total;
			}
			this.body = _body;
		}
		
	}

	public String getUri() {
		return uri;
	}

	public byte[] getBody() {
		return body;
	}

	public Operation getOperation() {
		return operation;
	}

	public ClientResponse getResponse() {
		return response;
	}
	
}
