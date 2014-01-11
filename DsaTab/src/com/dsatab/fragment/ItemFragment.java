package com.dsatab.fragment;

import java.util.UUID;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.activity.ItemEditActivity;
import com.dsatab.data.Hero;
import com.dsatab.data.items.Item;
import com.dsatab.data.items.ItemSpecification;
import com.dsatab.util.Util;
import com.dsatab.view.CardView;
import com.dsatab.view.ItemListItem;
import com.dsatab.xml.DataManager;

public class ItemFragment extends BaseFragment {

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
		itemView = (ItemListItem) root.findViewById(R.id.inc_gal_item_view);
		categoryView = (TextView) root.findViewById(R.id.popup_edit_category);

		return root;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Hero hero = DsaTabApplication.getInstance().getHero();

		if (hero == null) {
			Toast.makeText(getActivity(), "Fehler: Kein Held geladen.", Toast.LENGTH_SHORT).show();
			getActivity().finish();
			super.onActivityCreated(savedInstanceState);
			return;
		}
		Bundle extra = getActivity().getIntent().getExtras();
		if (extra != null) {
			UUID itemId = (UUID) extra.getSerializable(INTENT_EXTRA_ITEM_ID);
			if (itemId != null) {
				item = hero.getItem(itemId);
				if (item == null)
					item = DataManager.getItemById(itemId);
			}
		}

		imageView.setHighQuality(true);
		itemView.setTextColor(Color.BLACK);
		itemView.setBackgroundColor(getResources().getColor(R.color.Brighter));
		if (item != null) {
			itemSpecification = item.getSpecifications().get(0);
			showCard(item, itemSpecification);
		}
		super.onActivityCreated(savedInstanceState);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.fragment.BaseFragment#onHeroLoaded(com.dsatab.data.Hero)
	 */
	@Override
	public void onHeroLoaded(Hero hero) {

	}

	private void showCard(Item card, ItemSpecification itemSpecification) {
		imageView.setItem(card);
		itemView.setItem(card, itemSpecification);

		if (card.getIconUri() != null)
			iconView.setImageURI(card.getIconUri());
		else
			iconView.setImageResource(itemSpecification.getResourceId());

		nameView.setText(card.getName());
		if (card.hasTitle())
			titleView.setText(card.getTitle());
		else
			titleView.setText(null);

		priceView.setText(Util.toString(card.getPrice()));
		weightView.setText(Util.toString(card.getWeight()));
		categoryView.setText(card.getCategory());
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
			ItemEditActivity.edit(getActivity(), getHero(), this.item);
			return true;
		}
		}
		return super.onOptionsItemSelected(item);
	}

}
