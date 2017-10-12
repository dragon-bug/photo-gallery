package net.threeple.pg.shared.message;

import static net.threeple.pg.shared.constant.MagicNumber.DONE;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PushbackInputStream;

public abstract class AbstractMessageHandler implements MessageHandler {
	protected DataInputStream in;
	protected DataOutputStream out;
	protected PushbackInputStream pbin;
	protected MessageHandler next;
	
	public void setNext(MessageHandler handler) {
		this.next = handler;
	}
	
	protected void done() throws IOException {
		this.out.write(DONE);
		this.out.flush();
		this.out.close();
	}
	
	@Override
	public abstract void receive() throws IOException;

	@Override
	public abstract void send() throws IOException ;

}
