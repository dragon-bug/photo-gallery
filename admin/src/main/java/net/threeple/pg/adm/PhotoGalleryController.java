package net.threeple.pg.adm;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.threeple.pg.adm.cmd.DisplayClusterView;
import net.threeple.pg.adm.cmd.MonitorInitialtor;


public class PhotoGalleryController {
	static final Logger logger = LoggerFactory.getLogger(PhotoGalleryController.class);
	
	public static void main(String[] args) throws Exception {
		Option getOpt = Option.builder("get").hasArg().argName("get").desc("Display one or many resources").build();
		Option fileOpt = Option.builder("f").hasArg().argName("filename").desc("Filename to file to use to do something").build();
		Option initOpt = Option.builder("init").hasArg().argName("initiate").desc("Initiate some data or components on the cluster").build();
		Options options = new Options();
		options.addOption(getOpt);
		options.addOption(fileOpt);
		options.addOption(initOpt);
		
		CommandLineParser parser = new DefaultParser();
		
		CommandLine cmd = parser.parse(options, args);
		
		if(cmd.hasOption("get")) {
			String resource = cmd.getOptionValue("get");
			if("cv".equals(resource)) {
				DisplayClusterView dcv = new DisplayClusterView();
				dcv.display();
			}
		}
		
		if(cmd.hasOption("init")) {
			String item = cmd.getOptionValue("init");
			if("mon".equals(item)) {
				if(!cmd.hasOption("f")) {
					logger.error(" The arguments is absent: -f filename");
					return;
				}
				String filename = cmd.getOptionValue("f");
				MonitorInitialtor mi = new MonitorInitialtor();
				mi.init(filename);
			}
			
		}
	}
}
