package com.dsatab.fragment.item;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.activity.BaseItemActivity;
import com.dsatab.data.Hero;
import com.dsatab.data.adapter.SpinnerSimpleAdapter;
import com.dsatab.data.enums.ItemType;
import com.dsatab.data.items.EquippedItem;
import com.dsatab.data.items.Item;
import com.dsatab.data.items.ItemSpecification;
import com.dsatab.data.items.MiscSpecification;
import com.dsatab.db.DataManager;
import com.dsatab.fragment.BaseEditFragment;
import com.dsatab.fragment.dialog.ImageChooserDialog;
import com.dsatab.util.DsaUtil;
import com.dsatab.util.Util;
import com.dsatab.view.CardView;
import com.dsatab.view.ItemListItem;
import com.dsatab.util.DefaultTextWatcher;
import com.dsatab.util.ResUtil;

import java.util.UUID;

public class ItemEditFragment extends BaseEditFragment implements OnClickListener, OnCheckedChangeListener {

    private static final int ACTION_PHOTO = 1;

    public static final String INTENT_EXTRA_ITEM_ID = "itemId";
    public static final String INTENT_EXTRA_EQUIPPED_ITEM_ID = "equippedItemId";
    public static final String INTENT_EXTRA_HERO_KEY = "heroKey";

    private CardView imageView;
    private ItemListItem itemView;

    private EditText nameView, titleView, priceView, weightView, countView;
    private ImageView iconView;
    private CheckBox imageTextOverlayView;
    private Spinner categorySpn;
    private SpinnerSimpleAdapter<String> categoryAdapter;

    private Item origItem = null, cloneItem = null;
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

    /*
         * (non-Javadoc)
         *
         * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
         */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.sheet_item_edit, container, false);

        nameView = (EditText) root.findViewById(R.id.popup_edit_name);
        nameView.addTextChangedListener(new DefaultTextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                if (cloneItem != null) {
                    cloneItem.setName(s.toString());
                    if (itemView != null)
                        itemView.setItem(cloneItem, itemSpecification);
                    imageView.setItem(cloneItem);
                }
            }
        });

        titleView = (EditText) root.findViewById(R.id.popup_edit_title);
        titleView.addTextChangedListener(new DefaultTextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                if (cloneItem != null) {
                    cloneItem.setTitle(s.toString());
                    if (itemView != null)
                        itemView.setItem(cloneItem, itemSpecification);
                    imageView.setItem(cloneItem);
                }
            }
        });

        priceView = (EditText) root.findViewById(R.id.popup_edit_price);
        weightView = (EditText) root.findViewById(R.id.popup_edit_weight);
        countView = (EditText) root.findViewById(R.id.popup_edit_count);
        iconView = (ImageView) root.findViewById(R.id.popup_edit_icon);
        iconView.setOnClickListener(this);

        imageView = (CardView) root.findViewById(R.id.popup_edit_image);
        imageView.setOnClickListener(this);
        imageView.setHighQuality(true);

        itemView = (ItemListItem) root.findViewById(R.id.inc_gal_item_view);
        if (itemView != null)
            itemView.setBackgroundResource(0);

        categorySpn = (Spinner) root.findViewById(R.id.popup_edit_category);
        categoryAdapter = new SpinnerSimpleAdapter<String>(getActivity(), DataManager.getItemCategories());
        categorySpn.setAdapter(categoryAdapter);

        imageTextOverlayView = (CheckBox) root.findViewById(R.id.popup_edit_overlay);
        imageTextOverlayView.setOnCheckedChangeListener(this);

        return root;
    }

    public void setItem(Item item, ItemSpecification itemSpecification) {

        if (item == null) {
            origItem = new Item();
            cloneItem = origItem;
            this.itemSpecification = new MiscSpecification(origItem, ItemType.Sonstiges);
            origItem.addSpecification(this.itemSpecification);
        } else {

            this.itemSpecification = itemSpecification;
            this.origItem = item;
            this.cloneItem = item.clone();

            for (ItemSpecification specification : cloneItem.getSpecifications()) {
                if (specification.equals(itemSpecification)) {
                    this.itemSpecification = specification;
                    break;
                }
            }
        }

        showCard();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onActivityResult(int, int, android.content.Intent)
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case ACTION_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    cloneItem.setImageUri(Util.retrieveBitmapUri(getActivity(), data));
                    imageView.setItem(cloneItem);
                }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showCard() {
        if (cloneItem != null) {

            if (imageView != null) {
                imageView.setItem(cloneItem);
            }
            if (itemView != null) {
                itemView.setItem(cloneItem, itemSpecification);
            }

            if (iconView != null) {
                if (cloneItem.getIconUri() != null) {
                    iconView.setImageDrawable(ResUtil.getDrawableByUri(iconView.getContext(),cloneItem.getIconUri()));
                } else if (itemSpecification != null) {
                    iconView.setImageResource(DsaUtil.getResourceId(itemSpecification));
                }
            }
            if (nameView != null) {
                nameView.setText(cloneItem.getName());
            }

            if (titleView != null) {
                if (cloneItem.hasTitle())
                    titleView.setText(cloneItem.getTitle());
                else
                    titleView.setText(null);
            }


            if (priceView != null && cloneItem.getPrice() > 0) {
                priceView.setText(Util.toString(cloneItem.getPrice()));
            }
            if (weightView != null && cloneItem.getWeight() > 0.0f) {
                weightView.setText(Util.toString(cloneItem.getWeight()));
            }
            if (countView != null && cloneItem.getCount() > 0) {
                countView.setText(Util.toString(cloneItem.getCount()));
            }
            if (categorySpn != null) {
                categorySpn.setSelection(categoryAdapter.getPosition(cloneItem.getCategory()));
            }
            if (imageTextOverlayView != null) {
                imageTextOverlayView.setChecked(cloneItem.isImageTextOverlay());
            }
        }
    }

    public Item getItem() {
        return origItem;
    }

    public ItemSpecification getItemSpecification() {
        return itemSpecification;
    }

    public Bundle accept() {
        Bundle bundle = new Bundle();

        origItem.setName(nameView.getText().toString());
        origItem.setTitle(titleView.getText().toString());
        origItem.setPrice(Util.parseInt(priceView.getText().toString(), 0));
        origItem.setWeight(Util.parseFloat(weightView.getText().toString(), 0.0f));
        origItem.setCount(Util.parseInt(countView.getText().toString(), 0));
        origItem.setIconUri(cloneItem.getIconUri());
        origItem.setImageUri(cloneItem.getImageUri());
        origItem.setCategory((String) categorySpn.getSelectedItem());
        origItem.setImageTextOverlay(cloneItem.isImageTextOverlay());

        DataManager.createOrUpdateItem(origItem);

        if (getActivity().getIntent().hasExtra(ItemEditFragment.INTENT_EXTRA_HERO_KEY)) {
            if (DsaTabApplication.getInstance().getHero().getItem(origItem.getId()) == null) {
                DsaTabApplication.getInstance().getHero().addItem(origItem);
            } else {
                DsaTabApplication.getInstance().getHero().fireItemChangedEvent(origItem);
            }
        }

        return bundle;
    }

    public void cancel() {
        cloneItem = origItem;
        return;
    }

    private void pickIcon() {

        ImageChooserDialog.pickIcons(this, new ImageChooserDialog.OnImageSelectedListener() {

            @Override
            public void onImageSelected(Uri imageUri) {
                if (imageUri != null && cloneItem != null) {
                    cloneItem.setIconUri(imageUri);

                    if (itemView != null)
                        itemView.setItem(cloneItem, itemSpecification);

                    if (cloneItem.getIconUri() != null)
                        iconView.setImageDrawable(ResUtil.getDrawableByUri(iconView.getContext(), cloneItem.getIconUri()));
                    else
                        iconView.setImageResource(DsaUtil.getResourceId(itemSpecification));
                }
            }
        }, 0);

    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.CompoundButton.OnCheckedChangeListener#onCheckedChanged (android.widget.CompoundButton,
     * boolean)
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.popup_edit_overlay:
                if (cloneItem != null) {
                    cloneItem.setImageTextOverlay(isChecked);
                    imageView.setItem(cloneItem);
                }
                break;
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.popup_edit_image:
                Util.pickImage(this, ACTION_PHOTO);
                break;
            case R.id.popup_edit_icon:
                pickIcon();
                break;
        }

    }

}
