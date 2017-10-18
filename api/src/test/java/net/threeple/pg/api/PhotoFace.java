package net.threeple.pg.api;

public class PhotoFace {
	private String uri;
	private String digest;
	
	public PhotoFace(String _uri, String _digest) {
		this.uri = _uri;
		this.digest = _digest;
	}
	
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public String getDigest() {
		return digest;
	}
	public void setDigest(String digest) {
		this.digest = digest;
	}
	
	
}
