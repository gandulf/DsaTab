package com.dsatab.data.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dsatab.R;
import com.dsatab.activity.DsaTabActivity.EditListener;
import com.dsatab.activity.DsaTabActivity.ProbeListener;
import com.dsatab.data.CombatDistanceTalent;
import com.dsatab.data.CombatMeleeTalent;
import com.dsatab.data.Hero;
import com.dsatab.data.Talent;
import com.dsatab.data.Talent.Flags;
import com.dsatab.data.TalentGroup;
import com.dsatab.data.enums.TalentGroupType;
import com.dsatab.util.Util;
import com.dsatab.view.ListFilterSettings;

public class ExpandableTalentAdapter extends BaseExpandableListAdapter {

	private static final int TYPE_COMBAT_TALENT = 1;

	private static final int TYPE_SIMPLE_TALENT = 0;

	private List<TalentGroupType> groups;

	private Hero hero;

	private Bitmap indicatorStar, indicatorStarGray, indicatorHouse, indicatorFlash, indicatorFlashGray;

	private ListFilterSettings filterSettings;

	private ProbeListener probeListener;
	private EditListener editListener;

	private Map<TalentGroupType, List<Talent>> groupsMap;

	private LayoutInflater inflater;

	public ExpandableTalentAdapter(Context context, Hero hero, ListFilterSettings settings) {
		this.hero = hero;
		this.filterSettings = new ListFilterSettings();
		this.filterSettings.set(settings);

		groups = new ArrayList<TalentGroupType>(Arrays.asList(TalentGroupType.values()));
		groups.retainAll(hero.getTalentGroups().keySet());

		groupsMap = new EnumMap<TalentGroupType, List<Talent>>(TalentGroupType.class);

		inflater = LayoutInflater.from(context);

		indicatorStar = BitmapFactory.decodeResource(context.getResources(), R.drawable.indicator_star);
		indicatorStarGray = BitmapFactory.decodeResource(context.getResources(), R.drawable.indicator_star_gray);
		indicatorHouse = BitmapFactory.decodeResource(context.getResources(), R.drawable.indicator_house);
		indicatorFlash = BitmapFactory.decodeResource(context.getResources(), R.drawable.indicator_flash);
		indicatorFlashGray = BitmapFactory.decodeResource(context.getResources(), R.drawable.indicator_flash_gray);

	}

	public boolean filter(ListFilterSettings settings) {

		boolean hasChanged = !filterSettings.equals(settings);

		if (hasChanged) {
			filterSettings.set(settings);
			notifyDataSetChanged();
		}

		return hasChanged;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.BaseExpandableListAdapter#notifyDataSetChanged()
	 */
	@Override
	public void notifyDataSetChanged() {
		groupsMap.clear();
		super.notifyDataSetChanged();
	}

	@Override
	public Talent getChild(int groupPosition, int childPosition) {
		TalentGroupType groupType = getGroup(groupPosition);

		List<Talent> talents = getTalents(groupType);

		if (talents != null && childPosition < talents.size() && childPosition >= 0)
			return talents.get(childPosition);
		else
			return null;
	}

	private List<Talent> getTalents(TalentGroupType groupType) {
		List<Talent> talents = groupsMap.get(groupType);

		if (talents == null) {
			TalentGroup talentGroup = hero.getTalentGroup(groupType);

			if (talentGroup != null && talentGroup.getTalents() != null) {
				talents = filter(talentGroup.getTalents());
			}
			groupsMap.put(groupType, talents);
		}

		return talents;
	}

	private List<Talent> filter(List<Talent> in) {

		if (filterSettings.isAllVisible()) {
			return in;
		} else {
			List<Talent> result = new ArrayList<Talent>();
			for (Talent t : in) {
				if (filterSettings.isVisible(t)) {
					result.add(t);
				}
			}

			return result;
		}
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		TalentGroupType groupType = getGroup(groupPosition);

		List<Talent> talents = getTalents(groupType);

		if (talents != null)
			return talents.size();
		else
			return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.BaseExpandableListAdapter#getChildTypeCount()
	 */
	@Override
	public int getChildTypeCount() {
		return 2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.BaseExpandableListAdapter#getChildType(int, int)
	 */
	@Override
	public int getChildType(int groupPosition, int childPosition) {
		Talent talent = getChild(groupPosition, childPosition);
		if (talent instanceof CombatMeleeTalent)
			return TYPE_COMBAT_TALENT;
		else
			return TYPE_SIMPLE_TALENT;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
			ViewGroup parent) {

		View listItem;
		ViewHolder holder;
		if (convertView == null) {
			listItem = inflater.inflate(R.layout.talent_list_item, parent, false);

			holder = new ViewHolder();
			// name
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

		Talent talent = getChild(groupPosition, childPosition);

		holder.text1.setText(talent.getName());

		String be = talent.getProbeInfo().getBe();

		if (TextUtils.isEmpty(be)) {
			Util.setVisibility(holder.text2, false, holder.text1);
			holder.text2.setText(null);
		} else {
			Util.setVisibility(holder.text2, true, holder.text1);
			holder.text2.setText(be);
		}
		if (talent.getComplexity() != null) {
			Util.setVisibility(holder.text2, true, holder.text1);
			if (holder.text2.length() > 0)
				holder.text2.append(" ");
			holder.text2.append(Util.toString(talent.getComplexity()));
		}
		holder.text3.setText(talent.getProbeInfo().getAttributesString());

		if (talent instanceof CombatMeleeTalent) {
			CombatMeleeTalent meleeTalent = (CombatMeleeTalent) talent;

			if (meleeTalent.getAttack() != null && meleeTalent.getAttack().getValue() != null) {
				int modifier = 0;
				if (filterSettings.isIncludeModifiers()) {
					modifier = hero.getModifier(meleeTalent.getAttack());
				}
				Util.setText(holder.text4, meleeTalent.getAttack().getValue(), modifier, null);
				holder.text4.setOnClickListener(probeListener);
				holder.text4.setOnLongClickListener(editListener);
				holder.text4.setTag(R.id.TAG_KEY_VALUE, meleeTalent);
				holder.text4.setTag(R.id.TAG_KEY_PROBE, meleeTalent.getAttack());
				Util.setVisibility(holder.text4, true, holder.text1);
			} else {
				Util.setVisibility(holder.text4, false, holder.text1);
			}

			if (meleeTalent.getDefense() != null && meleeTalent.getDefense().getValue() != null) {
				int modifier = 0;
				if (filterSettings.isIncludeModifiers()) {
					modifier = hero.getModifier(meleeTalent.getDefense());
				}
				Util.setText(holder.text5, meleeTalent.getDefense().getValue(), modifier, null);
				holder.text5.setOnClickListener(probeListener);
				holder.text5.setOnLongClickListener(editListener);
				holder.text5.setTag(R.id.TAG_KEY_VALUE, meleeTalent);
				holder.text5.setTag(R.id.TAG_KEY_PROBE, meleeTalent.getDefense());
				Util.setVisibility(holder.text5, true, holder.text1);
			} else {
				holder.text5.setVisibility(View.INVISIBLE);
			}

		} else if (talent instanceof CombatDistanceTalent) {

			CombatDistanceTalent distanceTalent = (CombatDistanceTalent) talent;

			if (distanceTalent.getValue() != null) {
				int modifier = 0;

				if (filterSettings.isIncludeModifiers()) {
					modifier = hero.getModifier(distanceTalent);
				}
				Util.setText(holder.text4, distanceTalent.getValue(), modifier, null);
				holder.text4.setOnClickListener(null);
				holder.text4.setClickable(false);
				Util.setVisibility(holder.text4, true, holder.text1);
			} else {
				Util.setVisibility(holder.text4, false, holder.text1);
			}
			Util.setVisibility(holder.text5, false, holder.text1);
		} else {
			int modifier = 0;
			if (filterSettings.isIncludeModifiers()) {
				modifier = hero.getModifier(talent);
			}
			Util.setText(holder.text4, talent.getValue(), modifier, null);
			holder.text4.setOnClickListener(null);
			holder.text4.setClickable(false); // hide text5 and expand text1
												// with its width
			Util.setVisibility(holder.text5, false, holder.text1);
		}

		if (holder.indicator != null) {
			if (!TextUtils.isEmpty(talent.getTalentSpezialisierung())) {
				holder.indicator.setVisibility(View.VISIBLE);
				holder.indicator.setImageBitmap(indicatorFlash);
			} else if (talent.hasFlag(Flags.Meisterhandwerk)) {
				holder.indicator.setVisibility(View.VISIBLE);
				holder.indicator.setImageBitmap(indicatorHouse);
			} else if (talent.hasFlag(Flags.Talentschub)) {
				holder.indicator.setVisibility(View.VISIBLE);
				holder.indicator.setImageBitmap(indicatorStar);
			} else if (talent.hasFlag(Flags.Begabung)) {
				holder.indicator.setVisibility(View.VISIBLE);
				holder.indicator.setImageBitmap(indicatorStarGray);
			} else {
				holder.indicator.setVisibility(View.INVISIBLE);
			}
		}

		Util.applyRowStyle(talent, listItem, childPosition);
		return listItem;
	}

	@Override
	public TalentGroupType getGroup(int groupPosition) {
		return groups.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return groups.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

		View listItem = null;
		ViewHeaderHolder holder;
		if (convertView != null) {
			listItem = convertView;
			holder = (ViewHeaderHolder) convertView.getTag();
		} else {
			listItem = inflater.inflate(R.layout.talent_list_headeritem, parent, false);

			holder = new ViewHeaderHolder();
			holder.text1 = (TextView) listItem.findViewById(R.id.talent_list_headeritem);
			holder.indicator = (ImageView) listItem.findViewById(R.id.talent_list_item_indicator);

			listItem.setTag(holder);
		}

		TalentGroupType groupType = getGroup(groupPosition);

		if (groupType != null) {
			TalentGroup talentGroup = hero.getTalentGroup(groupType);

			holder.text1.setText(groupType.name());

			if (holder.indicator != null && talentGroup.hasFlag(Flags.Begabung)) {
				holder.indicator.setVisibility(View.VISIBLE);
				holder.indicator.setImageResource(R.drawable.indicator_star);
			}
		}

		return listItem;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	public ProbeListener getProbeListener() {
		return probeListener;
	}

	public void setProbeListener(ProbeListener probeListener) {
		this.probeListener = probeListener;
	}

	public EditListener getEditListener() {
		return editListener;
	}

	public void setEditListener(EditListener editListener) {
		this.editListener = editListener;
	}

	private static class ViewHolder {
		TextView text1, text2, text3, text4, text5;
		ImageView indicator;
	}

	private static class ViewHeaderHolder {
		TextView text1;
		ImageView indicator;
	}

}
