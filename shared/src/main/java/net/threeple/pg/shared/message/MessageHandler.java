package net.threeple.pg.shared.message;

import java.io.IOException;

public interface MessageHandler {
	public void setNext(MessageHandler handler);
	public void receive() throws IOException;
	public void send() throws IOException;
}
