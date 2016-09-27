package com.dsatab.exception;


public class InconsistentDataException extends DsaTabRuntimeException {

	private static final long serialVersionUID = -3303083601903889609L;

	public InconsistentDataException() {
		super();

	}

	public InconsistentDataException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);

	}

	public InconsistentDataException(String detailMessage) {
		super(detailMessage);

	}

	public InconsistentDataException(Throwable throwable) {
		super(throwable);

	}

}
