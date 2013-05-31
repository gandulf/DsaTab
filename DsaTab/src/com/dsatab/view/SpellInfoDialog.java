package com.dsatab.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.common.StyleableSpannableStringBuilder;
import com.dsatab.data.Hero;
import com.dsatab.data.Spell;
import com.dsatab.data.SpellInfo;
import com.j256.ormlite.dao.RuntimeExceptionDao;

public class SpellInfoDialog extends AlertDialog implements DialogInterface.OnClickListener {

	private Spell spell;

	private View popupcontent = null;

	private boolean editMode = false;

	private Hero hero;

	public SpellInfoDialog(Context context, Hero hero) {
		super(context);
		this.hero = hero;
		init();
	}

	public Spell getSpell() {
		return spell;
	}

	public void setSpell(Spell spell) {
		this.spell = spell;
		if (spell != null) {
			StyleableSpannableStringBuilder sb = new StyleableSpannableStringBuilder();

			StringBuilder addons = new StringBuilder();
			if (spell.hasFlag(Spell.Flags.ZauberSpezialisierung)
					|| !TextUtils.isEmpty(spell.getZauberSpezialisierung())) {

				addons.append(", Spez.");
				if (!TextUtils.isEmpty(spell.getZauberSpezialisierung())) {
					addons.append(" " + spell.getZauberSpezialisierung());
				}
			}
			if (spell.hasFlag(Spell.Flags.Hauszauber)) {
				addons.append(", Hauszauber");
			}
			if (spell.hasFlag(Spell.Flags.ÜbernatürlicheBegabung)) {
				addons.append(", Übernat. Begabung");
			}
			if (spell.hasFlag(Spell.Flags.Begabung)) {
				addons.append(", Begabung");
			}
			sb.append(spell.getName());

			SpellInfo info = spell.getInfo();
			if (addons.length() > 0) {
				addons.delete(0, 2);
				addons.append(")");
				addons.insert(0, " (");
				sb.appendWithStyle(new RelativeSizeSpan(0.5f), addons);
			}

			setTitle(sb);

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
			set(R.id.popup_spell_row_comment, R.id.popup_spell_comment, spell.getComments());
			set(R.id.popup_spell_row_variant, R.id.popup_spell_variant, spell.getVariant());
		}

	}

	private void init() {
		setCanceledOnTouchOutside(true);

		popupcontent = LayoutInflater.from(getContext()).inflate(R.layout.popup_spell_info, null, false);
		popupcontent.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		setView(popupcontent);

		setButton(DialogInterface.BUTTON_POSITIVE, getContext().getString(R.string.label_ok), this);
		setButton(DialogInterface.BUTTON_NEGATIVE, getContext().getString(R.string.label_edit), this);

		TableLayout table = (TableLayout) popupcontent.findViewById(R.id.popup_spell_table);

		int childCount = table.getChildCount();
		for (int i = 0; i < childCount; i++) {
			if (i % 2 == 1) {
				table.getChildAt(i).setBackgroundResource(R.color.RowOdd);
			} else {
				table.getChildAt(i).setBackgroundResource(R.color.RowEven);
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.AlertDialog#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (editMode) {
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

					hero.fireValueChangedEvent(spell);
					Toast.makeText(getContext(), "Zauberinformationen wurden gespeichert", Toast.LENGTH_SHORT).show();
					SpellInfoDialog.this.dismiss();
				} else {
					SpellInfoDialog.this.dismiss();
				}

			}
		});
		getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (editMode)
					SpellInfoDialog.this.dismiss();
				else
					switchMode(true);
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Dialog#onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();
		switchMode(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.DialogInterface.OnClickListener#onClick(android.content .DialogInterface, int)
	 */
	@Override
	public void onClick(DialogInterface dialog, int which) {
	}

	/**
	 * @param b
	 */
	private void switchMode(boolean edit) {
		this.editMode = edit;
		if (spell != null) {
			SpellInfo info = spell.getInfo();

			edit(0, R.id.popup_spell_castduration, R.id.popup_spell_castduration_edit, info.getCastDurationDetailed(),
					edit);
			edit(0, R.id.popup_spell_costs, R.id.popup_spell_costs_edit, info.getCosts(), edit);
			edit(0, R.id.popup_spell_effect, R.id.popup_spell_effect_edit, info.getEffect(), edit);
			edit(0, R.id.popup_spell_target, R.id.popup_spell_target_edit, info.getTargetDetailed(), edit);
			edit(0, R.id.popup_spell_range, R.id.popup_spell_range_edit, info.getRangeDetailed(), edit);
			edit(0, R.id.popup_spell_effectduration, R.id.popup_spell_effectduration_edit, info.getEffectDuration(),
					edit);
			edit(0, R.id.popup_spell_representation, R.id.popup_spell_representation_edit, info.getRepresentation(),
					edit);
			edit(0, R.id.popup_spell_source, R.id.popup_spell_source_edit, info.getSource(), edit);
			edit(0, R.id.popup_spell_complexity, R.id.popup_spell_complexity_edit, info.getComplexity(), edit);
			edit(0, R.id.popup_spell_merkmal, R.id.popup_spell_merkmal_edit, info.getMerkmale(), edit);

			edit(R.id.popup_spell_row_comment, R.id.popup_spell_comment, R.id.popup_spell_comment_edit,
					spell.getComments(), edit);
			edit(R.id.popup_spell_row_variant, R.id.popup_spell_variant, R.id.popup_spell_variant_edit,
					spell.getVariant(), edit);
			edit(0, R.id.popup_spell_probe, R.id.popup_spell_probe_edit, info.getProbe(), edit);

			if (findViewById(R.id.popup_spell_hero_values_row) != null) {
				if (findViewById(R.id.popup_spell_row_comment) != null
						&& findViewById(R.id.popup_spell_row_comment).getVisibility() == View.GONE
						&& findViewById(R.id.popup_spell_row_variant) != null
						&& findViewById(R.id.popup_spell_row_variant).getVisibility() == View.GONE) {
					findViewById(R.id.popup_spell_hero_values_row).setVisibility(View.GONE);
				} else {
					findViewById(R.id.popup_spell_hero_values_row).setVisibility(View.VISIBLE);
				}
			}
		}

		if (edit) {
			getButton(DialogInterface.BUTTON_NEGATIVE).setText(R.string.label_cancel);
			getButton(DialogInterface.BUTTON_POSITIVE).setText(R.string.label_save);
		} else {
			getButton(DialogInterface.BUTTON_NEGATIVE).setText(R.string.label_edit);
			getButton(DialogInterface.BUTTON_POSITIVE).setText(R.string.label_ok);
		}

	}

	private void set(int rowId, int tfid, String v) {
		if (TextUtils.isEmpty(v))
			popupcontent.findViewById(rowId).setVisibility(View.GONE);
		else {
			popupcontent.findViewById(rowId).setVisibility(View.VISIBLE);
			set(tfid, v);
		}
	}

	private void set(int tfid, String v) {
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

}
