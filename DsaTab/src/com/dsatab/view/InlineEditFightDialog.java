package com.dsatab.view;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.NumericWheelAdapter;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.dsatab.R;
import com.dsatab.data.CombatMeleeAttribute;
import com.dsatab.data.CombatMeleeTalent;
import com.dsatab.data.Value;
import com.dsatab.util.Util;

public class InlineEditFightDialog extends AlertDialog implements DialogInterface.OnClickListener,
		OnWheelChangedListener {

	private CombatMeleeTalent talent;
	private Value valueTotal;

	private CombatMeleeAttribute valueAt;
	private CombatMeleeAttribute valuePa;

	private WheelView editText;
	private NumericWheelAdapter editTextAdapter, editAtAdapter, editPaAdapter;
	private WheelView editAt;
	private WheelView editPa;

	private TextView textFreeValue, textFreeLabel;
	private View labels;

	private boolean singleValued;

	public InlineEditFightDialog(Context context, CombatMeleeTalent value) {
		super(context);
		init();
		setValue(value);
	}

	public CombatMeleeTalent getValue() {
		return talent;
	}

	public void setValue(CombatMeleeTalent combatTalent) {
		this.talent = combatTalent;

		singleValued = false;

		valueTotal = combatTalent;
		valueAt = combatTalent.getAttack();
		valuePa = combatTalent.getDefense();

		editTextAdapter.setRange(valueTotal.getMinimum(), valueTotal.getMaximum());
		editText.setCurrentItem(editTextAdapter.getPosition(valueTotal.getValue()));

		if (valueAt != null) {
			editAtAdapter.setRange(valueAt.getMinimum(), valueAt.getMaximum());
			editAt.setCurrentItem(editAtAdapter.getPosition(valueAt.getValue()));
		} else {
			singleValued = true;
		}

		if (valuePa != null) {
			editPaAdapter.setRange(valuePa.getMinimum(), valuePa.getMaximum());
			editPa.setCurrentItem(editPaAdapter.getPosition(valuePa.getValue()));

		} else {
			singleValued = true;
		}
		if (getButton(BUTTON_NEGATIVE) != null)
			getButton(BUTTON_NEGATIVE).setEnabled(valueTotal.getReferenceValue() != null);

		if (singleValued) {

			// we have to add the baseValue of the singlevalued entry
			if (valueAt != null) {
				editTextAdapter.setRange(valueAt.getBaseValue() + valueTotal.getMinimum(), valueAt.getBaseValue()
						+ valueTotal.getMaximum());
				editText.setCurrentItem(editTextAdapter.getPosition(valueAt.getBaseValue() + valueTotal.getValue()));
			} else if (valuePa != null) {
				editTextAdapter.setRange(valuePa.getBaseValue() + valueTotal.getMinimum(), valuePa.getBaseValue()
						+ valueTotal.getMaximum());
				editText.setCurrentItem(editTextAdapter.getPosition(valuePa.getBaseValue() + valueTotal.getValue()));
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

		updateView();
	}

	private void updateView() {

		if (singleValued) {

		} else {

			int talent = editTextAdapter.getItem(editText.getCurrentItem());
			int free = talent;
			if (valueAt != null) {
				free -= (editAtAdapter.getItem(editAt.getCurrentItem()) - valueAt.getBaseValue());
				editAtAdapter.setRange(valueAt.getMinimum(), valueAt.getBaseValue() + talent);
			}
			if (valuePa != null) {
				free -= (editPaAdapter.getItem(editPa.getCurrentItem()) - valuePa.getBaseValue());
				editPaAdapter.setRange(valuePa.getMinimum(), valuePa.getBaseValue() + talent);
			}

			if (free < 0)
				textFreeValue.setTextColor(getContext().getResources().getColor(R.color.ValueRed));
			else if (free > 0)
				textFreeValue.setTextColor(getContext().getResources().getColor(R.color.ValueGreen));
			else {
				textFreeValue.setTextColor(Util.getThemeColors(getContext(), android.R.attr.textColorPrimary));
			}
			textFreeValue.setText(Util.toString(free));

			if (getButton(BUTTON_POSITIVE) != null)
				getButton(BUTTON_POSITIVE).setEnabled(free >= 0);

		}

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

			if (singleValued) {
				if (valueAt != null) {
					valueTotal.setValue(editTextAdapter.getItem(editText.getCurrentItem()) - valueAt.getBaseValue());
					valueAt.setValue(valueAt.getBaseValue() + valueTotal.getValue());
				}
				if (valuePa != null) {
					valueTotal.setValue(editTextAdapter.getItem(editText.getCurrentItem()) - valuePa.getBaseValue());
					valuePa.setValue(valuePa.getBaseValue() + valueTotal.getValue());
				}
			} else {
				valueTotal.setValue(editTextAdapter.getItem(editText.getCurrentItem()));
				if (valueAt != null) {
					valueAt.setValue(editAtAdapter.getItem(editAt.getCurrentItem()));
				}
				if (valuePa != null) {
					valuePa.setValue(editPaAdapter.getItem(editPa.getCurrentItem()));
				}
			}
			Util.hideKeyboard(editText);
			dismiss();
			break;
		case BUTTON_NEGATIVE:
			valueTotal.reset();

			if (valueAt != null) {
				valueAt.reset();
			}
			if (valuePa != null) {
				valuePa.reset();
			}
			Util.hideKeyboard(editText);
			dismiss();
			break;
		}

	}

	private void init() {
		setCanceledOnTouchOutside(true);

		View popupcontent = LayoutInflater.from(getContext()).inflate(R.layout.popup_edit_fight, null, false);
		popupcontent.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		setView(popupcontent);

		editText = (WheelView) popupcontent.findViewById(R.id.popup_edit_text);
		editAt = (WheelView) popupcontent.findViewById(R.id.popup_edit_at);
		editPa = (WheelView) popupcontent.findViewById(R.id.popup_edit_pa);

		editTextAdapter = new NumericWheelAdapter(getContext());
		editText.setViewAdapter(editTextAdapter);
		editAtAdapter = new NumericWheelAdapter(getContext());
		editAt.setViewAdapter(editAtAdapter);
		editPaAdapter = new NumericWheelAdapter(getContext());
		editPa.setViewAdapter(editPaAdapter);

		textFreeValue = (TextView) popupcontent.findViewById(R.id.popup_edit_free_value);
		textFreeLabel = (TextView) popupcontent.findViewById(R.id.popup_edit_free_label);

		labels = popupcontent.findViewById(R.id.popup_edit_labels);

		editText.setOnWheelChangedListeners(this);
		editPa.setOnWheelChangedListeners(this);
		editAt.setOnWheelChangedListeners(this);

		setButton(BUTTON_POSITIVE, "Ok", this);
		setButton(BUTTON_NEGATIVE, "Reset", this);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see kankan.wheel.widget.OnWheelChangedListener#onChanged(kankan.wheel.widget .WheelView, int, int)
	 */
	@Override
	public void onWheelChanged(WheelView wheel, int oldValue, int newValue) {
		updateView();
	}

}
