package net.threeple.pg.mon.exception;

public class CloudNotParseRequestException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4963544406571690327L;

	public CloudNotParseRequestException(String message) {
		super(message);
	}

	public CloudNotParseRequestException(Throwable cause) {
		super(cause);
	}

	public CloudNotParseRequestException(String message, Throwable cause) {
		super(message, cause);
	}

	public CloudNotParseRequestException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
