package com.dsatab.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.dsatab.R;
import com.dsatab.activity.DsaTabActivity;
import com.dsatab.data.CombatProbe;
import com.dsatab.data.adapter.SpinnerSimpleAdapter;
import com.dsatab.data.enums.FeatureType;
import com.dsatab.data.items.DistanceWeapon;
import com.dsatab.data.items.EquippedItem;
import com.dsatab.data.items.Item;
import com.dsatab.util.Util;

public class ArcheryChooserDialog extends AlertDialog implements android.view.View.OnClickListener,
		OnItemClickListener, DialogInterface.OnClickListener {

	private int[] distanceProbe;
	private int[] sizeProbe;
	private int[] modificationValues;

	private final int SCHNELLSCHUSS_INDEX = 13;

	private EquippedItem equippedItem;

	private Spinner distanceSpinner, sizeSpinner;

	private TextView text1, text2, probeValue;

	private ListView othersList;

	private ImageButton iconLeft, iconRight;

	private int erschwernis;

	private DsaTabActivity main;

	public ArcheryChooserDialog(DsaTabActivity context) {
		super(context);
		this.main = context;
		init();
	}

	public EquippedItem getWeapon() {
		return equippedItem;
	}

	public void setWeapon(EquippedItem weapon) {
		this.equippedItem = weapon;

		Item item = equippedItem.getItem();
		if (weapon != null) {
			text1.setText(item.getTitle());
			text2.setText(item.getInfo());
		}
		iconLeft.setImageURI(item.getIconUri());
		iconLeft.setOnClickListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.content.DialogInterface.OnClickListener#onClick(android.content
	 * .DialogInterface, int)
	 */
	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		case BUTTON_POSITIVE:
			accept();
		case BUTTON_NEGATIVE:
			dismiss();
			break;
		}

	}

	private void accept() {
		CombatProbe combatProbe = equippedItem.getCombatProbeAttacke();
		combatProbe.getProbeInfo().setErschwernis(erschwernis);

		dismiss();
		main.checkProbe(combatProbe);
	}

	public void onClick(View v) {
		if (v == iconLeft) {
			accept();
		}
	}

	private void updateProbeValue() {
		erschwernis = 0;

		SparseBooleanArray checkedPositions = othersList.getCheckedItemPositions();
		if (checkedPositions != null) {
			for (int i = checkedPositions.size() - 1; i >= 0; i--) {
				if (checkedPositions.valueAt(i)) {
					erschwernis += modificationValues[checkedPositions.keyAt(i)];
				}
			}
		}

		if (distanceSpinner.getSelectedItemPosition() != Spinner.INVALID_POSITION)
			erschwernis += distanceProbe[distanceSpinner.getSelectedItemPosition()];

		if (sizeSpinner.getSelectedItemPosition() != Spinner.INVALID_POSITION)
			erschwernis += sizeProbe[sizeSpinner.getSelectedItemPosition()];

		probeValue.setText(getContext().getString(R.string.message_modifikator, Util.toProbe(erschwernis)));
	}

	@Override
	protected void onStart() {

		String[] distances = getContext().getResources().getStringArray(R.array.archeryDistance);
		if (equippedItem != null) {
			DistanceWeapon item = (DistanceWeapon) equippedItem.getItem().getSpecification(DistanceWeapon.class);
			if (item != null) {
				String from, to;

				for (int i = 0; i < distances.length; i++) {
					to = item.getDistance(i);

					if (to != null) {
						distances[i] += " (";
						if (i > 0) {
							from = item.getDistance(i - 1);
							distances[i] += from;
						}

						distances[i] += " bis " + to + "m)";
					}
				}
			}
		}

		SpinnerAdapter distanceAdapter = new SpinnerSimpleAdapter<String>(getContext(), distances);
		distanceSpinner.setAdapter(distanceAdapter);

		super.onStart();
	}

	private void init() {

		distanceProbe = getContext().getResources().getIntArray(R.array.archeryDistanceValues);
		sizeProbe = getContext().getResources().getIntArray(R.array.archerySizeValues);
		modificationValues = getContext().getResources().getIntArray(R.array.archeryModificationValues);

		setCanceledOnTouchOutside(true);

		RelativeLayout popupcontent = (RelativeLayout) LayoutInflater.from(getContext()).inflate(
				R.layout.popup_archery, null, false);
		popupcontent.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		setView(popupcontent);

		distanceSpinner = (Spinner) popupcontent.findViewById(R.id.archery_distance);
		distanceSpinner.setPrompt("Entfernung");
		distanceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				updateProbeValue();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}

		});
		sizeSpinner = (Spinner) popupcontent.findViewById(R.id.archery_size);
		sizeSpinner.setPrompt("Größe");
		sizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				updateProbeValue();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}

		});

		probeValue = (TextView) popupcontent.findViewById(R.id.archery_probe);

		text1 = (TextView) popupcontent.findViewById(android.R.id.text1);
		text2 = (TextView) popupcontent.findViewById(android.R.id.text2);

		iconLeft = (ImageButton) popupcontent.findViewById(android.R.id.icon1);
		iconLeft.setVisibility(View.VISIBLE);
		iconLeft.setFocusable(true);
		iconLeft.setClickable(true);
		iconRight = (ImageButton) popupcontent.findViewById(android.R.id.icon2);
		iconRight.setVisibility(View.GONE);

		String[] modificationStrings = getContext().getResources().getStringArray(R.array.archeryModificationStrings);
		if (main.getHero().hasFeature(FeatureType.Meisterschütze)) {
			modificationStrings[SCHNELLSCHUSS_INDEX] = "Schnellschuß +0";
			modificationValues[SCHNELLSCHUSS_INDEX] = 0;

		} else if (main.getHero().hasFeature(FeatureType.Scharfschütze)) {
			modificationStrings[SCHNELLSCHUSS_INDEX] = "Schnellschuß +1";
			modificationValues[SCHNELLSCHUSS_INDEX] = 1;
		}

		othersList = (ListView) popupcontent.findViewById(R.id.archery_others);
		othersList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		othersList.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_multiple_choice,
				modificationStrings));
		othersList.setOnItemClickListener(this);

		setButton(BUTTON_POSITIVE, "Angreifen", this);
		setButton(BUTTON_NEGATIVE, getContext().getString(R.string.label_cancel), this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget
	 * .AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
		updateProbeValue();
	}

}
