package net.threeple.pg.psd;

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
import java.net.Socket;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.threeple.pg.shared.message.AbstractUriMessageHandler;


public class Client extends AbstractUriMessageHandler implements Runnable {
	final Logger logger = LoggerFactory.getLogger(Client.class);
	private byte[] body;
	private Socket socket;
	private FileWriter writer;
	private FileReader reader;
	private boolean downloadRequest = true;
	
	public Client(Socket _socket, Storage _storage) {
		this.socket = _socket;
		this.writer = _storage;
		this.reader = _storage;
	}
	
	private void prepare() throws IOException {
		InputStream _in = this.socket.getInputStream();
		this.in = new DataInputStream(new BufferedInputStream(_in));
		this.pbin = new PushbackInputStream(this.in);
		
		OutputStream _out = this.socket.getOutputStream();
		this.out = new DataOutputStream(new BufferedOutputStream(_out));
	}
	
	private boolean isDownloadRequest() throws IOException {
		int magicNum = this.in.read();
		return magicNum == DOWNLOAD;
	}
	
	

	@Override
	public void receive() throws IOException {
		super.receive();
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
		
		this.writer.write(this.uri, _body);
		
		this.out.write(DONE);
		this.out.flush();
		
		this.out.close();
		this.in.close();
	}
	
	@Override
	public void send() throws IOException {
		int length = this.body.length;
		this.out.writeInt(length);
		this.out.write(this.body);
		this.out.flush();
		
		logger.debug("发送文件{},共发送{}字节", this.uri, this.body.length);
		this.out.close();
		this.in.close();
	}
	
	@Override
	public void run() {
		try {
			this.prepare();
			
			if(isDownloadRequest()) { // 处理下载请求
				super.receive();
				this.body = this.reader.read(this.uri);
				this.send();
			} else { // 处理上传请求
				this.downloadRequest = false;
				this.receive();
			}
		} catch (IOException e) {
			logger.warn("文件{}的{}处理失败, 失败信息: {}", this.uri, downloadRequest ? "下载请求":"上传请求", e.getMessage());
		}
	}

}
