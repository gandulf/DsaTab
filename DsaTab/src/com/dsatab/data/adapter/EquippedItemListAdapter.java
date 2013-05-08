package com.dsatab.data.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.dsatab.R;
import com.dsatab.data.Hero;
import com.dsatab.data.filter.ItemListFilter;
import com.dsatab.data.items.EquippedItem;
import com.dsatab.data.items.Item;
import com.dsatab.data.items.ItemType;
import com.dsatab.util.Util;
import com.dsatab.view.CheckableImageButton;
import com.dsatab.view.EquippedItemListItem;

public class EquippedItemListAdapter extends OpenArrayAdapter<Item> implements OnClickListener {

	private Hero hero;

	private ItemListFilter filter;

	private LayoutInflater inflater;

	public EquippedItemListAdapter(Context context, Hero hero, Item[] objects) {
		super(context, 0, objects);
		this.hero = hero;
		inflater = LayoutInflater.from(context);
	}

	public EquippedItemListAdapter(Context context, Hero hero, List<Item> objects) {
		super(context, 0, objects);
		this.hero = hero;
		inflater = LayoutInflater.from(context);
	}

	public void filter(List<ItemType> type, String category, String constraint) {
		getFilter().setTypes(type);
		filter.setCategory(category);
		filter.filter(constraint);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ArrayAdapter#getFilter()
	 */
	@Override
	public ItemListFilter getFilter() {
		if (filter == null)
			filter = new ItemListFilter(this);

		return filter;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// View view = super.getView(position, convertView, parent);
		EquippedItemListItem view;
		if (!(convertView instanceof EquippedItemListItem)) {
			view = (EquippedItemListItem) inflater.inflate(R.layout.equippeditem_listitem, parent, false);
		} else {
			view = (EquippedItemListItem) convertView;
		}

		Item item = getItem(position);
		view.setItem(item);

		if (item.isEquipable()) {

			for (int set = 0; set < Hero.MAXIMUM_SET_NUMBER; set++) {
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
			}

		} else {
			for (int set = 0; set < Hero.MAXIMUM_SET_NUMBER; set++) {
				view.getSet(set).setTag(null);
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

}
