package net.threeple.pg.shared.message;

import java.io.IOException;

public abstract class TestAbstractPhotoMessage extends TestAbstractUriMessage {

	protected void receiveUri() throws IOException {
		super.receive();
	}
	
	protected void sendUri() throws IOException {
		super.send();
	}
	
	protected void receiveBody() throws IOException {
		System.out.println("Receive body");
	}
	
	protected void sendBody() throws IOException {
		System.out.println("Send body");
	}
	
	public void receive() throws IOException {
		this.receiveUri();
		this.receiveBody();
	};
	
	public void send() throws IOException {
		this.sendUri();
		this.sendBody();
	};
}
