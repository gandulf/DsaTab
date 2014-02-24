package com.dsatab.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dsatab.R;
import com.dsatab.data.items.Item;
import com.dsatab.data.items.ItemSpecification;
import com.dsatab.util.Debug;
import com.dsatab.util.DsaUtil;

public class ItemListItem extends CheckableRelativeLayout {

	private int textColor = 0;

	public TextView text1, text2, text3;
	public ImageView icon1, icon2;

	public ItemListItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs);
	}

	public ItemListItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
	}

	public ItemListItem(Context context) {
		super(context);
		init(null);
	}

	/**
	 * @param textColor
	 *            the textColor to set
	 */
	public void setTextColor(int textColor) {
		this.textColor = textColor;

		if (text1 != null && (textColor != Color.TRANSPARENT)) {
			text1.setTextColor(textColor);
		}
		if (text2 != null && (textColor != Color.TRANSPARENT)) {
			text2.setTextColor(textColor);
		}
	}

	/**
	 * @param attrs
	 */
	private void init(AttributeSet attrs) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.TwoLineListItem#onFinishInflate()
	 */
	@Override
	protected void onFinishInflate() {
		text1 = (TextView) findViewById(android.R.id.text1);
		text2 = (TextView) findViewById(android.R.id.text2);
		icon1 = (ImageView) findViewById(android.R.id.icon1);
		icon2 = (ImageView) findViewById(android.R.id.icon2);
		text3 = (TextView) findViewById(R.id.text3);

		if (text1 != null && (textColor != Color.TRANSPARENT)) {
			text1.setTextColor(textColor);
		}
		if (text2 != null && (textColor != Color.TRANSPARENT)) {
			text2.setTextColor(textColor);
		}
		if (text3 != null && (textColor != Color.TRANSPARENT)) {
			text3.setTextColor(textColor);
		}

		if (icon1 != null) {
			icon1.setFocusable(false);
			icon1.setClickable(false);
		}
		if (icon2 != null) {
			icon2.setVisibility(View.GONE);
			icon2.setFocusable(false);
			icon2.setClickable(false);
		}

		super.onFinishInflate();
	}

	public void setItem(Item e) {
		if (e != null) {
			if (e.getSpecifications().isEmpty()) {
				Debug.error("Item without spec found " + e.getName());
				setItem(e, null);
			} else {
				setItem(e, e.getSpecifications().get(0));
			}
		} else {
			setItem(null, null);
		}
	}

	public void setItem(Item e, ItemSpecification spec) {
		if (icon1 != null) {
			icon1.setVisibility(View.VISIBLE);
			if (e != null && e.getIconUri() != null)
				icon1.setImageURI(e.getIconUri());
			else if (spec != null)
				icon1.setImageResource(DsaUtil.getResourceId(spec));
			else
				icon1.setImageResource(0);
		}
		// Set value for the first text field
		if (text1 != null) {
			if (e != null) {
				text1.setText(e.getTitle());
			} else {
				text1.setText(null);
			}
			if (textColor != Color.TRANSPARENT)
				text1.setTextColor(textColor);
		}

		// set value for the second text field
		if (text2 != null) {
			if (spec != null) {
				text2.setText(spec.getInfo());
				if (textColor != Color.TRANSPARENT)
					text2.setTextColor(textColor);
			} else {
				text2.setText(null);
			}
		}
	}

}
