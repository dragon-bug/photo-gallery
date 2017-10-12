package net.threeple.pg.shared.message;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractPhotoMessageHandler extends AbstractUriMessageHandler {
	final Logger logger = LoggerFactory.getLogger(AbstractPhotoMessageHandler.class);
	protected byte[] body;
	
	@Override
	public void receive() throws IOException {
		super.receive();
		int length = in.readInt();
		this.body = new byte[length];
		in.read(this.body);
	};

	@Override
	public void send() throws IOException {
		super.send();
		logger.debug("URI: {}, Body Length: {}", this.uri, this.body.length);
		this.out.writeInt(this.body.length);
		this.out.write(this.body);
		this.out.flush();
	};
}
