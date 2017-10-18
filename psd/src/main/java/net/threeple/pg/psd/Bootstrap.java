package net.threeple.pg.psd;

import java.io.File;

public class Bootstrap {

	public static void main(String[] args) {
		if(args.length < 3) {
			System.err.println("参数错误,应用3个参数,分别为:ID, 监听的端口,以及存储路径");
			System.exit(1);
		}
		
		if(!args[0].matches("\\d+")) {
			System.err.println("参数错误,第一个参数应该是存储节点的ID");
			System.exit(1);
		}
		
		if(!args[1].matches("\\d+")) {
			System.err.println("参数错误,第二个参数应该是监听的端口");
			System.exit(1);
		}
		
		File dir = new File(args[2]);
		if(!dir.exists() || !dir.isDirectory()) {
			System.err.println("参数错误,第三个参数应该是存储路径");
			System.exit(1);
		}
		
		int id = Integer.parseInt(args[0]);
		int port = Integer.parseInt(args[1]);
		String root = args[2];
		
		Storage storage = new Storage(id, root);
		
		PsdServer server = new PsdServer(storage);
		server.startup(port);

	}

}
