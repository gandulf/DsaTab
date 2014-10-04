package com.dsatab.cloud;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.AsyncTaskLoader;

import com.dsatab.data.HeroFileInfo;
import com.dsatab.data.HeroFileInfo.StorageType;

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

			try {
				heroes.addAll(exchange.getHeroes(StorageType.FileSystem));
			} catch (Exception e) {
				exception.add(e);
			}
			try {
				heroes.addAll(exchange.getHeroes(StorageType.Dropbox));
			} catch (Exception e) {
				exception.add(e);
			}
			try {
				heroes.addAll(exchange.getHeroes(StorageType.HeldenAustausch));
			} catch (Exception e) {
				exception.add(e);
			}

			List<HeroFileInfo> fileInfos = new ArrayList<HeroFileInfo>();
			for (HeroFileInfo fileInfo : heroes) {
				int index = fileInfos.indexOf(fileInfo);
				if (index >= 0) {
					HeroFileInfo info = fileInfos.get(index);
					info.merge(fileInfo);
				} else {
					fileInfos.add(fileInfo);
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
