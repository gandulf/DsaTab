/*
 * Copyright (C) 2010 Gandulf Kohlweiss
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dsatab.util.download;

import android.app.DownloadManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

public class DownloadBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG="Downloader";

	public static final int UNZIP_ID = 1;

	private String basePath;

    public DownloadBroadcastReceiver() {

    }
	public DownloadBroadcastReceiver(String basePath) {
		this.basePath = basePath;
	}

	private void notify(Context context, String message) {

		NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
		builder.setSmallIcon(android.R.drawable.stat_sys_download);
		builder.setContentTitle("Unpacking package");
		builder.setAutoCancel(true);
		builder.setContentTitle("DsaTab Download");
		builder.setContentText(message);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(), 0);
		builder.setContentIntent(contentIntent);

		notificationManager.notify(UNZIP_ID, builder.build());
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		String action = intent.getAction();

		if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {

			long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

			if (downloadId >= 0 && Downloader.todoUnzip.contains(downloadId)) {

				DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

				Log.d(TAG, "Received download completed " + downloadId);

				DownloadManager.Query query = new DownloadManager.Query();
				query.setFilterById(downloadId);
				Cursor cursor = downloadManager.query(query);

				if (cursor != null) {
					if (cursor.moveToFirst()) {
						int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
						int status = cursor.getInt(columnIndex);
						int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
						int reason = cursor.getInt(columnReason);

						if (status == DownloadManager.STATUS_SUCCESSFUL) {
							Intent serviceIntent = new Intent(context, UnzipIntentService.class);
							serviceIntent.putExtra(UnzipIntentService.INTENT_DOWNLOAD_ID, downloadId);
							serviceIntent.putExtra(UnzipIntentService.INTENT_OUTPUT_URI, basePath);
							context.startService(serviceIntent);
							Downloader.todoUnzip.remove(downloadId);
						} else if (status == DownloadManager.STATUS_FAILED) {
							notify(context, "Fehler:\n" + reason);
						} else if (status == DownloadManager.STATUS_PAUSED) {
							notify(context, "Pausiert:\n" + reason);
						} else if (status == DownloadManager.STATUS_PENDING) {
							notify(context, "Pending!");
						} else if (status == DownloadManager.STATUS_RUNNING) {
							notify(context, "Läuft!");
						}
					}

					cursor.close();
				}

			}
		}
	}

}