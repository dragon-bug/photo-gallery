package net.threeple.pg.shared.message;

import java.io.IOException;

public class TestConcretePhotoMessage extends TestAbstractPhotoMessage {

	@Override
	public void receive() throws IOException {
		this.sendUri();
		this.receiveBody();

	}

	@Override
	public void send() throws IOException {
		this.sendUri();
		this.sendBody();

	}
	
	public static void main(String[] args) throws Exception {
		TestConcretePhotoMessage message = new TestConcretePhotoMessage();
		message.receive();
		message.send();
	}

}
