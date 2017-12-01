package net.threeple.pg.adm;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import net.threeple.pg.adm.cmd.DisplayClusterView;


public class PhotoGalleryController {

	public static void main(String[] args) throws Exception {
		Option getOpt = Option.builder("get").hasArg().argName("get").desc("Display one or many resources").build();
		Options options = new Options();
		options.addOption(getOpt);
		
		CommandLineParser parser = new DefaultParser();
		
		CommandLine cmd = parser.parse(options, args);
		
		if(cmd.hasOption("get")) {
			String resource = cmd.getOptionValue("get");
			if("cv".equals(resource)) {
				DisplayClusterView dcv = new DisplayClusterView();
				dcv.display();
			}
		}
	}
}
