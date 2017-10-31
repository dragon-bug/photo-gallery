package net.threeple.pg.psd.persistent;

import java.io.File;

public class Volume {
	private final File patition;
	
	public Volume(String root) {
		this.patition = new File(root);
	}
	
	public long getTotalSpace() {
		return this.patition.getTotalSpace();
	}
	
	public long getFreeSpace() {
		return this.patition.getFreeSpace();
	}
	
	public long getUsableSpace() {
		return this.patition.getUsableSpace();
	}
}
