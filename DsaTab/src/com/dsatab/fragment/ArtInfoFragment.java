package com.dsatab.fragment;

import net.simonvt.numberpicker.NumberPicker;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.data.Art;
import com.dsatab.data.ArtInfo;
import com.dsatab.data.Hero;
import com.dsatab.data.Value;
import com.dsatab.util.Debug;
import com.j256.ormlite.dao.RuntimeExceptionDao;

public class ArtInfoFragment extends BaseFragment {

	private static final String DATA_INTENT_ART = "art";

	private Art art;

	private View popupcontent = null;

	private boolean editMode = false;

	public static ArtInfoFragment newInstance(Art spell) {
		ArtInfoFragment f = new ArtInfoFragment();

		// Supply num input as an argument.
		Bundle args = new Bundle();
		args.putSerializable(DATA_INTENT_ART, spell);
		f.setArguments(args);

		return f;
	}

	public Art getArt() {
		return art;
	}

	protected void setArtValue(Value value) {

		NumberPicker numberPicker = (NumberPicker) popupcontent.findViewById(R.id.popup_edit_value);
		if (numberPicker != null) {
			if (value != null) {

				Integer currentValue = value.getValue();
				numberPicker.setMinValue(value.getMinimum());
				numberPicker.setMaxValue(value.getMaximum());
				if (currentValue != null) {
					numberPicker.setVisibility(View.VISIBLE);
					numberPicker.setValue(currentValue);
				} else {
					numberPicker.setValue(0);
					numberPicker.setVisibility(View.GONE);
				}
				numberPicker.setEnabled(true);
			} else {
				numberPicker.setEnabled(false);
			}
		}
	}

	protected void applyArtValue() {
		try {
			NumberPicker numberPicker = (NumberPicker) popupcontent.findViewById(R.id.popup_edit_value);
			if (numberPicker != null && numberPicker.getVisibility() == View.VISIBLE) {
				art.setValue(numberPicker.getValue());
			}
		} catch (NumberFormatException e) {
			Debug.error(e);
		}
	}

	protected void setArt(Art art) {
		this.art = art;

		ArtInfo info = art.getInfo();

		setArtValue(art);

		set(R.id.popup_liturgie_title, art.getTitle());
		set(R.id.popup_liturgie_type, art.getGroupType().getName());
		if (info != null) {
			set(R.id.popup_liturgie_costs, info.getCosts());
			set(R.id.popup_liturgie_effect, info.getEffect());
			set(R.id.popup_liturgie_probe, info.getProbe());
			set(R.id.popup_liturgie_castduration, info.getCastDurationDetailed());
			set(R.id.popup_liturgie_effectduration, info.getEffectDuration());
			set(R.id.popup_liturgie_origin, info.getOrigin());
			set(R.id.popup_liturgie_range, info.getRangeDetailed());
			set(R.id.popup_liturgie_target, info.getTargetDetailed());
			set(R.id.popup_liturgie_merkmal, info.getMerkmale());
			set(R.id.popup_liturgie_source, info.getSource());
		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		popupcontent = inflater.inflate(R.layout.sheet_art_info, container, false);
		return popupcontent;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

		if (!editMode) {
			inflater.inflate(R.menu.menuitem_ok, menu);
			inflater.inflate(R.menu.menuitem_edit, menu);
		} else {
			inflater.inflate(R.menu.menuitem_ok, menu);
			inflater.inflate(R.menu.menuitem_cancel, menu);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.option_ok:
			accept();
			return true;
		case R.id.option_cancel:
			cancel();
			return true;
		case R.id.option_edit:
			switchMode(true);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		art = (Art) getArguments().getSerializable(DATA_INTENT_ART);

		setArt(art);
		switchMode(false);
	}

	public Art accept() {

		if (editMode) {
			try {
				NumberPicker numberPicker = (NumberPicker) popupcontent.findViewById(R.id.popup_edit_value);
				if (numberPicker != null && numberPicker.getVisibility() == View.VISIBLE) {
					art.setValue(numberPicker.getValue());
				}

				ArtInfo info = art.getInfo();

				info.setCastDuration(getValue(R.id.popup_liturgie_castduration_edit));
				info.setCosts(getValue(R.id.popup_liturgie_costs_edit));
				info.setEffect(getValue(R.id.popup_liturgie_effect_edit));
				info.setEffectDuration(getValue(R.id.popup_liturgie_effectduration_edit));
				info.setMerkmale(getValue(R.id.popup_liturgie_merkmal_edit));
				info.setOrigin(getValue(R.id.popup_liturgie_origin_edit));
				info.setProbe(getValue(R.id.popup_liturgie_probe_edit));
				info.setRange(getValue(R.id.popup_liturgie_range_edit));
				info.setSource(getValue(R.id.popup_liturgie_source_edit));
				info.setTarget(getValue(R.id.popup_liturgie_target_edit));

				art.setProbePattern(info.getProbe());

				RuntimeExceptionDao<ArtInfo, Long> dao = DsaTabApplication.getInstance().getDBHelper()
						.getRuntimeExceptionDao(ArtInfo.class);
				dao.createOrUpdate(info);

				art.fireValueChangedEvent();
				Toast.makeText(getActivity(), "Kunstinformationen wurden gespeichert", Toast.LENGTH_SHORT).show();

				getBaseActivity().getSupportFragmentManager().popBackStack();
			} catch (NumberFormatException e) {
				Debug.error(e);
			}
		} else {
			getBaseActivity().getSupportFragmentManager().popBackStack();
		}
		return art;
	}

	/**
	 * 
	 */
	public void cancel() {
		getBaseActivity().getSupportFragmentManager().popBackStack();
	}

	private void switchMode(boolean edit) {
		this.editMode = edit;
		if (art != null) {
			ArtInfo info = art.getInfo();

			edit(0, R.id.popup_liturgie_castduration, R.id.popup_liturgie_castduration_edit,
					info.getCastDurationDetailed(), edit);
			edit(0, R.id.popup_liturgie_effect, R.id.popup_liturgie_effect_edit, info.getEffect(), edit);
			edit(0, R.id.popup_liturgie_effectduration, R.id.popup_liturgie_effectduration_edit,
					info.getEffectDuration(), edit);
			edit(0, R.id.popup_liturgie_merkmal, R.id.popup_liturgie_merkmal_edit, info.getMerkmale(), edit);
			edit(0, R.id.popup_liturgie_origin, R.id.popup_liturgie_origin_edit, info.getOrigin(), edit);
			edit(0, R.id.popup_liturgie_probe, R.id.popup_liturgie_probe_edit, info.getProbe(), edit);
			edit(0, R.id.popup_liturgie_costs, R.id.popup_liturgie_costs_edit, info.getCosts(), edit);
			edit(0, R.id.popup_liturgie_range, R.id.popup_liturgie_range_edit, info.getRangeDetailed(), edit);
			edit(0, R.id.popup_liturgie_source, R.id.popup_liturgie_source_edit, info.getSource(), edit);
			edit(0, R.id.popup_liturgie_target, R.id.popup_liturgie_target_edit, info.getTargetDetailed(), edit);
		}

		getActivity().supportInvalidateOptionsMenu();

	}

	private void set(int tfid, CharSequence v) {
		TextView tf = (TextView) popupcontent.findViewById(tfid);
		if (tf != null) {
			tf.setText(v);
		}
	}

	private String getValue(int etid) {
		return ((EditText) findViewById(etid)).getText().toString();
	}

	private void edit(int rowId, int tfid, int etid, String v, boolean edit) {
		int editVisibility = edit ? View.VISIBLE : View.GONE;
		int viewVisibility = edit ? View.GONE : View.VISIBLE;

		TextView tf = (TextView) popupcontent.findViewById(tfid);
		TextView et = (TextView) popupcontent.findViewById(etid);

		if (tf != null && et != null) {
			tf.setVisibility(viewVisibility);
			et.setVisibility(editVisibility);
			et.setText(v);
		}

		if (edit && rowId != 0) {
			findViewById(rowId).setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onHeroLoaded(Hero hero) {

	}

}
