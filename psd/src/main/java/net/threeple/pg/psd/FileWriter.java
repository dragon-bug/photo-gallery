package net.threeple.pg.psd;

import java.io.IOException;

public interface FileWriter {
	public void write(String uri, byte[] body) throws IOException;
}
