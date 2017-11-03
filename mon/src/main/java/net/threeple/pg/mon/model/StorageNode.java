package net.threeple.pg.mon.model;

import java.util.List;

public class StorageNode {
	private int id;
	private String address;
	private List<PlacementGroup> roomers;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public List<PlacementGroup> getRoomers() {
		return roomers;
	}
	public void setRoomers(List<PlacementGroup> roomers) {
		this.roomers = roomers;
	}
	@Override
	public String toString() {
		return this.id + "=" + this.address;
	}
	
}
