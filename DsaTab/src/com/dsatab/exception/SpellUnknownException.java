package com.dsatab.exception;

public class SpellUnknownException extends InconsistentDataException {

	private static final long serialVersionUID = 1L;

	public SpellUnknownException(String spell) {
		super("Unknown Spell:" + spell);

	}

}
