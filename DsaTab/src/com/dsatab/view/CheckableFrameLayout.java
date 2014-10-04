package com.dsatab.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.gandulf.guilib.listener.CheckableListenable;
import com.gandulf.guilib.listener.OnCheckedChangeListener;

public class CheckableFrameLayout extends FrameLayout implements CheckableListenable {
	boolean mChecked = false;

	private static final int[] CHECKED_STATE_SET = { android.R.attr.state_checked };

	private float xFraction;

	private float yFraction;

	private OnCheckedChangeListener mOnCheckedChangeListener;

	public CheckableFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CheckableFrameLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CheckableFrameLayout(Context context) {
		super(context);
	}

	@Override
	public boolean isChecked() {
		return mChecked;
	}

	@Override
	public void setChecked(boolean checked) {
		if (mChecked != checked) {
			mChecked = checked;
			refreshDrawableState();

			if (mOnCheckedChangeListener != null) {
				mOnCheckedChangeListener.onCheckedChanged(this, mChecked);
			}
		}
	}

	@Override
	public void toggle() {
		setChecked(!mChecked);
	}

	@Override
	protected int[] onCreateDrawableState(int extraSpace) {
		final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
		if (isChecked()) {
			mergeDrawableStates(drawableState, CHECKED_STATE_SET);
		}
		return drawableState;
	}

	public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
		this.mOnCheckedChangeListener = onCheckedChangeListener;
	}

	private ViewTreeObserver.OnPreDrawListener preDrawYListener = null;
	private ViewTreeObserver.OnPreDrawListener preDrawXListener = null;

	public void setYFraction(float fraction) {

		this.yFraction = fraction;

		if (getHeight() == 0) {
			if (preDrawYListener == null) {
				preDrawYListener = new ViewTreeObserver.OnPreDrawListener() {
					@Override
					public boolean onPreDraw() {
						getViewTreeObserver().removeOnPreDrawListener(preDrawYListener);
						setYFraction(yFraction);
						return true;
					}
				};
				getViewTreeObserver().addOnPreDrawListener(preDrawYListener);
			}
			return;
		}

		float translationY = getHeight() * fraction;
		setTranslationY(translationY);
	}

	public float getYFraction() {
		return this.yFraction;
	}

	public void setXFraction(float fraction) {

		this.xFraction = fraction;

		if (getWidth() == 0) {
			if (preDrawXListener == null) {
				preDrawXListener = new ViewTreeObserver.OnPreDrawListener() {
					@Override
					public boolean onPreDraw() {
						getViewTreeObserver().removeOnPreDrawListener(preDrawXListener);
						setXFraction(xFraction);
						return true;
					}
				};
				getViewTreeObserver().addOnPreDrawListener(preDrawXListener);
			}
			return;
		}

		float translationX = getWidth() * fraction;
		setTranslationX(translationX);
	}

	public float getXFraction() {
		return this.xFraction;
	}

}