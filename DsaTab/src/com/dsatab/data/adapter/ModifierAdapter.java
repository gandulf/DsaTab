package com.dsatab.data.adapter;

import java.util.Collection;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.dsatab.R;
import com.dsatab.data.modifier.Modifier;
import com.dsatab.util.Util;
import com.gandulf.guilib.data.OpenArrayAdapter;
import com.gandulf.guilib.view.SeekBarEx;
import com.gandulf.guilib.view.SeekBarEx.SeekBarLabelRenderer;

public class ModifierAdapter extends OpenArrayAdapter<Modifier> implements OnSeekBarChangeListener {

	private static final int TYPE_MANUAL = 0;
	private static final int TYPE_STRING = 1;
	private static final int TYPE_SPINNER = 2;

	private LayoutInflater inflater;

	public interface OnModifierChangedListener {
		void onModifierChanged(Modifier modifier);
	}

	private OnModifierChangedListener onModifierChangedListener;

	private SeekBarLabelRenderer labelRenderer = new SeekBarLabelRenderer() {

		@Override
		public String render(int value) {
			return Util.toProbe(value);
		}
	};

	private View.OnClickListener modificerClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View view) {

			if (view.getTag() instanceof Modifier) {
				Modifier mod = (Modifier) view.getTag();
				boolean active = mod.toggleActive();
				if (view instanceof CheckedTextView) {
					CheckedTextView checkedTextView = (CheckedTextView) view;
					checkedTextView.setChecked(active);
				}

				onModifierChanged(mod);

			}
		}
	};

	public ModifierAdapter(Context context, Collection<Modifier> objects) {
		super(context, 0, objects);

		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getViewTypeCount() {
		return 3;
	}

	@Override
	public int getItemViewType(int position) {
		Modifier modifier = getItem(position);
		if (Modifier.TITLE_MANUAL.equals(modifier.getTitle())) {
			return TYPE_MANUAL;
		} else if (modifier.getSpinnerOptions() != null || modifier.getSpinnerValues() != null) {
			return TYPE_SPINNER;
		} else {
			return TYPE_STRING;
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		int type = getItemViewType(position);

		if (convertView == null) {
			switch (type) {
			case TYPE_MANUAL: {
				convertView = inflater.inflate(R.layout.item_probe_modifier_manual, parent, false);

				ViewHolderManual holder = new ViewHolderManual();
				holder.text1 = (TextView) convertView.findViewById(android.R.id.text1);
				holder.text2 = (CheckedTextView) convertView.findViewById(android.R.id.text2);
				holder.seekBar = (SeekBarEx) convertView.findViewById(R.id.wheel);
				holder.seekBar.setLabelRenderer(labelRenderer);
				convertView.setTag(holder);
				break;
			}
			case TYPE_STRING: {
				convertView = (ViewGroup) inflater.inflate(R.layout.item_probe_modifier, parent, false);
				ViewHolderString holder = new ViewHolderString();
				holder.text1 = (TextView) convertView.findViewById(android.R.id.text1);
				holder.text2 = (CheckedTextView) convertView.findViewById(android.R.id.text2);
				convertView.setTag(holder);
				break;
			}
			case TYPE_SPINNER: {
				convertView = inflater.inflate(R.layout.item_probe_modifier_spinner, parent, false);

				ViewHolderSpinner holder = new ViewHolderSpinner();
				holder.text1 = (TextView) convertView.findViewById(android.R.id.text1);
				holder.text2 = (CheckedTextView) convertView.findViewById(android.R.id.text2);
				holder.spinner = (Spinner) convertView.findViewById(R.id.spinner);
				convertView.setTag(holder);
				break;
			}
			}

		}

		final Modifier modifier = getItem(position);
		switch (type) {
		case TYPE_MANUAL: {

			ViewHolderManual holder = (ViewHolderManual) convertView.getTag();

			holder.seekBar.setOnSeekBarChangeListener(null);
			holder.seekBar.setMin(-15);
			holder.seekBar.setMax(15);
			holder.seekBar.setValue(-modifier.getModifier());
			holder.seekBar.setOnSeekBarChangeListener(this);

			holder.text1.setText(modifier.getTitle());

			holder.text2.setText(Util.toProbe(holder.seekBar.getValue()));
			holder.text2.setChecked(modifier.isActive());
			Util.setTextColor(holder.text2, -holder.seekBar.getValue());

			holder.text2.setOnClickListener(modificerClickListener);
			holder.text2.setTag(modifier);
			holder.seekBar.setLabel(holder.text2);
			holder.seekBar.setTag(modifier);
			break;
		}
		case TYPE_STRING: {
			ViewHolderString holder = (ViewHolderString) convertView.getTag();
			holder.text1.setText(modifier.getTitle());

			holder.text2.setText(Util.toProbe(-modifier.getModifier()));
			holder.text2.setChecked(modifier.isActive());
			holder.text2.setOnClickListener(modificerClickListener);
			holder.text2.setTag(modifier);

			Util.setTextColor(holder.text2, modifier.getModifier());
			break;
		}
		case TYPE_SPINNER: {

			final ViewHolderSpinner holder = (ViewHolderSpinner) convertView.getTag();
			holder.text1.setText(modifier.getTitle());

			SpinnerSimpleAdapter<String> adapter = new SpinnerSimpleAdapter<String>(getContext(),
					modifier.getSpinnerOptions());
			holder.spinner.setAdapter(adapter);
			holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

					if (modifier.getSpinnerIndex() != position) {
						modifier.setModifier(-modifier.getSpinnerValues()[position]);
						modifier.setSpinnerIndex(position);
						holder.text2.setText(Util.toProbe(-modifier.getModifier()));
						Util.setTextColor(holder.text2, modifier.getModifier());
						onModifierChanged(modifier);

						SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
						Editor edit = preferences.edit();
						edit.putInt(Modifier.PREF_PREFIX_SPINNER_INDEX + modifier.getTitle(), position);
						edit.commit();
					}

				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					modifier.setModifier(0);
					modifier.setSpinnerIndex(-1);
					holder.text2.setText(Util.toProbe(-modifier.getModifier()));
					Util.setTextColor(holder.text2, modifier.getModifier());
					onModifierChanged(modifier);
				}

			});

			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
			int index = preferences.getInt(Modifier.PREF_PREFIX_SPINNER_INDEX + modifier.getTitle(), 0);
			if (index < adapter.getCount()) {
				holder.spinner.setSelection(index);
				modifier.setSpinnerIndex(index);
			}

			holder.text2.setText(Util.toProbe(-modifier.getModifier()));
			holder.text2.setChecked(modifier.isActive());
			holder.text2.setOnClickListener(modificerClickListener);
			holder.text2.setTag(modifier);
			Util.setTextColor(holder.text2, modifier.getModifier());

			break;
		}
		}

		return convertView;
	}

	private void onModifierChanged(Modifier modifier) {
		if (onModifierChangedListener != null) {
			onModifierChangedListener.onModifierChanged(modifier);
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

		if (seekBar instanceof SeekBarEx) {
			SeekBarEx seekBarEx = (SeekBarEx) seekBar;
			Modifier modifier = (Modifier) seekBarEx.getTag();
			modifier.setModifier(-seekBarEx.getValue());

			if (seekBarEx.getLabel() != null) {
				Util.setTextColor(seekBarEx.getLabel(), modifier.getModifier());
			}
		}

	}

	@Override
	public void onStartTrackingTouch(SeekBar paramSeekBar) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {

		if (seekBar instanceof SeekBarEx) {
			SeekBarEx seekBarEx = (SeekBarEx) seekBar;
			Modifier modifier = (Modifier) seekBarEx.getTag();
			onModifierChanged(modifier);
			if (seekBarEx.getLabel() != null) {
				Util.setTextColor(seekBarEx.getLabel(), modifier.getModifier());
			}

		}
	}

	public OnModifierChangedListener getOnModifierChangedListener() {
		return onModifierChangedListener;
	}

	public void setOnModifierChangedListener(OnModifierChangedListener onModifierChangedListener) {
		this.onModifierChangedListener = onModifierChangedListener;
	}

	private static class ViewHolderManual {
		private TextView text1;
		private CheckedTextView text2;
		private SeekBarEx seekBar;
	}

	private static class ViewHolderString {
		private TextView text1;
		private CheckedTextView text2;
	}

	private static class ViewHolderSpinner {
		private TextView text1;
		private CheckedTextView text2;
		private Spinner spinner;
	}
}
