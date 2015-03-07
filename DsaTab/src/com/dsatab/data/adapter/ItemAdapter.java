package com.dsatab.data.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.dsatab.R;
import com.dsatab.data.Hero;
import com.dsatab.data.enums.ItemType;
import com.dsatab.data.filter.ItemCardListFilter;
import com.dsatab.data.items.EquippedItem;
import com.dsatab.data.items.Item;
import com.dsatab.data.items.ItemCard;
import com.dsatab.data.items.ItemContainer;
import com.dsatab.util.Util;
import com.dsatab.view.CheckableImageButton;
import com.dsatab.view.EquippedItemListItem;
import com.gandulf.guilib.data.OpenArrayAdapter;

import java.util.List;

public class ItemAdapter extends OpenArrayAdapter<ItemCard> implements OnClickListener {

	private Hero hero;

	private ItemCardListFilter filter;

	private int containerId = -1;

	public ItemAdapter(Context context, Hero hero) {
		super(context, 0, 0);
		this.hero = hero;
	}

	public ItemCardListFilter getFilter() {
		if (filter == null)
			filter = new ItemCardListFilter(this);

		return filter;
	}

	public void filter(List<ItemType> types, String category, String constraint) {
		getFilter().setTypes(types);
		filter.setCategory(category);
		filter.filter(constraint);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		EquippedItemListItem view;
		if (!(convertView instanceof EquippedItemListItem)) {
			view = (EquippedItemListItem) mInflater.inflate(R.layout.item_listitem_item, parent, false);
			convertView = view;
		} else {
			view = (EquippedItemListItem) convertView;
		}

		Item item = getItem(position).getItem();
		view.setItem(item);

		if (item != null && item.isEquipable()) {
			for (int set = 0; set < Hero.INVENTORY_SET_COUNT; set++) {
				CheckableImageButton setButton = view.getSet(set);
				setButton.setChecked(false);
				setButton.setOnClickListener(this);
				setButton.setTag(item);

				for (EquippedItem equippedItem : hero.getEquippedItems(set)) {
					if (equippedItem.getItem().equals(item)) {
						setButton.setChecked(true);
						setButton.setTag(equippedItem);
						break;
					}
				}

				if (containerId >= ItemContainer.SET1 && containerId <= ItemContainer.SET3) {
					setButton.setVisibility(containerId == set ? View.VISIBLE : View.INVISIBLE);
				} else {
					setButton.setVisibility(View.VISIBLE);
				}
			}
		} else {
			for (int set = 0; set < Hero.INVENTORY_SET_COUNT; set++) {
				view.getSet(set).setTag(null);
				view.getSet(set).setVisibility(View.INVISIBLE);
			}
		}

		Util.applyRowStyle(view, position);
		return view;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {

		if (v.getTag() instanceof Item) {
			Item item = (Item) v.getTag();
			switch (v.getId()) {
			case R.id.set1:
				hero.addEquippedItem(getContext(), item, null, null, 0);
				break;
			case R.id.set2:
				hero.addEquippedItem(getContext(), item, null, null, 1);
				break;
			case R.id.set3:
				hero.addEquippedItem(getContext(), item, null, null, 2);
				break;
			}
		} else if (v.getTag() instanceof EquippedItem) {
			EquippedItem equippedItem = (EquippedItem) v.getTag();
			hero.removeEquippedItem(equippedItem);
		}

	}

	public int getContainerId() {
		return containerId;
	}

	public void setContainerId(int activeSet) {
		this.containerId = activeSet;
	}

}
