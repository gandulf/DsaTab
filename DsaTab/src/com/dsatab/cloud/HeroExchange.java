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
package com.dsatab.cloud;

import java.io.File;

import android.app.Activity;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.widget.Toast;

import com.dsatab.DsaTabApplication;
import com.dsatab.activity.DsaTabPreferenceActivity;
import com.dsatab.data.HeroFileInfo;
import com.dsatab.util.Debug;

public class HeroExchange {

	public static final int RESULT_OK = 1;
	public static final int RESULT_ERROR = 2;
	public static final int RESULT_CANCELED = 3;
	public static final int RESULT_EMPTY = 4;

	private Activity context;

	private OnHeroExchangeListener onHeroExchangeListener;

	public interface OnHeroExchangeListener {
		public void onHeroLoaded(String path);

		public void onHeroInfoLoaded(HeroFileInfo info);
	};

	public HeroExchange(Activity context) {
		this.context = context;
	}

	public OnHeroExchangeListener getOnHeroExchangeListener() {
		return onHeroExchangeListener;
	}

	public void setOnHeroExchangeListener(OnHeroExchangeListener onHeroExchangeListener) {
		this.onHeroExchangeListener = onHeroExchangeListener;
	}

	private boolean isConfigured() {
		final SharedPreferences preferences = DsaTabApplication.getPreferences();
		String token = preferences.getString(DsaTabPreferenceActivity.KEY_EXCHANGE_TOKEN, "");
		return !TextUtils.isEmpty(token);
	}

	public void syncHeroes() {

		if (!checkSettings())
			return;

		final SharedPreferences preferences = DsaTabApplication.getPreferences();

		ImportHeroesTaskNew importFileTask = new ImportHeroesTaskNew(context, preferences.getString(
				DsaTabPreferenceActivity.KEY_EXCHANGE_TOKEN, ""));
		importFileTask.setOnHeroExchangeListener(onHeroExchangeListener);
		importFileTask.execute();
	}

	public void importHero(HeroFileInfo heroInfo) {

		if (!checkSettings())
			return;

		final SharedPreferences preferences = DsaTabApplication.getPreferences();

		ImportHeroTaskNew importFileTask = new ImportHeroTaskNew(context, heroInfo, preferences.getString(
				DsaTabPreferenceActivity.KEY_EXCHANGE_TOKEN, ""));
		importFileTask.setOnHeroExchangeListener(onHeroExchangeListener);
		importFileTask.execute();
	}

	private boolean checkSettings() {
		if (!isConfigured()) {

			Toast.makeText(context, "Bitte zuerst das Logintoken unter Setup > Heldenaustausch Einstellungen angeben.",
					Toast.LENGTH_LONG).show();

			DsaTabPreferenceActivity.startPreferenceActivity(context);
			return false;
		} else
			return true;
	}

	public void exportHero(File heroFile) {

		Debug.verbose("Exporting " + heroFile.getName());

		if (!checkSettings())
			return;

		// Intent intent = new Intent(context, ExportHeroService.class);

		// intent.putExtra(ExportHeroService.INTENT_FILE,
		// heroFile.getAbsolutePath());
		// context.startService(intent);
	}

}
