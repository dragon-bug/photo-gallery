package net.threeple.pg.monitor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MonitoringServer {
	private int port;
	
	public MonitoringServer(int _port) {
		this.port = _port;
	}
	
	public void startup() {
		ServerSocket server = null;
		NodeLoader loader = new NodeLoader();
		try {
			server = new ServerSocket(this.port);
			while(true) {
				Socket socket = server.accept();
				loader.load(socket);
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// TODO verify args
		int port = Integer.parseInt(args[0]);
		MonitoringServer server = new MonitoringServer(port);
		server.startup();
	}

}
