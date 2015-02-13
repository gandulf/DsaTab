package com.dsatab.fragment.dialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import uk.me.lewisdeane.ldialogs.CustomDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
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

public class ImageChooserDialog extends DialogFragment implements AdapterView.OnItemClickListener {

	public static final String TAG = "ImageChooserDialog";

	private OnImageSelectedListener imageSelectedListener;

	private GridView list;
	private PortraitAdapter adapter;

	private ScaleType scaleType = ScaleType.CENTER_CROP;
	private int columnWidth;
	private int columnHeight;

	private Uri imageUri;

	private List<Uri> imageUris = new ArrayList<Uri>();

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

	public static void pickPortrait(Fragment parent, File dir, final OnImageSelectedListener imageSelectedListener,
			int requestCode) {
		ImageChooserDialog dialog = new ImageChooserDialog();

		Bundle args = new Bundle();
		// TODO value should be set as argument
		File[] files = dir.listFiles(new FileFileFilter());
		if (files != null) {
			for (File file : files) {
				dialog.imageUris.add(Uri.fromFile(file));
			}
		}

		if (dialog.imageUris.isEmpty()) {
			String path = dir.getAbsolutePath();
			Toast.makeText(
					parent.getActivity(),
					"Keine Bilder gefunden. Kopiere deine eigenen auf deine SD-Karte unter \"" + path
							+ "\" oder lade die Standardportraits in den Einstellungen herunter.", Toast.LENGTH_LONG)
					.show();
			return;
		} else {
			dialog.imageSelectedListener = imageSelectedListener;

			dialog.setGridColumnHeight(DsaTabApplication.getInstance().getResources()
					.getDimensionPixelSize(R.dimen.portrait_height_small));
			dialog.setGridColumnWidth(DsaTabApplication.getInstance().getResources()
					.getDimensionPixelSize(R.dimen.portrait_width_small));

			dialog.setArguments(args);
			dialog.setTargetFragment(parent, requestCode);
			dialog.show(parent.getFragmentManager(), TAG);
		}
	}

	public static boolean hasPortraits() {
		return hasFiles(DsaTabApplication.getDirectory(DsaTabApplication.DIR_PORTRAITS));
	}

	public static void pickIcons(Fragment parent, FragmentManager fragmentManager,
			final OnImageSelectedListener imageSelectedListener, int requestCode) {
		ImageChooserDialog dialog = new ImageChooserDialog();

		List<Integer> itemIcons = DsaTabApplication.getInstance().getConfiguration().getDsaIcons();
		dialog.setImageIds(itemIcons);
		dialog.setGridColumnWidth(DsaTabApplication.getInstance().getResources()
				.getDimensionPixelSize(R.dimen.icon_button_size));
		dialog.setScaleType(ScaleType.FIT_CENTER);
		dialog.setImageSelectedListener(imageSelectedListener);

		if (parent != null) {
			dialog.setTargetFragment(parent, 0);
		}
		dialog.show(fragmentManager, ImageChooserDialog.TAG);
	}

	public static void pickIcons(Fragment parent, final OnImageSelectedListener imageSelectedListener, int requestCode) {
		pickIcons(parent, parent.getFragmentManager(), imageSelectedListener, requestCode);
	}

	public static void pickPortrait(Fragment parent, final AbstractBeing being, int requestCode) {

		OnImageSelectedListener imageSelectedListener = new OnImageSelectedListener() {
			@Override
			public void onImageSelected(Uri imageUri) {
				being.setPortraitUri(imageUri);
			}
		};

		pickPortrait(parent, DsaTabApplication.getDirectory(DsaTabApplication.DIR_PORTRAITS), imageSelectedListener,
				requestCode);
	}

	public void setImageIds(List<Integer> imageIds) {
		imageUris.clear();
		for (Integer resId : imageIds) {
			imageUris.add(Util.getUriForResourceId(resId));
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
		builder.setDarkTheme(DsaTabApplication.getInstance().isDarkTheme());
		builder.setListTitleStyle(true);

		View popupcontent = builder.setView(R.layout.popup_portrait_chooser);

		list = (GridView) popupcontent.findViewById(R.id.popup_portrait_chooser_list);
		adapter = new PortraitAdapter(builder.getContext());
		adapter.setMinHeight(columnHeight);
		adapter.setScaleType(scaleType);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
		if (columnWidth != 0)
			list.setColumnWidth(columnWidth);

		builder.setTitle("Wähle ein Bild...");

		for (Uri uri : imageUris) {
			adapter.add(uri);
		}

		CustomDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(true);
		return dialog;

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		imageUri = adapter.getItem(position);
		if (imageUri != null && imageSelectedListener != null) {
			imageSelectedListener.onImageSelected(imageUri);
		}
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

	public void setImageSelectedListener(OnImageSelectedListener imageSelectedListener) {
		this.imageSelectedListener = imageSelectedListener;
	}

	public void setGridColumnWidth(int width) {
		this.columnWidth = width;
		if (list != null) {
			list.setColumnWidth(width);
		}
	}

	public void setGridColumnHeight(int height) {
		this.columnHeight = height;
	}

	static class PortraitAdapter extends ArrayAdapter<Uri> {

		private ScaleType scaleType;

		private int minHeight;

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

		public int getMinHeight() {
			return minHeight;
		}

		public void setMinHeight(int minHeight) {
			this.minHeight = minHeight;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ImageView tv = null;
			if (convertView instanceof ImageView) {
				tv = (ImageView) convertView;
			} else {
				tv = new ImageView(getContext());
				tv.setScaleType(scaleType);
				tv.setMinimumHeight(minHeight);
			}

			Uri file = getItem(position);
			tv.setImageURI(file);

			return tv;
		}
	}

}
