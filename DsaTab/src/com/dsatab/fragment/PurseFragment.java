package com.dsatab.fragment;

import java.util.List;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.NumericWheelAdapter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.dsatab.R;
import com.dsatab.data.Hero;
import com.dsatab.data.Purse;
import com.dsatab.data.Purse.Currency;
import com.dsatab.data.Purse.PurseUnit;
import com.dsatab.data.adapter.SpinnerSimpleAdapter;
import com.dsatab.util.Debug;

public class PurseFragment extends BaseFragment implements OnItemSelectedListener, OnWheelChangedListener {

	private Spinner currencySpinner;

	private WheelView[] picker;
	private NumericWheelAdapter pickerAdapter;
	private TextView[] labels;

	private Purse purse;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = configureContainerView(inflater.inflate(R.layout.sheet_purse, container, false));

		currencySpinner = (Spinner) root.findViewById(R.id.sp_currency);

		picker = new WheelView[4];
		picker[0] = (WheelView) root.findViewById(R.id.popup_purse_dukat);
		picker[1] = (WheelView) root.findViewById(R.id.popup_purse_silver);
		picker[2] = (WheelView) root.findViewById(R.id.popup_purse_heller);
		picker[3] = (WheelView) root.findViewById(R.id.popup_purse_kreuzer);

		labels = new TextView[4];
		labels[0] = (TextView) root.findViewById(R.id.tv_currency1);
		labels[1] = (TextView) root.findViewById(R.id.tv_currency2);
		labels[2] = (TextView) root.findViewById(R.id.tv_currency3);
		labels[3] = (TextView) root.findViewById(R.id.tv_currency4);

		return root;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		if (currencySpinner != null) {
			currencySpinner.setAdapter(new SpinnerSimpleAdapter<Currency>(getActivity(), Currency.values()));
		} else {
			Debug.error("Spinner was NULL!?!?!");
		}

		pickerAdapter = new NumericWheelAdapter(getActivity(), 0, 9999);

		for (int i = 0; i < picker.length; i++) {
			picker[i].setOnWheelChangedListeners(this);
			picker[i].setViewAdapter(pickerAdapter);
		}
		super.onActivityCreated(savedInstanceState);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.activity.BaseMainActivity#onHeroLoaded(com.dsatab.data.Hero)
	 */
	@Override
	public void onHeroLoaded(Hero hero) {

		purse = hero.getPurse();

		if (purse.getActiveCurrency() == null) {
			purse.setActiveCurrency(Currency.Mittelreich);
		}

		int index = -1;
		Currency[] values = Currency.values();
		for (int i = 0; i < values.length; i++) {
			if (values[i].equals(purse.getActiveCurrency())) {
				index = i;
				break;
			}
		}
		updateCurrency(purse.getActiveCurrency());
		currencySpinner.setSelection(index);
		currencySpinner.setOnItemSelectedListener(this);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see kankan.wheel.widget.OnWheelChangedListener#onChanged(kankan.wheel.widget .WheelView, int, int)
	 */
	@Override
	public void onWheelChanged(WheelView wheel, int oldValue, int newValue) {
		if (purse != null) {
			PurseUnit unit = (PurseUnit) wheel.getTag();
			if (unit != null) {
				purse.setCoins(unit, pickerAdapter.getItem(wheel.getCurrentItem()));
			}
		}
	}

	private void updateCurrency(Currency c) {

		List<PurseUnit> units = c.units();
		for (int i = 0; i < units.size(); i++) {
			picker[i].setVisibility(View.VISIBLE);
			picker[i].setTag(units.get(i));
			picker[i].setCurrentItem(pickerAdapter.getPosition(purse.getCoins(units.get(i))));

			labels[i].setVisibility(View.VISIBLE);
			labels[i].setText(units.get(i).xmlName());
		}

		for (int i = units.size(); i < 4; i++) {
			picker[i].setVisibility(View.GONE);
			picker[i].setTag(null);
			labels[i].setVisibility(View.GONE);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.AdapterView.OnItemSelectedListener#onItemSelected(android .widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		if (purse != null) {
			Currency cur = (Currency) parent.getItemAtPosition(position);
			purse.setActiveCurrency(cur);
			updateCurrency(cur);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.AdapterView.OnItemSelectedListener#onNothingSelected(android .widget.AdapterView)
	 */
	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

}
