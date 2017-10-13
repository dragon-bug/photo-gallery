package net.threeple.pg.shared.message;

import java.io.IOException;

public interface MessageReceiver {
	public void receive() throws IOException;
}
