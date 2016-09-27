package com.dsatab.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dsatab.R;
import com.dsatab.data.items.Item;
import com.dsatab.data.items.ItemSpecification;
import com.dsatab.util.Debug;
import com.dsatab.util.DsaUtil;
import com.dsatab.util.ViewUtils;
import com.franlopez.flipcheckbox.FlipCheckBox;

public class ItemListItem extends CheckableRelativeLayout {

	public TextView text1, text2, text3;
    public FlipCheckBox icon1;
	public ImageView icon2;

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
		icon1 = (FlipCheckBox) findViewById(android.R.id.checkbox);
		icon2 = (ImageView) findViewById(android.R.id.icon2);
		text3 = (TextView) findViewById(R.id.text3);

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
				icon1.setFrontDrawable(ViewUtils.circleIcon(icon1.getContext(), e.getIconUri()));
			else if (spec != null)
                icon1.setFrontDrawable(ViewUtils.circleIcon(icon1.getContext(), DsaUtil.getResourceId(spec)));
			else
				icon1.setFrontDrawable(null);
		}
		// Set value for the first text field
		if (text1 != null) {
			if (e != null) {
				text1.setText(e.getTitle());
			} else {
				text1.setText(null);
			}
		}

		// set value for the second text field
		if (text2 != null) {
			if (spec != null) {
				text2.setText(spec.getInfo());
			} else {
				text2.setText(null);
			}
		}
	}

}
