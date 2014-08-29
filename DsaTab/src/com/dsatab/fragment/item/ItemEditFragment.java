package com.dsatab.fragment.item;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.Spinner;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.data.Hero;
import com.dsatab.data.adapter.SpinnerSimpleAdapter;
import com.dsatab.data.enums.ItemType;
import com.dsatab.data.items.Item;
import com.dsatab.data.items.ItemSpecification;
import com.dsatab.data.items.MiscSpecification;
import com.dsatab.db.DataManager;
import com.dsatab.fragment.BaseFragment;
import com.dsatab.util.DsaUtil;
import com.dsatab.util.Util;
import com.dsatab.view.CardView;
import com.dsatab.view.ItemListItem;
import com.dsatab.view.dialog.ImageChooserDialog;
import com.gandulf.guilib.util.DefaultTextWatcher;

public class ItemEditFragment extends BaseFragment implements OnClickListener, OnCheckedChangeListener {

	private static final int ACTION_PHOTO = 1;

	public static final String INTENT_EXTRA_ITEM_ID = "itemId";
	public static final String INTENT_EXTRA_EQUIPPED_ITEM_ID = "equippedItemId";
	public static final String INTENT_EXTRA_HERO_KEY = "heroKey";

	private CardView imageView;
	private ItemListItem itemView;

	private EditText nameView, titleView, priceView, weightView;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup,
	 * android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.sheet_edit_item, container, false);

		nameView = (EditText) root.findViewById(R.id.popup_edit_name);
		nameView.addTextChangedListener(new DefaultTextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				if (cloneItem != null) {
					cloneItem.setName(s.toString());
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
					itemView.setItem(cloneItem, itemSpecification);
					imageView.setItem(cloneItem);
				}
			}
		});

		priceView = (EditText) root.findViewById(R.id.popup_edit_price);
		weightView = (EditText) root.findViewById(R.id.popup_edit_weight);
		iconView = (ImageView) root.findViewById(R.id.popup_edit_icon);
		iconView.setOnClickListener(this);

		imageView = (CardView) root.findViewById(R.id.popup_edit_image);
		imageView.setOnClickListener(this);
		imageView.setHighQuality(true);

		itemView = (ItemListItem) root.findViewById(R.id.inc_gal_item_view);
		itemView.setTextColor(Color.BLACK);
		itemView.setBackgroundColor(getResources().getColor(R.color.Brighter));

		categorySpn = (Spinner) root.findViewById(R.id.popup_edit_category);
		categoryAdapter = new SpinnerSimpleAdapter<String>(getActivity(), DataManager.getItemCategories());
		categorySpn.setAdapter(categoryAdapter);

		imageTextOverlayView = (CheckBox) root.findViewById(R.id.popup_edit_overlay);
		imageTextOverlayView.setOnCheckedChangeListener(this);

		return root;
	}

	@Override
	public void onResume() {
		super.onResume();

		showCard();
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.fragment.BaseFragment#onHeroLoaded(com.dsatab.data.Hero)
	 */
	@Override
	public void onHeroLoaded(Hero hero) {

	}

	private void showCard() {
		if (cloneItem != null && imageView != null) {
			imageView.setItem(cloneItem);
			itemView.setItem(cloneItem, itemSpecification);

			if (cloneItem.getIconUri() != null) {
				iconView.setImageURI(cloneItem.getIconUri());
			} else if (itemSpecification != null) {
				iconView.setImageResource(DsaUtil.getResourceId(itemSpecification));
			}
			nameView.setText(cloneItem.getName());
			if (cloneItem.hasTitle())
				titleView.setText(cloneItem.getTitle());
			else
				titleView.setText(null);

			if (cloneItem.getPrice() > 0) {
				priceView.setText(Util.toString(cloneItem.getPrice()));
			}
			if (cloneItem.getWeight() > 0.0f) {
				weightView.setText(Util.toString(cloneItem.getWeight()));
			}
			categorySpn.setSelection(categoryAdapter.getPosition(cloneItem.getCategory()));
			imageTextOverlayView.setChecked(cloneItem.isImageTextOverlay());
		}
	}

	public Item getItem() {
		return origItem;
	}

	public ItemSpecification getItemSpecification() {
		return itemSpecification;
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

		inflater.inflate(R.menu.menuitem_ok, menu);
		inflater.inflate(R.menu.menuitem_cancel, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.option_ok:
			accept();
			return true;
		case R.id.option_cancel:
			cancel();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public Item accept() {
		origItem.setName(nameView.getText().toString());
		origItem.setTitle(titleView.getText().toString());
		origItem.setPrice(Util.parseInt(priceView.getText().toString(), 0));
		origItem.setWeight(Util.parseFloat(weightView.getText().toString(), 0.0f));
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

		return origItem;
	}

	public Item cancel() {
		cloneItem = origItem;
		return origItem;
	}

	private void pickIcon() {
		final ImageChooserDialog pdialog = new ImageChooserDialog(getActivity());

		pdialog.setImageIds(DsaTabApplication.getInstance().getConfiguration().getDsaIcons());
		pdialog.setGridColumnWidth(getResources().getDimensionPixelSize(R.dimen.icon_button_size));
		pdialog.setScaleType(ScaleType.FIT_CENTER);
		pdialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				if (pdialog.getImageUri() != null && cloneItem != null) {
					cloneItem.setIconUri(pdialog.getImageUri());

					itemView.setItem(cloneItem, itemSpecification);

					if (cloneItem.getIconUri() != null)
						iconView.setImageURI(cloneItem.getIconUri());
					else
						iconView.setImageResource(DsaUtil.getResourceId(itemSpecification));
				}
			}
		});
		pdialog.show();

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
