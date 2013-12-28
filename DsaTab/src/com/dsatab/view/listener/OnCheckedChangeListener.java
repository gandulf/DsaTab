package com.dsatab.view.listener;

import android.view.View;

/**
 * Interface definition for a callback to be invoked when the checked state of this View is changed.
 */
public interface OnCheckedChangeListener {

	/**
	 * Called when the checked state of a compound button has changed.
	 * 
	 * @param checkableView
	 *            The view whose state has changed.
	 * @param isChecked
	 *            The new checked state of checkableView.
	 */
	void onCheckedChanged(View checkableView, boolean isChecked);
}