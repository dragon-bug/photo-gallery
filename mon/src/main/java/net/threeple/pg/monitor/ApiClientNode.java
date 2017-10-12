package net.threeple.pg.monitor;

import java.net.Socket;
import java.util.Observable;
import java.util.Observer;

public class ApiClientNode extends AbstractNode implements Observer{
	
	public ApiClientNode( Socket _socket) {
		super(_socket);
		
	}

	@Override
	public void update(Observable o, Object arg) {
		((Mediator)o).syncPgMap(this.socket);
	}

}
