package com.dsatab.view.dialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.data.AbstractBeing;
import com.dsatab.util.Util;
import com.gandulf.guilib.util.FileFileFilter;

public class ImageChooserDialog extends AlertDialog implements AdapterView.OnItemClickListener {

	private GridView list;
	private PortraitAdapter adapter;

	private ScaleType scaleType = ScaleType.CENTER_CROP;

	private Uri imageUri;

	public static interface OnImageSelectedListener {
		public void onImageSelected(Uri imageUri);
	}

	public static boolean hasFiles(File dir) {
		File[] files = dir.listFiles(new FileFileFilter());
		if (files != null && files.length > 0) {
			return true;
		}
		return false;
	}

	public static boolean pickFile(File dir, Context context, final OnImageSelectedListener imageSelectedListener) {
		final ImageChooserDialog pdialog = new ImageChooserDialog(context);

		File[] files = dir.listFiles(new FileFileFilter());
		List<Uri> portraitPaths = null;
		if (files != null) {
			portraitPaths = new ArrayList<Uri>(files.length);
			for (File file : files) {
				portraitPaths.add(Uri.fromFile(file));
			}
		}

		if (portraitPaths == null || portraitPaths.isEmpty()) {
			String path = dir.getAbsolutePath();
			Toast.makeText(
					context,
					"Keine Bilder gefunden. Kopiere deine eigenen auf deine SD-Karte unter \"" + path
							+ "\" oder lade die Standardportraits in den Einstellungen herunter.", Toast.LENGTH_LONG)
					.show();

			return false;
		} else {
			pdialog.setImages(portraitPaths);
			pdialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface dialog) {
					if (pdialog.getImageUri() != null) {
						imageSelectedListener.onImageSelected(pdialog.getImageUri());
					}
				}
			});
			pdialog.show();
			return true;
		}

	}

	public static boolean hasPortraits() {
		return hasFiles(DsaTabApplication.getDirectory(DsaTabApplication.DIR_PORTRAITS));
	}

	public static boolean pickPortrait(Context context, final AbstractBeing being) {

		OnImageSelectedListener imageSelectedListener = new OnImageSelectedListener() {
			@Override
			public void onImageSelected(Uri imageUri) {
				being.setPortraitUri(imageUri);
			}
		};

		return pickFile(DsaTabApplication.getDirectory(DsaTabApplication.DIR_PORTRAITS), context, imageSelectedListener);
	}

	public ImageChooserDialog(Context context) {
		super(context);
		init();
	}

	public void setImageIds(List<Integer> imageIds) {
		adapter.clear();
		for (Integer resId : imageIds) {
			adapter.add(Util.getUriForResourceId(resId));
		}
	}

	public void setImages(List<Uri> imageUris) {
		adapter.clear();
		for (Uri uri : imageUris) {
			adapter.add(uri);
		}
	}

	private void init() {
		setTitle("Wähle ein Bild...");

		setCanceledOnTouchOutside(true);

		View popupcontent = LayoutInflater.from(getContext()).inflate(R.layout.popup_portrait_chooser, null, false);
		popupcontent.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		setView(popupcontent);

		list = (GridView) popupcontent.findViewById(R.id.popup_portrait_chooser_list);
		adapter = new PortraitAdapter(getContext());
		adapter.setScaleType(scaleType);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		imageUri = adapter.getItem(position);
		dismiss();
	}

	public Uri getImageUri() {
		return imageUri;
	}

	public ScaleType getScaleType() {
		return scaleType;
	}

	public void setScaleType(ScaleType scaleType) {
		this.scaleType = scaleType;
		if (adapter != null) {
			adapter.setScaleType(scaleType);
		}
	}

	public void setGridColumnWidth(int width) {
		list.setColumnWidth(width);
	}

	static class PortraitAdapter extends ArrayAdapter<Uri> {

		private ScaleType scaleType;

		public PortraitAdapter(Context context) {
			super(context, 0);
		}

		public PortraitAdapter(Context context, List<Uri> objects) {
			super(context, 0, objects);
		}

		public PortraitAdapter(Context context, Uri[] objects) {
			super(context, 0, objects);
		}

		public ScaleType getScaleType() {
			return scaleType;
		}

		public void setScaleType(ScaleType scaleType) {
			this.scaleType = scaleType;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ImageView tv = null;
			if (convertView instanceof ImageView) {
				tv = (ImageView) convertView;
			} else {
				tv = new ImageView(getContext());
				tv.setScaleType(scaleType);
			}

			Uri file = getItem(position);
			tv.setImageBitmap(Util.decodeBitmap(file, 200));

			return tv;
		}
	}

}
