package com.dsatab.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.franlopez.flipcheckbox.CheckableListenable;
import com.franlopez.flipcheckbox.OnCheckedChangeListener;
import com.h6ah4i.android.widget.advrecyclerview.selectable.CheckableState;

public class CheckableRelativeLayout extends RelativeLayout implements CheckableListenable,CheckableState {

    private boolean mChecked = false;
    private boolean mCheckable = false;

	private static final int[] CHECKED_STATE_SET = { android.R.attr.state_checked };

    private static final int[] CHECKABLE_STATE_SET = { android.R.attr.state_checkable };

	private OnCheckedChangeListener mOnCheckedChangeListener;

    public CheckableRelativeLayout(Context context) {
        super(context);
    }

	public CheckableRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CheckableRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CheckableRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public boolean isCheckable() {
        return mCheckable;
    }

    public void setCheckable(boolean checkable) {
        if (mCheckable != checkable) {
            mCheckable = checkable;
            refreshDrawableState();
        }
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
    public void setCheckedImmediate(boolean checked) {
        if (mChecked != checked) {
            mChecked = checked;
            refreshDrawableState();
        }
    }

    @Override
	public void toggle() {
		setChecked(!mChecked);
	}

	@Override
	protected int[] onCreateDrawableState(int extraSpace) {
		final int[] drawableState = super.onCreateDrawableState(extraSpace + 2);
        if (isCheckable()) {
            mergeDrawableStates(drawableState, CHECKABLE_STATE_SET);
        }
		if (isChecked()) {
			mergeDrawableStates(drawableState, CHECKED_STATE_SET);
		}
		return drawableState;
	}

	public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
		this.mOnCheckedChangeListener = onCheckedChangeListener;
	}

}