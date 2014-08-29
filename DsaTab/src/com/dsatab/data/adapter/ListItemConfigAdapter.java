package com.dsatab.data.adapter;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.dsatab.R;
import com.dsatab.data.Art;
import com.dsatab.data.Hero;
import com.dsatab.data.Purse.Currency;
import com.dsatab.data.Spell;
import com.dsatab.data.Talent;
import com.dsatab.data.enums.AttributeType;
import com.dsatab.data.enums.EventCategory;
import com.dsatab.data.enums.TalentGroupType;
import com.dsatab.data.items.EquippedItem;
import com.dsatab.data.modifier.Modificator;
import com.dsatab.util.DsaUtil;
import com.dsatab.util.Util;
import com.dsatab.view.ListSettings;
import com.dsatab.view.ListSettings.ListItem;
import com.dsatab.view.ListSettings.ListItemType;
import com.nhaarman.listviewanimations.ArrayAdapter;

public class ListItemConfigAdapter extends ArrayAdapter<ListItem> implements OnItemSelectedListener, OnClickListener {

	private LayoutInflater inflater;
	private Hero hero;

	private static final String NAME_EMPTY = "Alle";

	private Map<ListItemType, SpinnerSimpleAdapter<String>> spinnerAdapters;

	private Context context;

	public ListItemConfigAdapter(Context context, Hero hero, List<ListItem> objects) {
		super(objects);
		this.hero = hero;
		this.context = context;

		spinnerAdapters = new EnumMap<ListSettings.ListItemType, SpinnerSimpleAdapter<String>>(ListItemType.class);
		inflater = LayoutInflater.from(context);
	}

	protected SpinnerSimpleAdapter<String> getAdapter(ListItemType type) {
		SpinnerSimpleAdapter<String> result = spinnerAdapters.get(type);
		if (result == null) {
			List<String> types = new ArrayList<String>();

			types.add(NAME_EMPTY);

			switch (type) {
			case Attribute:
				for (AttributeType attributeType : AttributeType.values()) {
					types.add(attributeType.name());
				}
				break;
			case Talent:
				for (TalentGroupType groupType : TalentGroupType.values()) {
					types.add(groupType.name());
				}
				for (Talent talent : hero.getTalents()) {
					types.add(talent.getName());
				}
				break;
			case Spell:
				for (Spell spell : hero.getSpells().values()) {
					types.add(spell.getName());
				}
				break;
			case Art:
				for (Art art : hero.getArts().values()) {
					types.add(art.getName());
				}
				break;
			case Modificator:
				for (Modificator mod : hero.getUserModificators()) {
					types.add(mod.getModificatorName());
				}
				break;
			case EquippedItem:
				for (EquippedItem item : hero.getEquippedItems()) {
					types.add(item.getName());
				}
				break;
			case Header:
				break;
			case Notes:
				for (EventCategory category : EventCategory.values()) {
					types.add(category.name());
				}
				break;
			case Purse:
				for (Currency category : Currency.values()) {
					types.add(category.name());
				}
				break;
			}

			result = new SpinnerSimpleAdapter<String>(context, types);

			spinnerAdapters.put(type, result);
		}

		return result;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public long getItemId(int position) {
		return getItem(position).hashCode();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_listitem_config, parent, false);

			holder = new ViewHolder();
			holder.text1 = (TextView) convertView.findViewById(android.R.id.text1);
			holder.text2 = (TextView) convertView.findViewById(android.R.id.text2);
			holder.icon1 = (ImageView) convertView.findViewById(android.R.id.icon1);
			holder.icon2 = (ImageView) convertView.findViewById(android.R.id.icon2);
			holder.icon2.setOnClickListener(this);
			holder.icon2.setFocusable(false);

			holder.spinner = (Spinner) convertView.findViewById(R.id.spinner);
			holder.spinner.setFocusable(false);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final ListItem listItem = getItem(position);

		if (listItem.getType() == ListItemType.Header) {
			holder.spinner.setVisibility(View.GONE);
			holder.text2.setVisibility(View.VISIBLE);
			holder.text2.setText(listItem.getName());
		} else {
			SpinnerSimpleAdapter<String> spinnerAdapter = getAdapter(listItem.getType());
			holder.spinner.setVisibility(View.VISIBLE);
			holder.spinner.setAdapter(spinnerAdapter);
			holder.spinner.setSelection(spinnerAdapter.getPosition(listItem.getName()));
			holder.spinner.setOnItemSelectedListener(this);
			holder.spinner.setTag(listItem);
			holder.text2.setVisibility(View.GONE);
		}
		holder.text1.setText(listItem.getType().title());
		holder.icon1.setImageResource(DsaUtil.getResourceId(listItem.getType()));
		holder.icon2.setTag(listItem);

		Util.applyRowStyle(convertView, position);
		return convertView;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		if (parent.getTag() instanceof ListItem) {
			ListItem listItem = (ListItem) parent.getTag();

			String selection = (String) parent.getItemAtPosition(position);
			if (NAME_EMPTY.equals(selection)) {
				listItem.setName(null);
			} else {
				listItem.setName(selection);
			}
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		if (parent.getTag() instanceof ListItem) {
			ListItem listItem = (ListItem) parent.getTag();
			listItem.setName(null);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case android.R.id.icon2:
			if (v.getTag() instanceof ListItem) {
				ListItem listItem = (ListItem) v.getTag();
				remove(listItem);
			}
			break;
		}

	}

	private static final class ViewHolder {
		TextView text1, text2;
		Spinner spinner;
		ImageView icon1, icon2;
	}
}
