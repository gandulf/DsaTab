package com.dsatab.view;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.dsatab.R;
import com.dsatab.data.ArmorAttribute;
import com.dsatab.data.WoundAttribute;
import com.dsatab.data.enums.ArmorPosition;
import com.dsatab.data.enums.Position;
import com.dsatab.util.Util;

public class BodyLayout extends FrameLayout {

	private static final double OFFSET_LOWER_LEG_X = 0.70;
	private static final double OFFSET_UPPER_LEG_X = 0.35;
	private static final double OFFSET_RIGHT_ARM_X = 0.83;
	private static final double OFFSET_RIGHT_UPPER_ARM_X = 0.7;
	private static final double OFFSET_LEFT_ARM_X = 0.16;
	private static final double OFFSET_LEFT_UPPER_ARM_X = 0.26;
	private static final double OFFSET_LEFT_SHOULDER_X = 0.30;
	private static final double OFFSET_STOMACH_X = 0.5;
	private static final double OFFSET_CHEST_X = 0.45;
	private static final double OFFSET_BACK_X = 0.55;
	private static final double OFFSET_HEAD_X = 0.5;
	private static final double OFFSET_HEAD_SIDE_X = 0.4;

	private static final double OFFSET_RIGHT_SHOULDER_Y = 0.18;
	private static final double OFFSET_RIGHT_UPPER_ARM_Y = 0.18;
	private static final double OFFSET_RIGHT_ARM_Y = 0.35;

	private static final double OFFSET_LEFT_SHOULDER_Y = 0.175;
	private static final double OFFSET_LEFT_UPPER_ARM_Y = 0.17;
	private static final double OFFSET_LEFT_ARM_Y = 0.35;

	private static final double OFFSET_HEAD_UP_Y = 0.005;
	private static final double OFFSET_HEAD_Y = 0.05;
	private static final double OFFSET_NECK_Y = 0.18;
	private static final double OFFSET_CHEST_Y = 0.235;
	private static final double OFFSET_BACK_Y = 0.235;
	private static final double OFFSET_STOMACH_Y = 0.37;
	private static final double OFFSET_PELVIS_Y = 0.525;
	private static final double OFFSET_UPPER_LEG_Y = 0.6;
	private static final double OFFSET_LOWER_LEG_Y = 0.7;

	private static final int MAX_WOUNDS = 3;
	private int rsWidthMeasureSpec, rsHeightMeasureSpec;
	private int woundWidthMeasureSpec, woundHeightMeasureSpec;

	private int woundSize;
	private int rsSize, rsTextSize;

	private Map<ArmorPosition, TextView> armorButtons = new HashMap<ArmorPosition, TextView>(
			ArmorPosition.values().length);
	private Map<Position, ImageButton[]> woundButtons = new HashMap<Position, ImageButton[]>(Position.values().length);

	private OnClickListener onArmorClickListener, onWoundClickListener;
	private OnLongClickListener onArmorLongClickListener;

	public static class LayoutParams extends FrameLayout.LayoutParams {
		private Position position;
		private ArmorPosition armorPosition;

		public LayoutParams(int width, int height, Position position) {
			super(width, height);
			this.position = position;

		}

		public LayoutParams(int width, int height, ArmorPosition position) {
			super(width, height);
			this.armorPosition = position;
		}

		public Position getPosition() {
			return position;
		}

		public void setPosition(Position position) {
			this.position = position;
		}

		public ArmorPosition getArmorPosition() {
			return armorPosition;
		}

		public void setArmorPosition(ArmorPosition armorPosition) {
			this.armorPosition = armorPosition;
		}

	}

	public BodyLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public BodyLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public BodyLayout(Context context) {
		super(context);
		init();
	}

	public void setArmorAttribute(ArmorAttribute attr) {
		TextView rsText = armorButtons.get(attr.getPosition());
		if (rsText == null) {
			rsText = addArmorButton(attr.getPosition());
		}
		setValue(rsText, attr);
	}

	public void setArmorAttributes(Map<ArmorPosition, ArmorAttribute> attributes) {

		// remove old buttons if existing
		for (TextView tv : armorButtons.values()) {
			removeView(tv);
		}
		armorButtons.clear();

		// add new ones
		for (ArmorAttribute attr : attributes.values()) {
			setArmorAttribute(attr);
		}

		requestLayout();
	}

	public void setWoundAttributes(Map<Position, WoundAttribute> attributes) {

		for (WoundAttribute attr : attributes.values()) {

			ImageButton[] buttons = woundButtons.get(attr.getPosition());

			if (buttons == null) {
				buttons = new ImageButton[MAX_WOUNDS];
				woundButtons.put(attr.getPosition(), buttons);
			}

			for (int i = 0; i < MAX_WOUNDS; i++) {
				ImageButton ib = buttons[i];

				if (ib == null) {
					ib = addWoundButton(attr);
					buttons[i] = ib;
				}

				ib.setSelected(attr.getValue() > i);
				ib.setBackgroundResource(R.drawable.icon_wound_btn);
			}

		}
		requestLayout();
	}

	private void setValue(TextView rsText, ArmorAttribute rs) {
		rsText.setTag(rs);

		if (rs.getValue() != null)
			rsText.setText(Util.toString(rs.getValue()));
		else
			rsText.setText("0");

		if (rs.isManual()) {
			rsText.setTextColor(getResources().getColor(R.color.ValueGreen));
		} else
			rsText.setTextColor(getResources().getColor(android.R.color.primary_text_light));
	}

	protected void init() {

		woundSize = getResources().getDimensionPixelSize(R.dimen.wound_icon_size);
		rsSize = getResources().getDimensionPixelSize(R.dimen.rs_icon_size);
		rsTextSize = getResources().getDimensionPixelSize(R.dimen.rs_icon_text_size);

		rsWidthMeasureSpec = MeasureSpec.makeMeasureSpec(rsSize, MeasureSpec.EXACTLY);
		rsHeightMeasureSpec = MeasureSpec.makeMeasureSpec(rsSize, MeasureSpec.EXACTLY);

		woundWidthMeasureSpec = MeasureSpec.makeMeasureSpec(woundSize, MeasureSpec.EXACTLY);
		woundHeightMeasureSpec = MeasureSpec.makeMeasureSpec(woundSize, MeasureSpec.EXACTLY);
	}

	public OnLongClickListener getOnArmorLongClickListener() {
		return onArmorLongClickListener;
	}

	public void setOnArmorLongClickListener(OnLongClickListener onArmorLongClickListener) {
		this.onArmorLongClickListener = onArmorLongClickListener;

		for (TextView btn : armorButtons.values()) {
			btn.setOnLongClickListener(onArmorLongClickListener);
		}
	}

	public OnClickListener getOnArmorClickListener() {
		return onArmorClickListener;
	}

	public void setOnArmorClickListener(OnClickListener onArmorClickListener) {
		this.onArmorClickListener = onArmorClickListener;

		for (TextView btn : armorButtons.values()) {
			btn.setOnClickListener(onArmorClickListener);
		}
	}

	public OnClickListener getOnWoundClickListener() {
		return onWoundClickListener;
	}

	public void setOnWoundClickListener(OnClickListener onWoundClickListener) {
		this.onWoundClickListener = onWoundClickListener;

		for (ImageButton[] btns : woundButtons.values()) {
			for (ImageButton btn : btns) {
				btn.setOnClickListener(onWoundClickListener);
			}
		}
	}

	protected ImageButton addWoundButton(WoundAttribute attr) {
		ImageButton woundButton = new ImageButton(getContext());
		woundButton.setPadding(5, 5, 5, 5);
		woundButton.setTag(attr);
		woundButton.setBackgroundResource(R.drawable.icon_wound_btn);
		woundButton.setOnClickListener(onWoundClickListener);

		addView(woundButton, new LayoutParams(woundSize, woundSize, attr.getPosition()));

		return woundButton;
	}

	protected TextView addArmorButton(ArmorPosition pos) {
		TextView rsText = new TextView(getContext());

		rsText.setBackgroundResource(R.drawable.icon_armor_btn);
		rsText.setOnClickListener(onArmorClickListener);
		rsText.setOnLongClickListener(onArmorLongClickListener);
		rsText.setGravity(Gravity.CENTER);
		rsText.setTextSize(rsTextSize);
		rsText.setTextColor(getResources().getColor(android.R.color.primary_text_light));
		rsText.setMinimumWidth(rsSize);
		rsText.setMinimumHeight(rsSize);
		addView(rsText, new LayoutParams(rsSize, rsSize, pos));

		armorButtons.put(pos, rsText);

		return rsText;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int count = getChildCount();
		for (int i = 0; i < count; i++) {
			View child = getChildAt(i);
			// wound
			if (child instanceof ImageButton) {
				child.measure(woundWidthMeasureSpec, woundHeightMeasureSpec);
			} else if (child instanceof TextView) // armorbutton
				child.measure(rsWidthMeasureSpec, rsHeightMeasureSpec);
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {

		int width = r - l;
		int height = b - t;
		int count = getChildCount();

		int headWidth = 0;
		int torsoWidth = 0;
		int chestWidth = 0;
		int leftArmWidth = 0;
		int rightArmWidth = 0;
		int upperLegWidth = 0;
		int lowerLegWidth = 0;

		for (int i = 0; i < count; i++) {

			View child = getChildAt(i);

			if (child.getVisibility() != GONE) {

				LayoutParams lp = (LayoutParams) child.getLayoutParams();

				if (lp.getPosition() != null) {
					switch (lp.getPosition()) {
					case Kopf:
						headWidth += child.getMeasuredWidth();
						break;
					case Bauch:
						torsoWidth += child.getMeasuredWidth();
						break;
					case Brust:
						chestWidth += child.getMeasuredWidth();
						break;
					case LeftLowerArm:
						leftArmWidth += child.getMeasuredWidth();
						break;
					case RightLowerArm:
						rightArmWidth += child.getMeasuredWidth();
						break;
					case UpperLeg:
						upperLegWidth += child.getMeasuredWidth();
						break;
					case LowerLeg:
						lowerLegWidth += child.getMeasuredWidth();
						break;
					default:
						// do nothing
						break;
					}
				} else if (lp.getArmorPosition() != null) {
					switch (lp.getArmorPosition()) {
					case Kopf:
						headWidth += child.getMeasuredWidth();
						break;
					case Bauch:
						torsoWidth += child.getMeasuredWidth();
						break;
					case Brust:
						chestWidth += child.getMeasuredWidth();
						break;
					case LinkerArm:
						leftArmWidth += child.getMeasuredWidth();
						break;
					case RechterArm:
						rightArmWidth += child.getMeasuredWidth();
						break;
					case LinkesBein:
						upperLegWidth += child.getMeasuredWidth();
						break;
					case RechtesBein:
						lowerLegWidth += child.getMeasuredWidth();
						break;
					default:
						// do nothing
						break;
					}

				}
			}
		}

		int headX = (int) (width * OFFSET_HEAD_X) - (headWidth / 2);
		int headY = (int) (height * OFFSET_HEAD_Y);

		int torsoX = (int) (width * OFFSET_STOMACH_X) - (torsoWidth / 2);
		int torsoY = (int) (height * OFFSET_STOMACH_Y);

		int chestX = (int) (width * OFFSET_CHEST_X) - (chestWidth / 2);
		int chestY = (int) (height * OFFSET_CHEST_Y);

		int leftArmX = (int) (width * OFFSET_LEFT_ARM_X) - (leftArmWidth / 2);
		int leftArmY = (int) (height * OFFSET_LEFT_ARM_Y);

		int rightArmX = (int) (width * OFFSET_RIGHT_ARM_X) - (rightArmWidth / 2);
		int rightArmY = (int) (height * OFFSET_RIGHT_ARM_Y);

		int upperLegX = (int) (width * OFFSET_UPPER_LEG_X) - (upperLegWidth / 2);
		int upperLegY = (int) (height * OFFSET_UPPER_LEG_Y);

		int lowerLegX = (int) (width * OFFSET_LOWER_LEG_X) - (lowerLegWidth / 2);
		int lowerLegY = (int) (height * OFFSET_LOWER_LEG_Y);

		int cl = 0, ct = 0, cr = 0, cb = 0;
		for (int i = 0; i < count; i++) {

			View child = getChildAt(i);

			if (child.getVisibility() != GONE) {
				LayoutParams lp = (LayoutParams) child.getLayoutParams();

				// wounds
				if (lp.getPosition() != null) {
					switch (lp.getPosition()) {
					case Kopf:
						cl = headX;
						cr = headX = headX + child.getMeasuredWidth();
						ct = headY;
						cb = ct + child.getMeasuredHeight();
						break;
					case Bauch:
						cl = torsoX;
						cr = torsoX = torsoX + child.getMeasuredWidth();
						ct = torsoY;
						cb = ct + child.getMeasuredHeight();
						break;
					case Brust:
						cl = chestX;
						cr = chestX = chestX + child.getMeasuredWidth();
						ct = chestY;
						cb = ct + child.getMeasuredHeight();
						break;
					case LeftLowerArm:
						cl = leftArmX;
						cr = leftArmX = leftArmX + child.getMeasuredWidth();
						ct = leftArmY;
						cb = ct + child.getMeasuredHeight();
						break;
					case RightLowerArm:
						cl = rightArmX;
						cr = rightArmX = rightArmX + child.getMeasuredWidth();
						ct = rightArmY;
						cb = ct + child.getMeasuredHeight();
						break;
					case UpperLeg:
						cl = upperLegX;
						cr = upperLegX = upperLegX + child.getMeasuredWidth();
						ct = upperLegY;
						cb = ct + child.getMeasuredHeight();
						break;
					case LowerLeg:
						cl = lowerLegX;
						cr = lowerLegX = lowerLegX + child.getMeasuredWidth();
						ct = lowerLegY;
						cb = ct + child.getMeasuredHeight();
						break;
					default:
						// do nothing
						break;
					}
					// armor
				} else if (lp.getArmorPosition() != null) {
					{
						switch (lp.getArmorPosition()) {
						case Kopf:
							cl = (int) (width * OFFSET_HEAD_X) - (child.getMeasuredWidth() / 2);
							cr = cl + child.getMeasuredWidth();
							ct = (int) (height * (OFFSET_HEAD_Y) + woundSize);
							cb = ct + child.getMeasuredHeight();
							break;
						case Bauch:
							cl = (int) (width * OFFSET_STOMACH_X) - (child.getMeasuredWidth() / 2);
							cr = cl + child.getMeasuredWidth();
							ct = (int) (height * (OFFSET_STOMACH_Y) + woundSize);
							cb = ct + child.getMeasuredHeight();
							break;
						case Brust:
							cl = (int) (width * OFFSET_CHEST_X) - (child.getMeasuredWidth() / 2);
							cr = cl + child.getMeasuredWidth();
							ct = (int) (height * (OFFSET_CHEST_Y) + woundSize);
							cb = ct + child.getMeasuredHeight();
							break;
						case Ruecken:
							cl = (int) (width * OFFSET_BACK_X) - (child.getMeasuredWidth() / 2);
							cr = cl + child.getMeasuredWidth();
							ct = (int) (height * (OFFSET_BACK_Y) + woundSize);
							cb = ct + child.getMeasuredHeight();
							break;
						case LinkerArm:
							cl = (int) (width * OFFSET_LEFT_ARM_X) - (child.getMeasuredWidth() / 2);
							cr = cl + child.getMeasuredWidth();
							ct = (int) (height * (OFFSET_LEFT_ARM_Y) + woundSize);
							cb = ct + child.getMeasuredHeight();
							break;
						case RechterArm:
							cl = (int) (width * OFFSET_RIGHT_ARM_X) - (child.getMeasuredWidth() / 2);
							cr = cl + child.getMeasuredWidth();
							ct = (int) (height * (OFFSET_RIGHT_ARM_Y) + woundSize);
							cb = ct + child.getMeasuredHeight();
							break;
						case LinkesBein:
							cl = (int) (width * OFFSET_UPPER_LEG_X) - (child.getMeasuredWidth() / 2);
							cr = cl + child.getMeasuredWidth();
							ct = (int) (height * (OFFSET_UPPER_LEG_Y) + woundSize);
							cb = ct + child.getMeasuredHeight();
							break;
						case RechtesBein:
							cl = (int) (width * OFFSET_LOWER_LEG_X) - (child.getMeasuredWidth() / 2);
							cr = cl + child.getMeasuredWidth();
							ct = (int) (height * (OFFSET_LOWER_LEG_Y) + woundSize);
							cb = ct + child.getMeasuredHeight();
							break;
						default:
							// do nothing
							break;
						}
					}
				}

				child.layout(cl, ct, cr, cb);
			}

		}

	}

	@Override
	public LayoutParams generateLayoutParams(AttributeSet attrs) {
		return new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT, Position.Kopf);
	}

	@Override
	protected boolean checkLayoutParams(android.view.ViewGroup.LayoutParams p) {

		// if the layout params is invalid, the android will throw a runtime
		// exception.
		if (p instanceof LayoutParams) {
			return true;
		} else {
			return false;
		}

	}

}
