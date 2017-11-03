package net.threeple.pg.mon.model;

public class PlacementGroup {
	private int id;
	private StorageNode house;
	
	public PlacementGroup(int _id, StorageNode _house) {
		this.id = _id;
		this.house = _house;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public StorageNode getHouse() {
		return house;
	}
	public void setHouse(StorageNode house) {
		this.house = house;
	}
	
	@Override
	public String toString() {
		return id + ">" + house.getId();
	}
	
	
	
}
