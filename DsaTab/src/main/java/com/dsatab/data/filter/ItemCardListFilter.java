package com.dsatab.data.filter;

import android.text.TextUtils;

import com.dsatab.data.adapter.EquippedItemRecyclerAdapter;
import com.dsatab.data.adapter.ListRecyclerAdapter;
import com.dsatab.data.adapter.ListRecyclerFilter;
import com.dsatab.data.enums.ItemType;
import com.dsatab.data.items.EquippedItem;
import com.dsatab.data.items.Item;
import com.dsatab.data.items.ItemCard;
import com.dsatab.data.items.ItemSpecification;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class ItemCardListFilter<T extends ItemCard> extends ListRecyclerFilter<T> {

	private List<ItemType> types;

	private String category;

	public ItemCardListFilter(ListRecyclerAdapter<EquippedItemRecyclerAdapter.ItemViewHolder,T> list) {
		super(list);
	}

	public List<ItemType> getTypes() {
		return types;
	}

	public void setTypes(List<ItemType> type) {
		this.types = type;
	}

	public void setType(ItemType type) {
		if (type != null)
			this.types = Arrays.asList(type);
		else
			this.types = null;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	@Override
	protected boolean isFilterSet() {
		return constraint != null || types != null || category != null;
	}

	@Override
	public boolean filter(ItemCard m) {
		boolean valid = true;
		Item item = null;
		if (m instanceof Item)
			item = (Item) m;
		else if (m instanceof EquippedItem) {
			item = ((EquippedItem) m).getItem();
		}

		if (types != null) {
			boolean found = false;

			for (ItemSpecification spec : item.getSpecifications()) {
				if (types.contains(spec.getType())) {
					found = true;
					break;
				}
			}

			valid &= found;
		}

		if (!TextUtils.isEmpty(category)) {
			valid &= category.equals(item.getCategory());
		}

		if (constraint != null) {
			valid &= item.getName().toLowerCase(Locale.GERMAN).startsWith(constraint);
		}

		return valid;
	}

}
