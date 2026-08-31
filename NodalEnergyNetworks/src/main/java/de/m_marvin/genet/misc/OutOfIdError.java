package de.m_marvin.genet.misc;

public class OutOfIdError extends Error {
	
	private static final long serialVersionUID = 5306276142112720589L;

	public OutOfIdError() {
		super();
	}

	public OutOfIdError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public OutOfIdError(String message, Throwable cause) {
		super(message, cause);
	}

	public OutOfIdError(String message) {
		super(message);
	}

	public OutOfIdError(Throwable cause) {
		super(cause);
	}
	
}
