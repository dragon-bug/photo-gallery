package net.threeple.pg.monitor;

import java.net.Socket;

public class NodeMediator extends AbstractMediator {
	
	public NodeMediator(int size) {
		super(size);
	}

	@Override
	public void update(PsdNode psdNode) {
		int[] pgs = psdNode.getPgs();
		for(int i = 0; i < pgs.length; i++) {
			int pgId = pgs[i];
			this.pgMap[pgId] = psdNode.getId();
		}
		
		int id = psdNode.getId();
		String connection = this.connections[id];
		if(connection == null || connection.isEmpty()) {
			String address = psdNode.getAddress().getHostAddress() + ":" + psdNode.getPort();
			this.connections[id] = address;
		}
		this.notifyObservers();
	}

	@Override
	public void syncPgMap(Socket socket) {
		/*Transport<short[]> pgMapTransport = new PgMapTransport(socket);
		pgMapTransport.send(this.pgMap);*/
	}

	@Override
	public void syncPsd(Socket socket) {
		/*Transport<String[]> connectionTransport = new PsdConnectionTransport(socket);
		connectionTransport.send(this.connections);*/
	}

}
