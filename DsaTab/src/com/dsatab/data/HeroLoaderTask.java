package com.dsatab.data;

import java.io.FileNotFoundException;
import java.io.InputStream;

import android.app.Activity;
import android.content.AsyncTaskLoader;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.dsatab.DsaTabApplication;
import com.dsatab.activity.DsaTabActivity;
import com.dsatab.cloud.HeroExchange;
import com.dsatab.data.HeroFileInfo.FileType;
import com.dsatab.util.Debug;
import com.dsatab.util.Util;
import com.dsatab.xml.HeldenXmlParser;

public class HeroLoaderTask extends AsyncTaskLoader<Hero> {

	private Hero hero;

	private HeroFileInfo fileInfo;

	private Exception exception;

	private HeroExchange exchange;

	public HeroLoaderTask(Activity context, HeroFileInfo heroFileInfo) {
		super(context);
		this.fileInfo = heroFileInfo;
		this.exchange = DsaTabApplication.getInstance().getExchange();

	}

	/**
	 * Handles a request to start the Loader.
	 */
	@Override
	protected void onStartLoading() {
		if (hero != null) {
			// If we currently have a result available, deliver it
			// immediately.
			deliverResult(hero);
		}

		if (takeContentChanged() || hero == null) {
			forceLoad();
		}
	}

	/**
	 * Handles a request to stop the Loader.
	 */
	@Override
	protected void onStopLoading() {
		// Attempt to cancel the current load task if possible.
		cancelLoad();
	}

	/**
	 * This is where the bulk of our work is done. This function is called in a background thread and should generate a
	 * new set of data to be published by the loader.
	 */
	@Override
	public Hero loadInBackground() {
		exception = null;

		// Debug.verbose("Getting hero from " + path);
		if (fileInfo == null) {
			Debug.error("Error: fileInfo was null ");
			return null;
		}

		InputStream fis = null;
		InputStream fisConfig = null;
		SharedPreferences preferences = DsaTabApplication.getPreferences();

		try {
			fis = exchange.getInputStream(fileInfo, FileType.Hero);
			if (fis == null) {
				Debug.error("Error: Hero file not found at " + fileInfo);
				throw new FileNotFoundException(fileInfo.toString());
			}

			try {
				fisConfig = exchange.getInputStream(fileInfo, FileType.Config);
			} catch (FileNotFoundException e) {
				fisConfig = null;
			}
			hero = HeldenXmlParser.readHero(getContext(), fileInfo, fis, fisConfig);
			if (hero != null) {
				Editor editor = preferences.edit();
				editor.putString(DsaTabActivity.PREF_LAST_HERO, hero.getFileInfo().toJSONObject().toString());
				editor.commit();
				// Debug.verbose("Stored path of current hero in prefs:" +
				// hero.getPath());
				// Debug.verbose("Hero successfully loaded and return hero: " +
				// hero.getName());
			} else {
				Debug.error("Hero could not be parsed, was null after XmlParserNew.readHero.");
			}

			return hero;
		} catch (Exception e) {
			// clear last hero since loading resulted in an error
			Editor editor = preferences.edit();
			editor.remove(DsaTabActivity.PREF_LAST_HERO);
			editor.commit();
			exception = e;
			Debug.error(e);
			return null;
		} finally {
			Util.close(fis);
			Util.close(fisConfig);
		}
	}

	public Exception getException() {
		return exception;
	}

}
