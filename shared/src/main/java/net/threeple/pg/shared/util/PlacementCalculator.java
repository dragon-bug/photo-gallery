package net.threeple.pg.shared.util;

public class PlacementCalculator {
	private static int pgQuantity;
	
	static {
		pgQuantity = 8;
	}
	
	public static int calculate(String uri) {
		return Math.abs(uri.hashCode() % pgQuantity);
	}
	
	public static int getPgQuantity() {
		return pgQuantity;
	}
}
