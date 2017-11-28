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
	
	public void setIn() {
		if(isIn()) {
			return;
		}
		if((this.stateCode & OUT) == OUT) {
			this.stateCode -= OUT;
		}
		this.stateCode += IN;
	}
	
	public boolean isIn() {
		return (this.stateCode & IN) == IN;
	}
	
	public void setOut() {
		if(isOut()) {
			return;
		}
		if((this.stateCode & IN) == IN) {
			this.stateCode -= IN;
		}
		this.stateCode += OUT;
	}
	
	public boolean isOut() {
		return (this.stateCode & OUT) == OUT;
	}
	
	public void setUp() {
		if(isUp()) {
			return;
		}
		if((this.stateCode & DOWN) == DOWN) {
			this.stateCode -= DOWN;
		}
		this.stateCode += UP;
	}
	
	public boolean isUp() {
		return (this.stateCode & UP) == UP;
	}
	
	public void setDown() {
		if(isDown()) {
			return;
		}
		if((this.stateCode & UP) == UP) {
			this.stateCode -= UP;
		}
		this.stateCode += DOWN;
	}
	
	public boolean isDown() {
		return (this.stateCode & DOWN) == DOWN;
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
