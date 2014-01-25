package com.dsatab.fragment;

import java.util.UUID;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.activity.ItemEditActivity;
import com.dsatab.data.Hero;
import com.dsatab.data.items.EquippedItem;
import com.dsatab.data.items.Item;
import com.dsatab.data.items.ItemSpecification;
import com.dsatab.util.Util;
import com.dsatab.view.CardView;
import com.dsatab.view.ItemListItem;
import com.dsatab.xml.DataManager;

public class ItemViewFragment extends BaseFragment {

	public static final String INTENT_EXTRA_HERO = "hero";
	public static final String INTENT_EXTRA_ITEM_ID = "itemId";
	public static final String INTENT_EXTRA_EQUIPPED_ITEM_ID = "equippedItemId";

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

		itemView = (ItemListItem) root.findViewById(R.id.inc_gal_item_view);
		itemView.setTextColor(Color.BLACK);
		itemView.setBackgroundColor(getResources().getColor(R.color.Brighter));

		categoryView = (TextView) root.findViewById(R.id.popup_edit_category);

		return root;
	}

	@Override
	public void onStart() {
		super.onStart();
		loadData();
		showCard(item, itemSpecification);
	}

	protected void loadData() {
		Hero hero = DsaTabApplication.getInstance().getHero();
		Bundle extra = getActivity().getIntent().getExtras();
		if (extra != null) {
			UUID itemId = (UUID) extra.getSerializable(INTENT_EXTRA_ITEM_ID);

			UUID equippedItemId = (UUID) extra.getSerializable(INTENT_EXTRA_EQUIPPED_ITEM_ID);
			if (equippedItemId != null && hero != null) {
				EquippedItem equippedItem = hero.getEquippedItem(equippedItemId);
				if (equippedItem != null) {
					item = equippedItem.getItem();
					itemSpecification = equippedItem.getItemSpecification();
				}
			}

			if (item == null && itemId != null) {
				if (hero != null) {
					item = hero.getItem(itemId);
				}
				if (item == null) {
					item = DataManager.getItemById(itemId);
				}
				if (item != null) {
					itemSpecification = item.getSpecifications().get(0);
				}
			}
		}

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
				itemSpecification = item.getSpecifications().get(0);
			}
		}
		showCard(item, itemSpecification);
	}

	private void showCard(Item card, ItemSpecification itemSpecification) {
		if (card != null) {
			if (card.hasImage()) {
				imageView.setItem(card);
				imageView.setVisibility(View.VISIBLE);
			} else {
				imageView.setItem(null);
				imageView.setVisibility(View.GONE);
			}
			itemView.setItem(card, itemSpecification);

			if (card.getIconUri() != null) {
				iconView.setImageURI(card.getIconUri());
			} else if (itemSpecification != null) {
				iconView.setImageResource(itemSpecification.getResourceId());
			}

			nameView.setText(card.getName());
			if (card.hasTitle())
				titleView.setText(card.getTitle());
			else
				titleView.setText(null);

			priceView.setText(Util.toString(card.getPrice()));
			weightView.setText(Util.toString(card.getWeight()));
			categoryView.setText(card.getCategory());
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
				UUID itemId = (UUID) extra.getSerializable(INTENT_EXTRA_ITEM_ID);
				UUID equippedItemId = (UUID) extra.getSerializable(INTENT_EXTRA_EQUIPPED_ITEM_ID);
				ItemEditActivity.edit(getActivity(), getHero(), itemId, equippedItemId);
			} else {
				ItemEditActivity.edit(getActivity(), getHero(), this.item);
			}
			return true;
		}
		}
		return super.onOptionsItemSelected(item);
	}

}
