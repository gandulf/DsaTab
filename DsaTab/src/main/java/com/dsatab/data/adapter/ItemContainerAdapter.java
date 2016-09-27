package com.dsatab.data.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.dsatab.R;
import com.dsatab.data.Hero;
import com.dsatab.data.items.ItemContainer;
import com.dsatab.util.Util;
import com.dsatab.util.ViewUtils;
import com.franlopez.flipcheckbox.FlipCheckBox;
import com.h6ah4i.android.widget.advrecyclerview.selectable.ElevatingSelectableViewHolder;

import java.util.ArrayList;
import java.util.Collection;

public class ItemContainerAdapter extends ListRecyclerAdapter<ItemContainerAdapter.ContainerViewHolder, ItemContainer<?>> {


	public ItemContainerAdapter() {
		super(new ArrayList<ItemContainer<?>>());
	}

	public ItemContainerAdapter(Collection<ItemContainer<?>> container) {
		super(container);
	}

	@Override
	public long getItemId(int position) {
		ItemContainer<?> container = getItem(position);
		if (container != null)
			return container.getId();
		else
			return AdapterView.INVALID_ROW_ID;
	}

	@Override
	public ContainerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		return new ContainerViewHolder(inflate(inflater,parent,R.layout.item_listitem_view,false));
	}

	@Override
	public void onBindViewHolder(ContainerViewHolder holder, int position) {
		super.onBindViewHolder(holder, position);

		prepareView(holder, position);

		Util.applyRowStyle(holder.itemView, position);
	}

	protected void prepareView(ContainerViewHolder holder, int position) {
		ItemContainer<?> itemContainer = getItem(position);

		holder.text1.setText(itemContainer.getName());
		holder.icon1.setFrontDrawable(ViewUtils.circleIcon(holder.icon1.getContext(), itemContainer.getIconUri()));

		holder.text2.setText(itemContainer.size() + " "
				+ holder.text3.getContext().getResources().getQuantityString(R.plurals.items, itemContainer.size()));

		if (itemContainer.getId() >= Hero.FIRST_INVENTORY_SCREEN) {
			if (itemContainer.getCapacity() != 0 || itemContainer.getWeight() != 0.0f) {
				holder.text3.setVisibility(View.VISIBLE);
				holder.text3.setText(holder.text3.getContext().getResources().getString(R.string.capacity_value,
						itemContainer.getWeight(), itemContainer.getCapacity()));
			} else {
				holder.text3.setVisibility(View.GONE);
			}
		} else {
			holder.text3.setText(null);
		}

	}

	public static class ContainerViewHolder extends ElevatingSelectableViewHolder{
		TextView text1, text2, text3;
        FlipCheckBox icon1;
		ImageView icon2;

		public ContainerViewHolder(View v) {
			super(v);
			text1 = (TextView) v.findViewById(android.R.id.text1);
			text2 = (TextView) v.findViewById(android.R.id.text2);
			text3 = (TextView) v.findViewById(R.id.text3);
			icon1 = (FlipCheckBox) v.findViewById(android.R.id.checkbox);
			icon2 = (ImageView) v.findViewById(android.R.id.icon2);

		}
	}

}
