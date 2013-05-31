package com.dsatab.data.adapter;

import java.util.Collection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dsatab.R;
import com.dsatab.data.Hero;
import com.dsatab.data.Spell;
import com.dsatab.data.Spell.Flags;
import com.dsatab.data.filter.FilterableListFilter;
import com.dsatab.util.Debug;
import com.dsatab.util.Util;
import com.dsatab.view.ListFilterSettings;

/**
 * 
 *
 */
public class SpellAdapter extends OpenArrayAdapter<Spell> {

	private OnClickListener onClickListener;

	private FilterableListFilter<Spell> filter;

	private ListFilterSettings filterSettings;

	private Hero hero;

	private Bitmap indicatorStar, indicatorStarGray, indicatorHouse, indicatorHouseGray, indicatorFlash;

	private LayoutInflater inflater;

	/**
	 * @param context
	 * @param textViewResourceId
	 * @param objects
	 */
	public SpellAdapter(Context context, Hero hero, Collection<Spell> spells, ListFilterSettings settings) {
		super(context, 0, 0, spells);

		sort(Spell.NAME_COMPARATOR);

		this.hero = hero;

		this.filterSettings = new ListFilterSettings();

		indicatorStar = BitmapFactory.decodeResource(context.getResources(), R.drawable.indicator_star);
		indicatorStarGray = BitmapFactory.decodeResource(context.getResources(), R.drawable.indicator_star_gray);
		indicatorHouse = BitmapFactory.decodeResource(context.getResources(), R.drawable.indicator_house);
		indicatorHouseGray = BitmapFactory.decodeResource(context.getResources(), R.drawable.indicator_house_gray);
		indicatorFlash = BitmapFactory.decodeResource(context.getResources(), R.drawable.indicator_flash);

		inflater = LayoutInflater.from(getContext());

		if (settings != null && !settings.isAllVisible())
			filter(settings);
	}

	public void filter(ListFilterSettings settings) {
		boolean hasChanged = !filterSettings.equals(settings);

		if (hasChanged) {
			filterSettings.set(settings);
			getFilter().setSettings(filterSettings);
			filter.filter((String) null);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ArrayAdapter#getFilter()
	 */
	@Override
	public FilterableListFilter<Spell> getFilter() {
		if (filter == null)
			filter = new FilterableListFilter<Spell>(this);

		return filter;
	}

	public OnClickListener getOnClickListener() {
		return onClickListener;
	}

	public void setOnClickListener(OnClickListener onClickListener) {
		this.onClickListener = onClickListener;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View listItem;
		ViewHolder holder;
		if (convertView == null) {
			listItem = inflater.inflate(R.layout.talent_list_item, parent, false);

			holder = new ViewHolder();
			holder.text1 = (TextView) listItem.findViewById(R.id.talent_list_item_text1);
			// be
			holder.text2 = (TextView) listItem.findViewById(R.id.talent_list_item_text2);
			// probe
			holder.text3 = (TextView) listItem.findViewById(R.id.talent_list_item_text3);
			// value / at
			holder.text4 = (TextView) listItem.findViewById(R.id.talent_list_item_text4);
			// pa
			holder.text5 = (TextView) listItem.findViewById(R.id.talent_list_item_text5);
			holder.indicator = (ImageView) listItem.findViewById(R.id.talent_list_item_indicator);
			listItem.setTag(holder);
		} else {
			listItem = convertView;
			holder = (ViewHolder) convertView.getTag();
		}
		// name

		Spell spell = getItem(position);

		holder.text1.setText(spell.getName());

		Util.setVisibility(holder.text2, false, holder.text1);
		holder.text3.setText(spell.getProbeInfo().getAttributesString());

		if (spell.getValue() != null) {
			int modifier = hero.getModifier(spell);
			Util.setText(holder.text4, spell.getValue(), modifier, null);
		} else {
			Debug.warning(spell.getName() + " has no value");
		}
		Util.setVisibility(holder.text5, false, holder.text1);

		if (holder.indicator != null) {
			if (spell.hasFlag(Flags.ZauberSpezialisierung) || !TextUtils.isEmpty(spell.getZauberSpezialisierung())) {
				holder.indicator.setVisibility(View.VISIBLE);
				holder.indicator.setImageBitmap(indicatorFlash);
			} else if (spell.hasFlag(Flags.ÜbernatürlicheBegabung)) {
				holder.indicator.setVisibility(View.VISIBLE);
				holder.indicator.setImageBitmap(indicatorStar);
			} else if (spell.hasFlag(Flags.Hauszauber)) {
				holder.indicator.setVisibility(View.VISIBLE);
				holder.indicator.setImageBitmap(indicatorHouseGray);
			} else if (spell.hasFlag(Flags.Begabung)) {
				holder.indicator.setVisibility(View.VISIBLE);
				holder.indicator.setImageBitmap(indicatorStarGray);
			} else {
				holder.indicator.setVisibility(View.INVISIBLE);
			}
		}
		Util.applyRowStyle(spell, listItem, position);

		return listItem;

	}

	private static class ViewHolder {
		TextView text1, text2, text3, text4, text5;
		ImageView indicator;
	}

}
