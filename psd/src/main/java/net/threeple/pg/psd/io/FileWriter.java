package net.threeple.pg.psd.io;

import java.io.IOException;

public interface FileWriter {
	public void write(String uri, byte[] body) throws IOException;
}
