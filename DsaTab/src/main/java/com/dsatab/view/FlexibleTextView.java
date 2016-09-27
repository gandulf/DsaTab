package com.dsatab.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

public class FlexibleTextView extends AppCompatTextView {

	private Rect textBounds;

	public FlexibleTextView(Context context) {
		super(context);
		init();
	}

	public FlexibleTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public FlexibleTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		textBounds = new Rect();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		if (getText() != null && getHint() != null) {
			getPaint().getTextBounds(getText().toString(), 0, getText().length(), textBounds);

			if (getMeasuredWidth() > 0
					&& textBounds.width() > getMeasuredWidth() - getPaddingLeft() - getPaddingRight()) {

				setText(getHint());
				setHint(null);
			}
		}
	}

}
