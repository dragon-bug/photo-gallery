package net.threeple.pg.shared.message;

import java.io.IOException;

public abstract class TestAbstractUriMessage extends AbstractMessageHandler {

	@Override
	public void receive() throws IOException {
		System.out.println("Receive URI");

	}

	@Override
	public void send() throws IOException {
		System.out.println("Send URI");
	}

}
