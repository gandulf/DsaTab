package com.dsatab.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.dsatab.R;
import com.dsatab.data.items.EquippedItem;
import com.dsatab.data.items.Item;
import com.dsatab.data.items.ItemSpecification;
import com.dsatab.util.Util;

/**
 * @author Seraphim
 * 
 */
public class EquippedItemListItem extends CheckableRelativeLayout {

	private int textColor = 0;

	private ImageView icon1;
	private CheckableImageButton set1, set2, set3;
	private TextView text1, text2;
	private TextView countOverlay;

	public EquippedItemListItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs);
	}

	public EquippedItemListItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
	}

	public EquippedItemListItem(Context context) {
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
		super.onFinishInflate();

		text1 = (TextView) findViewById(android.R.id.text1);
		text2 = (TextView) findViewById(android.R.id.text2);
		icon1 = (ImageView) findViewById(android.R.id.icon1);

		set1 = (CheckableImageButton) findViewById(R.id.set1);
		set2 = (CheckableImageButton) findViewById(R.id.set2);
		set3 = (CheckableImageButton) findViewById(R.id.set3);

		countOverlay = (TextView) findViewById(R.id.icon_1_overlay);

		if (text1 != null && (textColor != Color.TRANSPARENT)) {
			text1.setTextColor(textColor);
		}
		if (text2 != null && (textColor != Color.TRANSPARENT)) {
			text2.setTextColor(textColor);
		}

		if (icon1 != null) {
			icon1.setFocusable(false);
			icon1.setClickable(false);
		}

		if (set1 != null) {
			set1.setFocusable(false);
			set1.setClickable(false);
		}
		if (set2 != null) {
			set2.setFocusable(false);
			set2.setClickable(false);
		}
		if (set3 != null) {
			set3.setFocusable(false);
			set3.setClickable(false);
		}

	}

	public ImageView getIcon1() {
		return icon1;
	}

	public CheckableImageButton getSet(int set) {
		switch (set) {
		case 0:
			return set1;
		case 1:
			return set2;
		case 2:
			return set3;
		default:
			return null;
		}
	}

	public ImageButton getSet1() {
		return set1;
	}

	public ImageButton getSet2() {
		return set2;
	}

	public ImageButton getSet3() {
		return set3;
	}

	public void setItem(EquippedItem e) {
		setItem(e.getItem());
	}

	public void setItem(Item e) {

		ItemSpecification itemSpecification = null;
		if (e != null && !e.getSpecifications().isEmpty()) {
			itemSpecification = e.getSpecifications().get(0);
		}
		setItem(e, itemSpecification);
	}

	public void setItem(Item e, ItemSpecification spec) {

		if (icon1 != null) {
			icon1.setVisibility(View.VISIBLE);
			icon1.setImageURI(e.getIconUri());
		}
		// Set value for the first text field
		if (text1 != null) {
			text1.setText(e.getTitle());
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

		int visibility = e.isEquipable() ? View.VISIBLE : View.GONE;
		set1.setVisibility(visibility);
		set2.setVisibility(visibility);
		set3.setVisibility(visibility);

		if (e.getCount() > 1) {
			countOverlay.setText(Util.toString(e.getCount()));
			countOverlay.setVisibility(View.VISIBLE);
		} else {
			countOverlay.setVisibility(View.GONE);
		}

	}

}
