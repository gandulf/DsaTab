package com.dsatab.exception;


public class TalentTypeUnknownException extends InconsistentDataException {

	private static final long serialVersionUID = 1L;

	public TalentTypeUnknownException(String talentType) {
		super("Unknown TalentType:" + talentType);

	}

}
