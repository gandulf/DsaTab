package com.dsatab.util;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore.Images.ImageColumns;
import android.provider.MediaStore.Images.Media;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;

public class PhotoPicker {

	static int MAX_WIDTH = 600;
	static int MAX_HEIGHT = 800;

	Uri uri;
	ContentResolver resolver;
	String path;
	Matrix orientation;
	int storedHeight;
	int storedWidth;

	public PhotoPicker(Uri uri, ContentResolver resolver) {
		this.uri = uri;
		this.resolver = resolver;
	}

	private boolean getInformation() throws IOException {
		if (getInformationFromMediaDatabase())
			return true;

		if (getInformationFromFileSystem())
			return true;

		return false;
	}

	/* Support for gallery apps and remote ("picasa") images */
	private boolean getInformationFromMediaDatabase() {
		String[] fields = { Media.DATA, ImageColumns.ORIENTATION };
		Cursor cursor = resolver.query(uri, fields, null, null, null);

		if (cursor == null)
			return false;

		cursor.moveToFirst();
		path = cursor.getString(cursor.getColumnIndex(Media.DATA));
		int orientation = cursor.getInt(cursor.getColumnIndex(ImageColumns.ORIENTATION));
		this.orientation = new Matrix();
		this.orientation.setRotate(orientation);
		cursor.close();

		return true;
	}

	/* Support for file managers and dropbox */
	private boolean getInformationFromFileSystem() throws IOException {
		path = uri.getPath();

		if (path == null)
			return false;

		ExifInterface exif = new ExifInterface(path);
		int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

		this.orientation = new Matrix();
		switch (orientation) {
		case ExifInterface.ORIENTATION_NORMAL:
			/* Identity matrix */
			break;
		case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
			this.orientation.setScale(-1, 1);
			break;
		case ExifInterface.ORIENTATION_ROTATE_180:
			this.orientation.setRotate(180);
			break;
		case ExifInterface.ORIENTATION_FLIP_VERTICAL:
			this.orientation.setScale(1, -1);
			break;
		case ExifInterface.ORIENTATION_TRANSPOSE:
			this.orientation.setRotate(90);
			this.orientation.postScale(-1, 1);
			break;
		case ExifInterface.ORIENTATION_ROTATE_90:
			this.orientation.setRotate(90);
			break;
		case ExifInterface.ORIENTATION_TRANSVERSE:
			this.orientation.setRotate(-90);
			this.orientation.postScale(-1, 1);
			break;
		case ExifInterface.ORIENTATION_ROTATE_270:
			this.orientation.setRotate(-90);
			break;
		}

		return true;
	}

	private boolean getStoredDimensions() throws IOException {
		InputStream input = resolver.openInputStream(uri);
		Options options = new Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(resolver.openInputStream(uri), null, options);

		/*
		 * The input stream could be reset instead of closed and reopened if it were possible to reliably wrap the input
		 * stream on a buffered stream, but it's not possible because decodeStream() places an upper read limit of 1024
		 * bytes for a reset to be made (it calls mark(1024) on the stream).
		 */
		input.close();

		if (options.outHeight <= 0 || options.outWidth <= 0)
			return false;

		storedHeight = options.outHeight;
		storedWidth = options.outWidth;

		return true;
	}

	public Uri getUri() throws IOException {
		if (!getInformation())
			throw new FileNotFoundException();

		if (!getStoredDimensions())
			throw new InvalidObjectException(null);

		return uri;
	}

	public Bitmap getBitmap() throws IOException {
		if (!getInformation())
			throw new FileNotFoundException();

		if (!getStoredDimensions())
			throw new InvalidObjectException(null);

		RectF rect = new RectF(0, 0, storedWidth, storedHeight);
		orientation.mapRect(rect);
		int width = (int) rect.width();
		int height = (int) rect.height();
		int subSample = 1;

		while (width > MAX_WIDTH || height > MAX_HEIGHT) {
			width /= 2;
			height /= 2;
			subSample *= 2;
		}

		if (width == 0 || height == 0)
			throw new InvalidObjectException(null);

		Options options = new Options();
		options.inSampleSize = subSample;
		Bitmap subSampled = BitmapFactory.decodeStream(resolver.openInputStream(uri), null, options);

		Bitmap picture;
		if (!orientation.isIdentity()) {
			picture = Bitmap.createBitmap(subSampled, 0, 0, options.outWidth, options.outHeight, orientation, false);
			subSampled.recycle();
		} else
			picture = subSampled;

		return picture;
	}

}
