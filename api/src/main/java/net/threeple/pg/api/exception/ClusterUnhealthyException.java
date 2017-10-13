package net.threeple.pg.api.exception;

public class ClusterUnhealthyException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1368894720619369244L;
	
	public ClusterUnhealthyException() {
		super();
	}

	public ClusterUnhealthyException(String message) {
		super(message);
	}

	public ClusterUnhealthyException(Throwable cause) {
		super(cause);
	}

	public ClusterUnhealthyException(String message, Throwable cause) {
		super(message, cause);
	}

	public ClusterUnhealthyException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
