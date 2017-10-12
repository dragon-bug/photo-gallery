package net.threeple.pg.monitor;

import java.net.Socket;

public abstract class AbstractNode {
	protected Socket socket;
	
	public AbstractNode(Socket _socket) {
		this.socket = _socket;
	}
	
}
