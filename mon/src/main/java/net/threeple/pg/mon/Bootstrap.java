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
		Option dataOpt = Option.builder("data").required().hasArg().argName("datapath").desc("The data dir path").build();
		Options options = new Options();
		options.addOption(nameOpt);
		options.addOption(portOpt);
		options.addOption(dataOpt);
		
		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine cmd = parser.parse(options, args);
			ClusterViewMonitor monitor = new ClusterViewMonitor(
					cmd.getOptionValue("name"),
					Integer.parseInt(cmd.getOptionValue("port")));
			monitor.init(cmd.getOptionValue("data"));
			monitor.start();
		} catch (ParseException e) {
			System.err.println(e.getMessage());
		}
	}

}
