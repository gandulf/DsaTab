package com.dsatab.view.dialog;

import net.simonvt.numberpicker.NumberPicker;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

import com.dsatab.R;
import com.dsatab.data.ArmorAttribute;
import com.dsatab.data.Attribute;
import com.dsatab.data.CombatDistanceTalent;
import com.dsatab.data.Hero.CombatStyle;
import com.dsatab.data.Value;
import com.dsatab.data.enums.AttributeType;
import com.dsatab.util.Debug;
import com.dsatab.util.Util;

public class InlineEditDialog extends AlertDialog implements DialogInterface.OnClickListener, OnCheckedChangeListener {

	private Value value;

	private NumberPicker numberPicker;

	private ToggleButton combatStyleBtn;
	private CheckBox beCalculation;

	private Context context;

	public InlineEditDialog(Context context, Value value) {
		super(context);

		this.context = context;
		init();
		setValue(value);

	}

	public Value getValue() {
		return value;
	}

	public void setValue(Value value) {
		this.value = value;

		if (value != null) {

			Integer currentValue = value.getValue();
			numberPicker.setMinValue(value.getMinimum());
			numberPicker.setMaxValue(value.getMaximum());

			if (currentValue != null) {
				numberPicker.setValue(currentValue);
			} else {
				Debug.error("Setting value was null:" + value);
				numberPicker.setValue(0);
			}
			numberPicker.setEnabled(true);

			int visible = View.GONE;
			if (value instanceof Attribute) {
				Attribute attr = (Attribute) value;
				if (attr.getType() == AttributeType.Behinderung) {
					visible = View.VISIBLE;
					combatStyleBtn.setChecked(attr.getHero().getCombatStyle() == CombatStyle.Offensive);
					beCalculation.setChecked(attr.getHero().isBeCalculation());
					numberPicker.setEnabled(!beCalculation.isChecked());
				}
			}

			combatStyleBtn.setVisibility(visible);
			beCalculation.setVisibility(visible);

			if (getButton(BUTTON_NEGATIVE) != null) {
				getButton(BUTTON_NEGATIVE).setEnabled(value.getReferenceValue() != null);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Dialog#onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();

		if (getButton(BUTTON_NEGATIVE) != null && value != null) {
			getButton(BUTTON_NEGATIVE).setEnabled(value.getReferenceValue() != null);
		}

	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (buttonView == combatStyleBtn) {

			if (value instanceof Attribute) {
				Attribute attr = (Attribute) value;
				if (isChecked)
					attr.getHero().setCombatStyle(CombatStyle.Offensive);
				else
					attr.getHero().setCombatStyle(CombatStyle.Defensive);
			}
		} else if (buttonView == beCalculation) {

			numberPicker.setEnabled(!isChecked);

		}

	}

	private void accept() {
		if (value instanceof Attribute) {
			Attribute attr = (Attribute) value;
			if (attr.getType() == AttributeType.Behinderung) {
				attr.getHero().setBeCalculation(beCalculation.isChecked());

				// if we autocalculate the value to not overwrite it with
				// current value of editText afterwards
				if (beCalculation.isChecked()) {
					dismiss();
					Util.hideKeyboard(beCalculation);
					return;
				}
			}
		}

		try {

			int currentValue = numberPicker.getValue();

			if (value instanceof ArmorAttribute) {
				ArmorAttribute armorAttribute = (ArmorAttribute) value;
				armorAttribute.setValue(currentValue, true);

			} else if (value instanceof CombatDistanceTalent) {
				int baseValue = ((CombatDistanceTalent) value).getBaseValue();
				value.setValue(currentValue - baseValue);
			} else {
				value.setValue(currentValue);
			}
		} catch (NumberFormatException e) {
			Debug.error(e);
		}
		Util.hideKeyboard(beCalculation);
		dismiss();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.DialogInterface.OnClickListener#onClick(android.content .DialogInterface, int)
	 */
	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		case BUTTON_POSITIVE:
			accept();
			break;

		case BUTTON_NEGATIVE:
			value.reset();
			dismiss();
			break;
		}

	}

	private void init() {
		setCanceledOnTouchOutside(true);

		ViewGroup popupcontent = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.popup_edit, null, false);
		setView(popupcontent);

		numberPicker = (NumberPicker) popupcontent.findViewById(R.id.popup_edit_text);
		numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		numberPicker.setWrapSelectorWheel(false);

		combatStyleBtn = (ToggleButton) popupcontent.findViewById(R.id.popup_edit_combat_style);
		combatStyleBtn.setTextOn(getContext().getText(R.string.offensive));
		combatStyleBtn.setTextOff(getContext().getText(R.string.defensive));
		combatStyleBtn.setOnCheckedChangeListener(this);

		beCalculation = (CheckBox) popupcontent.findViewById(R.id.popup_edit_be_calculation);
		beCalculation.setOnCheckedChangeListener(this);

		setButton(BUTTON_POSITIVE, "Ok", this);
		setButton(BUTTON_NEGATIVE, "Reset", this);

	}

}
