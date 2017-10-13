package net.threeple.pg.mon;

import java.net.InetAddress;
import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PsdNode extends Observable implements Observer {
	final Logger logger = LoggerFactory.getLogger(PsdNode.class);
	private final int id;
	private final int port;
	private final InetAddress address;
	private PlacementGroup[] placementGroups;
	private Status status;
	
	public PsdNode(int _id, InetAddress _address, int _port) {
		this.id = _id;
		this.address = _address;
		this.port = _port;
		this.status = Status.OUT;
	}
	
	public int getId() {
		return id;
	}

	public int getPort() {
		return port;
	}

	public InetAddress getAddress() {
		return address;
	}

	public PlacementGroup[] getPlacementGroups() {
		return placementGroups;
	}
	
	public void setPlacementGroups(PlacementGroup[] placementGroups) {
		this.placementGroups = placementGroups;
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		
	}
	
	public boolean isUp() {
		return status.equals(Status.UP);
	}
	
	public boolean isIn() {
		return status.equals(Status.IN);
	}
	
	public boolean isOut() {
		return status.equals(Status.OUT);
	}
	
	private enum Status {
		UP, IN, OUT;
	}
}
