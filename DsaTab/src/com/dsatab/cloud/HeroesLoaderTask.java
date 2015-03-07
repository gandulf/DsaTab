package com.dsatab.cloud;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.dsatab.DsaTabApplication;
import com.dsatab.data.HeroFileInfo;
import com.gandulf.guilib.util.FileFileFilter;

public class HeroesLoaderTask extends AsyncTaskLoader<List<HeroFileInfo>> {

	private List<Exception> exception;

	private List<HeroFileInfo> fileInfos;

	public HeroesLoaderTask(Context context) {
		super(context);
		this.exception = new ArrayList<Exception>();
	}

	/**
	 * Handles a request to start the Loader.
	 */
	@Override
	protected void onStartLoading() {
		if (fileInfos != null) {
			// If we currently have a result available, deliver it
			// immediately.
			deliverResult(fileInfos);
		}

		if (takeContentChanged() || fileInfos == null) {
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
	public List<HeroFileInfo> loadInBackground() {
		exception.clear();
		try {
			File heroesDir = DsaTabApplication.getInternalHeroDirectory();

			File[] files = heroesDir.listFiles(new FileFileFilter());

			List<HeroFileInfo> fileInfos = new ArrayList<HeroFileInfo>();
			for (File file : files) {
				if (file.getName().toLowerCase(Locale.GERMAN).endsWith(HeroFileInfo.HERO_FILE_EXTENSION)) {
					HeroFileInfo heroFileInfo = new HeroFileInfo(file, null, DsaTabApplication.getInstance()
							.getExchange());

					int index = fileInfos.indexOf(heroFileInfo);
					if (index >= 0) {
						HeroFileInfo info = fileInfos.get(index);
						info.merge(heroFileInfo);
					} else {
						fileInfos.add(heroFileInfo);
					}
				}
			}
			return fileInfos;
		} catch (Exception e) {
			exception.add(e);
			return Collections.emptyList();
		}
	}

	public List<Exception> getExceptions() {
		return exception;
	}

}
