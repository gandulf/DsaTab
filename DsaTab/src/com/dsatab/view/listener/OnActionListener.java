package com.dsatab.view.listener;

public interface OnActionListener {

	static final int ACTION_DOCUMENTS_CHOOSE = 1;
	static final int ACTION_NOTES_ADD = 2;
	static final int ACTION_NOTES_RECORD = 3;
	static final int ACTION_MODIFICATOR_ADD = 4;
	static final int ACTION_CUSTOM_PROBE_ADD = 5;

	public boolean onAction(int actionId);
}