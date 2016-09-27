package com.dsatab.cloud;

import android.app.Activity;
import android.content.AsyncTaskLoader;

import com.dsatab.cloud.HeroExchange.StorageType;
import com.dsatab.data.HeroFileInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HeroesSyncTask extends AsyncTaskLoader<List<HeroFileInfo>> {

	private List<Exception> exception;

	private HeroExchange exchange;

	private List<HeroFileInfo> fileInfos;

	public HeroesSyncTask(Activity context, HeroExchange exchange) {
		super(context);
		this.exchange = exchange;
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
			List<HeroFileInfo> heroes = new ArrayList<HeroFileInfo>();
			for (StorageType type : HeroExchange.storageTypes) {
				try {
                    HeroFileInfo.merge(heroes, exchange.getHeroes(type));
				} catch (Exception e) {
					exception.add(e);
				}
			}
			return heroes;
		} catch (Exception e) {
			exception.add(e);
			return Collections.emptyList();
		}
	}

	public List<Exception> getExceptions() {
		return exception;
	}

}
