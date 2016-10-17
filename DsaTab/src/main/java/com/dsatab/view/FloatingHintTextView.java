package com.dsatab.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import com.dsatab.R;

public class FloatingHintTextView extends TextView {

	private final Paint mFloatingHintPaint = new Paint();
	private final ColorStateList mHintColors;
	private final int mHintSize;

	private CharSequence mHint;

	public FloatingHintTextView(Context context) {
		this(context, null);
	}

	public FloatingHintTextView(Context context, AttributeSet attrs) {
		this(context, attrs, R.attr.floatingHintTextViewStyle);
	}

	public FloatingHintTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		mHintSize = getResources().getDimensionPixelSize(R.dimen.floatinghintedittext_hint_size);
		mHintColors = getHintTextColors();
	}

	@Override
	public int getCompoundPaddingTop() {
		return super.getCompoundPaddingTop() + mHintSize;
	}

	protected void convertHint() {
		if (getHint() != null) {
			mHint = getHint();
			setHint(null);
		}
	}

	@Override
	public boolean onPreDraw() {
		convertHint();
		return super.onPreDraw();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		convertHint();
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		convertHint();
		super.onLayout(changed, left, top, right, bottom);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (TextUtils.isEmpty(mHint)) {
			return;
		}

		mFloatingHintPaint.set(getPaint());
		mFloatingHintPaint.setColor(mHintColors.getColorForState(getDrawableState(), mHintColors.getDefaultColor()));

		final float hintPosX = getCompoundPaddingLeft() + getScrollX();
		final float normalHintPosY = getBaseline();
		final float floatingHintPosY = normalHintPosY + getPaint().getFontMetricsInt().top + getScrollY();

		// If we're not animating, we're showing the floating hint, so draw it and bail.
		mFloatingHintPaint.setTextSize(mHintSize);
		canvas.drawText(mHint.toString(), hintPosX, floatingHintPosY, mFloatingHintPaint);

	}

}
