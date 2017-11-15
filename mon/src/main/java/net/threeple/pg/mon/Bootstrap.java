package net.threeple.pg.mon;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Bootstrap {
	
	public void start(String[] args) {
		
	}
	
	public static void main(String[] args) {
		
		Option nameOpt = Option.builder("name").required().hasArg().argName("name").desc("The monitor's name").build();
		Option portOpt = Option.builder("port").required().hasArg().argName("port").desc("To listen this port").build();
		Option addrOpt = Option.builder("addr").required().hasArg().argName("addr").desc("The IP address the API Server will advertise it's listening on. 0.0.0.0 means the default network interface's address.").build();
		Options options = new Options();
		options.addOption(nameOpt);
		options.addOption(portOpt);
		options.addOption(addrOpt);
		
		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine cmd = parser.parse(options, args);
			
			String name = cmd.getOptionValue("name");
			String address = cmd.getOptionValue("addr");
			int port = Integer.parseInt(cmd.getOptionValue("port"));
			
			MonitorServer server = new MonitorServer(name, address, port);
			server.start();
		} catch (ParseException e) {
			System.err.println(e.getMessage());
		}
	}

}
