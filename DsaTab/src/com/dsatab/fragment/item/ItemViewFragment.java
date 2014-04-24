package com.dsatab.fragment.item;

import java.util.UUID;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dsatab.R;
import com.dsatab.activity.ItemsActivity;
import com.dsatab.data.Hero;
import com.dsatab.data.items.Item;
import com.dsatab.data.items.ItemSpecification;
import com.dsatab.fragment.BaseFragment;
import com.dsatab.util.DsaUtil;
import com.dsatab.util.Util;
import com.dsatab.view.CardView;
import com.dsatab.view.ItemListItem;

public class ItemViewFragment extends BaseFragment {

	private CardView imageView;
	private ItemListItem itemView;

	private TextView nameView, titleView, priceView, weightView;
	private ImageView iconView;
	private TextView categoryView;

	private Item item = null;
	private ItemSpecification itemSpecification;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.fragment.BaseFragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onResume() {
		super.onResume();
		showCard();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup,
	 * android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.sheet_item_view, container, false);

		nameView = (TextView) root.findViewById(R.id.popup_edit_name);
		titleView = (TextView) root.findViewById(R.id.popup_edit_title);
		priceView = (TextView) root.findViewById(R.id.popup_edit_price);
		weightView = (TextView) root.findViewById(R.id.popup_edit_weight);
		iconView = (ImageView) root.findViewById(R.id.popup_edit_icon);
		imageView = (CardView) root.findViewById(R.id.popup_edit_image);
		imageView.setHighQuality(true);
		imageView.setVisibility(View.GONE);

		itemView = (ItemListItem) root.findViewById(R.id.inc_gal_item_view);
		itemView.setTextColor(Color.BLACK);
		itemView.setBackgroundColor(getResources().getColor(R.color.Brighter));

		categoryView = (TextView) root.findViewById(R.id.popup_edit_category);

		return root;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.fragment.BaseFragment#onHeroLoaded(com.dsatab.data.Hero)
	 */
	@Override
	public void onHeroLoaded(Hero hero) {

	}

	public void setItem(Item item, ItemSpecification itemSpecification) {
		if (item != null) {
			this.item = item;
			if (itemSpecification != null) {
				this.itemSpecification = itemSpecification;
			} else if (!item.getSpecifications().isEmpty()) {
				this.itemSpecification = item.getSpecifications().get(0);
			}
		} else {
			this.item = null;
			this.itemSpecification = null;
		}

		showCard();
	}

	public Item getItem() {
		return item;
	}

	public ItemSpecification getItemSpecification() {
		return itemSpecification;
	}

	public void showCard() {
		if (item != null && imageView != null) {
			if (item.hasImage()) {
				imageView.setItem(item);
				imageView.setVisibility(View.VISIBLE);
			} else {
				imageView.setItem(null);
				imageView.setVisibility(View.GONE);
			}
			itemView.setItem(item, itemSpecification);

			if (item.getIconUri() != null) {
				iconView.setImageURI(item.getIconUri());
			} else if (itemSpecification != null) {
				iconView.setImageResource(DsaUtil.getResourceId(itemSpecification));
			}

			nameView.setText(item.getName());
			if (item.hasTitle())
				titleView.setText(item.getTitle());
			else
				titleView.setText(null);

			priceView.setText(Util.toString(item.getPrice()));
			weightView.setText(Util.toString(item.getWeight()));
			categoryView.setText(item.getCategory());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.actionbarsherlock.app.SherlockFragment#onCreateOptionsMenu(com. actionbarsherlock.view.Menu,
	 * com.actionbarsherlock.view.MenuInflater)
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

		inflater.inflate(R.menu.menuitem_edit, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.option_edit: {

			Bundle extra = getActivity().getIntent().getExtras();
			if (extra != null) {
				UUID itemId = (UUID) extra.getSerializable(ItemsActivity.INTENT_EXTRA_ITEM_ID);
				UUID equippedItemId = (UUID) extra.getSerializable(ItemsActivity.INTENT_EXTRA_EQUIPPED_ITEM_ID);

				String heroKey = extra.getString(ItemsActivity.INTENT_EXTRA_HERO_KEY);
				ItemsActivity.edit(getActivity(), heroKey, itemId, equippedItemId, ItemsActivity.ACTION_EDIT);
			} else {
				ItemsActivity.edit(getActivity(), (String) null, this.item, ItemsActivity.ACTION_EDIT);
			}
			return true;
		}
		}
		return super.onOptionsItemSelected(item);
	}

}
