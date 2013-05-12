package com.dsatab.common;

public class DsaTabException extends Exception {

	private static final long serialVersionUID = -510090776420094384L;

	public DsaTabException() {
		super();

	}

	public DsaTabException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);

	}

	public DsaTabException(String detailMessage) {
		super(detailMessage);

	}

	public DsaTabException(Throwable throwable) {
		super(throwable);
	}

}
