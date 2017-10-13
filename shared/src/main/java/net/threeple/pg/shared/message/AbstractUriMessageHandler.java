package net.threeple.pg.shared.message;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractUriMessageHandler extends AbstractByteMessageHandler implements MessageSender, MessageReceiver {
	final Logger logger = LoggerFactory.getLogger(AbstractUriMessageHandler.class);
	protected String uri;
	private Charset utf8 = StandardCharsets.UTF_8;
	
	@Override
	public void receive() throws IOException {
		int length = this.in.readInt();
		byte[] buf = new byte[length];
		this.in.read(buf);
		this.uri = new String(buf, utf8);
	}

	@Override
	public void send() throws IOException {
		byte[] buf = this.uri.getBytes(utf8);
		this.out.writeInt(buf.length);
		this.out.write(buf);
		this.out.flush();
	}

}
