package com.dsatab.view;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
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

import com.dsatab.R;
import com.dsatab.util.Util;

public class PortraitChooserDialog extends AlertDialog implements AdapterView.OnItemClickListener {

	private GridView list;
	private PortraitAdapter adapter;

	private ScaleType scaleType = ScaleType.CENTER_CROP;

	private Uri imageUri;

	public PortraitChooserDialog(Context context) {
		super(context);
		init();
	}

	public void setImages(List<Uri> imageUris) {
		adapter.clear();
		for (Uri uri : imageUris) {
			adapter.add(uri);
		}
	}

	private void init() {
		setTitle("Wähle ein Portrait...");

		setCanceledOnTouchOutside(true);

		View popupcontent = LayoutInflater.from(getContext()).inflate(R.layout.popup_portrait_chooser, null, false);
		popupcontent.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		setView(popupcontent);

		list = (GridView) popupcontent.findViewById(R.id.popup_portrait_chooser_list);
		adapter = new PortraitAdapter(getContext());
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
	}

	class PortraitAdapter extends ArrayAdapter<Uri> {

		public PortraitAdapter(Context context) {
			super(context, 0);
		}

		public PortraitAdapter(Context context, List<Uri> objects) {
			super(context, 0, objects);
		}

		public PortraitAdapter(Context context, Uri[] objects) {
			super(context, 0, objects);
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
