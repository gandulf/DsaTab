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
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.Uri;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class Downloader {

	public static List<Long> todoUnzip = new LinkedList<>();

	private DownloadManager downloadManager;

	private BroadcastReceiver receiver;

    public static Downloader getInstance(File baseDir, Context context) {
        return new Downloader(baseDir.getAbsolutePath(), context);
    }

    Downloader(final String basePath, Context context) {

        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        receiver = new DownloadBroadcastReceiver(basePath);

        context.getApplicationContext().registerReceiver(receiver,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    public void download(String path,boolean unzip) {
        Request request = new Request(Uri.parse(path));
        if (unzip)
            todoUnzip.add(downloadManager.enqueue(request));
        else
            downloadManager.enqueue(request);
    }

    public void download(String path) {
        download(path,true);
    }

}
