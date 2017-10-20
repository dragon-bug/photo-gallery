package net.threeple.pg.psd;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Bootstrap {

	public static void main(String[] args) {
		Option idOpt = Option.builder("id").required().hasArg().argName("id").desc("Psd ID").build();
		Option portOpt = Option.builder("port").required().hasArg().argName("port").desc("To listen this port").build();
		Option rootOpt = Option.builder("root").required().hasArg().argName("storage").desc("The root path of storage").build();
		
		Options options = new Options();
		options.addOption(idOpt);
		options.addOption(portOpt);
		options.addOption(rootOpt);
		
		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine cmd = parser.parse(options, args);
			Storage storage = new Storage(
					Integer.parseInt(cmd.getOptionValue("id")), 
					cmd.getOptionValue("root"));
			PsdServer server = new PsdServer(storage);
			server.startup(Integer.parseInt(cmd.getOptionValue("port")));
		} catch (ParseException e) {
			System.err.println(e.getMessage());
		}

	}

}
