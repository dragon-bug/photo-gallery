package net.threeple.pg.shared.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlacementCalculator {
	final static Logger logger = LoggerFactory.getLogger(PlacementCalculator.class);
	
	public static int calculate(String uri, int pgQuantity) {
		return Math.abs(uri.hashCode() % pgQuantity);
	}
}
