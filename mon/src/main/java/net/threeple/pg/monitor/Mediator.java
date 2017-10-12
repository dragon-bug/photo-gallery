package net.threeple.pg.monitor;

import java.net.Socket;

public interface Mediator {
	public void update(PsdNode psdNode); // Be call by psd node
	public void syncPgMap(Socket socket); // Be call by api client node
	public void syncPsd(Socket socket);
}
