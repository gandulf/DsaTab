package com.dsatab.exception;

public class FeatureTypeUnknownExcpetion extends InconsistentDataException {

	private static final long serialVersionUID = 1L;

	public FeatureTypeUnknownExcpetion(String featureType) {
		super("FeatureType " + featureType + " not recognized.");

	}

}
