package com.dsatab.data.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dsatab.R;
import com.dsatab.config.TabInfo;
import com.dsatab.data.adapter.TabDrawerAdapter.DrawerItem;
import com.dsatab.util.Util;
import com.gandulf.guilib.data.OpenArrayAdapter;
import com.gandulf.guilib.util.ColorUtil;

import de.hdodenhof.circleimageview.CircleImageView;

public class TabDrawerAdapter extends OpenArrayAdapter<DrawerItem> {

	private static final int TYPE_HEADER = 0;
	private static final int TYPE_TAB = 1;
	private static final int TYPE_SYSTEM = 2;
	private static final int TYPE_PROFILE = 3;

	private LayoutInflater inflater;

	private Palette palette;

	public enum DrawerItemType {
		Header, Tab, System, Profile
	};

	public static class DrawerItem {

		int id;
		String text;
		String subText;
		Uri image;
		int imageId;
		int color;
		DrawerItemType type;

		public DrawerItem(String text) {
			this(-1, text, 0, -1, DrawerItemType.Header);
		}

		public DrawerItem(int id, String text, Uri image, int imageResourceId, int color, DrawerItemType drawerItemType) {
			this.id = id;
			this.text = text;
			this.image = image;
			this.imageId = imageResourceId;
			this.type = drawerItemType;
			this.color = color;
		}

		public DrawerItem(int id, String text, int imageResourceID, int color, DrawerItemType drawerItemType) {
			this.id = id;
			this.text = text;
			this.imageId = imageResourceID;
			this.type = drawerItemType;
			this.color = color;
		}

		public DrawerItem(int id, TabInfo tabInfo) {
			text = tabInfo.getTitle();
			image = tabInfo.getIconUri();
			this.id = id;
			type = DrawerItemType.Tab;
			color = tabInfo.getColor();
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

		public DrawerItemType getType() {
			return type;
		}

		public int getColor() {
			return color;
		}

		public String getSubText() {
			return subText;
		}

		public void setSubText(String subText) {
			this.subText = subText;
		}
	}

	public TabDrawerAdapter(Context context, List<DrawerItem> objects) {
		super(context, 0, objects);

		inflater = LayoutInflater.from(context);
	}

	public void applyPalette(Palette palette) {
		this.palette = palette;
		notifyDataSetChanged();
	}

	@Override
	public int getItemViewType(int position) {
		DrawerItem drawerItem = getItem(position);

		switch (drawerItem.getType()) {
		case Header:
			return TYPE_HEADER;
		case Tab:
			return TYPE_TAB;
		case System:
			return TYPE_SYSTEM;
		case Profile:
			return TYPE_PROFILE;
		default:
			return 0;
		}
	}

	@Override
	public int getViewTypeCount() {
		return 4;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		DrawerItem drawerInfo = getItem(position);

		final int itemViewType = getItemViewType(position);
		if (convertView == null) {

			switch (itemViewType) {
			case TYPE_HEADER:
				convertView = inflater.inflate(R.layout.item_listitem_header, parent, false);
				break;
			case TYPE_TAB:
				convertView = inflater.inflate(R.layout.list_item_icon_text, parent, false);
				break;
			case TYPE_SYSTEM:
				convertView = inflater.inflate(R.layout.list_item_text, parent, false);
				break;
			case TYPE_PROFILE:
				convertView = inflater.inflate(R.layout.list_item_profile, parent, false);
				break;
			}

		}

		switch (itemViewType) {
		case TYPE_HEADER: {
			TextView textView1 = (TextView) convertView.findViewById(android.R.id.text1);
			if (textView1 != null) {
				textView1.setText(drawerInfo.text);
			}
			TextView textView2 = (TextView) convertView.findViewById(android.R.id.text2);
			if (textView2 != null) {
				textView2.setText(drawerInfo.subText);
			}
			break;
		}
		case TYPE_PROFILE: {
			TextView textView = (TextView) convertView.findViewById(android.R.id.text1);
			TextView textView2 = (TextView) convertView.findViewById(android.R.id.text2);

			ImageView image = (ImageView) convertView.findViewById(android.R.id.icon);
			CircleImageView circleImage = (CircleImageView) convertView.findViewById(android.R.id.icon1);

			textView.setText(drawerInfo.text);
			if (textView2 != null) {
				textView2.setText(drawerInfo.subText);
			}
			image.setImageResource(R.drawable.profile_picture);
			circleImage.setImageResource(R.drawable.profile_picture);

			Util.setImage(image, drawerInfo.image, drawerInfo.imageId);
			Util.setImage(circleImage, drawerInfo.image, drawerInfo.imageId);

			if (palette != null) {
				textView.setTextColor(palette.getLightVibrantColor(Color.WHITE));
				textView2.setTextColor(palette.getLightVibrantColor(Color.GRAY));
			}
			break;
		}
		case TYPE_SYSTEM:
		case TYPE_TAB: {
			TextView textView1 = (TextView) convertView.findViewById(android.R.id.text1);
			if (textView1 != null) {
				textView1.setText(drawerInfo.text);
			}

			TextView textView2 = (TextView) convertView.findViewById(android.R.id.text2);
			if (textView2 != null) {
				textView2.setText(drawerInfo.subText);
			}

			ImageView image = (ImageView) convertView.findViewById(android.R.id.icon);
			if (image != null) {
				if (drawerInfo.image != null) {
					image.setImageURI(drawerInfo.image);
				} else {
					image.setImageResource(drawerInfo.imageId);
				}
			}

			if (drawerInfo.color != Color.TRANSPARENT && convertView.getBackground() != null) {
				convertView.getBackground().setColorFilter(
						new PorterDuffColorFilter(ColorUtil.addAlpha(150, drawerInfo.color), Mode.SRC_ATOP));
			}
			break;
		}
		}

		return convertView;
	}

}
