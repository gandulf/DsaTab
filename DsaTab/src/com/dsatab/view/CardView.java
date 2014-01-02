package com.dsatab.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Checkable;
import android.widget.ImageView;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.data.items.ItemCard;
import com.gandulf.guilib.util.Debug;
import com.squareup.picasso.Picasso;

/**
 * 
 */
public class CardView extends ImageView implements Checkable {

	private static final int HQ_IMAGE_SIZE = 600;
	private static final int LQ_IMAGE_SIZE = 300;

	private static final int[] CHECKED_STATE_SET = { android.R.attr.state_checked };

	private String itemText;

	private int textGravity;
	private boolean imageTextOverlay;

	private Paint paint;

	private Path textPath;
	private Rect textBox;

	private boolean calculated = false;

	private boolean highQuality = false;

	private int TEXT_PADDING = 0;

	boolean mChecked = false;

	/**
	 * @param context
	 */
	public CardView(Context context, ItemCard item) {
		this(context, null, 0);
		setItem(item);
	}

	public CardView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CardView(Context context) {
		this(context, null, 0);
	}

	public CardView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	/**
	 * 
	 */
	@TargetApi(11)
	private void init() {

		setDrawingCacheEnabled(false);
		setBackgroundResource(R.drawable.border_patch);

		textBox = new Rect();
		paint = new Paint();
		paint.setTextAlign(Align.CENTER);
		paint.setTextSize(20);
		paint.setAntiAlias(true);
		if (!isInEditMode()) {
			paint.setTypeface(DsaTabApplication.getInstance().getPoorRichardFont());
			TEXT_PADDING = getResources().getDimensionPixelOffset(R.dimen.card_text_padding);
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
	}

	@Override
	public boolean isChecked() {
		return mChecked;
	}

	@Override
	public void setChecked(boolean checked) {
		if (mChecked != checked) {
			mChecked = checked;
			refreshDrawableState();
		}
	}

	@Override
	public void toggle() {
		mChecked = !mChecked;
		refreshDrawableState();
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
				Debug.verbose("Loading image from " + item.getImageUri().toString());

				setScaleType(ScaleType.CENTER_INSIDE);

				if (highQuality) {
					Picasso.with(getContext()).load(item.getImageUri().toString()).placeholder(R.drawable.item_card)
							.resize(HQ_IMAGE_SIZE, HQ_IMAGE_SIZE).centerInside().into(this);
					// setImageBitmap(DataManager.getBitmap(item.getImageUri(), HQ_IMAGE_SIZE));
					// setScaleType(ScaleType.CENTER_INSIDE);
				} else {

					Picasso.with(getContext()).load(item.getImageUri().toString()).placeholder(R.drawable.item_card)
							.resize(LQ_IMAGE_SIZE, LQ_IMAGE_SIZE).centerInside().into(this);
					// setImageBitmap(DataManager.getBitmap(item.getImageUri(), LQ_IMAGE_SIZE));
					// setScaleType(ScaleType.FIT_CENTER);
				}
				textGravity = Gravity.CENTER;
			} else {
				textGravity = Gravity.CENTER;
				setImageResource(R.drawable.item_card);
				setScaleType(ScaleType.FIT_XY);
			}

			itemText = item.getTitle();
			imageTextOverlay = item.isImageTextOverlay();
		} else {
			imageTextOverlay = false;
			setImageResource(R.drawable.item_card);
			setScaleType(ScaleType.FIT_XY);
			itemText = null;
			textGravity = Gravity.CENTER;
		}

		invalidate();
	}

	public boolean isHighQuality() {
		return highQuality;
	}

	public void setHighQuality(boolean highQuality) {
		this.highQuality = highQuality;
	}

	private void calcTextSize(int w, int h) {
		if (calculated || TextUtils.isEmpty(itemText) || !imageTextOverlay)
			return;
		paint.setTextSize(getWidth() / 7);

		int maxWidth = 0;
		switch (textGravity) {
		case Gravity.TOP:
			maxWidth = (w - TEXT_PADDING * 2);
			break;
		case Gravity.CENTER:
			maxWidth = (int) Math.sqrt((w - TEXT_PADDING * 2) * (w - TEXT_PADDING * 2) + (h - TEXT_PADDING * 2)
					* (h - TEXT_PADDING * 2));
			break;
		default:
			maxWidth = (w - TEXT_PADDING * 2);
			break;
		}

		float width = paint.measureText(itemText);

		while (width > maxWidth && paint.getTextSize() > 1.0f) {
			paint.setTextSize(paint.getTextSize() - 2);
			width = paint.measureText(itemText);
		}

		if (textPath == null)
			textPath = new Path();
		else
			textPath.reset();

		switch (textGravity) {
		case Gravity.TOP:
			textPath.moveTo(0, TEXT_PADDING + paint.getTextSize() / 2);
			textPath.lineTo(w, TEXT_PADDING + paint.getTextSize() / 2);
			textBox.set(0, 0, w, (int) (TEXT_PADDING * 2 + paint.getTextSize()));
			break;
		case Gravity.CENTER:
			textPath.moveTo(TEXT_PADDING, TEXT_PADDING);
			textPath.lineTo(w - TEXT_PADDING, h - TEXT_PADDING);
			textBox.set(0, 0, 0, 0);
			break;
		}

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ImageView#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (!TextUtils.isEmpty(itemText) && imageTextOverlay) {
			calcTextSize(getWidth(), getHeight());
			if (!textBox.isEmpty()) {
				paint.setAlpha(80);
				canvas.drawRect(textBox, paint);
				paint.setAlpha(255);
			}
			canvas.drawTextOnPath(itemText, textPath, 0, paint.getTextSize() / 2, paint);

		}
	}
}