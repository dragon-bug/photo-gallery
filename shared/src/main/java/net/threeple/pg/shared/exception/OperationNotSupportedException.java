package net.threeple.pg.shared.exception;

public class OperationNotSupportedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3174578496485539886L;

	public OperationNotSupportedException() {
		super();
	}

	public OperationNotSupportedException(String message) {
		super(message);
	}

}
