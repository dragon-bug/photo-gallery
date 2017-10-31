package net.threeple.pg.shared.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnknownOperationException extends RuntimeException {
	final Logger logger = LoggerFactory.getLogger(UnknownOperationException.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 586837533516541945L;

	public UnknownOperationException(String message) {
		super(message);
		logger.error(message);
	}

	public UnknownOperationException(Throwable cause) {
		super(cause);
	}

	public UnknownOperationException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnknownOperationException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
