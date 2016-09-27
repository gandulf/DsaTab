package com.dsatab.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.dsatab.R;
import com.dsatab.data.items.ItemCard;
import com.dsatab.util.Util;

public class CardView extends FrameLayout implements Checkable {

	private static final int[] CHECKED_STATE_SET = { android.R.attr.state_checked };

	private ImageView imageView;
	private TextView textView;

	private boolean highQuality = false;

	private boolean mChecked = false;
	private boolean mCheckable = false;

	/**
	 * @param context
	 */
	public CardView(Context context, ItemCard item) {
		this(context,(AttributeSet) null);
		setItem(item);
	}

	public CardView(Context context) {
		this(context, (AttributeSet) null);
	}

	public CardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		LayoutInflater.from(getContext()).inflate(R.layout._card_view, this);

		imageView = (ImageView) findViewById(android.R.id.icon);
		textView = (TextView) findViewById(android.R.id.text1);
		imageView.setScaleType(ScaleType.FIT_CENTER);

	}

	@Override
	public boolean isChecked() {
		return mCheckable && mChecked;
	}

	@Override
	public void setChecked(boolean checked) {
		if (mCheckable && mChecked != checked) {
			mChecked = checked;
			refreshDrawableState();
		}
	}

	public boolean isCheckable() {
		return mCheckable;
	}

	public void setCheckable(boolean checkable) {
		this.mCheckable = checkable;
	}

	@Override
	public void toggle() {
		if (mCheckable) {
			mChecked = !mChecked;
			refreshDrawableState();
		}
	}

	@Override
	public int[] onCreateDrawableState(int extraSpace) {
		final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
		if (isChecked()) {
			mergeDrawableStates(drawableState, CHECKED_STATE_SET);
		}
		return drawableState;
	}

	public void setItem(ItemCard item) {
		setTag(item);
        boolean imageTextOverlay;
		if (item != null) {
			if (item.hasImage()) {
				Util.setImage(imageView, item.getImageUri(), R.drawable.item_card);
			} else {
				imageView.setImageResource(R.drawable.item_card);
			}
			textView.setText(item.getTitle());
			imageTextOverlay = item.isImageTextOverlay();
		} else {
			imageTextOverlay = false;
			if (highQuality) {
				imageView.setImageResource(R.drawable.item_card);
			} else {
				imageView.setImageResource(R.drawable.item_card_small);
            }
            textView.setText(null);
		}

		Util.setVisibility(textView, imageTextOverlay);
	}

	public boolean isHighQuality() {
		return highQuality;
	}

	public void setHighQuality(boolean highQuality) {
		this.highQuality = highQuality;
	}

}