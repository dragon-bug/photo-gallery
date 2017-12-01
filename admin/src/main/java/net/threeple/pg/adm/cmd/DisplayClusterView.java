package net.threeple.pg.adm.cmd;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.threeple.pg.shared.config.ApplicationConfig;
import net.threeple.pg.shared.util.CustomInetAddressParser;

public class DisplayClusterView {
	final Logger logger = LoggerFactory.getLogger(DisplayClusterView.class);
	
	public void display() throws Exception{
		Socket socket = new Socket();
		String ma = ApplicationConfig.getMonitorAddresses();
		int ind = ma.indexOf(',');
		if(ind > 0) {
			ma = ma.substring(0, ind);
		}
		logger.info("获得监视器地址：{}", ma);
		InetSocketAddress address = CustomInetAddressParser.parse(ma);
		socket.connect(address, 5 * 1000);
		
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

		writer.write("Require:ClusterView");
		writer.newLine();
		
		writer.write("End");
		writer.newLine();
		
		writer.flush();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		String line = null;
		logger.info("正在从监视器获得集群视图...");
		while((line = reader.readLine()) != null) {
			if("End".equals(line)) {
				break;
			}
			logger.info(line);
		}
		logger.info("完成");
		socket.close();
	}
	
}
