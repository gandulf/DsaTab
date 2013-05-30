package com.dsatab.exception;

public class FeatureTypeUnknownException extends InconsistentDataException {

	private static final long serialVersionUID = 1L;

	public FeatureTypeUnknownException(String featureType) {
		super("FeatureType " + featureType + " not recognized.");

	}

}
