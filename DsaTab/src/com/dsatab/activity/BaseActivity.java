package com.dsatab.activity;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.util.Hint;
import com.dsatab.util.Util;

public class BaseActivity extends AppCompatActivity {

	protected Toolbar toolbar;
	protected CollapsingToolbarLayout toolbarCollapse;
	protected AppBarLayout appBarLayout;

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

		Hint.showRandomHint(getClass().getSimpleName(), this);

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
		toolbarCollapse  = (CollapsingToolbarLayout) findViewById(R.id.toolbar_collapse);
		appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
		if (toolbar != null) {
			setSupportActionBar(toolbar);
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

	public Toolbar getToolbar() {
		return toolbar;
	}


	protected  void setToolbarTitle(int title) {
		if (toolbar!=null)
			toolbar.setTitle(title);

		if (toolbarCollapse!=null)
			toolbarCollapse.setTitle(getText(title));
	}
	protected  void setToolbarTitle(CharSequence title) {
		if (toolbar!=null)
			toolbar.setTitle(title);

		if (toolbarCollapse!=null)
			toolbarCollapse.setTitle(title);

	}
}
