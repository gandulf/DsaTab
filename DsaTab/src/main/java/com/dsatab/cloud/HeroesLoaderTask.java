package com.dsatab.cloud;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.dsatab.DsaTabApplication;
import com.dsatab.data.HeroFileInfo;
import com.gandulf.guilib.util.FileFileFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class HeroesLoaderTask extends AsyncTaskLoader<List<HeroFileInfo>> {

	private List<Exception> exception;

	private List<HeroFileInfo> fileInfos;

    private HeroExchange heroExchange;

	public HeroesLoaderTask(Context context, HeroExchange heroExchange) {
		super(context);
		this.exception = new ArrayList<Exception>();
        this.heroExchange = heroExchange;
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
			File heroesDir = DsaTabApplication.getHeroDirectory();

			File[] files = heroesDir.listFiles(new FileFileFilter());
			List<HeroFileInfo> fileInfos = new ArrayList<HeroFileInfo>();
            if (files!=null) {
                for (File file : files) {
                    if (file.getName().toLowerCase(Locale.GERMAN).endsWith(HeroFileInfo.HERO_FILE_EXTENSION)) {
                        HeroFileInfo heroFileInfo = new HeroFileInfo(file, null, heroExchange);
                        HeroFileInfo.merge(fileInfos, heroFileInfo);
                    }
                }
            }
            this.fileInfos = fileInfos;
			return this.fileInfos;
		} catch (Exception e) {
			exception.add(e);
			return Collections.emptyList();
		}
	}

	public List<Exception> getExceptions() {
		return exception;
	}

}
