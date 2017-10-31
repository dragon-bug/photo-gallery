package net.threeple.pg.psd.io;

import java.io.IOException;

public interface FileReader {
	public byte[] read(String uri) throws IOException;
}
