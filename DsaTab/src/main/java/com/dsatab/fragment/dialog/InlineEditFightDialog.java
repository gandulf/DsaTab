package com.dsatab.fragment.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.TextView;

import com.dsatab.R;
import com.dsatab.data.CombatMeleeAttribute;
import com.dsatab.data.CombatMeleeTalent;
import com.dsatab.data.Value;
import com.dsatab.util.Util;

public class InlineEditFightDialog extends DialogFragment implements android.content.DialogInterface.OnClickListener,
		OnValueChangeListener {

	public static final String TAG = "InlineEditFightDialog";

	private CombatMeleeTalent talent;
	private Value valueTotal;

	private CombatMeleeAttribute valueAt;
	private CombatMeleeAttribute valuePa;

	private NumberPicker numberPicker;

	private NumberPicker editAt;
	private NumberPicker editPa;

	private TextView textFreeValue, textFreeLabel;
	private View labels;

	private boolean singleValued;

	public static void show(Fragment parent, CombatMeleeTalent value, int requestCode) {
		InlineEditFightDialog dialog = new InlineEditFightDialog();

		Bundle args = new Bundle();
		// TODO value should be set as argument
		dialog.talent = value;
		dialog.setArguments(args);
		dialog.setTargetFragment(parent, requestCode);
		dialog.show(parent.getFragmentManager(), TAG);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		Bundle args = getArguments();
		// TODO value should be set as argument
		// Value value = (Value) args.get(KEY_VALUE);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(talent.getName());

        LayoutInflater inflater = LayoutInflater.from(builder.getContext());
		View popupcontent = inflater.inflate(R.layout.popup_edit_fight, null, false);
        builder.setView(popupcontent);
		numberPicker = (NumberPicker) popupcontent.findViewById(R.id.popup_edit_text);
		editAt = (NumberPicker) popupcontent.findViewById(R.id.popup_edit_at);
		editPa = (NumberPicker) popupcontent.findViewById(R.id.popup_edit_pa);

		textFreeValue = (TextView) popupcontent.findViewById(R.id.popup_edit_free_value);
		textFreeLabel = (TextView) popupcontent.findViewById(R.id.popup_edit_free_label);

		labels = popupcontent.findViewById(R.id.popup_edit_labels);

		numberPicker.setOnValueChangedListener(this);
		editPa.setOnValueChangedListener(this);
		editAt.setOnValueChangedListener(this);

		if (talent != null && talent.getReferenceValue() != null)
			builder.setNegativeButton("Reset", this);

		builder.setPositiveButton(android.R.string.ok, this);
		singleValued = false;

		valueTotal = talent;
		valueAt = talent.getAttack();
		valuePa = talent.getDefense();

		numberPicker.setMinValue(valueTotal.getMinimum());
		numberPicker.setMaxValue(valueTotal.getMaximum());
		numberPicker.setValue(valueTotal.getValue());

		if (valueAt != null) {
			editAt.setMinValue(valueAt.getMinimum());
			editAt.setMaxValue(valueAt.getMaximum());
			editAt.setValue(valueAt.getValue());
		} else {
			singleValued = true;
		}

		if (valuePa != null) {
			editPa.setMinValue(valuePa.getMinimum());
			editPa.setMaxValue(valuePa.getMaximum());
			editPa.setValue(valuePa.getValue());

		} else {
			singleValued = true;
		}

		numberPicker.setWrapSelectorWheel(false);
        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		editAt.setWrapSelectorWheel(false);
        editAt.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		editPa.setWrapSelectorWheel(false);
        editPa.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

		if (singleValued) {

			// we have to add the baseValue of the singlevalued entry
			if (valueAt != null) {
				numberPicker.setMinValue(valueAt.getBaseValue() + valueTotal.getMinimum());
				numberPicker.setMaxValue(valueAt.getBaseValue() + valueTotal.getMaximum());
				numberPicker.setValue(valueAt.getBaseValue() + valueTotal.getValue());
			} else if (valuePa != null) {
				numberPicker.setMinValue(valuePa.getBaseValue() + valueTotal.getMinimum());
				numberPicker.setMaxValue(valuePa.getBaseValue() + valueTotal.getMaximum());
				numberPicker.setValue(valuePa.getBaseValue() + valueTotal.getValue());
			}

			editAt.setVisibility(View.GONE);
			editPa.setVisibility(View.GONE);
			labels.setVisibility(View.GONE);
			textFreeValue.setVisibility(View.GONE);
			textFreeLabel.setVisibility(View.GONE);
		} else {
			editAt.setVisibility(View.VISIBLE);
			editPa.setVisibility(View.VISIBLE);
			labels.setVisibility(View.VISIBLE);
			textFreeValue.setVisibility(View.VISIBLE);
			textFreeLabel.setVisibility(View.VISIBLE);
		}

		AlertDialog dialog = builder.create();
		updateView(dialog);
		dialog.setCanceledOnTouchOutside(true);
		return dialog;

	}

	public CombatMeleeTalent getValue() {
		return talent;
	}

	private void updateView(AlertDialog dialog) {

		if (singleValued) {

		} else {

			int talent = numberPicker.getValue();
			int free = talent;
			if (valueAt != null) {
				free -= (editAt.getValue() - valueAt.getBaseValue());
				editAt.setMinValue(valueAt.getMinimum());
				editAt.setMaxValue(valueAt.getBaseValue() + talent);

			}
			if (valuePa != null) {
				free -= (editPa.getValue() - valuePa.getBaseValue());
				editPa.setMinValue(valuePa.getMinimum());
				editPa.setMaxValue(valuePa.getBaseValue() + talent);
			}

			if (free < 0)
				textFreeValue.setTextColor(getResources().getColor(R.color.ValueRed));
			else if (free > 0)
				textFreeValue.setTextColor(getResources().getColor(R.color.ValueGreen));
			else {
				textFreeValue.setTextColor(Util.getThemeColors(getActivity(), android.R.attr.textColorPrimary));
			}
			textFreeValue.setText(Util.toString(free));

			if (dialog.getButton(AlertDialog.BUTTON_POSITIVE) != null)
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(free >= 0);

		}

	}

	@Override
	public void onClick(DialogInterface paramDialogInterface, int paramInt) {
		switch (paramInt) {
		case DialogInterface.BUTTON_POSITIVE:
			if (singleValued) {
				if (valueAt != null) {
					valueTotal.setValue(numberPicker.getValue() - valueAt.getBaseValue());
					valueAt.setValue(valueAt.getBaseValue() + valueTotal.getValue());
				}
				if (valuePa != null) {
					valueTotal.setValue(numberPicker.getValue() - valuePa.getBaseValue());
					valuePa.setValue(valuePa.getBaseValue() + valueTotal.getValue());
				}
			} else {
				valueTotal.setValue(numberPicker.getValue());
				if (valueAt != null) {
					valueAt.setValue(editAt.getValue());
				}
				if (valuePa != null) {
					valuePa.setValue(editPa.getValue());
				}
			}
			Util.hideKeyboard(numberPicker);
			dismiss();

			break;
		case DialogInterface.BUTTON_NEGATIVE:
			valueTotal.reset();

			if (valueAt != null) {
				valueAt.reset();
			}
			if (valuePa != null) {
				valuePa.reset();
			}
			Util.hideKeyboard(numberPicker);
			dismiss();
			break;
		}

	}

	@Override
	public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
		updateView((AlertDialog) getDialog());
	}

}
