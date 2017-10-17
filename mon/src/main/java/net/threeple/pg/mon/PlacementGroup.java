package net.threeple.pg.mon;

public class PlacementGroup {
	private final int id;
	private int placement;
	
	public PlacementGroup(int _id, int _placement) {
		this.id = _id;
		this.placement = _placement;
	}

	public int getId() {
		return id;
	}

	public int getPlacement() {
		return placement;
	}

}
