package net.threeple.pg.monitor;


import java.net.Socket;

public class NodeLoader {
	private AbstractMediator mediator;
	private int sequence;
	
	public NodeLoader() {
		this.mediator = new NodeMediator(64);
	}
	
	public void load(Socket socket) {
		
		/*Transport<Integer> identityTransport = new IdentityTransport(socket);
		Integer identity = identityTransport.receive();
		
		switch(identity) {
		case (int) API_CLIENT:
			this.mediator.addObserver(
					new ApiClientNode(socket));
			this.mediator.syncPsd(socket);
			this.mediator.syncPgMap(socket);
			break;
		case (int) PSD:
			sequence++;
			Thread thread = new Thread(
					new PsdNode(this.mediator, socket), 
					"PSD_Node_Thread_" + sequence);
			thread.setDaemon(true);
			thread.start();
			break;
		default:
			// TODO Logger Warn
		}*/
		
	}
}
