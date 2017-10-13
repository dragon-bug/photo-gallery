package net.threeple.pg.shared.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.PushbackInputStream;

public abstract class AbstractByteMessageHandler {
	protected DataInputStream in;
	protected DataOutputStream out;
	protected PushbackInputStream pbin;
		
}
