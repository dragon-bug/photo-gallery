package net.threeple.pg.mon;

import java.io.File;

public class Bootstrap {

	public static void main(String[] args) {
		if(args.length < 2) {
			System.err.println("参数错误,正确的参数应该有两个,第一个参数为监控的端口,第二个参数为配置文件路径");
			System.exit(1);
		}
		
		if(!args[0].matches("\\d+")) {
			System.err.println("参数错误,第一个参数应为数字,代表一个未使用的端口");
			System.exit(1);
		}
		
		File dir = new File(args[1]);
		if(!dir.exists() || !dir.isDirectory()) {
			System.err.println("参数错误,参数指定的配置文件目录不存在");
			System.exit(1);
		}
		
		int port = Integer.parseInt(args[0]);
		String configPath = args[1];
		
		ClusterViewMonitor monitor = new ClusterViewMonitor(port);
		monitor.init(configPath);
		monitor.start();
	}

}
