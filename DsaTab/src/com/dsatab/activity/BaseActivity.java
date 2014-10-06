package com.dsatab.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.util.Hint;
import com.dsatab.util.Util;

public class BaseActivity extends Activity {

	private Drawable.Callback mDrawableCallback = new Drawable.Callback() {
		@Override
		public void invalidateDrawable(Drawable who) {
			getActionBar().setBackgroundDrawable(who);
		}

		@Override
		public void scheduleDrawable(Drawable who, Runnable what, long when) {
		}

		@Override
		public void unscheduleDrawable(Drawable who, Runnable what) {
		}
	};
	private Drawable mActionBarBackgroundDrawable;
	private int mActionBarBackgroundBaseAlpha = 255;
	private boolean mActionBarTranslucent;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.FragmentActivity#onPostCreate(android.os.Bundle)
	 */
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		SharedPreferences preferences = DsaTabApplication.getPreferences();
		updateFullscreenStatus(preferences.getBoolean(DsaTabPreferenceActivity.KEY_FULLSCREEN, false));

		Hint.showRandomHint(getClass().getSimpleName(), this);
	}

	public boolean isActionbarTranslucent() {
		return mActionBarTranslucent;
	}

	public int getActionbarBackgroundBaseAlpha() {
		return mActionBarBackgroundBaseAlpha;
	}

	public void setActionbarBackgroundBaseAlpha(float alpha) {
		setActionbarBackgroundBaseAlpha((int) (255 * alpha));

	}

	public void setActionbarBackgroundBaseAlpha(int alpha) {
		mActionBarBackgroundBaseAlpha = alpha;
	}

	public void setActionbarBackgroundAlpha(float alpha) {
		setActionbarBackgroundAlpha((int) (255 * alpha));
	}

	public void setActionbarBackgroundAlpha(int alpha) {
		if (mActionBarBackgroundDrawable != null) {
			mActionBarBackgroundDrawable.setAlpha(alpha);
		}
	}

	protected void applyPreferencesToTheme() {

		SharedPreferences pref = DsaTabApplication.getPreferences();
		String bgPath = pref.getString(DsaTabPreferenceActivity.KEY_STYLE_BG_PATH, null);

		if (bgPath != null) {
			getWindow().setBackgroundDrawable(Drawable.createFromPath(bgPath));
		} else {
			getWindow().setBackgroundDrawableResource(Util.getThemeResourceId(this, android.R.attr.windowBackground));
		}
	}

	protected void updateFullscreenStatus(boolean bUseFullscreen) {
		if (bUseFullscreen) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		} else {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
		getWindow().getDecorView().requestLayout();
	}

	protected void setActionbarTranslucent(boolean translucent) {
		mActionBarTranslucent = translucent;

		if (mActionBarBackgroundDrawable == null) {
			mActionBarBackgroundDrawable = getResources().getDrawable(R.drawable.ab_solid_dark_holo);
			mActionBarBackgroundDrawable.setAlpha(0);
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
				mActionBarBackgroundDrawable.setCallback(mDrawableCallback);
			}
		}

		getActionBar().setBackgroundDrawable(mActionBarBackgroundDrawable);

		View wrapper = findViewById(R.id.slidepanel);
		if (translucent) {
			if (wrapper != null) {
				wrapper.setPadding(0, 0, 0, 0);
			}
			setActionbarBackgroundAlpha(0);
			setActionbarBackgroundBaseAlpha(0);
		} else {
			if (wrapper != null) {
				int actionbarSize = getResources().getDimensionPixelSize(
						Util.getThemeResourceId(this, android.R.attr.actionBarSize));
				wrapper.setPadding(0, actionbarSize, 0, 0);
			}
			setActionbarBackgroundAlpha(255);
			setActionbarBackgroundBaseAlpha(255);
		}

	}

}
