package com.dsatab.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v4.content.AsyncTaskLoader;

import com.bugsense.trace.BugSenseHandler;
import com.dsatab.DsaTabApplication;
import com.dsatab.activity.DsaTabActivity;
import com.dsatab.util.Debug;
import com.dsatab.xml.HeldenXmlParser;

public class HeroLoaderTask extends AsyncTaskLoader<Hero> {

	private Hero hero;

	private String path;

	private Exception exception;

	public HeroLoaderTask(Context context, String heroPath) {
		super(context);
		this.path = heroPath;

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
	 * This is where the bulk of our work is done. This function is called in a background thread and should generate a new set of data to be published by the
	 * loader.
	 */
	@Override
	public Hero loadInBackground() {
		exception = null;

		// Debug.verbose("Getting hero from " + path);
		if (path == null) {
			Debug.error("Error: Path was null ");
			return null;
		}

		FileInputStream fis = null;
		SharedPreferences preferences = DsaTabApplication.getPreferences();

		try {
			File file = new File(path);
			if (!file.exists()) {
				Debug.error("Error: Hero file not found at " + file.getAbsolutePath());
				throw new FileNotFoundException(file.getAbsolutePath());
			}

			fis = new FileInputStream(file);
			hero = HeldenXmlParser.readHero(getContext(), path, fis);
			if (hero != null) {
				// Debug.verbose("Hero successfully parsed");

				Editor editor = preferences.edit();
				editor.putString(DsaTabActivity.PREF_LAST_HERO, hero.getPath());
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
			BugSenseHandler.sendException(e);
			return null;
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					Debug.error(e);
				}
			}

		}
	}

	public Exception getException() {
		return exception;
	}

}
