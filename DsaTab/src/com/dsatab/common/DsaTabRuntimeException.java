package com.dsatab.common;

public class DsaTabRuntimeException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4603128387568878199L;

	/**
	 * 
	 */
	public DsaTabRuntimeException() {

	}

	/**
	 * @param detailMessage
	 */
	public DsaTabRuntimeException(String detailMessage) {
		super(detailMessage);

	}

	/**
	 * @param throwable
	 */
	public DsaTabRuntimeException(Throwable throwable) {
		super(throwable);

	}

	/**
	 * @param detailMessage
	 * @param throwable
	 */
	public DsaTabRuntimeException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);

	}

}
