package com.dsatab.data.adapter;

import java.util.List;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dsatab.R;
import com.dsatab.activity.DsaTabActivity.ProbeListener;
import com.dsatab.common.StyleableSpannableStringBuilder;
import com.dsatab.data.CombatProbe;
import com.dsatab.data.Hero;
import com.dsatab.data.enums.AttributeType;
import com.dsatab.data.enums.Hand;
import com.dsatab.data.enums.UsageType;
import com.dsatab.data.filter.EquippedItemListFilter;
import com.dsatab.data.items.Armor;
import com.dsatab.data.items.DistanceWeapon;
import com.dsatab.data.items.EquippedItem;
import com.dsatab.data.items.Item;
import com.dsatab.data.items.ItemSpecification;
import com.dsatab.data.items.Shield;
import com.dsatab.data.items.Weapon;
import com.dsatab.fragment.FightFragment.TargetListener;
import com.dsatab.util.Util;
import com.dsatab.view.FightFilterSettings;
import com.dsatab.view.ItemListItem;

public class FightEquippedItemAdapter extends OpenArrayAdapter<EquippedItem> {

	private Hero hero;

	private EquippedItemListFilter filter;

	private ProbeListener probeListener;
	private TargetListener targetListener;

	private LayoutInflater inflater;

	private static final int ITEM_TYPE_VIEW = 0;

	private static final int ITEM_TYPE_EDIT = 1;

	public FightEquippedItemAdapter(Context context, Hero hero, List<EquippedItem> items, FightFilterSettings settings) {
		super(context, 0, Util.sort(items));
		this.hero = hero;
		inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		if (settings != null && !settings.isAllVisible())
			filter(settings);
	}

	public void filter(FightFilterSettings settings) {
		boolean hasChanged = false;
		if (settings != null) {
			hasChanged = (getFilter().getSettings().isShowArmor() != settings.isShowArmor() || getFilter()
					.getSettings().isIncludeModifiers() != settings.isIncludeModifiers());

			if (hasChanged) {
				getFilter().getSettings().set(settings);
				filter.filter((String) null);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.BaseAdapter#getViewTypeCount()
	 */
	@Override
	public int getViewTypeCount() {
		return 2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.BaseAdapter#getItemViewType(int)
	 */
	@Override
	public int getItemViewType(int position) {
		EquippedItem equippedItem = getItem(position);
		if (equippedItem.getItemSpecification() instanceof Armor)
			return ITEM_TYPE_VIEW;
		else
			return ITEM_TYPE_EDIT;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ArrayAdapter#getFilter()
	 */
	@Override
	public EquippedItemListFilter getFilter() {
		if (filter == null) {
			filter = new EquippedItemListFilter(this);
		}
		return filter;
	}

	public ProbeListener getProbeListener() {
		return probeListener;
	}

	public void setProbeListener(ProbeListener probeListener) {
		this.probeListener = probeListener;
	}

	public TargetListener getTargetListener() {
		return targetListener;
	}

	public void setTargetListener(TargetListener targetListener) {
		this.targetListener = targetListener;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// View view = super.getView(position, convertView, parent);

		ViewHolder holder = null;
		ItemListItem itemLayout = null;
		if (!(convertView instanceof ItemListItem)) {
			if (getItemViewType(position) == ITEM_TYPE_VIEW)
				itemLayout = (ItemListItem) inflater.inflate(R.layout.item_listitem_view, parent, false);
			else
				itemLayout = (ItemListItem) inflater.inflate(R.layout.item_listitem, parent, false);
		} else {
			itemLayout = (ItemListItem) convertView;
			holder = (ViewHolder) itemLayout.getTag();
		}

		if (holder == null) {
			holder = new ViewHolder();
			holder.text1 = (TextView) itemLayout.findViewById(android.R.id.text1);
			holder.text2 = (TextView) itemLayout.findViewById(android.R.id.text2);
			holder.text3 = (TextView) itemLayout.findViewById(R.id.text3);
			holder.icon1 = (ImageView) itemLayout.findViewById(android.R.id.icon1);
			holder.icon2 = (ImageView) itemLayout.findViewById(android.R.id.icon2);
			holder.icon_chain_bottom = (ImageView) itemLayout.findViewById(R.id.icon_chain_bottom);
			holder.icon_chain_top = (ImageView) itemLayout.findViewById(R.id.icon_chain_top);
			itemLayout.setTag(holder);
		}

		EquippedItem equippedItem = getItem(position);
		Item item = equippedItem.getItem();
		ItemSpecification itemSpecification = equippedItem.getItemSpecification();

		// if (equippedItem.getSecondaryItem() != null
		// &&
		// (equippedItem.getSecondaryItem().getItem().hasSpecification(Shield.class)
		// || (equippedItem
		// .getSecondaryItem().getItem().hasSpecification(Weapon.class) &&
		// equippedItem.getHand() == Hand.rechts))) {
		//
		// } else {
		// fightItemsOdd = !fightItemsOdd;
		// }

		StyleableSpannableStringBuilder title = new StyleableSpannableStringBuilder();
		if (!TextUtils.isEmpty(item.getTitle()))
			title.append(item.getTitle());

		holder.text2.setText(itemSpecification.getInfo());

		holder.icon1.setImageResource(itemSpecification.getResourceId());
		holder.icon1.setVisibility(View.VISIBLE);
		holder.icon1.setTag(null);
		holder.icon1.setOnClickListener(null);
		holder.icon2.setTag(null);
		holder.icon2.setOnClickListener(null);
		holder.text3.setText("");
		if (itemSpecification instanceof DistanceWeapon) {
			DistanceWeapon distanceWeapon = (DistanceWeapon) itemSpecification;

			holder.icon2.setImageResource(R.drawable.icon_target);
			holder.icon2.setVisibility(View.VISIBLE);

			if (equippedItem.getTalent() != null) {
				CombatProbe probe = equippedItem.getCombatProbeAttacke();
				Util.appendValue(hero, title, probe, null, getFilter().getSettings().isIncludeModifiers());
				holder.icon2.setEnabled(true);
				holder.icon1.setEnabled(true);
				holder.icon2.setTag(equippedItem);
				holder.icon2.setOnClickListener(targetListener);
				holder.icon1.setTag(probe);
				holder.icon1.setOnClickListener(probeListener);
			} else {
				holder.icon2.setEnabled(false);
				holder.icon1.setEnabled(false);
			}

			holder.text2.setText(distanceWeapon.getInfo(hero.getModifierTP(equippedItem)));
		} else if (itemSpecification instanceof Shield) {
			holder.icon1.setVisibility(View.INVISIBLE);
			if (equippedItem.getUsageType() == UsageType.Paradewaffe)
				holder.icon2.setImageURI(item.getIconUri());
			else
				holder.icon2.setImageResource(R.drawable.icon_shield);

			holder.icon2.setVisibility(View.VISIBLE);

			if (equippedItem.getTalent() != null) {
				holder.icon2.setEnabled(true);
				CombatProbe probe = equippedItem.getCombatProbeDefense();
				Util.appendValue(hero, title, probe, null, getFilter().getSettings().isIncludeModifiers());
				holder.icon2.setTag(probe);
				holder.icon2.setOnClickListener(probeListener);

				holder.text3.setText(equippedItem.getTalent().getName());
			} else {
				holder.icon2.setEnabled(false);
			}

		} else if (itemSpecification instanceof Weapon) {
			Weapon weapon = (Weapon) itemSpecification;

			holder.icon2.setImageResource(R.drawable.icon_shield);
			holder.icon2.setVisibility(View.VISIBLE);
			if (equippedItem.getTalent() != null) {

				CombatProbe pa = null, at = null;

				if (weapon.isAttackable()) {
					holder.icon1.setEnabled(true);
					holder.icon1.setVisibility(View.VISIBLE);
					at = equippedItem.getCombatProbeAttacke();
					holder.icon1.setTag(at);
					holder.icon1.setOnClickListener(probeListener);
				} else {
					holder.icon1.setVisibility(View.INVISIBLE);
				}

				if (weapon.isDefendable()) {
					holder.icon2.setEnabled(true);
					holder.icon2.setVisibility(View.VISIBLE);
					pa = equippedItem.getCombatProbeDefense();
					holder.icon2.setTag(pa);
					holder.icon2.setOnClickListener(probeListener);
				} else {
					holder.icon2.setVisibility(View.INVISIBLE);
				}

				String talentName = null;
				if (equippedItem.getTalent() != null) {
					talentName = equippedItem.getTalent().getName();
				}

				SpannableStringBuilder sb = new SpannableStringBuilder();

				if (!TextUtils.isEmpty(weapon.getName())) {
					sb.append(weapon.getName());
					sb.append("/");
				}
				if (!TextUtils.isEmpty(equippedItem.getItemSpecification().getSpecificationLabel())) {
					sb.append(equippedItem.getItemSpecification().getSpecificationLabel());
					sb.append("/");
				}
				sb.append(talentName);

				if (equippedItem.getHand() == Hand.links) {
					sb.append(" (Links)");
				}

				if (equippedItem.isBeidhändigerKampf()) {
					sb.append(" - BK");
				}

				holder.text3.setText(sb);

				Util.appendValue(hero, title, at, pa, getFilter().getSettings().isIncludeModifiers());
			} else {
				holder.icon2.setEnabled(false);
				holder.icon1.setEnabled(false);
			}
			if (getFilter().getSettings().isIncludeModifiers()) {
				holder.text2.setText(weapon.getInfo(hero.getModifiedValue(AttributeType.Körperkraft, true, true),
						hero.getModifierTP(equippedItem)));
			} else {
				holder.text2.setText(weapon.getInfo());
			}
		} else if (itemSpecification instanceof Armor) {
			// Armor armor = (Armor) itemSpecification;
			holder.icon2.setVisibility(View.GONE);
			holder.icon1.setFocusable(false);
			holder.icon1.setClickable(false);
		}

		if (hero.getHuntingWeapon() != null && hero.getHuntingWeapon().equals(equippedItem)) {
			holder.text3.setText(" Jagdwaffe");
		}

		if (holder.icon_chain_top != null && holder.icon_chain_bottom != null) {
			if (equippedItem.getSecondaryItem() != null) {
				if (position > 0 && getItem(position - 1).equals(equippedItem.getSecondaryItem())) {
					holder.icon_chain_bottom.setVisibility(View.VISIBLE);
					holder.icon_chain_top.setVisibility(View.GONE);
				} else if (position < getCount() && getItem(position + 1).equals(equippedItem.getSecondaryItem())) {
					holder.icon_chain_top.setVisibility(View.VISIBLE);
					holder.icon_chain_bottom.setVisibility(View.GONE);
				}
			} else {
				holder.icon_chain_top.setVisibility(View.GONE);
				holder.icon_chain_bottom.setVisibility(View.GONE);
			}
		}

		holder.text1.setText(title);

		Util.applyRowStyle(itemLayout, position);

		return itemLayout;
	}

	private static class ViewHolder {
		TextView text1, text2, text3;
		ImageView icon1, icon2, icon_chain_top, icon_chain_bottom;
	}

}
