package com.dsatab.cloud;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.widget.Toast;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.cloud.HeroExchange.OnHeroExchangeListener;
import com.dsatab.data.HeroFileInfo;
import com.dsatab.data.HeroFileInfo.FileType;
import com.dsatab.data.HeroFileInfo.StorageType;
import com.dsatab.util.Debug;
import com.dsatab.util.Util;

public class ImportHeroTaskNew extends AsyncTask<String, String, Integer> implements OnCancelListener {

	private ProgressDialog progressDialog;

	private Exception caughtException = null;

	private String token = null;

	private HeroFileInfo heroInfo;

	private Context context;

	private OnHeroExchangeListener onHeroExchangeListener;

	private HeroExchange exchange;

	/**
		 * 
		 */
	public ImportHeroTaskNew(Activity context, HeroFileInfo heroInfo, String token) {
		this.context = context;
		this.heroInfo = heroInfo;
		this.token = token;
		this.exchange = new HeroExchange(context);
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
	 * @see android.os.AsyncTask#onPreExecute()
	 */
	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		progressDialog = ProgressDialog.show(context, context.getString(R.string.title_import_heroes),
				context.getString(R.string.message_loading_data_from_server));

		progressDialog.setCancelable(true);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setOnCancelListener(this);
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
			bufferedOutputStream = new BufferedWriter(new OutputStreamWriter(exchange.getOutputStream(heroInfo,
					FileType.Hero)));
			bufferedOutputStream.write(stringheld);

			// Flush and close the buffers
			bufferedOutputStream.flush();

			exchange.closeStream(heroInfo, FileType.Hero);

			heroInfo.setStorageType(StorageType.FileSystem);
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

		if (progressDialog != null) {
			if (progressDialog.isShowing())
				progressDialog.dismiss();
			progressDialog = null;
		}

		switch (result) {
		case HeroExchange.RESULT_OK:

			String path = heroInfo.getPath(FileType.Hero);
			if (path != null && path.endsWith(".xml")) {
				if (onHeroExchangeListener != null) {
					onHeroExchangeListener.onHeroInfoLoaded(Arrays.asList(heroInfo));
				} else {
					Toast.makeText(context, R.string.message_hero_import_successful, Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(context, R.string.message_invalid_hero_file + (path != null ? path : ""),
						Toast.LENGTH_SHORT).show();
			}

			break;
		case HeroExchange.RESULT_CANCELED:
			Toast.makeText(context, R.string.download_canceled, Toast.LENGTH_SHORT).show();
			break;
		case HeroExchange.RESULT_EMPTY:
			Toast.makeText(context, R.string.message_no_heo_file_found_on_server, Toast.LENGTH_SHORT).show();
			break;
		case HeroExchange.RESULT_ERROR:
			if (caughtException instanceof AuthorizationException) {
				Toast.makeText(context, R.string.message_invalid_token_please_check, Toast.LENGTH_SHORT).show();
			} else if (caughtException instanceof IOException) {
				Toast.makeText(context, R.string.message_connection_to_server_failed, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(context, R.string.download_error, Toast.LENGTH_SHORT).show();
				Debug.error(caughtException);
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
		if (progressDialog != null)
			progressDialog.setMessage(values[0]);
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
