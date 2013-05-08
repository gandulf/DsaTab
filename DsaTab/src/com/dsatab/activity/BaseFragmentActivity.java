/**
 *  This file is part of DsaTab.
 *
 *  DsaTab is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  DsaTab is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with DsaTab.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.dsatab.activity;

import java.io.File;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Display;
import android.view.WindowManager;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.dsatab.DsaTabApplication;
import com.dsatab.util.Util;

/**
 * @author Ganymede
 * 
 */
public class BaseFragmentActivity extends SherlockFragmentActivity {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.FragmentActivity#onPostCreate(android.os.Bundle)
	 */
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		SharedPreferences preferences = DsaTabApplication.getPreferences();
		updateFullscreenStatus(preferences.getBoolean(DsaTabPreferenceActivity.KEY_FULLSCREEN, false));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		Util.unbindDrawables(getWindow().getDecorView());
		super.onDestroy();
	}

	protected void applyPreferencesToTheme() {

		SharedPreferences pref = DsaTabApplication.getPreferences();
		String bgPath = pref.getString(DsaTabPreferenceActivity.KEY_STYLE_BG_PATH, null);

		if (bgPath != null) {

			WindowManager wm = (WindowManager) DsaTabApplication.getInstance().getSystemService(Context.WINDOW_SERVICE);
			Display display = wm.getDefaultDisplay();
			Bitmap bg = Util.decodeBitmap(new File(bgPath), Math.max(display.getWidth(), display.getHeight()));
			BitmapDrawable drawable = new BitmapDrawable(DsaTabApplication.getInstance().getResources(), bg);
			getWindow().setBackgroundDrawable(drawable);
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
}
