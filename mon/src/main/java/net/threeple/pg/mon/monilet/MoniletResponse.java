package net.threeple.pg.mon.monilet;

import java.io.BufferedWriter;

public class MoniletResponse implements IMoniletResponse {
	private BufferedWriter writer;
	private int statusCode;
	
	public MoniletResponse(BufferedWriter _writer) {
		this.writer = _writer;
	}

	@Override
	public BufferedWriter getWriter() {
		return this.writer;
	}
	
	public void sentError(int statusCode) {
		this.statusCode = statusCode;
	}
	
	public void setStatus(int statusCode) {
		this.statusCode = statusCode;
	}
	
	public int getStatus() {
		return this.statusCode;
	}
}
