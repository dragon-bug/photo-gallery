package net.threeple.pg.mon.node;

import java.util.StringJoiner;

public class StorageNode {
	private final static byte IN = 1;
	private final static byte OUT = 2;
	private final static byte UP = 4;
	private final static byte DOWN = 8;
	
	private int id;
	private String address;
	private byte stateCode = 5;
	private int[] pgs = new int[0];
	
	public StorageNode() {
		
	}
	
	public StorageNode(int _id, String _address) {
		this.id = _id;
		this.address = _address;
	}

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
	
	private void setStatus(byte status, byte reverse) {
		if(hasStatus(status)) {
			return;
		}
		if(hasStatus(reverse)) {
			this.stateCode -= reverse;
		}
		this.stateCode += status;
	}
	
	private boolean hasStatus(byte status) {
		return (this.stateCode & status) == status;
	}
	
	public void setIn() {
		setStatus(IN, OUT);
	}
	
	public boolean isIn() {
		return hasStatus(IN);
	}
	
	public void setOut() {
		setStatus(OUT, IN);
	}
	
	public boolean isOut() {
		return hasStatus(OUT);
	}
	
	public void setUp() {
		setStatus(UP, DOWN);
	}
	
	public boolean isUp() {
		return hasStatus(UP);
	}
	
	public void setDown() {
		setStatus(DOWN, UP);
	}
	
	public boolean isDown() {
		return hasStatus(DOWN);
	}

	public int[] getPgs() {
		return pgs;
	}

	public void setPgs(int[] pgs) {
		this.pgs = pgs;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(",");
		for(int i = 0; i < this.pgs.length; i++) {
			joiner.add(Integer.valueOf(this.pgs[i]).toString());
		}
		return this.id + "=" + this.address + "<" + joiner.toString();
	}
	
}
