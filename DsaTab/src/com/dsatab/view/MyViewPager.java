package com.dsatab.view;

import org.osmdroid.views.MapView;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class MyViewPager extends ViewPager {

	public MyViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyViewPager(Context context) {
		super(context);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (this.isEnabled()) {
			return super.onTouchEvent(event);
		}

		return false;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		if (this.isEnabled()) {
			try {
				return super.onInterceptTouchEvent(event);
			} catch (IllegalArgumentException e) {
				// catch java.lang.IllegalArgumentException: pointerIndex out of range
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.view.ViewPager#canScroll(android.view.View, boolean, int, int, int)
	 */
	@Override
	protected boolean canScroll(View child, boolean checkV, int dx, int x, int y) {
		if (child instanceof MapView) {
			return true;
		} else {
			return super.canScroll(child, checkV, dx, x, y);
		}
	}

}
