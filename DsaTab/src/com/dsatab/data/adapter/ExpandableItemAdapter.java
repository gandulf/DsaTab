package com.dsatab.data.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dsatab.R;
import com.dsatab.data.Hero;
import com.dsatab.data.enums.ItemType;
import com.dsatab.data.filter.ItemListFilter;
import com.dsatab.data.items.EquippedItem;
import com.dsatab.data.items.Item;
import com.dsatab.data.items.ItemContainer;
import com.dsatab.util.Util;
import com.dsatab.view.CheckableImageButton;
import com.dsatab.view.EquippedItemListItem;

public class ExpandableItemAdapter extends BaseExpandableListAdapter implements OnClickListener {

	private List<ItemContainer> itemContainers;

	private Hero hero;

	private ItemListFilter filter;

	private Map<ItemContainer, List<Item>> itemContainersMap;

	private LayoutInflater inflater;

	private Context context;

	public ExpandableItemAdapter(Context context, Hero hero) {
		this.hero = hero;
		this.context = context;

		itemContainers = new ArrayList<ItemContainer>(hero.getItemContainers());
		itemContainersMap = new HashMap<ItemContainer, List<Item>>();

		inflater = LayoutInflater.from(context);

	}

	public void filter(List<ItemType> type, String category, String constraint) {
		getFilter().setTypes(type);
		filter.setCategory(category);
		notifyDataSetChanged();
	}

	public ItemListFilter getFilter() {
		if (filter == null)
			filter = new ItemListFilter(null);

		return filter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.BaseExpandableListAdapter#notifyDataSetChanged()
	 */
	@Override
	public void notifyDataSetChanged() {
		itemContainers = new ArrayList<ItemContainer>(hero.getItemContainers());
		itemContainersMap.clear();
		super.notifyDataSetChanged();
	}

	@Override
	public Item getChild(int groupPosition, int childPosition) {
		ItemContainer groupType = getGroup(groupPosition);
		if (groupType != null) {
			List<Item> items = getItems(groupType);

			if (items != null && childPosition < items.size() && childPosition >= 0) {
				return items.get(childPosition);
			}
		}
		return null;
	}

	private List<Item> getItems(ItemContainer itemContainer) {
		List<Item> items = itemContainersMap.get(itemContainer);

		if (items == null) {
			items = filter(hero.getItems(itemContainer.getId()));
			itemContainersMap.put(itemContainer, items);
		}

		return items;
	}

	private List<Item> filter(List<Item> in) {

		if (!getFilter().isFilterSet()) {
			return in;
		} else {
			List<Item> result = new ArrayList<Item>();
			for (Item t : in) {
				if (getFilter().filter(t)) {
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
		ItemContainer groupType = getGroup(groupPosition);
		if (groupType != null) {
			List<Item> items = getItems(groupType);

			if (items != null) {
				return items.size();
			}
		}
		return 0;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
			ViewGroup parent) {

		EquippedItemListItem view;
		if (!(convertView instanceof EquippedItemListItem)) {
			view = (EquippedItemListItem) inflater.inflate(R.layout.equippeditem_listitem, parent, false);
		} else {
			view = (EquippedItemListItem) convertView;
		}

		Item item = getChild(groupPosition, childPosition);
		view.setItem(item);

		if (item != null && item.isEquipable()) {
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

		Util.applyRowStyle(view, childPosition);
		return view;
	}

	@Override
	public ItemContainer getGroup(int groupPosition) {
		if (groupPosition >= 0 && groupPosition < itemContainers.size())
			return itemContainers.get(groupPosition);
		else
			return null;
	}

	@Override
	public int getGroupCount() {
		return itemContainers.size();
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

		ItemContainer item = getGroup(groupPosition);

		if (item != null) {
			StringBuilder sb = new StringBuilder(item.getName());
			sb.append(" (");
			sb.append(item.getItems().size() + " "
					+ context.getResources().getQuantityString(R.plurals.items, item.getItems().size()));
			sb.append(", ");
			sb.append(context.getResources().getString(R.string.label_capacity, item.getWeight(), item.getCapacity()));
			sb.append(")");

			holder.text1.setText(sb);

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
				hero.addEquippedItem(context, item, null, null, 0);
				break;
			case R.id.set2:
				hero.addEquippedItem(context, item, null, null, 1);
				break;
			case R.id.set3:
				hero.addEquippedItem(context, item, null, null, 2);
				break;
			}
		} else if (v.getTag() instanceof EquippedItem) {
			EquippedItem equippedItem = (EquippedItem) v.getTag();
			hero.removeEquippedItem(equippedItem);
		}

	}

	private static class ViewHeaderHolder {
		TextView text1;
		ImageView indicator;
	}

}
