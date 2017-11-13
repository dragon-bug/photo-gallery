package net.threeple.pg.psd.monitor;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.threeple.pg.shared.config.ClusterMoniterFactory;

public class Heartbeat implements Runnable {
	final Logger logger = LoggerFactory.getLogger(Heartbeat.class);
	private final int id;
	
	public Heartbeat(int _id) {
		this.id = _id;
	}
	
	private void connect() throws IOException {
		Socket socket = ClusterMoniterFactory.getFirstUseableMonitor();
		
		try {
			pitAPat(socket);
		} catch (IOException e) {
			logger.error("存储节点#{}向监视器发送心跳包失败，错误信息：{}", this.id, e.getMessage());
		} finally {
			if(socket != null && socket.isConnected()) {
				try {
					socket.close();
				} catch (IOException e) {
					logger.error("无法正常关闭与监视器的连接");
				}
			}
		}
		
	}
	
	private void pitAPat(Socket socket) throws IOException {
		
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		writer.write("Require:Heartbeat");
		writer.newLine();
		
		writer.write("id=" + this.id);
		writer.newLine();
		
		writer.write("End");
		writer.newLine();
		
		writer.flush();
		
		while(true) {
			writer.write("pit-a-pat");
			writer.newLine();
			writer.flush();
			
			try {
				Thread.sleep(5 * 1000);
			} catch (InterruptedException e) {
			}
		}
		
		
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				connect();
			} catch (IOException e) {
				logger.error("存储节点#{}向监视器发送心跳包失败，错误信息：{}", this.id, e.getMessage());
			}
			try {
				Thread.sleep(30 * 1000);
			} catch (InterruptedException e) {
				
			}
		}
	}

}
