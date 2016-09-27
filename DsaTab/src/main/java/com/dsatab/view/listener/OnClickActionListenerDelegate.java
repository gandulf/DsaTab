package com.dsatab.view.listener;

import android.view.View;
import android.view.View.OnClickListener;

public class OnClickActionListenerDelegate implements OnClickListener {

	private int action;
	private OnActionListener actionListener;

	public OnClickActionListenerDelegate(int action, OnActionListener onActionListener) {
		this.action = action;
		this.actionListener = onActionListener;
	}

	@Override
	public void onClick(View paramView) {
		if (actionListener != null) {
			actionListener.onAction(action);
		}
	}

}
