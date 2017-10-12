package net.threeple.pg.monitor;

import java.net.InetAddress;
import java.net.Socket;

public class PsdNode extends AbstractNode implements Runnable {
	private short id;
	private int[] pgs;
	private InetAddress address;
	private int port;
	private Mediator mediator;
	
	public PsdNode(Mediator _mediator, Socket _socket) {
		super( _socket);
		this.mediator = _mediator;
		this.address = _socket.getInetAddress();
		this.port = _socket.getPort();
	}
	
	public short getId() {
		return this.id;
	}
	
	public int[] getPgs() {
		return this.pgs;
	}
	
	public InetAddress getAddress() {
		return this.address;
	}
	
	public int getPort() {
		return this.port;
	}
	
	@Override
	public void run() {
		/*Transport<Short> idTransport = new PsdIdTransport(this.socket);
		this.id = idTransport.receive();
		
		Transport<int[]> psdTransport = new PsdTransport(this.socket);
		while(true) {
			this.pgs = psdTransport.receive();
			this.mediator.update(this);
		}*/
	}

}
