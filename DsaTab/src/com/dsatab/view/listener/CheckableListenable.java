package com.dsatab.view.listener;

import android.widget.Checkable;

public interface CheckableListenable extends Checkable {

	public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener);

}
