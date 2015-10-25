package com.dsatab.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.dsatab.R;
import com.dsatab.activity.BaseEditActivity;
import com.dsatab.data.Art;
import com.dsatab.data.ArtInfo;
import com.dsatab.data.enums.ArtGroupType;
import com.dsatab.util.Debug;

public class ArtInfoFragment extends BaseEditFragment {

	public static final String DATA_INTENT_ART_INFO = "artInfo";
	public static final String DATA_INTENT_ART_TITLE = "artTitle";
	public static final String DATA_INTENT_ART_GROUP_TYPE = "artGroupType";
	public static final String DATA_INTENT_ART_VALUE = "artValue";
	public static final String DATA_INTENT_ART_VALUE_MIN = "artValueMin";
	public static final String DATA_INTENT_ART_VALUE_MAX = "artValueMax";

	private ArtInfo artInfo;

	private View popupcontent = null;

	private boolean editMode = false;

	public static void edit(Activity activity, Art art, int requestCode) {
		Intent intent = new Intent(activity, BaseEditActivity.class);
		intent.setAction(Intent.ACTION_EDIT);
		intent.putExtra(BaseEditActivity.EDIT_FRAGMENT_CLASS, ArtInfoFragment.class);
		intent.putExtra(DATA_INTENT_ART_INFO, art.getInfo());
		if (art.getValue() != null) {
			intent.putExtra(DATA_INTENT_ART_VALUE, (int) art.getValue());
		}
		intent.putExtra(DATA_INTENT_ART_VALUE_MIN, art.getMinimum());
		intent.putExtra(DATA_INTENT_ART_VALUE_MAX, art.getMaximum());
		intent.putExtra(DATA_INTENT_ART_TITLE, art.getTitle());
		intent.putExtra(DATA_INTENT_ART_GROUP_TYPE, art.getGroupType());

		activity.startActivityForResult(intent, requestCode);
	}

	public static void view(Activity activity, Art art, int requestCode) {
		Intent intent = new Intent(activity, BaseEditActivity.class);
		intent.setAction(Intent.ACTION_VIEW);
		intent.putExtra(BaseEditActivity.EDIT_FRAGMENT_CLASS, ArtInfoFragment.class);
		intent.putExtra(DATA_INTENT_ART_INFO, art.getInfo());
		if (art.getValue() != null) {
			intent.putExtra(DATA_INTENT_ART_VALUE, (int) art.getValue());
		}
		intent.putExtra(DATA_INTENT_ART_VALUE_MIN, art.getMinimum());
		intent.putExtra(DATA_INTENT_ART_VALUE_MAX, art.getMaximum());
		intent.putExtra(DATA_INTENT_ART_TITLE, art.getTitle());
		intent.putExtra(DATA_INTENT_ART_GROUP_TYPE, art.getGroupType());

		activity.startActivityForResult(intent, requestCode);
	}

	protected void setArtValue(Integer value, Integer min, Integer max) {

		NumberPicker numberPicker = (NumberPicker) popupcontent.findViewById(R.id.popup_edit_value);
		if (numberPicker != null) {
			if (value != null) {
				numberPicker.setMinValue(min);
				numberPicker.setMaxValue(max);
				numberPicker.setValue(value);
				numberPicker.setVisibility(View.VISIBLE);
			} else {
				numberPicker.setVisibility(View.GONE);
			}
		}
	}

	protected void setArtInfo(CharSequence title, ArtGroupType artGroupType, ArtInfo info) {

		set(R.id.popup_liturgie_title, title);
		set(R.id.popup_liturgie_type, artGroupType.getName());
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
			inflater.inflate(R.menu.menuitem_edit, menu);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
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

		artInfo = (ArtInfo) getExtra().getSerializable(DATA_INTENT_ART_INFO);

		CharSequence title = getExtra().getCharSequence(DATA_INTENT_ART_TITLE);
		Integer value = getExtra().getInt(DATA_INTENT_ART_VALUE, Integer.MIN_VALUE);
		int min = getExtra().getInt(DATA_INTENT_ART_VALUE_MIN);
		int max = getExtra().getInt(DATA_INTENT_ART_VALUE_MAX);
		ArtGroupType artGroupType = (ArtGroupType) getExtra().getSerializable(DATA_INTENT_ART_GROUP_TYPE);

		setArtInfo(title, artGroupType, artInfo);
		if (value == Integer.MIN_VALUE)
			setArtValue(null, min, max);
		else
			setArtValue(value, min, max);

		if (Intent.ACTION_EDIT.equals(getAction())) {
			setMode(true);
		} else {
			setMode(false);
		}
	}

	public Bundle accept() {
		Bundle bundle = new Bundle();

		if (editMode) {
			Integer value = null;
			try {
				NumberPicker numberPicker = (NumberPicker) popupcontent.findViewById(R.id.popup_edit_value);
				if (numberPicker != null && numberPicker.getVisibility() == View.VISIBLE) {
					value = numberPicker.getValue();
				}

				artInfo.setCastDuration(getValue(R.id.popup_liturgie_castduration_edit));
				artInfo.setCosts(getValue(R.id.popup_liturgie_costs_edit));
				artInfo.setEffect(getValue(R.id.popup_liturgie_effect_edit));
				artInfo.setEffectDuration(getValue(R.id.popup_liturgie_effectduration_edit));
				artInfo.setMerkmale(getValue(R.id.popup_liturgie_merkmal_edit));
				artInfo.setOrigin(getValue(R.id.popup_liturgie_origin_edit));
				artInfo.setProbe(getValue(R.id.popup_liturgie_probe_edit));
				artInfo.setRange(getValue(R.id.popup_liturgie_range_edit));
				artInfo.setSource(getValue(R.id.popup_liturgie_source_edit));
				artInfo.setTarget(getValue(R.id.popup_liturgie_target_edit));

				bundle.putSerializable(DATA_INTENT_ART_INFO, artInfo);
				if (value != null) {
					bundle.putInt(DATA_INTENT_ART_VALUE, value);
				}

			} catch (NumberFormatException e) {
				Debug.error(e);
			}
		}

		return bundle;
	}

	private void switchMode(boolean edit) {
		setMode(edit);

		if (edit)
			inflateSaveAndDiscard();
		else
			inflateDone();

		getActivity().invalidateOptionsMenu();

	}

	private void setMode(boolean edit) {
		this.editMode = edit;
		if (artInfo != null) {

			NumberPicker numberPicker = (NumberPicker) popupcontent.findViewById(R.id.popup_edit_value);
			if (numberPicker != null) {
				numberPicker.setEnabled(edit);
                numberPicker.setVisibility(edit ? View.VISIBLE : View.GONE);
			}

			edit(R.id.popup_liturgie_castduration, R.id.popup_liturgie_castduration_edit,
					artInfo.getCastDurationDetailed(), edit);
			edit(R.id.popup_liturgie_effect, R.id.popup_liturgie_effect_edit, artInfo.getEffect(), edit);
			edit(R.id.popup_liturgie_effectduration, R.id.popup_liturgie_effectduration_edit,
					artInfo.getEffectDuration(), edit);
			edit(R.id.popup_liturgie_merkmal, R.id.popup_liturgie_merkmal_edit, artInfo.getMerkmale(), edit);
			edit(R.id.popup_liturgie_origin, R.id.popup_liturgie_origin_edit, artInfo.getOrigin(), edit);
			edit(R.id.popup_liturgie_probe, R.id.popup_liturgie_probe_edit, artInfo.getProbe(), edit);
			edit(R.id.popup_liturgie_costs, R.id.popup_liturgie_costs_edit, artInfo.getCosts(), edit);
			edit(R.id.popup_liturgie_range, R.id.popup_liturgie_range_edit, artInfo.getRangeDetailed(), edit);
			edit(R.id.popup_liturgie_source, R.id.popup_liturgie_source_edit, artInfo.getSource(), edit);
			edit(R.id.popup_liturgie_target, R.id.popup_liturgie_target_edit, artInfo.getTargetDetailed(), edit);
		}

	}

	private void set(int tfid, CharSequence v) {
		TextView tf = (TextView) popupcontent.findViewById(tfid);
		if (tf != null) {
			tf.setText(v);
		}
	}

	private String getValue(int etid) {
		return ((EditText) popupcontent.findViewById(etid)).getText().toString();
	}

	private void edit(int tfid, int etid, String v, boolean edit) {
		int editVisibility = edit ? View.VISIBLE : View.GONE;
		int viewVisibility = edit ? View.GONE : View.VISIBLE;

		TextView tf = (TextView) popupcontent.findViewById(tfid);
		TextView et = (TextView) popupcontent.findViewById(etid);

		if (tf != null && et != null) {
			tf.setVisibility(viewVisibility);
			et.setVisibility(editVisibility);
			et.setText(v);
			tf.setText(v);
		}
	}

	@Override
	public void cancel() {

	}
}
