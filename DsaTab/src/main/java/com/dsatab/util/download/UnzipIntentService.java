package com.dsatab.util.download;

import android.app.DownloadManager;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;
import android.util.Log;

import com.dsatab.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnzipIntentService extends JobIntentService {

    private static final String TAG="Downloader";

	public static final String INTENT_DOWNLOAD_ID = "downloadId";
	public static final String INTENT_OUTPUT_URI = "outputURI";

	public static final int UNZIP_ID = 1;

	public static final String ACTION_UNZIP_COMPLETE = "com.dsatab.intent.action.ACTION_UNZIP_COMPLETE";
	public static final String INTENT_RESULT = "result";

	public static final int RESULT_OK = 1;
	public static final int RESULT_ERROR = 2;
	public static final int RESULT_CANCELED = 3;

	public UnzipIntentService() {

	}
    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, UnzipIntentService.class, UNZIP_ID, work);
    }

	public static int unzip(Context context, long downloadId, Uri outputURI) {

		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(), 0);

		DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
		NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);
		notificationBuilder.setSmallIcon(android.R.drawable.stat_sys_download);
		notificationBuilder.setContentTitle("Unpacking package");
		notificationBuilder.setChannelId(NotificationChannel.DEFAULT_CHANNEL_ID);
		notificationBuilder.setWhen(System.currentTimeMillis());
		notificationBuilder.setContentIntent(contentIntent);

		int result = RESULT_OK;
		File baseDir = null;
		if (outputURI != null && downloadId != -1) {
			// Create a directory in the SDCard to store the files
			baseDir = new File(outputURI.getPath());
			if (!baseDir.exists()) {
				baseDir.mkdirs();
			}

			ZipInputStream inputStream = null;
			try {

				DownloadManager.Query q = new DownloadManager.Query();
				q.setFilterById(downloadId);
				Cursor c = downloadManager.query(q);
				String title = "Unpacking ...";
				int totalSize = 0;
				if (c.moveToFirst()) {
					int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
					if (status == DownloadManager.STATUS_SUCCESSFUL) {
						// process download
						title = c.getString(c.getColumnIndex(DownloadManager.COLUMN_TITLE));

						totalSize = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
						// get other required data by changing the constant passed to getColumnIndex
					}
				}
				c.close();

				// Open the ZipInputStream
				ParcelFileDescriptor pfd = downloadManager.openDownloadedFile(downloadId);

				inputStream = new ZipInputStream(new ParcelFileDescriptor.AutoCloseInputStream(pfd));
				int bitsread = 0;
				// Loop through all the files and folders
				for (ZipEntry entry = inputStream.getNextEntry(); entry != null; entry = inputStream.getNextEntry()) {

					bitsread += entry.getCompressedSize();

					notificationBuilder.setChannelId(NotificationChannel.DEFAULT_CHANNEL_ID);
					notificationBuilder.setContentTitle(title);
					notificationBuilder.setContentText(entry.getName());
					notificationBuilder.setProgress(totalSize, bitsread, true);

					notificationManager.notify(UNZIP_ID, notificationBuilder.build());

					Log.d(TAG, "Extracting: " + entry.getName() + "...");

					File innerFile = new File(baseDir, entry.getName());
					// if (innerFile.exists()) {
					// innerFile.delete();
					// }

					// Check if it is a folder
					if (entry.isDirectory()) {
						// Its a folder, create that folder
						innerFile.mkdirs();
					} else {
						// Create a file output stream
						BufferedOutputStream bufferedOutputStream = null;
						try {
							if (!innerFile.getParentFile().canWrite()) {
								result = RESULT_ERROR;
								break;
							}
							FileOutputStream outputStream = new FileOutputStream(innerFile.getAbsolutePath());
							final int BUFFER = 2048;

							// Buffer the output to the file
							bufferedOutputStream = new BufferedOutputStream(outputStream, BUFFER);

							// Write the contents
							int count = 0;
							byte[] data = new byte[BUFFER];
							while ((count = inputStream.read(data, 0, BUFFER)) != -1) {
								bufferedOutputStream.write(data, 0, count);
							}

							// Flush and close the buffers
							bufferedOutputStream.flush();
							bufferedOutputStream.close();

						} finally {
							if (bufferedOutputStream != null)
								bufferedOutputStream.close();
						}
					}

					// Close the current entry
					inputStream.closeEntry();
				}
				inputStream.close();
			} catch (Exception e) {
				Log.e(TAG,e.getLocalizedMessage(), e);
				result = RESULT_ERROR;
			} finally {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException e) {
					}
				}
			}

		} else {
			result = RESULT_CANCELED;
		}

		switch (result) {
		case RESULT_OK:
            notificationBuilder.setChannelId(NotificationChannel.DEFAULT_CHANNEL_ID);
			notificationBuilder.setContentTitle("Unpacking completed");
			notificationBuilder.setContentText(context.getString(R.string.download_finished));
			notificationBuilder.setSmallIcon(android.R.drawable.stat_sys_download_done);
			notificationBuilder.setProgress(100, 100, false);
			notificationManager.notify(UNZIP_ID, notificationBuilder.build());
			notificationManager.cancel(UNZIP_ID);

			MediaScannerWrapper wrapper = new MediaScannerWrapper(context.getApplicationContext(),
					baseDir.getAbsolutePath(), "image/*");
			wrapper.scan();

			break;
		case RESULT_CANCELED:
			notificationManager.cancel(UNZIP_ID);
			break;
		case RESULT_ERROR:
            notificationBuilder.setChannelId(NotificationChannel.DEFAULT_CHANNEL_ID);
			notificationBuilder.setContentTitle("Unpacking failed");
			notificationBuilder.setContentText(context.getString(R.string.download_error));
			notificationBuilder.setSmallIcon(android.R.drawable.stat_sys_warning);
			notificationBuilder.setProgress(100, 100, false);
			notificationManager.notify(UNZIP_ID, notificationBuilder.build());
			break;
		}

		return result;
	}

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        long downloadId = intent.getLongExtra(INTENT_DOWNLOAD_ID, -1);
        String outputURIExtra = intent.getStringExtra(INTENT_OUTPUT_URI);
        int result;
        if (!TextUtils.isEmpty(outputURIExtra)) {
            Uri outputURI = Uri.parse(outputURIExtra);
            result = unzip(this, downloadId, outputURI);
        } else {
            result = RESULT_CANCELED;
        }
        Intent broadcastIntent = new Intent(ACTION_UNZIP_COMPLETE);
        broadcastIntent.putExtra(INTENT_RESULT, result);
        sendBroadcast(broadcastIntent);
    }



    static class MediaScannerWrapper implements MediaScannerConnection.MediaScannerConnectionClient {

        private MediaScannerConnection mConnection;
        private String mPath;
        private String mMimeType;

        // filePath - where to scan;
        // mime type of media to scan i.e. "image/jpeg".
        // use "*/*" for any media
        public MediaScannerWrapper(Context ctx, String filePath, String mime) {
            mPath = filePath;
            mMimeType = mime;
            mConnection = new MediaScannerConnection(ctx, this);
        }

        // do the scanning
        public void scan() {
            mConnection.connect();
        }

        // start the scan when scanner is ready
        public void onMediaScannerConnected() {
            mConnection.scanFile(mPath, mMimeType);
        }

        public void onScanCompleted(String path, Uri uri) {
            mConnection.disconnect();
        }
    }
}
