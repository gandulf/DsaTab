package com.dsatab.fragment.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.dsatab.R;
import com.dsatab.data.ArmorAttribute;
import com.dsatab.data.Attribute;
import com.dsatab.data.CombatDistanceTalent;
import com.dsatab.data.Hero.CombatStyle;
import com.dsatab.data.Value;
import com.dsatab.data.enums.AttributeType;
import com.dsatab.util.Debug;
import com.dsatab.util.NumberPickerUtils;
import com.dsatab.util.Util;

public class InlineEditDialog extends AppCompatDialogFragment implements android.content.DialogInterface.OnClickListener,View.OnClickListener,
		OnCheckedChangeListener {

	public static final String TAG = "InlineEditDialog";

    private enum InputMode {
        Slider,EditText
    }

	private Value value;
    private InputMode mode = InputMode.EditText;

    private EditText editValue;
    private Button btnPlus,btnMinus;

	private NumberPicker numberPicker;
	private NumberPickerUtils numberPickerUtils;

	private CompoundButton combatStyleBtn;
	private CheckBox beCalculation;

	public static void show(Fragment parent, Value value, int requestCode) {
		InlineEditDialog dialog = new InlineEditDialog();

		Bundle args = new Bundle();
		// TODO value should be set as argument
		dialog.value = value;
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
		if (value != null)
			builder.setTitle(value.getName());


		if (value != null && value.getReferenceValue() != null)
			builder.setNegativeButton("Reset", this);

		builder.setPositiveButton(android.R.string.ok, this);

        LayoutInflater inflater = LayoutInflater.from(builder.getContext());
		View popupcontent = inflater.inflate(R.layout.popup_edit,null,false);
        builder.setView(popupcontent);


		numberPicker = (NumberPicker) popupcontent.findViewById(R.id.popup_edit_text);
		numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		numberPickerUtils = new NumberPickerUtils(numberPicker);

        editValue = (EditText) popupcontent.findViewById(R.id.popup_edit_value);
        editValue.setText("");

		combatStyleBtn = (CompoundButton) popupcontent.findViewById(R.id.popup_edit_combat_style);
		combatStyleBtn.setOnCheckedChangeListener(this);

		beCalculation = (CheckBox) popupcontent.findViewById(R.id.popup_edit_be_calculation);
		beCalculation.setOnCheckedChangeListener(this);

		if (value != null) {

			Integer currentValue = value.getValue();
			numberPickerUtils.setMinValue(value.getMinimum());
			numberPickerUtils.setMaxValue(value.getMaximum());

			if (currentValue != null) {
				numberPickerUtils.setValue(currentValue);
                editValue.append(String.valueOf(currentValue));
			} else {
				Debug.error("Setting value was null:" + value);
				numberPickerUtils.setValue(0);
                editValue.append("0");
			}
			numberPicker.setEnabled(true);
			numberPicker.setWrapSelectorWheel(false);

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

		}


        btnMinus = (Button) popupcontent.findViewById(R.id.popup_edit_value_minus);
        btnMinus.setOnClickListener(this);
        btnPlus = (Button) popupcontent.findViewById(R.id.popup_edit_value_plus);
        btnPlus.setOnClickListener(this);
        editValue.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                accept();
                return true;
            }
        });

        numberPicker.setVisibility(mode == InputMode.Slider ? View.VISIBLE : View.GONE);
        popupcontent.findViewById(R.id.popup_edit_value_container).setVisibility(mode == InputMode.EditText ? View.VISIBLE : View.GONE);

        editValue.setSelection(0,editValue.getText().length());

		// Now we can build the dialog.
		AlertDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(true);

        if (mode == InputMode.EditText)
            Util.showKeyboard(editValue);
		return dialog;
	}

	protected Value getValue() {
		return value;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (buttonView == combatStyleBtn) {

            if (isChecked)
                combatStyleBtn.setText(getText(R.string.combat_style)+" ("+getText(R.string.offensive)+")");
            else
                combatStyleBtn.setText(getText(R.string.combat_style)+" ("+getText(R.string.defensive)+")");

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
            int currentValue = 0;
            switch (mode) {
                case Slider:
                    currentValue = numberPickerUtils.getValue();
                    break;
                case EditText:
                    currentValue = Util.parseInt(editValue.getText().toString(),0);
                    break;
            }

            if (currentValue < value.getMinimum())
                currentValue = value.getMinimum();
            else if (currentValue > value.getMaximum())
                currentValue = value.getMaximum();

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

    @Override
    public void onClick(View v) {
        int currentValue = Util.parseInt(editValue.getText().toString(),0);
        switch (v.getId()) {
            case R.id.popup_edit_value_minus:
                if (currentValue > value.getMinimum()) {
                    currentValue--;
                    editValue.setText("");
                    editValue.append(String.valueOf(currentValue));
                    editValue.setSelection(0,editValue.getText().length());
                }
                break;
            case R.id.popup_edit_value_plus:
                if (currentValue < value.getMaximum()) {
                    currentValue++;
                    editValue.setText("");
                    editValue.append(String.valueOf(currentValue));
                    editValue.setSelection(0,editValue.getText().length());
                }
                break;
        }
    }

    @Override
	public void onClick(DialogInterface paramDialogInterface, int paramInt) {
		switch (paramInt) {
		case DialogInterface.BUTTON_POSITIVE:
			accept();
			break;
		case DialogInterface.BUTTON_NEGATIVE:
			value.reset();
			dismiss();
			break;
		}

	}

}
