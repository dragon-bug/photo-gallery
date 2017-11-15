package net.threeple.pg.psd;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import net.threeple.pg.psd.persistent.Storage;

public class Bootstrap {

	public static void main(String[] args) {
		Option idOpt = Option.builder("id").required().hasArg().argName("id").desc("Psd ID").build();
		Option portOpt = Option.builder("port").required().hasArg().argName("port").desc("To listen this port").build();
		Option rootOpt = Option.builder("root").required().hasArg().argName("storage").desc("The root path of storage").build();
		Option addrOpt = Option.builder("addr").required().hasArg().argName("addr").desc("The IP address the API Server will advertise it's listening on. 0.0.0.0 means the default network interface's address.").build();
		
		Options options = new Options();
		options.addOption(idOpt);
		options.addOption(portOpt);
		options.addOption(rootOpt);
		options.addOption(addrOpt);
		
		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine cmd = parser.parse(options, args);
			Storage storage = new Storage(
					Integer.parseInt(cmd.getOptionValue("id")), 
					cmd.getOptionValue("root"));
			PsdServer server = new PsdServer(storage);
			String address = cmd.getOptionValue("addr");
			int port = Integer.parseInt(cmd.getOptionValue("port"));
			server.startup(address, port);
		} catch (ParseException e) {
			System.err.println(e.getMessage());
		}

	}

}
