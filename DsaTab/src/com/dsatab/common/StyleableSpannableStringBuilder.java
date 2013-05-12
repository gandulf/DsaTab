package com.dsatab.common;

import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import com.dsatab.util.ClickSpan;
import com.dsatab.util.ClickSpan.OnSpanClickListener;

public class StyleableSpannableStringBuilder extends SpannableStringBuilder {

	public StyleableSpannableStringBuilder appendWithStyle(CharacterStyle c, CharSequence text) {
		super.append(text);
		int startPos = length() - text.length();
		setSpan(c, startPos, length(), 0);
		return this;
	}

	public StyleableSpannableStringBuilder appendBold(CharSequence text) {
		return appendWithStyle(new StyleSpan(Typeface.BOLD), text);
	}

	public StyleableSpannableStringBuilder appendColor(int color, CharSequence text) {
		return appendWithStyle(new ForegroundColorSpan(color), text);
	}

	public StyleableSpannableStringBuilder appendClick(OnSpanClickListener clickListener, CharSequence text,
			CharSequence tag) {
		return appendWithStyle(new ClickSpan(tag, clickListener), text);
	}
}