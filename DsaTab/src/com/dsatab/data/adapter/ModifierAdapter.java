package com.dsatab.data.adapter;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.data.modifier.Modifier;
import com.dsatab.util.Util;
import com.gandulf.guilib.util.Debug;
import com.gandulf.guilib.view.SeekBarEx;
import com.gandulf.guilib.view.SeekBarEx.SeekBarLabelRenderer;

import java.util.Collection;

public class ModifierAdapter extends ListRecyclerAdapter<ModifierAdapter.ModifierViewHolder, Modifier> implements OnSeekBarChangeListener {

    private static final int TYPE_MANUAL = 0;
    private static final int TYPE_STRING = 1;
    private static final int TYPE_SPINNER = 2;

    public interface OnModifierChangedListener {
        void onModifierChanged(Modifier modifier);
    }

    private static SeekBarLabelRenderer labelRenderer = new SeekBarLabelRenderer() {
        @Override
        public String render(int value) {
            return Util.toProbe(value);
        }
    };

    protected static class ModifierViewHolder extends RecyclerView.ViewHolder {
        private TextView text1;
        private TextView text2;
        private CheckBox checkBox;
        private SeekBarEx seekBar;
        private Spinner spinner;

        public ModifierViewHolder(View v) {
            super(v);
            text1 = (TextView) v.findViewById(android.R.id.text1);
            text2 = (TextView) v.findViewById(android.R.id.text2);
            checkBox = (CheckBox) v.findViewById(android.R.id.checkbox);
            seekBar = (SeekBarEx) v.findViewById(R.id.wheel);
            seekBar.setLabelRenderer(labelRenderer);
            spinner = (Spinner) v.findViewById(R.id.spinner);
        }
    }

    private OnModifierChangedListener onModifierChangedListener;

    private View.OnClickListener modificerClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {

            if (view.getTag() instanceof Modifier) {
                Modifier mod = (Modifier) view.getTag();
                boolean active = mod.toggleActive();
                if (view instanceof CheckBox) {
                    CheckBox checked = (CheckBox) view;
                    checked.setChecked(active);
                }
                onModifierChanged(mod);
            }
        }
    };

    public ModifierAdapter(Collection<Modifier> objects) {
        super(objects);
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
    public ModifierViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ModifierViewHolder(inflate(inflater,parent,R.layout.item_probe_modifier, false));
    }

    @Override
    public void onBindViewHolder(final ModifierViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        int type = getItemViewType(position);

        Modifier modifier = getItem(position);
        switch (type) {
            case TYPE_MANUAL: {
                Util.setVisibility(holder.spinner, false);
                Util.setVisibility(holder.seekBar, true);

                holder.seekBar.setOnSeekBarChangeListener(null);

                int nextStep = Math.abs(modifier.getModifier()) + (5 - modifier.getModifier() % 5);
                nextStep = Math.max(10, nextStep);
                Debug.verbose("mod = " + modifier.getModifier() + ", next" + nextStep);

                if (-modifier.getModifier() < 0) {
                    holder.seekBar.setMin(-nextStep);
                } else {
                    holder.seekBar.setMin(-10);
                }

                if (-modifier.getModifier() > 0) {
                    holder.seekBar.setMax(nextStep);
                } else {
                    holder.seekBar.setMax(10);
                }

                holder.seekBar.setValue(-modifier.getModifier());
                holder.seekBar.setOnSeekBarChangeListener(this);

                holder.text1.setText(modifier.getTitle());
                holder.text2.setText(Util.toProbe(holder.seekBar.getValue()));

                Util.setTextColor(holder.text2, -holder.seekBar.getValue());


                holder.seekBar.setLabel(holder.text2);
                holder.seekBar.setTag(modifier);
                break;
            }
            case TYPE_STRING: {
                Util.setVisibility(holder.spinner, false);
                Util.setVisibility(holder.seekBar, false, holder.text1);

                holder.text1.setText(modifier.getTitle());
                holder.text2.setText(Util.toProbe(-modifier.getModifier()));

                Util.setTextColor(holder.text2, modifier.getModifier());
                break;
            }
            case TYPE_SPINNER: {
                Util.setVisibility(holder.spinner, true);
                Util.setVisibility(holder.seekBar, false);

                holder.text1.setText(modifier.getTitle());

                SpinnerSimpleAdapter<String> adapter = new SpinnerSimpleAdapter<String>(holder.spinner.getContext(),
                        modifier.getSpinnerOptions());
                holder.spinner.setOnItemSelectedListener(null);
                holder.spinner.setAdapter(adapter);
                holder.spinner.setTag(modifier);
                int index = modifier.getSpinnerIndex();
                if (index >= 0 && index < adapter.getCount()) {
                    holder.spinner.setSelection(index);
                }
                holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        Modifier spinnerModifier;
                        if (parent.getTag() instanceof Modifier) {
                            spinnerModifier = (Modifier) parent.getTag();

                            if (spinnerModifier.getSpinnerIndex() != position) {
                                spinnerModifier.setModifier(-spinnerModifier.getSpinnerValues()[position]);
                                spinnerModifier.setSpinnerIndex(position);
                                holder.text2.setText(Util.toProbe(-spinnerModifier.getModifier()));
                                Util.setTextColor(holder.text2, spinnerModifier.getModifier());
                                onModifierChanged(spinnerModifier);

                                SharedPreferences preferences = DsaTabApplication.getPreferences();
                                Editor edit = preferences.edit();
                                edit.putInt(Modifier.PREF_PREFIX_SPINNER_INDEX + spinnerModifier.getTitle(), position);
                                edit.commit();
                            }
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        Modifier spinnerModifier;
                        if (parent.getTag() instanceof Modifier) {
                            spinnerModifier = (Modifier) parent.getTag();
                            spinnerModifier.setModifier(0);
                            spinnerModifier.setSpinnerIndex(-1);
                            holder.text2.setText(Util.toProbe(-spinnerModifier.getModifier()));
                            Util.setTextColor(holder.text2, spinnerModifier.getModifier());
                            onModifierChanged(spinnerModifier);
                        }
                    }

                });

                holder.text2.setText(Util.toProbe(-modifier.getModifier()));
                Util.setTextColor(holder.text2, modifier.getModifier());
                break;
            }
        }

        holder.checkBox.setChecked(modifier.isActive());
        holder.checkBox.setOnClickListener(modificerClickListener);
        holder.checkBox.setTag(modifier);
    }

    @Override
    protected void onItemClicked(ModifierViewHolder viewHolder, View v) {
        Modifier modifier = getItem(viewHolder.getAdapterPosition());
        modifier.toggleActive();
        viewHolder.checkBox.setChecked(modifier.isActive());
        onModifierChanged(modifier);
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

            seekBarEx.setOnSeekBarChangeListener(null);

            if (seekBarEx.getValue() == seekBarEx.getMin()) {
                seekBarEx.setMin(seekBarEx.getMin() - 5);
            }

            if (seekBarEx.getValue() == seekBarEx.getMax()) {
                seekBarEx.setMax(seekBarEx.getMax() + 5);
            }

            seekBarEx.setOnSeekBarChangeListener(this);

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

}
