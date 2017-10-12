package net.threeple.pg.api.request;

public enum RequestFactory {
	Download, Upload;
	
	public Request create() {
		switch(this) {
		case Download:
			return new DownloadRequest();
		case Upload:
			return new UploadRequest();
			default:
				return null;
		}
	}
}
