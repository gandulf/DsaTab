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
import com.dsatab.data.Art;
import com.dsatab.data.Art.Flags;
import com.dsatab.data.ArtInfo;
import com.dsatab.data.filter.FilterableListFilter;
import com.dsatab.util.Util;
import com.dsatab.view.ListFilterSettings;

/**
 *
 */
public class ArtAdapter extends OpenArrayAdapter<Art> {

	private OnClickListener onClickListener;

	private ListFilterSettings filterSettings;

	private FilterableListFilter<Art> filter;

	private LayoutInflater inflater;

	private Bitmap indicatorStarGray;

	public ArtAdapter(Context context, Collection<Art> liturgies, ListFilterSettings settings) {
		super(context, 0, 0, liturgies);

		sort(Art.NAME_COMPARATOR);

		this.filterSettings = new ListFilterSettings();

		inflater = LayoutInflater.from(context);
		indicatorStarGray = BitmapFactory.decodeResource(context.getResources(), R.drawable.indicator_star_gray);

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
	public FilterableListFilter<Art> getFilter() {
		if (filter == null)
			filter = new FilterableListFilter<Art>(this);

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

			Util.setVisibility(holder.text5, false, holder.text1);
		} else {
			listItem = convertView;
			holder = (ViewHolder) convertView.getTag();
		}

		Art art = getItem(position);

		holder.text1.setText(art.getFullName());
		ArtInfo artInfo = art.getInfo();

		StringBuilder info = new StringBuilder();
		if (artInfo != null && !TextUtils.isEmpty(artInfo.getTarget())) {
			info.append(artInfo.getTarget());
		}
		if (artInfo != null && !TextUtils.isEmpty(artInfo.getRange())) {
			if (info.length() > 0)
				info.append(",");
			info.append(artInfo.getRange());
		}
		if (artInfo != null && !TextUtils.isEmpty(artInfo.getCastDuration())) {
			if (info.length() > 0)
				info.append(",");
			info.append(artInfo.getCastDuration());
		}

		holder.text2.setText(info);

		if (art.hasCustomProbe() && !TextUtils.isEmpty(art.getProbeInfo().getAttributesString())) {
			holder.text3.setText(art.getProbeInfo().getAttributesString());
		} else if (!TextUtils.isEmpty(artInfo.getProbe()) && art.getProbeInfo().getAttributeTypes() == null) {
			holder.text3.setText(artInfo.getProbe());
		} else {
			if (artInfo != null && !TextUtils.isEmpty(artInfo.getEffectDuration()))
				holder.text3.setText(artInfo.getEffectDuration());
			else
				holder.text3.setText(null);
		}
		if (art.getProbeInfo().getErschwernis() != null) {
			holder.text4.setText(Util.toProbe(art.getProbeInfo().getErschwernis()));
			holder.text4.setVisibility(View.VISIBLE);
		} else
			holder.text4.setVisibility(View.INVISIBLE);

		if (holder.indicator != null) {
			if (art.hasFlag(Flags.Begabung)) {
				holder.indicator.setVisibility(View.VISIBLE);
				holder.indicator.setImageBitmap(indicatorStarGray);
			} else {
				holder.indicator.setVisibility(View.INVISIBLE);
			}
		}
		Util.applyRowStyle(art, listItem, position);

		return listItem;
	}

	private static class ViewHolder {
		TextView text1, text2, text3, text4, text5;
		ImageView indicator;
	}

}
