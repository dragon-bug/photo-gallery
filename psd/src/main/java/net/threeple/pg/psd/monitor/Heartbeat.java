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
	
	@Override
	public void run() {
		Socket socket = null;
		try {
			socket = ClusterMoniterFactory.getFirstUseableMonitor();
		} catch (IOException e) {
			logger.error("无法获得监视器连接，错误信息：{}", e.getMessage());
		}
		
		try {
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
			
		} catch (IOException e) {
			logger.error("无法向监视器发送数据，错误信息：{}", e.getMessage());
		}
		
		if(socket != null && socket.isConnected()) {
			try {
				socket.close();
			} catch (IOException e) {
				logger.error("无法正常关闭与监视器的连接");
			}
		}
	}

}
