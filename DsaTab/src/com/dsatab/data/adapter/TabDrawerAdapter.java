package com.dsatab.data.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dsatab.R;
import com.dsatab.TabInfo;
import com.dsatab.data.adapter.TabDrawerAdapter.DrawerItem;
import com.dsatab.util.Util;
import com.gandulf.guilib.data.OpenArrayAdapter;

public class TabDrawerAdapter extends OpenArrayAdapter<DrawerItem> {

	private static final int TYPE_HEADER = 0;
	private static final int TYPE_TAB = 1;

	private LayoutInflater inflater;

	public static class DrawerItem {
		int id;
		String text;
		Uri image;
		int imageId;

		public DrawerItem(String text) {
			this(-1, text, 0);
		}

		public DrawerItem(int id, String text, Uri image) {
			this.id = id;
			this.text = text;
			this.image = image;
		}

		public DrawerItem(int id, String text, int imageResourceID) {
			this.id = id;
			this.text = text;
			this.imageId = imageResourceID;
		}

		public DrawerItem(TabInfo tabInfo) {
			text = tabInfo.getTitle();
			image = tabInfo.getIconUri();
			id = tabInfo.getId().hashCode();
		}

		public int getId() {
			return id;
		}

		public String getText() {
			return text;
		}

		public Uri getImage() {
			return image;
		}

		public int getImageId() {
			return imageId;
		}

	}

	public TabDrawerAdapter(Context context, List<DrawerItem> objects) {
		super(context, 0, objects);

		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getItemViewType(int position) {
		DrawerItem tabInfo = getItem(position);

		if (tabInfo.getId() == -1) {
			return TYPE_HEADER;
		} else {
			return TYPE_TAB;
		}
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		DrawerItem tabInfo = getItem(position);

		if (convertView == null) {
			switch (getItemViewType(position)) {
			case TYPE_HEADER:
				convertView = inflater.inflate(R.layout.item_listitem_header, parent, false);
				break;
			case TYPE_TAB:
				convertView = inflater.inflate(R.layout.list_item_icon_text, parent, false);
				break;
			}

		}

		switch (getItemViewType(position)) {
		case TYPE_HEADER: {
			TextView textView = (TextView) convertView.findViewById(android.R.id.text1);
			textView.setText(tabInfo.text);
			break;
		}
		case TYPE_TAB: {
			TextView textView = (TextView) convertView.findViewById(android.R.id.text1);
			ImageView imge = (ImageView) convertView.findViewById(android.R.id.icon);
			textView.setText(tabInfo.text);

			if (tabInfo.image != null) {
				Drawable d = Util.getDrawableByUri(tabInfo.image);
				imge.setImageDrawable(d);
			} else {
				imge.setImageResource(tabInfo.imageId);
			}
			break;
		}
		}

		return convertView;
	}

}
