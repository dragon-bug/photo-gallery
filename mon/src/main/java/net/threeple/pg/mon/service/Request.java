package net.threeple.pg.mon.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.threeple.pg.mon.exception.CloudNotParseRequestException;

public class Request {
	final Logger logger = LoggerFactory.getLogger(Request.class);
	private final Socket socket;
	private Response response;
	private List<String> requires = new ArrayList<>();
	
	public Request(Socket _socket) {
		this.socket = _socket;
	}
	
	public void parse() throws CloudNotParseRequestException{
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			String t = reader.readLine();
			if(t.startsWith("Response")) {
				Response _response = new Response();
				this.response = _response;
				_response.setRequest(this);
				
				int c = t.indexOf(':');
				if(c > 0) { // 通过下面解析的地址返回响应
					t = t.substring(c + 1);
					c = t.indexOf('=');
					if("port".equals(t.substring(0, c))) {
						int port = Integer.parseInt(t.substring(c + 1));
						InetSocketAddress address = new InetSocketAddress(socket.getInetAddress(), port);
						Socket _socket = new Socket();
						_socket.connect(address, 5 * 1000);
						_response.setSocket(_socket);
						logger.info("来自{}的请求要求通过{}端口返回响应", socket.getInetAddress(), port);
					}
				} else {
					_response.setSocket(this.socket);
				}
				
				while(!"End".equals((t = reader.readLine()))) {
					if(t.startsWith("Require")) {
						c = t.indexOf(':');
						String require = t.substring(c + 1);
						requires.add(require);
						logger.info("接收到来自{}的请求：{}", socket.getInetAddress(), require);
					}
				}
			} else {
				String msg = String.format("无法解析来自%s的请求", this.socket.getInetAddress());
				throw new CloudNotParseRequestException(msg);
			}
		} catch(IOException e) {
			String msg = String.format("无法解析来自%s的请求", this.socket.getInetAddress());
			throw new CloudNotParseRequestException(msg);
		}
		
	}

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}

	public List<String> getRequires() {
		return requires;
	}

	public void setRequires(List<String> requires) {
		this.requires = requires;
	}
	
	public static void main(String[] args) {
	}
	
}
