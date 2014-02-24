package com.dsatab.common;

import android.text.style.ClickableSpan;
import android.view.View;

public class ClickSpan extends ClickableSpan {

	private OnSpanClickListener listener;

	private CharSequence tag;

	public ClickSpan(CharSequence tag, OnSpanClickListener listener) {
		this.listener = listener;
		this.tag = tag;
	}

	@Override
	public void onClick(View widget) {
		if (listener != null)
			listener.onClick(tag, this);
	}

	public interface OnSpanClickListener {
		public void onClick(CharSequence tag, ClickSpan span);
	}

}