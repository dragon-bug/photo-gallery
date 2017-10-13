package net.threeple.pg.mon;

public class PlacementGroup {
	private final int id;
	private PsdNode psd;
	
	public PlacementGroup(int _id) {
		this.id = _id;
	}

	public int getId() {
		return id;
	}

	public PsdNode getPsd() {
		return psd;
	}

	public void setPsd(PsdNode psd) {
		this.psd = psd;
	}
	
}
