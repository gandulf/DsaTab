package com.dsatab.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
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
import com.dsatab.data.Art;
import com.dsatab.data.ArtInfo;
import com.dsatab.data.Hero;
import com.j256.ormlite.dao.RuntimeExceptionDao;

public class ArtInfoDialog extends AlertDialog implements DialogInterface.OnClickListener {

	private Art art;

	private View popupcontent = null;

	private boolean editMode = false;

	private Hero hero;

	public ArtInfoDialog(Context context, Hero hero) {
		super(context);
		this.hero = hero;
		init();
	}

	public Art getArt() {
		return art;
	}

	public void setArt(Art art) {
		this.art = art;

		ArtInfo info = art.getInfo();

		StyleableSpannableStringBuilder sb = new StyleableSpannableStringBuilder();

		sb.append(art.getFullName());
		if (art.hasFlag(Art.Flags.Begabung)) {
			sb.appendWithStyle(new RelativeSizeSpan(0.5f), " (Begabung)");
		}
		setTitle(sb);

		set(R.id.popup_liturgie_type, art.getGroupType().getName());
		set(R.id.popup_liturgie_costs, info.getCosts());
		set(R.id.popup_liturgie_effect, info.getEffect());
		set(R.id.popup_liturgie_probe, info.getProbe());
		if (info != null) {
			set(R.id.popup_liturgie_castduration, info.getCastDurationDetailed());
			set(R.id.popup_liturgie_effectduration, info.getEffectDuration());
			set(R.id.popup_liturgie_origin, info.getOrigin());
			set(R.id.popup_liturgie_range, info.getRangeDetailed());
			set(R.id.popup_liturgie_target, info.getTargetDetailed());
			set(R.id.popup_liturgie_merkmal, info.getMerkmale());
			set(R.id.popup_liturgie_source, info.getSource());
		}

	}

	private void init() {
		setCanceledOnTouchOutside(true);

		popupcontent = LayoutInflater.from(getContext()).inflate(R.layout.popup_art_info, null, false);
		popupcontent.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		setView(popupcontent);

		setButton(DialogInterface.BUTTON_POSITIVE, getContext().getString(R.string.label_ok), this);
		setButton(DialogInterface.BUTTON_NEGATIVE, getContext().getString(R.string.label_edit), this);

		TableLayout table = (TableLayout) popupcontent.findViewById(R.id.popup_liturgie_table);

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
	 * @see android.content.DialogInterface.OnClickListener#onClick(android.content .DialogInterface, int)
	 */
	@Override
	public void onClick(DialogInterface dialog, int which) {

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
					ArtInfo info = art.getInfo();

					info.setCastDuration(getValue(R.id.popup_liturgie_castduration_edit));
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

					hero.fireValueChangedEvent(art);
					Toast.makeText(getContext(), "Kunstinformationen wurden gespeichert", Toast.LENGTH_SHORT).show();

					ArtInfoDialog.this.dismiss();
				} else {
					ArtInfoDialog.this.dismiss();
				}
			}
		});
		getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (editMode)
					ArtInfoDialog.this.dismiss();
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

		if (edit) {
			getButton(DialogInterface.BUTTON_NEGATIVE).setText(R.string.label_cancel);
			getButton(DialogInterface.BUTTON_POSITIVE).setText(R.string.label_save);
		} else {
			getButton(DialogInterface.BUTTON_NEGATIVE).setText(R.string.label_edit);
			getButton(DialogInterface.BUTTON_POSITIVE).setText(R.string.label_ok);
		}

	}

	private void set(int tfid, String v) {
		if (popupcontent.findViewById(tfid) != null) {
			((TextView) popupcontent.findViewById(tfid)).setText(v);
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
