package com.dsatab.exception;

public class ArtUnknownException extends InconsistentDataException {

	private static final long serialVersionUID = 1L;

	public ArtUnknownException(String art, String grade) {
		super("Unknown Art:" + art + ", Grade:" + grade);

	}

}
