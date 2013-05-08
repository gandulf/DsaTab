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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.widget.Toast;

import com.bugsense.trace.BugSenseHandler;
import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.cloud.HeroExchange.OnHeroExchangeListener;
import com.dsatab.data.HeroFileInfo;
import com.dsatab.util.Debug;

/**
 * @author Ganymede
 * 
 */
public class ImportHeroTaskNew extends AsyncTask<String, String, Integer> implements OnCancelListener {

	private ProgressDialog progressDialog;

	private Exception caughtException = null;

	private String token = null;

	private HeroFileInfo heroInfo;
	private File heroFile = null;

	private Context context;

	private OnHeroExchangeListener onHeroExchangeListener;

	/**
		 * 
		 */
	public ImportHeroTaskNew(Context context, HeroFileInfo heroInfo, String token) {
		this.context = context;
		this.heroInfo = heroInfo;
		this.token = token;
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

		progressDialog = ProgressDialog.show(context, "Held importieren",
				"Daten werden von Helden-Austausch Server geladen");

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
		// Create a directory in the SDCard to store the files
		File baseDir = new File(DsaTabApplication.getDsaTabPath());
		if (!baseDir.exists()) {
			baseDir.mkdirs();
		}

		Helper.disableSSLCheck(); // nur für die Testphase, bis ein gültiges
									// Zertifikate vorhanden ist

		BufferedWriter bufferedOutputStream = null;
		try {
			publishProgress("Verbinde mit Server...");

			String stringheld = Helper.postRequest(token, "action", "returnheld", "format", "heldenxml", "heldenid",
					heroInfo.getId());

			if (heroInfo.getFile() == null) {
				heroFile = new File(baseDir, heroInfo.getId() + ".xml");
			} else {
				heroFile = heroInfo.getFile();
			}

			// Create a file output stream
			bufferedOutputStream = new BufferedWriter(new FileWriter(heroFile.getAbsolutePath()));
			bufferedOutputStream.write(stringheld);

			// Flush and close the buffers
			bufferedOutputStream.flush();

		} catch (Exception e) {
			Debug.error(e);
			caughtException = e;
			return HeroExchange.RESULT_ERROR;
		} finally {
			if (bufferedOutputStream != null) {
				try {
					bufferedOutputStream.close();
				} catch (IOException e) {
				}
			}
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
			if (heroFile != null && heroFile.isFile() && heroFile.getName().endsWith(".xml")) {
				if (onHeroExchangeListener != null) {
					onHeroExchangeListener.onHeroLoaded(heroFile.getAbsolutePath());
				} else {
					Toast.makeText(context, "Held erfolgreich importiert.", Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(
						context,
						"Konnte Heldendatei nicht öffnen, ungültige Datei "
								+ (heroFile != null ? heroFile.getName() : ""), Toast.LENGTH_SHORT).show();
			}

			break;
		case HeroExchange.RESULT_CANCELED:
			Toast.makeText(context, R.string.download_canceled, Toast.LENGTH_SHORT).show();
			break;
		case HeroExchange.RESULT_EMPTY:
			Toast.makeText(context, "Konnte keine Heldendatei am Helden-Austausch Server finden.", Toast.LENGTH_SHORT)
					.show();
			break;
		case HeroExchange.RESULT_ERROR:
			if (caughtException instanceof AuthorizationException) {
				Toast.makeText(
						context,
						"Token ungültig. Überprüfe ob das Token mit dem in der Helden-Software erstelltem Zugangstoken übereinstimmt.",
						Toast.LENGTH_SHORT).show();
			} else if (caughtException instanceof IOException) {
				Toast.makeText(context, "Konnte keine Verbindung zum Austausch Server herstellen.", Toast.LENGTH_SHORT)
						.show();
			} else {
				Toast.makeText(context, R.string.download_error, Toast.LENGTH_SHORT).show();
				Debug.error(caughtException);
				BugSenseHandler.sendException(caughtException);
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
	 * @see
	 * android.content.DialogInterface.OnCancelListener#onCancel(android.content
	 * .DialogInterface)
	 */
	@Override
	public void onCancel(DialogInterface dialog) {
		cancel(true);
	}

}
