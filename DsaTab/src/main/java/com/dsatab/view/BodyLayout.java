package com.dsatab.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.dsatab.R;
import com.dsatab.data.ArmorAttribute;
import com.dsatab.data.WoundAttribute;
import com.dsatab.data.enums.Position;
import com.dsatab.util.Util;

import java.util.HashMap;
import java.util.Map;

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

	private int woundSizePx;

	private Map<Position, CheckedTextView> armorButtons = new HashMap<Position, CheckedTextView>(
			Position.ARMOR_POSITIONS.size());
	private Map<Position, ToggleButton[]> woundButtons = new HashMap<Position, ToggleButton[]>(Position.values().length);

	private OnClickListener onArmorClickListener;
	private OnCheckedChangeListener onWoundClickListener;
	private OnLongClickListener onArmorLongClickListener;

	public static class LayoutParams extends FrameLayout.LayoutParams {
		private Position position;
		private Position armorPosition;

		public LayoutParams(int width, int height, Position woundPosition, Position armorPosition) {
			super(width, height);
			this.position = woundPosition;
			this.armorPosition = armorPosition;
		}

		public Position getPosition() {
			return position;
		}

		public void setPosition(Position position) {
			this.position = position;
		}

		public Position getArmorPosition() {
			return armorPosition;
		}

		public void setArmorPosition(Position armorPosition) {
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

	public void setArmorAttributeChecked(Position position, boolean checked) {
		armorButtons.get(position).setChecked(checked);
	}

	public void clearArmorAttributeChecked() {
		for (CheckedTextView textView : armorButtons.values()) {
			textView.setChecked(false);
		}
	}

	public void setArmorAttribute(ArmorAttribute attr) {
		TextView rsText = armorButtons.get(attr.getPosition());
		if (rsText == null) {
			rsText = addArmorButton(attr.getPosition());
		}
		setValue(rsText, attr);
	}

	public void setArmorAttributes(Map<Position, ArmorAttribute> attributes) {

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

	public void setWoundAttribute(WoundAttribute attr) {
		ToggleButton[] buttons = woundButtons.get(attr.getPosition());

		if (buttons == null) {
			buttons = new ToggleButton[MAX_WOUNDS];
			woundButtons.put(attr.getPosition(), buttons);
		}
		int selectedButtons = 0;

		for (int i = 0; i < MAX_WOUNDS; i++) {
			ToggleButton ib = buttons[i];
			if (ib == null) {
				ib = addWoundButton(attr);
				buttons[i] = ib;
			}
			selectedButtons += ib.isChecked() ? 1 : 0;

			ib.setOnCheckedChangeListener(null);
		}

		if (selectedButtons != attr.getValue()) {
			for (int i = 0; i < MAX_WOUNDS; i++) {
				ToggleButton ib = buttons[i];
				ib.setChecked(attr.getValue() > i);
			}
		}

		for (int i = 0; i < MAX_WOUNDS; i++) {
			buttons[i].setOnCheckedChangeListener(onWoundClickListener);
		}
	}

	public void setWoundAttributes(Map<Position, WoundAttribute> attributes) {

		for (WoundAttribute attr : attributes.values()) {
			setWoundAttribute(attr);
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

		rsWidthMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		rsHeightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);

		woundSizePx = getContext().getResources().getDimensionPixelSize(R.dimen.icon_button_size_plain);
		woundWidthMeasureSpec = MeasureSpec.makeMeasureSpec(woundSizePx, MeasureSpec.EXACTLY);
		woundHeightMeasureSpec = MeasureSpec.makeMeasureSpec(woundSizePx, MeasureSpec.EXACTLY);
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

	public OnCheckedChangeListener getOnWoundClickListener() {
		return onWoundClickListener;
	}

	public void setOnWoundClickListener(OnCheckedChangeListener onWoundClickListener) {
		this.onWoundClickListener = onWoundClickListener;

		for (ToggleButton[] btns : woundButtons.values()) {
			for (ToggleButton btn : btns) {
				btn.setOnCheckedChangeListener(onWoundClickListener);
			}
		}
	}

	protected ToggleButton addWoundButton(WoundAttribute attr) {
		ToggleButton woundButton = new ToggleButton(getContext());
		woundButton.setTag(attr);
		woundButton.setBackgroundResource(R.drawable.bg_wound_btn);
		woundButton.setTextOff("");
		woundButton.setTextOn("");
		woundButton.setChecked(false);
		woundButton.setOnCheckedChangeListener(onWoundClickListener);

		addView(woundButton, new LayoutParams(woundSizePx, woundSizePx, attr.getPosition(), null));
		return woundButton;
	}

	protected TextView addArmorButton(Position pos) {
		CheckedTextView rsText = new CheckedTextView(getContext());

		rsText.setCheckMarkDrawable(0);
		rsText.setBackgroundResource(R.drawable.icon_armor_btn);
		rsText.setOnClickListener(onArmorClickListener);
		rsText.setOnLongClickListener(onArmorLongClickListener);
		rsText.setGravity(Gravity.CENTER);
		rsText.setTextColor(getResources().getColor(android.R.color.primary_text_light));

		addView(rsText, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, null, pos));

		armorButtons.put(pos, rsText);

		rsText.measure(rsWidthMeasureSpec, rsHeightMeasureSpec);

		rsText.setTextSize(TypedValue.COMPLEX_UNIT_PX, rsText.getMeasuredHeight() / 1.7f);

		return rsText;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int count = getChildCount();
		for (int i = 0; i < count; i++) {
			View child = getChildAt(i);
			// wound
			if (child instanceof ToggleButton) {
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
				/*
				 * else if (lp.getArmorPosition() != null) { switch (lp.getArmorPosition()) { case Kopf: headWidth +=
				 * child.getMeasuredWidth(); break; case Bauch: torsoWidth += child.getMeasuredWidth(); break; case
				 * Brust: chestWidth += child.getMeasuredWidth(); break; case LinkerArm: leftArmWidth +=
				 * child.getMeasuredWidth(); break; case RechterArm: rightArmWidth += child.getMeasuredWidth(); break;
				 * case LinkesBein: upperLegWidth += child.getMeasuredWidth(); break; case RechtesBein: lowerLegWidth +=
				 * child.getMeasuredWidth(); break; default: // do nothing break; }
				 * 
				 * }
				 */
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
					case LinkerArm:
						cl = leftArmX;
						cr = leftArmX = leftArmX + child.getMeasuredWidth();
						ct = leftArmY;
						cb = ct + child.getMeasuredHeight();
						break;
					case RechterArm:
						cl = rightArmX;
						cr = rightArmX = rightArmX + child.getMeasuredWidth();
						ct = rightArmY;
						cb = ct + child.getMeasuredHeight();
						break;
					case LinkesBein:
						cl = upperLegX;
						cr = upperLegX = upperLegX + child.getMeasuredWidth();
						ct = upperLegY;
						cb = ct + child.getMeasuredHeight();
						break;
					case RechtesBein:
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
							ct = (int) (height * (OFFSET_HEAD_Y) + woundSizePx);
							cb = ct + child.getMeasuredHeight();
							break;
						case Bauch:
							cl = (int) (width * OFFSET_STOMACH_X) - (child.getMeasuredWidth() / 2);
							cr = cl + child.getMeasuredWidth();
							ct = (int) (height * (OFFSET_STOMACH_Y) + woundSizePx);
							cb = ct + child.getMeasuredHeight();
							break;
						case Brust:
							cl = (int) (width * OFFSET_CHEST_X) - (child.getMeasuredWidth() / 2);
							cr = cl + child.getMeasuredWidth();
							ct = (int) (height * (OFFSET_CHEST_Y) + woundSizePx);
							cb = ct + child.getMeasuredHeight();
							break;
						case Ruecken:
							cl = (int) (width * OFFSET_BACK_X) - (child.getMeasuredWidth() / 2);
							cr = cl + child.getMeasuredWidth();
							ct = (int) (height * (OFFSET_BACK_Y) + woundSizePx);
							cb = ct + child.getMeasuredHeight();
							break;
						case LinkerArm:
							cl = (int) (width * OFFSET_LEFT_ARM_X) - (child.getMeasuredWidth() / 2);
							cr = cl + child.getMeasuredWidth();
							ct = (int) (height * (OFFSET_LEFT_ARM_Y) + woundSizePx);
							cb = ct + child.getMeasuredHeight();
							break;
						case RechterArm:
							cl = (int) (width * OFFSET_RIGHT_ARM_X) - (child.getMeasuredWidth() / 2);
							cr = cl + child.getMeasuredWidth();
							ct = (int) (height * (OFFSET_RIGHT_ARM_Y) + woundSizePx);
							cb = ct + child.getMeasuredHeight();
							break;
						case LinkesBein:
							cl = (int) (width * OFFSET_UPPER_LEG_X) - (child.getMeasuredWidth() / 2);
							cr = cl + child.getMeasuredWidth();
							ct = (int) (height * (OFFSET_UPPER_LEG_Y) + woundSizePx);
							cb = ct + child.getMeasuredHeight();
							break;
						case RechtesBein:
							cl = (int) (width * OFFSET_LOWER_LEG_X) - (child.getMeasuredWidth() / 2);
							cr = cl + child.getMeasuredWidth();
							ct = (int) (height * (OFFSET_LOWER_LEG_Y) + woundSizePx);
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
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT, Position.Kopf, null);
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
