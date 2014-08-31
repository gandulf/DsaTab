package com.dsatab.view.dialog;

import java.util.List;

import net.simonvt.numberpicker.NumberPicker;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences.Editor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.data.AbstractBeing;
import com.dsatab.data.Animal;
import com.dsatab.data.Attribute;
import com.dsatab.data.Hero;
import com.dsatab.data.WoundAttribute;
import com.dsatab.data.adapter.SpinnerSimpleAdapter;
import com.dsatab.data.enums.AttributeType;
import com.dsatab.data.enums.Position;
import com.dsatab.util.Util;

public class TakeHitDialog extends AlertDialog implements DialogInterface.OnClickListener, OnItemSelectedListener {

	private static final String PREF_CONSIDER_RS = "takeHitDialog.considerRs";
	private static final String PREF_CONSIDER_WOUND = "takeHitDialog.considerWound";
	private static final String PREF_DAMAGE_TYPE = "takeHitDialog.damageType";

	private NumberPicker numberPicker;

	private CompoundButton rsConsideration, woundConsideration;
	private ToggleButton damageType;
	private Spinner targetZone;
	private TextView targetZoneLabel;

	private Context context;
	private AbstractBeing being;

	public TakeHitDialog(Context context, AbstractBeing being, Position position) {
		super(context);
		this.context = context;
		this.being = being;
		init(position);
	}

	protected int getRS() {
		int rs = 0;
		Position damageZone = (Position) targetZone.getSelectedItem();

		if (being instanceof Animal) {
			Animal animal = (Animal) being;
			rs = animal.getAttributeValue(AttributeType.Rüstungsschutz);
		} else if (being instanceof Hero) {
			Hero hero = (Hero) being;

			switch (DsaTabApplication.getInstance().getConfiguration().getArmorType()) {
			case GesamtRuestung:
				rs = hero.getArmorRs();
				break;
			case ZonenRuestung:
				rs = hero.getArmorRs(damageZone);
			}
		}

		return rs;
	}

	private void accept() {

		boolean damageTp = damageType.isChecked();
		Position damageZone = (Position) targetZone.getSelectedItem();
		Position woundDamageZone = damageZone;
		if (woundDamageZone == Position.Ruecken)
			woundDamageZone = Position.Brust;

		int damage = numberPicker.getValue();

		if (rsConsideration.isChecked()) {
			damage = Math.max(0, damage - getRS());
		}

		int leDamage = 0;

		if (damageTp) {
			leDamage = damage;

			Attribute le = being.getAttribute(AttributeType.Lebensenergie_Aktuell);
			le.setValue(le.getValue() - leDamage);

		} else {
			Attribute au = being.getAttribute(AttributeType.Ausdauer_Aktuell);
			au.setValue(au.getValue() - damage);

			Attribute le = being.getAttribute(AttributeType.Lebensenergie_Aktuell);
			leDamage = damage / 2;
			le.setValue(le.getValue() - leDamage);
		}

		if (being instanceof Hero && woundConsideration.isChecked()) {
			Hero hero = (Hero) being;

			WoundAttribute woundAttribute = hero.getWounds().get(woundDamageZone);
			if (woundAttribute != null) {
				int wounds = 0;
				for (int ws : hero.getWundschwelle()) {
					if (leDamage > ws) {
						wounds++;
					}
				}

				if (wounds > 0) {
					woundAttribute.addValue(wounds);
				}
			}
		}

		Editor edit = DsaTabApplication.getPreferences().edit();
		edit.putBoolean(PREF_CONSIDER_RS, rsConsideration.isChecked());
		edit.putBoolean(PREF_CONSIDER_WOUND, woundConsideration.isChecked());
		edit.putBoolean(PREF_DAMAGE_TYPE, damageTp);
		edit.commit();

		Util.hideKeyboard(rsConsideration);
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
			dismiss();
			break;
		}

	}

	@Override
	public void onItemSelected(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong) {
		if (paramAdapterView == targetZone) {
			rsConsideration.setText(getContext().getText(R.string.label_consider_rs) + " (" + getRS() + ")");
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> paramAdapterView) {

	}

	private void init(Position position) {
		setCanceledOnTouchOutside(true);

		ViewGroup popupcontent = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.popup_take_hit, null, false);
		setView(popupcontent);
		setIcon(R.drawable.dsa_wound_patch);

		setTitle("Treffer kassieren");

		targetZone = (Spinner) popupcontent.findViewById(R.id.popup_position);
		targetZoneLabel = (TextView) popupcontent.findViewById(R.id.popup_position_label);

		final List<Position> positions = DsaTabApplication.getInstance().getConfiguration().getArmorPositions();
		SpinnerSimpleAdapter<Position> typeAdapter = new SpinnerSimpleAdapter<Position>(getContext(),
				android.R.layout.simple_spinner_item, positions);

		typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		targetZone.setAdapter(typeAdapter);
		targetZone.setOnItemSelectedListener(this);
		if (position != null) {
			targetZone.setSelection(positions.indexOf(position));
		}

		numberPicker = (NumberPicker) popupcontent.findViewById(R.id.popup_edit_text);
		numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		numberPicker.setWrapSelectorWheel(false);
		numberPicker.setMinValue(0);
		numberPicker.setMaxValue(50);
		numberPicker.setValue(0);

		rsConsideration = (CompoundButton) popupcontent.findViewById(R.id.popup_consider_rs);
		rsConsideration.setText(getContext().getText(R.string.label_consider_rs) + " (" + getRS() + ")");
		rsConsideration.setChecked(DsaTabApplication.getPreferences().getBoolean(PREF_CONSIDER_RS, true));

		woundConsideration = (CompoundButton) popupcontent.findViewById(R.id.popup_consider_wound);
		woundConsideration.setChecked(DsaTabApplication.getPreferences().getBoolean(PREF_CONSIDER_WOUND, true));

		damageType = (ToggleButton) popupcontent.findViewById(R.id.popup_damage_type);

		damageType.setTextOn("Trefferpunkte");
		damageType.setTextOff("Ausdauerpunkte");
		damageType.setChecked(DsaTabApplication.getPreferences().getBoolean(PREF_DAMAGE_TYPE, true));

		setButton(BUTTON_POSITIVE, getContext().getText(android.R.string.ok), this);
		setButton(BUTTON_NEGATIVE, getContext().getText(android.R.string.cancel), this);

	}

}
