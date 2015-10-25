package com.dsatab.fragment.item;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.activity.BaseItemActivity;
import com.dsatab.activity.ItemsActivity;
import com.dsatab.data.Hero;
import com.dsatab.data.items.EquippedItem;
import com.dsatab.data.items.Item;
import com.dsatab.data.items.ItemSpecification;
import com.dsatab.db.DataManager;
import com.dsatab.fragment.BaseEditFragment;
import com.dsatab.util.DsaUtil;
import com.dsatab.util.Util;
import com.dsatab.view.CardView;
import com.dsatab.view.ItemListItem;

import java.util.UUID;

public class ItemViewFragment extends BaseEditFragment {

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
        setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle extra = getExtra();

        Item item = null;
        ItemSpecification itemSpecification = null;

        Hero hero = DsaTabApplication.getInstance().getHero();

        if (extra != null) {
            UUID itemId = (UUID) extra.getSerializable(BaseItemActivity.INTENT_EXTRA_ITEM_ID);

            UUID equippedItemId = (UUID) extra.getSerializable(BaseItemActivity.INTENT_EXTRA_EQUIPPED_ITEM_ID);
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

        setItem(item, itemSpecification);
    }

    @Override
    public Bundle accept() {
        return null;
    }

    @Override
    public void cancel() {

    }

    /*
    * (non-Javadoc)
    *
    * @see android.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
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
        itemView.setBackgroundResource(0);

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
        if (getActivity() != null) {
            getActivity().invalidateOptionsMenu();
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
        if (item != null) {

            if (imageView != null) {
                if (item.hasImage()) {
                    imageView.setItem(item);
                    imageView.setVisibility(View.VISIBLE);
                } else {
                    imageView.setItem(null);
                    imageView.setVisibility(View.GONE);
                }
            }
            if (itemView != null) {
                itemView.setItem(item, itemSpecification);
            }

            if (iconView != null) {
                if (item.getIconUri() != null) {
                    iconView.setImageURI(item.getIconUri());
                } else if (itemSpecification != null) {
                    iconView.setImageResource(DsaUtil.getResourceId(itemSpecification));
                }
            }

            if (nameView != null) {
                nameView.setText(item.getName());
            }
            if (titleView != null) {
                if (item.hasTitle())
                    titleView.setText(item.getTitle());
                else
                    titleView.setText(null);
            }

            if (priceView != null)
                priceView.setText(Util.toString(item.getPrice()) + " " + getString(R.string.label_kreuzer));
            if (weightView != null)
                weightView.setText(Util.toString(item.getWeight()) + " " + getString(R.string.label_ounces));
            if (categoryView != null)
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
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem edit = menu.findItem(R.id.option_edit);
        if (edit != null) {
            edit.setVisible(item != null);
        }
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
