package com.dsatab.cloud;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.cloud.HeroExchange.OnHeroExchangeListener;
import com.dsatab.data.HeroFileInfo;
import com.dsatab.data.HeroFileInfo.FileType;
import com.dsatab.util.Debug;
import com.dsatab.util.Util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;

public class ImportHeroTask extends AsyncTask<String, String, Integer> implements OnCancelListener {

	private Exception caughtException = null;

	private String token = null;

	private HeroFileInfo heroInfo;

	private Context context;

	private OnHeroExchangeListener onHeroExchangeListener;

	private HeroExchange exchange;

	/**
		 * 
		 */
	public ImportHeroTask(Context context, HeroFileInfo heroInfo, String token) {
		this.context = context;
		this.heroInfo = heroInfo;
		this.token = token;
		this.exchange = DsaTabApplication.getInstance().getExchange();
	}

	public OnHeroExchangeListener getOnHeroExchangeListener() {
		return onHeroExchangeListener;
	}

	public void setOnHeroExchangeListener(OnHeroExchangeListener onHeroExchangeListener) {
		this.onHeroExchangeListener = onHeroExchangeListener;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Integer doInBackground(String... params) {

		boolean cancel = false;

		// nur für die Testphase, bis ein gültiges Zertifikate vorhanden ist
		Helper.disableSSLCheck();

		BufferedWriter bufferedOutputStream = null;
		try {
			publishProgress(DsaTabApplication.getInstance().getString(R.string.message_connect_to_server));

			String stringheld = Helper.postRequest(token, "action", "returnheld", "format", "heldenxml", "heldenid",
					heroInfo.getId());

			// Create a file output stream

			OutputStream out = exchange.getOutputStream(heroInfo, FileType.Hero);

			if (out == null) {
				throw new IOException("Unable to open outputstream: " + heroInfo);
			}
			bufferedOutputStream = new BufferedWriter(new OutputStreamWriter(out));
			bufferedOutputStream.write(stringheld);

			// Flush and close the buffers
			bufferedOutputStream.flush();
		} catch (Exception e) {
			Debug.error(e);
			caughtException = e;
			return HeroExchange.RESULT_ERROR;
		} finally {
			Util.close(bufferedOutputStream);
		}

		if (isCancelled() || cancel)
			return HeroExchange.RESULT_CANCELED;
		else
			return HeroExchange.RESULT_OK;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(Integer result) {

		switch (result) {
		case HeroExchange.RESULT_OK:
			File file = heroInfo.getFile(FileType.Hero);
			if (file != null) {
				if (onHeroExchangeListener != null) {
					onHeroExchangeListener.onHeroInfoLoaded(Arrays.asList(heroInfo));
				}
			} else {
				if (onHeroExchangeListener != null) {
					onHeroExchangeListener.onError(context.getString(R.string.message_invalid_hero_file), null);
				}
			}

			break;
		case HeroExchange.RESULT_CANCELED:
			break;
		case HeroExchange.RESULT_EMPTY:
			if (onHeroExchangeListener != null) {
				onHeroExchangeListener.onError(context.getString(R.string.message_no_heo_file_found_on_server), null);
			}
			break;
		case HeroExchange.RESULT_ERROR:
			if (caughtException instanceof AuthorizationException) {
				if (onHeroExchangeListener != null) {
					onHeroExchangeListener.onError(context.getString(R.string.message_invalid_token_please_check),
							caughtException);
				}
			} else if (caughtException instanceof IOException) {
				if (onHeroExchangeListener != null) {
					onHeroExchangeListener.onError(context.getString(R.string.message_connection_to_server_failed),
							caughtException);
				}
			} else {
				Debug.error(caughtException);
				if (onHeroExchangeListener != null) {
					onHeroExchangeListener.onError(context.getString(R.string.download_error), caughtException);
				}
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onProgressUpdate(Progress[])
	 */
	@Override
	protected void onProgressUpdate(String... values) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.DialogInterface.OnCancelListener#onCancel(android.content .DialogInterface)
	 */
	@Override
	public void onCancel(DialogInterface dialog) {
		cancel(true);
	}

}
