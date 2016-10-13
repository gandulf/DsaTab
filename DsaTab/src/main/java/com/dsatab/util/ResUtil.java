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
package com.dsatab.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

/**
 * Utility class to bundle resource helper methods
 * 
 */
public class ResUtil {

	public static String loadResourceIdToString(int resId, Resources res) {

		try {
			InputStream is = res.openRawResource(resId);

			byte[] buffer = new byte[4096];
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			while (true) {
				int read = is.read(buffer);
				if (read == -1) {
					break;
				}
				baos.write(buffer, 0, read);
			}

			baos.close();
			is.close();

			String data = baos.toString();

			return data;
		} catch (Exception e) {
			com.gandulf.guilib.util.Debug.error(e);
			return null;
		}

	}

	public static String loadAssestToString(String fileName, Context ctx) {

		try {
			InputStream is = ctx.getAssets().open(fileName);

			byte[] buffer = new byte[4096];
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			while (true) {
				int read = is.read(buffer);

				if (read == -1) {
					break;
				}

				baos.write(buffer, 0, read);
			}

			baos.close();
			is.close();

			String data = baos.toString();

			return data;
		} catch (Exception e) {
			com.gandulf.guilib.util.Debug.error(e);
			return null;
		}

	}

	public static Drawable getDrawableByUri(Context context, Uri mUri) {
		Drawable d = null;
		if (mUri != null) {
			String scheme = mUri.getScheme();

			if (ContentResolver.SCHEME_ANDROID_RESOURCE.equals(scheme)) {
				try {
					// Load drawable through Resources, to get the source density information
					OpenResourceIdResult resourceId = getResourceId(context, mUri);
                    d = VectorDrawableCompat.create(context.getResources(),resourceId.id,context.getTheme());
				} catch (Exception e) {
					com.gandulf.guilib.util.Debug.warning("Unable to open content: " + mUri, e);
				}
			} else if (ContentResolver.SCHEME_CONTENT.equals(scheme) || ContentResolver.SCHEME_FILE.equals(scheme)) {
				try {
					String uri = mUri.toString();
					if (uri.startsWith("file:/") && !uri.startsWith("file:///")) {
						uri = uri.replace("file:/", "file:///");
					}

					d = Drawable.createFromStream(context.getContentResolver().openInputStream(Uri.parse(uri)), null);
				} catch (Exception e) {
					com.gandulf.guilib.util.Debug.warning("Unable to open content: " + mUri, e);
				}
			} else {
				d = Drawable.createFromPath(mUri.toString());
			}

			if (d == null) {
				com.gandulf.guilib.util.Debug.verbose("resolveUri failed on bad uri: " + mUri);
			}
		}

		return d;

	}

	/**
	 * A resource identified by the {@link Resources} that contains it, and a resource id.
	 * 
	 * @hide
	 */
	protected static class OpenResourceIdResult {
		public Resources r;
		public int id;
	}

	/**
	 * Resolves an android.resource URI to a {@link Resources} and a resource id.
	 * 
	 * @hide
	 */
	protected static OpenResourceIdResult getResourceId(Context mContext, Uri uri) throws FileNotFoundException {
		String authority = uri.getAuthority();
		Resources r;
		if (TextUtils.isEmpty(authority)) {
			throw new FileNotFoundException("No authority: " + uri);
		} else {
			try {
				r = mContext.getPackageManager().getResourcesForApplication(authority);
			} catch (NameNotFoundException ex) {
				throw new FileNotFoundException("No package found for authority: " + uri);
			}
		}
		List<String> path = uri.getPathSegments();
		if (path == null) {
			throw new FileNotFoundException("No path: " + uri);
		}
		int len = path.size();
		int id;
		if (len == 1) {
			try {
				id = Integer.parseInt(path.get(0));
			} catch (NumberFormatException e) {
				throw new FileNotFoundException("Single path segment is not a resource ID: " + uri);
			}
		} else if (len == 2) {
			id = r.getIdentifier(path.get(1), path.get(0), authority);
		} else {
			throw new FileNotFoundException("More than two path segments: " + uri);
		}
		if (id == 0) {
			throw new FileNotFoundException("No resource found for: " + uri);
		}
		OpenResourceIdResult res = new OpenResourceIdResult();
		res.r = r;
		res.id = id;
		return res;
	}
}
