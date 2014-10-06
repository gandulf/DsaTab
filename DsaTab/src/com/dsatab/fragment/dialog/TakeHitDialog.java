package com.dsatab.fragment.dialog;

import java.util.List;

import uk.me.lewisdeane.ldialogs.CustomDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

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

public class TakeHitDialog extends DialogFragment implements DialogInterface.OnClickListener, OnItemSelectedListener {

	public static final String TAG = "TakeHitDialog";

	private static final String PREF_CONSIDER_RS = "takeHitDialog.considerRs";
	private static final String PREF_CONSIDER_WOUND = "takeHitDialog.considerWound";
	private static final String PREF_DAMAGE_TYPE = "takeHitDialog.damageType";

	private NumberPicker numberPicker;

	private CompoundButton rsConsideration, woundConsideration;
	private CompoundButton damageType;
	private Spinner targetZone;
	private TextView targetZoneLabel;

	private AbstractBeing being;
	private Position position;

	public static void show(Fragment parent, FragmentManager fragmentManager, AbstractBeing being, Position position,
			int requestCode) {
		TakeHitDialog dialog = new TakeHitDialog();

		Bundle args = new Bundle();
		// TODO value should be set as argument
		dialog.being = being;
		dialog.position = position;
		dialog.setArguments(args);
		if (parent != null) {
			dialog.setTargetFragment(parent, requestCode);
		}
		dialog.show(fragmentManager, TAG);

	}

	public static void show(Fragment parent, AbstractBeing being, Position position, int requestCode) {
		show(parent, parent.getFragmentManager(), being, position, requestCode);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
		builder.setDarkTheme(DsaTabApplication.getInstance().isDarkTheme());

		View popupcontent = builder.setView(R.layout.popup_take_hit);
		builder.setIcon(R.drawable.dsa_wound_patch);
		builder.setTitle("Treffer kassieren");

		targetZone = (Spinner) popupcontent.findViewById(R.id.popup_position);
		targetZoneLabel = (TextView) popupcontent.findViewById(R.id.popup_position_label);

		final List<Position> positions = DsaTabApplication.getInstance().getConfiguration().getArmorPositions();
		SpinnerSimpleAdapter<Position> typeAdapter = new SpinnerSimpleAdapter<Position>(builder.getContext(),
				android.R.layout.simple_spinner_item, positions);

		typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		targetZone.setAdapter(typeAdapter);
		targetZone.setOnItemSelectedListener(this);
		if (position != null) {
			targetZone.setSelection(positions.indexOf(position));
		}

		numberPicker = (NumberPicker) popupcontent.findViewById(R.id.popup_edit_text);
		numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		numberPicker.setMinValue(0);
		numberPicker.setMaxValue(50);
		numberPicker.setValue(0);
		numberPicker.setWrapSelectorWheel(false);

		rsConsideration = (CompoundButton) popupcontent.findViewById(R.id.popup_consider_rs);
		rsConsideration.setText(builder.getContext().getText(R.string.label_consider_rs) + " (" + getRS() + ")");
		rsConsideration.setChecked(DsaTabApplication.getPreferences().getBoolean(PREF_CONSIDER_RS, true));

		woundConsideration = (CompoundButton) popupcontent.findViewById(R.id.popup_consider_wound);
		woundConsideration.setChecked(DsaTabApplication.getPreferences().getBoolean(PREF_CONSIDER_WOUND, true));

		damageType = (CompoundButton) popupcontent.findViewById(R.id.popup_damage_type);
		damageType.setChecked(DsaTabApplication.getPreferences().getBoolean(PREF_DAMAGE_TYPE, true));

		builder.setPositiveButton(android.R.string.ok, this);
		builder.setNegativeButton(android.R.string.cancel, this);

		CustomDialog dialog = builder.create();

		dialog.setCanceledOnTouchOutside(true);

		return dialog;

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
		case DialogInterface.BUTTON_POSITIVE:
			accept();
			break;

		case DialogInterface.BUTTON_NEGATIVE:
			dismiss();
			break;
		}

	}

	@Override
	public void onItemSelected(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong) {
		if (paramAdapterView == targetZone) {
			rsConsideration.setText(DsaTabApplication.getInstance().getText(R.string.label_consider_rs) + " ("
					+ getRS() + ")");
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> paramAdapterView) {

	}

}
