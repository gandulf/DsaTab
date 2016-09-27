package com.dsatab.view.listener;

import com.dsatab.data.items.EquippedItem;
import com.dsatab.data.items.Item;
import com.dsatab.data.items.ItemContainer;

public interface HeroInventoryChangedListener {

	void onActiveSetChanged(int newSet, int oldSet);

	void onItemAdded(Item item);

	void onItemRemoved(Item item);

	void onItemChanged(Item item);

	void onItemChanged(EquippedItem item);

	void onItemEquipped(EquippedItem item);

	void onItemUnequipped(EquippedItem item);

	void onItemContainerAdded(ItemContainer itemContainer);

	void onItemContainerRemoved(ItemContainer itemContainer);

	void onItemContainerChanged(ItemContainer itemContainer);

}
