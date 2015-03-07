package com.dsatab.activity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.util.Hint;
import com.dsatab.util.Util;

public class BaseActivity extends ActionBarActivity {

	protected Toolbar toolbar;

	private Drawable mActionBarBackgroundDrawable;
	private int mOriginalWrapperPadding = 0;
	private int mActionBarBackgroundBaseAlpha = 255;
	private boolean mActionBarTranslucent;

	private boolean toolbarRefreshing = false;

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

		prepareToolbar();

		if (DsaTabApplication.getInstance().getPalette() != null) {
			applyPalette(DsaTabApplication.getInstance().getPalette());
		}
	}

	@SuppressLint("NewApi")
	public void applyPalette(Palette palette) {
		if (!DsaTabApplication.getPreferences().getBoolean(DsaTabPreferenceActivity.KEY_USE_PALETTE, false))
			return;

		if (palette != null) {
			getToolbar().setBackgroundColor(palette.getDarkMutedColor(Color.BLUE));
			getToolbar().setTitleTextColor(palette.getLightVibrantColor(Color.WHITE));

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				getWindow().setStatusBarColor(palette.getDarkMutedColor(Color.BLUE));
			}
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		final MenuItem refreshItem = menu.findItem(R.id.option_refresh);
		if (refreshItem != null) {
			if (toolbarRefreshing) {
				refreshItem.setActionView(R.layout._toolbar_progress);
			} else {
				refreshItem.setActionView(null);
			}
		}

		return super.onPrepareOptionsMenu(menu);
	}

	public boolean isToolbarRefreshing() {
		return toolbarRefreshing;
	}

	public void setToolbarRefreshing(boolean toolbarRefreshing) {
		this.toolbarRefreshing = toolbarRefreshing;

		supportInvalidateOptionsMenu();
	}

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);

		toolbar = (Toolbar) findViewById(R.id.toolbar);
		if (toolbar != null) {
			setSupportActionBar(toolbar);
		}
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
		prepareToolbar();

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

	public Toolbar getToolbar() {
		return toolbar;
	}

	private void prepareToolbar() {
		if (mActionBarBackgroundDrawable == null && toolbar != null) {
			mActionBarBackgroundDrawable = toolbar.getBackground().mutate();
			mActionBarBackgroundDrawable.setAlpha(255);
			toolbar.setBackgroundDrawable(mActionBarBackgroundDrawable);

			View wrapper = findViewById(R.id.slidepanel);
			if (wrapper != null)
				mOriginalWrapperPadding = wrapper.getPaddingTop();
		}
	}

	protected void setActionbarTranslucent(boolean translucent) {
		prepareToolbar();

		mActionBarTranslucent = translucent;

		View wrapper = findViewById(R.id.slidepanel);
		if (translucent) {
			if (wrapper != null) {
				wrapper.setPadding(wrapper.getPaddingLeft(), mOriginalWrapperPadding, wrapper.getPaddingRight(),
						wrapper.getPaddingBottom());
			}
			setActionbarBackgroundAlpha(0);
			setActionbarBackgroundBaseAlpha(0);
		} else {
			int actionBarHeightId = Util.getThemeResourceId(this, R.attr.actionBarSize);
			float toolbarHeight = getResources().getDimension(actionBarHeightId);

			if (wrapper != null) {
				wrapper.setPadding(wrapper.getPaddingLeft(), (int) (mOriginalWrapperPadding + toolbarHeight),
						wrapper.getPaddingRight(), wrapper.getPaddingBottom());
			}
			setActionbarBackgroundAlpha(255);
			setActionbarBackgroundBaseAlpha(255);
		}

	}

}
