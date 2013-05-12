package com.dsatab.data.adapter;

import java.util.Collection;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dsatab.R;
import com.dsatab.data.CustomModificator;
import com.dsatab.data.modifier.AbstractModificator;
import com.dsatab.data.modifier.Modificator;
import com.dsatab.data.modifier.RulesModificator;
import com.dsatab.data.modifier.WoundModificator;
import com.dsatab.util.Util;

public class FightModificatorAdapter extends OpenArrayAdapter<Modificator> {

	private LayoutInflater inflater;

	public FightModificatorAdapter(Activity context, Collection<Modificator> objects) {
		super(context, 0, objects);
		init();
	}

	public FightModificatorAdapter(Activity context) {
		super(context, 0);
		init();
	}

	/**
	 * 
	 */
	private void init() {
		inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.data.adapter.OpenArrayAdapter#clear()
	 */
	@Override
	public void clear() {
		super.clear();
		add(null);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		if (!(convertView instanceof RelativeLayout)) {
			// We need the layoutinflater to pick up the view from xml
			// Pick up the TwoLineListItem defined in the xml file
			convertView = inflater.inflate(R.layout.fight_sheet_modifier, parent, false);

			holder = new ViewHolder();
			holder.text1 = (TextView) convertView.findViewById(android.R.id.text1);
			holder.text2 = (TextView) convertView.findViewById(android.R.id.text2);
			holder.icon1 = (ImageView) convertView.findViewById(android.R.id.icon1);
			holder.active = (CheckBox) convertView.findViewById(R.id.active);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Modificator item = getItem(position);

		if (item instanceof AbstractModificator) {
			AbstractModificator modificator = (AbstractModificator) item;
			holder.active.setVisibility(View.VISIBLE);
			holder.active.setChecked(modificator.isActive());
			holder.active.setClickable(false);
			holder.active.setFocusable(false);
			holder.active.setTag(modificator);
		} else {
			holder.active.setVisibility(View.GONE);
		}

		holder.icon1.setBackgroundResource(0);
		holder.icon1.setScaleType(ScaleType.CENTER);
		if (item instanceof WoundModificator) {
			if (item.isActive())
				holder.icon1.setImageResource(R.drawable.icon_wound_selected);
			else
				holder.icon1.setImageResource(R.drawable.icon_wound_normal);
		} else if (item instanceof RulesModificator) {
			holder.icon1.setImageResource(Util.getThemeResourceId(getContext(), R.attr.imgSettings));
		} else if (item instanceof CustomModificator) {
			holder.icon1.setImageResource(Util.getThemeResourceId(getContext(), R.attr.imgModifier));
		}

		if (item != null) {
			holder.text1.setText(item.getModificatorName());
			holder.text2.setText(item.getModificatorInfo());
		} else {
			holder.text1.setText(null);
			holder.text2.setText(null);
		}

		Util.applyRowStyle(convertView, position);

		return convertView;

	}

	private static class ViewHolder {
		TextView text1, text2;
		ImageView icon1;
		CheckBox active;
	}
}
