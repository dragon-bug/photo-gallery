package net.threeple.pg.shared.message;

import java.io.IOException;

public interface MessageSender {
	public void send() throws IOException;
}
