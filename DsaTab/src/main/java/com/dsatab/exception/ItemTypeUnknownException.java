package com.dsatab.exception;

public class ItemTypeUnknownException extends InconsistentDataException {

	private static final long serialVersionUID = 1L;

	public ItemTypeUnknownException(char itemType) {
		super("Character " + itemType + " not recognized for itemtype");

	}

}
