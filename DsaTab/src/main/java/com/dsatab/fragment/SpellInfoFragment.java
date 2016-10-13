package com.dsatab.fragment;

import android.support.v4.app.Fragment;
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
import com.dsatab.data.Hero;
import com.dsatab.data.Spell;
import com.dsatab.data.SpellInfo;
import com.dsatab.util.Debug;

public class SpellInfoFragment extends BaseEditFragment {

	public static final String DATA_INTENT_SPELL_INFO = "spellInfo";
	public static final String DATA_INTENT_SPELL_TITLE = "spellTitle";
	public static final String DATA_INTENT_SPELL_COMMENT = "spellComment";
	public static final String DATA_INTENT_SPELL_VARIANT = "spellVariant";

	public static final String DATA_INTENT_SPELL_VALUE = "spellValue";
	public static final String DATA_INTENT_SPELL_VALUE_MIN = "spellValueMin";
	public static final String DATA_INTENT_SPELL_VALUE_MAX = "spellValueMax";

	private SpellInfo spell;
	private String comments, variant;

	private View popupcontent = null;

	private boolean editMode = false;

	public static void edit(Fragment fragment, Spell spell, int requestCode) {
		Intent intent = new Intent(fragment.getActivity(), BaseEditActivity.class);
		intent.setAction(Intent.ACTION_EDIT);
		intent.putExtra(BaseEditActivity.EDIT_FRAGMENT_CLASS, SpellInfoFragment.class);
		intent.putExtra(DATA_INTENT_SPELL_INFO, spell.getInfo());
		intent.putExtra(DATA_INTENT_SPELL_TITLE, spell.getTitle());
		intent.putExtra(DATA_INTENT_SPELL_COMMENT, spell.getComments());
		intent.putExtra(DATA_INTENT_SPELL_VARIANT, spell.getVariant());
		if (spell.getValue() != null) {
			intent.putExtra(DATA_INTENT_SPELL_VALUE, (int) spell.getValue());
		}
		intent.putExtra(DATA_INTENT_SPELL_VALUE_MIN, spell.getMinimum());
		intent.putExtra(DATA_INTENT_SPELL_VALUE_MAX, spell.getMaximum());

		fragment.startActivityForResult(intent, requestCode);
	}

	public static void view(Fragment fragment, Spell spell, int requestCode) {
		Intent intent = new Intent(fragment.getActivity(), BaseEditActivity.class);
		intent.setAction(Intent.ACTION_VIEW);
		intent.putExtra(BaseEditActivity.EDIT_FRAGMENT_CLASS, SpellInfoFragment.class);
		intent.putExtra(DATA_INTENT_SPELL_INFO, spell.getInfo());
		intent.putExtra(DATA_INTENT_SPELL_TITLE, spell.getTitle());
		intent.putExtra(DATA_INTENT_SPELL_COMMENT, spell.getComments());
		intent.putExtra(DATA_INTENT_SPELL_VARIANT, spell.getVariant());
		if (spell.getValue() != null) {
			intent.putExtra(DATA_INTENT_SPELL_VALUE, (int) spell.getValue());
		}
		intent.putExtra(DATA_INTENT_SPELL_VALUE_MIN, spell.getMinimum());
		intent.putExtra(DATA_INTENT_SPELL_VALUE_MAX, spell.getMaximum());

		fragment.startActivityForResult(intent, requestCode);
	}

	protected void setSpellValue(Integer value, int min, int max) {

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

	public Bundle accept() {
		Bundle bundle = new Bundle();

		if (editMode) {
			try {

				Integer value = null;
				NumberPicker numberPicker = (NumberPicker) popupcontent.findViewById(R.id.popup_edit_value);
				if (numberPicker != null && numberPicker.getVisibility() == View.VISIBLE) {
					value = numberPicker.getValue();
				}

				spell.setCastDuration(getValue(R.id.popup_spell_castduration_edit));
				spell.setCosts(getValue(R.id.popup_spell_costs_edit));
				spell.setEffect(getValue(R.id.popup_spell_effect_edit));
				spell.setTarget(getValue(R.id.popup_spell_target_edit));
				spell.setRange(getValue(R.id.popup_spell_range_edit));
				spell.setEffectDuration(getValue(R.id.popup_spell_effectduration_edit));
				spell.setRepresentation(getValue(R.id.popup_spell_representation_edit));
				spell.setSource(getValue(R.id.popup_spell_source_edit));
				spell.setComplexity(getValue(R.id.popup_spell_complexity_edit));
				spell.setMerkmale(getValue(R.id.popup_spell_merkmal_edit));
				spell.setProbe(getValue(R.id.popup_spell_probe_edit));

				bundle.putString(DATA_INTENT_SPELL_COMMENT, getValue(R.id.popup_spell_comment_edit));
				bundle.putString(DATA_INTENT_SPELL_VARIANT, getValue(R.id.popup_spell_variant_edit));

				bundle.putSerializable(DATA_INTENT_SPELL_INFO, spell);
				if (value != null) {
					bundle.putInt(DATA_INTENT_SPELL_VALUE, value);
				}
			} catch (NumberFormatException e) {
				Debug.error(e);
			}
		}

		return bundle;
	}

	@Override
	public void cancel() {

	}

	protected void setSpell(CharSequence title, String comments, String variant, SpellInfo info) {
		this.spell = info;
		if (spell != null) {
			set(R.id.popup_spell_title, title);

			set(R.id.popup_spell_castduration, spell.getCastDurationDetailed());
			set(R.id.popup_spell_costs, spell.getCosts());
			set(R.id.popup_spell_effect, spell.getEffect());
			set(R.id.popup_spell_target, spell.getTargetDetailed());
			set(R.id.popup_spell_range, spell.getRangeDetailed());
			set(R.id.popup_spell_effectduration, spell.getEffectDuration());
			set(R.id.popup_spell_representation, spell.getRepresentation());
			set(R.id.popup_spell_source, spell.getSource());
			set(R.id.popup_spell_complexity, spell.getComplexity());
			set(R.id.popup_spell_merkmal, spell.getMerkmale());
			set(R.id.popup_spell_probe, spell.getProbe());

			set(R.id.popup_spell_comment, comments);
			set(R.id.popup_spell_variant, variant);
		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		popupcontent = inflater.inflate(R.layout.sheet_spell_info, container, false);
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

		spell = (SpellInfo) getExtra().getSerializable(DATA_INTENT_SPELL_INFO);
		CharSequence title = getExtra().getCharSequence(DATA_INTENT_SPELL_TITLE);
		comments = getExtra().getString(DATA_INTENT_SPELL_COMMENT);
		variant = getExtra().getString(DATA_INTENT_SPELL_VARIANT);

		int value = getExtra().getInt(DATA_INTENT_SPELL_VALUE, Integer.MIN_VALUE);
		int min = getExtra().getInt(DATA_INTENT_SPELL_VALUE_MIN);
		int max = getExtra().getInt(DATA_INTENT_SPELL_VALUE_MAX);

		setSpell(title, comments, variant, spell);

		if (value != Integer.MIN_VALUE)
			setSpellValue(value, min, max);
		else
			setSpellValue(null, min, max);

		if (Intent.ACTION_EDIT.equals(getAction())) {
			setMode(true);
		} else {
			setMode(false);
		}
	}

	/**
	 * @param edit
	 */
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
		if (spell != null) {

			NumberPicker numberPicker = (NumberPicker) popupcontent.findViewById(R.id.popup_edit_value);
			if (numberPicker != null) {
                numberPicker.setVisibility(edit ? View.VISIBLE: View.GONE);
				numberPicker.setEnabled(edit);
			}

			edit(R.id.popup_spell_castduration, R.id.popup_spell_castduration_edit, spell.getCastDurationDetailed(),
					edit);
			edit(R.id.popup_spell_costs, R.id.popup_spell_costs_edit, spell.getCosts(), edit);
			edit(R.id.popup_spell_effect, R.id.popup_spell_effect_edit, spell.getEffect(), edit);
			edit(R.id.popup_spell_target, R.id.popup_spell_target_edit, spell.getTargetDetailed(), edit);
			edit(R.id.popup_spell_range, R.id.popup_spell_range_edit, spell.getRangeDetailed(), edit);
			edit(R.id.popup_spell_effectduration, R.id.popup_spell_effectduration_edit, spell.getEffectDuration(), edit);
			edit(R.id.popup_spell_representation, R.id.popup_spell_representation_edit, spell.getRepresentation(), edit);
			edit(R.id.popup_spell_source, R.id.popup_spell_source_edit, spell.getSource(), edit);
			edit(R.id.popup_spell_complexity, R.id.popup_spell_complexity_edit, spell.getComplexity(), edit);
			edit(R.id.popup_spell_merkmal, R.id.popup_spell_merkmal_edit, spell.getMerkmale(), edit);

			edit(R.id.popup_spell_comment, R.id.popup_spell_comment_edit, comments, edit);
			edit(R.id.popup_spell_variant, R.id.popup_spell_variant_edit, variant, edit);

			edit(R.id.popup_spell_probe, R.id.popup_spell_probe_edit, spell.getProbe(), edit);
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
	public void onHeroLoaded(Hero hero) {
	}

}
