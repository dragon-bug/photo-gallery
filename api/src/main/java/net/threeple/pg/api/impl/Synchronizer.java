package net.threeple.pg.api.impl;

import java.net.InetSocketAddress;
import java.net.Socket;

public class Synchronizer implements Runnable {
	private InetSocketAddress[] addresses;
	private int[] pgs;
	private static Synchronizer instance;
	
	static {
		instance = new Synchronizer();
	}
	
	private Synchronizer() {
		this.addresses = new InetSocketAddress[2];
		this.addresses[0] = new InetSocketAddress("localhost", 6678);
		this.addresses[1] = new InetSocketAddress("localhost", 6679);
		this.pgs = new int[] {0, 0, 0, 0, 1, 1, 1, 1};
	}
	
	public static Synchronizer getInstance() {
		return instance;
	}
	
	public InetSocketAddress getAddress(int placement) {
		int id = pgs[placement];
		return this.addresses[id];
	}
	
	public int getAddressIndex(int placement) {
		return this.pgs[placement];
	}
	
	public Socket[] initSockets() {
		int length = addresses.length;
		return new Socket[length];
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

}
