package net.threeple.pg.psd;

import java.io.IOException;

public interface FileReader {
	public byte[] read(String uri) throws IOException;
}
