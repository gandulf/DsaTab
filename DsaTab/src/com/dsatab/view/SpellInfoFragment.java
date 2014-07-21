package com.dsatab.view;

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
import com.dsatab.data.Hero;
import com.dsatab.data.Spell;
import com.dsatab.data.SpellInfo;
import com.dsatab.data.Value;
import com.dsatab.fragment.BaseFragment;
import com.dsatab.util.Debug;
import com.j256.ormlite.dao.RuntimeExceptionDao;

public class SpellInfoFragment extends BaseFragment {

	private static final String DATA_INTENT_SPELL = "spell";

	private static final String TAG = "spelldialog";

	private Spell spell;

	private View popupcontent = null;

	private boolean editMode = false;

	public static SpellInfoFragment newInstance(Spell spell) {
		SpellInfoFragment f = new SpellInfoFragment();

		// Supply num input as an argument.
		Bundle args = new Bundle();
		args.putSerializable(DATA_INTENT_SPELL, spell);
		f.setArguments(args);

		return f;
	}

	public Spell getSpell() {
		return spell;
	}

	protected void setSpellValue(Value value) {

		NumberPicker numberPicker = (NumberPicker) popupcontent.findViewById(R.id.popup_edit_value);
		if (numberPicker != null) {
			if (value != null) {
				Integer currentValue = value.getValue();
				numberPicker.setMinValue(value.getMinimum());
				numberPicker.setMaxValue(value.getMaximum());
				if (currentValue != null) {
					numberPicker.setValue(currentValue);
				} else {
					numberPicker.setValue(0);
					numberPicker.setVisibility(View.GONE);
				}
			} else {
				numberPicker.setVisibility(View.GONE);
			}
		}
	}

	public Spell accept() {
		try {
			NumberPicker numberPicker = (NumberPicker) popupcontent.findViewById(R.id.popup_edit_value);
			if (numberPicker != null && numberPicker.getVisibility() == View.VISIBLE) {
				spell.setValue(numberPicker.getValue());
			}

			SpellInfo info = spell.getInfo();

			info.setCastDuration(getValue(R.id.popup_spell_castduration_edit));
			info.setCosts(getValue(R.id.popup_spell_costs_edit));
			info.setEffect(getValue(R.id.popup_spell_effect_edit));
			info.setTarget(getValue(R.id.popup_spell_target_edit));
			info.setRange(getValue(R.id.popup_spell_range_edit));
			info.setEffectDuration(getValue(R.id.popup_spell_effectduration_edit));
			info.setRepresentation(getValue(R.id.popup_spell_representation_edit));
			info.setSource(getValue(R.id.popup_spell_source_edit));
			info.setComplexity(getValue(R.id.popup_spell_complexity_edit));
			info.setMerkmale(getValue(R.id.popup_spell_merkmal_edit));
			info.setProbe(getValue(R.id.popup_spell_probe_edit));

			spell.setComments(getValue(R.id.popup_spell_comment_edit));
			spell.setVariant(getValue(R.id.popup_spell_variant_edit));
			spell.setProbePattern(info.getProbe());

			RuntimeExceptionDao<SpellInfo, Long> dao = DsaTabApplication.getInstance().getDBHelper()
					.getRuntimeExceptionDao(SpellInfo.class);
			dao.createOrUpdate(info);

			spell.fireValueChangedEvent();

			Toast.makeText(getActivity(), "Zauberinformationen wurden gespeichert", Toast.LENGTH_SHORT).show();

			getBaseActivity().getSupportFragmentManager().popBackStack();
		} catch (NumberFormatException e) {
			Debug.error(e);
		}

		return spell;
	}

	/**
	 * 
	 */
	public void cancel() {
		getBaseActivity().getSupportFragmentManager().popBackStack();
	}

	protected void setSpell(Spell spell) {
		this.spell = spell;
		if (spell != null) {

			set(R.id.popup_spell_title, spell.getTitle());
			setSpellValue(spell);

			SpellInfo info = spell.getInfo();
			if (info != null) {
				set(R.id.popup_spell_castduration, info.getCastDurationDetailed());
				set(R.id.popup_spell_costs, info.getCosts());
				set(R.id.popup_spell_effect, info.getEffect());
				set(R.id.popup_spell_target, info.getTargetDetailed());
				set(R.id.popup_spell_range, info.getRangeDetailed());
				set(R.id.popup_spell_effectduration, info.getEffectDuration());
				set(R.id.popup_spell_representation, info.getRepresentation());
				set(R.id.popup_spell_source, info.getSource());
				set(R.id.popup_spell_complexity, info.getComplexity());
				set(R.id.popup_spell_merkmal, info.getMerkmale());
				set(R.id.popup_spell_probe, info.getProbe());
			}
			set(R.id.popup_spell_comment, spell.getComments());
			set(R.id.popup_spell_variant, spell.getVariant());
		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		popupcontent = inflater.inflate(R.layout.popup_spell_info, container, false);
		return popupcontent;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

		if (!editMode) {
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

		spell = (Spell) getArguments().getSerializable(DATA_INTENT_SPELL);

		setSpell(spell);
		switchMode(false);
	}

	/**
	 * @param b
	 */
	private void switchMode(boolean edit) {
		this.editMode = edit;
		if (spell != null) {
			SpellInfo info = spell.getInfo();

			popupcontent.findViewById(R.id.popup_edit_value).setEnabled(edit);

			edit(R.id.popup_spell_castduration, R.id.popup_spell_castduration_edit, info.getCastDurationDetailed(),
					edit);
			edit(R.id.popup_spell_costs, R.id.popup_spell_costs_edit, info.getCosts(), edit);
			edit(R.id.popup_spell_effect, R.id.popup_spell_effect_edit, info.getEffect(), edit);
			edit(R.id.popup_spell_target, R.id.popup_spell_target_edit, info.getTargetDetailed(), edit);
			edit(R.id.popup_spell_range, R.id.popup_spell_range_edit, info.getRangeDetailed(), edit);
			edit(R.id.popup_spell_effectduration, R.id.popup_spell_effectduration_edit, info.getEffectDuration(), edit);
			edit(R.id.popup_spell_representation, R.id.popup_spell_representation_edit, info.getRepresentation(), edit);
			edit(R.id.popup_spell_source, R.id.popup_spell_source_edit, info.getSource(), edit);
			edit(R.id.popup_spell_complexity, R.id.popup_spell_complexity_edit, info.getComplexity(), edit);
			edit(R.id.popup_spell_merkmal, R.id.popup_spell_merkmal_edit, info.getMerkmale(), edit);

			edit(R.id.popup_spell_comment, R.id.popup_spell_comment_edit, spell.getComments(), edit);
			edit(R.id.popup_spell_variant, R.id.popup_spell_variant_edit, spell.getVariant(), edit);
			edit(R.id.popup_spell_probe, R.id.popup_spell_probe_edit, info.getProbe(), edit);

		}

		getBaseActivity().supportInvalidateOptionsMenu();

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
		}

	}

	@Override
	public void onHeroLoaded(Hero hero) {
	}

}
