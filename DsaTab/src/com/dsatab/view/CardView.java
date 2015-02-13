package com.dsatab.view;

import android.content.Context;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.dsatab.R;
import com.dsatab.data.items.ItemCard;
import com.dsatab.util.Util;

public class CardView extends android.support.v7.widget.CardView implements Checkable {

	private static final int[] CHECKED_STATE_SET = { android.R.attr.state_checked };

	private ImageView imageView;
	private TextView textView;

	private boolean imageTextOverlay;

	private boolean calculated = false;

	private boolean highQuality = false;

	boolean mChecked = false;
	boolean mCheckable = false;

	/**
	 * @param context
	 */
	public CardView(Context context, ItemCard item) {
		this(context, null, R.attr.cardViewStyle);
		setItem(item);
	}

	public CardView(Context context, AttributeSet attrs) {
		this(context, attrs, R.attr.cardViewStyle);
	}

	public CardView(Context context) {
		this(context, null, R.attr.cardViewStyle);
	}

	public CardView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	/**
	 * 
	 */

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
		calculated = false;
		setTag(item);

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

		invalidate();
	}

	public boolean isHighQuality() {
		return highQuality;
	}

	public void setHighQuality(boolean highQuality) {
		this.highQuality = highQuality;
	}

	private void calcTextSize(int w, int h) {
		if (calculated || TextUtils.isEmpty(textView.getText()) || !imageTextOverlay)
			return;

		Paint paint = new Paint(textView.getPaint());
		paint.setTextSize(getWidth() / 7);

		int maxWidth = 0;
		int paddingHorizontal = w / 10;
		int paddingVertical = h / 10;

		maxWidth = (int) Math.sqrt((w - paddingHorizontal * 2) * (w - paddingHorizontal * 2)
				+ (h - paddingVertical * 2) * (h - paddingVertical * 2));

		float width = paint.measureText(textView.getText().toString());

		while (width > maxWidth && paint.getTextSize() > 1.0f) {
			paint.setTextSize(paint.getTextSize() - 2);
			width = paint.measureText(textView.getText().toString());
		}

		textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, paint.getTextSize());

		calculated = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#onSizeChanged(int, int, int, int)
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		calculated = false;
		calcTextSize(w, h);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		calculated = false;
		calcTextSize(right - left, bottom - top);
	}

}