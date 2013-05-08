/*
 * Copyright (C) 2010 Gandulf Kohlweiss
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms
 * of the GNU General Public License as published by the Free Software Foundation;
 * either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program;
 * if not, see <http://www.gnu.org/licenses/>.
 * 
 */
package com.dsatab.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.ImageView.ScaleType;
import android.widget.Spinner;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.data.Hero;
import com.dsatab.data.adapter.SpinnerSimpleAdapter;
import com.dsatab.data.items.Item;
import com.dsatab.data.items.ItemSpecification;
import com.dsatab.data.items.ItemType;
import com.dsatab.data.items.MiscSpecification;
import com.dsatab.util.Util;
import com.dsatab.view.CardView;
import com.dsatab.view.ItemListItem;
import com.dsatab.view.PortraitChooserDialog;
import com.dsatab.xml.DataManager;
import com.gandulf.guilib.util.DefaultTextWatcher;

public class ItemEditFragment extends BaseFragment implements OnClickListener, OnCheckedChangeListener {

	private static final int ACTION_PHOTO = 1;

	public static final String INTENT_EXTRA_ITEM_ID = "itemId";
	public static final String INTENT_EXTRA_EQUIPPED_ITEM_ID = "equippedItemId";
	public static final String INTENT_EXTRA_HERO = "hero";

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
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.sheet_edit_item, container, false);

		nameView = (EditText) root.findViewById(R.id.popup_edit_name);
		titleView = (EditText) root.findViewById(R.id.popup_edit_title);
		priceView = (EditText) root.findViewById(R.id.popup_edit_price);
		weightView = (EditText) root.findViewById(R.id.popup_edit_weight);
		iconView = (ImageView) root.findViewById(R.id.popup_edit_icon);
		imageView = (CardView) root.findViewById(R.id.popup_edit_image);
		itemView = (ItemListItem) root.findViewById(R.id.inc_gal_item_view);
		categorySpn = (Spinner) root.findViewById(R.id.popup_edit_category);
		imageTextOverlayView = (CheckBox) root.findViewById(R.id.popup_edit_overlay);
		imageView.setOnClickListener(this);
		imageTextOverlayView.setOnCheckedChangeListener(this);

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
				origItem = hero.getItem(itemId);
				if (origItem == null)
					origItem = DataManager.getItemById(itemId);

				cloneItem = origItem.clone();
			}
		}

		nameView.addTextChangedListener(new DefaultTextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				cloneItem.setName(s.toString());
				itemView.setItem(cloneItem, itemSpecification);
				imageView.setItem(cloneItem);
			}
		});

		titleView.addTextChangedListener(new DefaultTextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				cloneItem.setTitle(s.toString());
				itemView.setItem(cloneItem, itemSpecification);
				imageView.setItem(cloneItem);
			}
		});

		imageView.setHighQuality(true);
		itemView.setTextColor(Color.BLACK);
		itemView.setBackgroundColor(getResources().getColor(R.color.Brighter));
		iconView.setOnClickListener(this);

		categoryAdapter = new SpinnerSimpleAdapter<String>(getActivity(), DataManager.getItemCategories());

		categorySpn.setAdapter(categoryAdapter);
		if (cloneItem == null) {
			cloneItem = new Item();
			cloneItem.addSpecification(new MiscSpecification(cloneItem, ItemType.Sonstiges));
			origItem = cloneItem;
		}

		itemSpecification = cloneItem.getSpecifications().get(0);

		showCard(cloneItem, itemSpecification);

		super.onActivityCreated(savedInstanceState);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onActivityResult(int, int,
	 * android.content.Intent)
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
		categorySpn.setSelection(categoryAdapter.getPosition(card.getCategory()));
		imageTextOverlayView.setChecked(card.isImageTextOverlay());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.actionbarsherlock.app.SherlockFragment#onCreateOptionsMenu(com.
	 * actionbarsherlock.view.Menu, com.actionbarsherlock.view.MenuInflater)
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
	}

	public Item accept() {
		origItem.setName(nameView.getText().toString());
		origItem.setTitle(titleView.getText().toString());
		origItem.setPrice(Util.parseInt(priceView.getText().toString()));
		origItem.setWeight(Util.parseFloat(weightView.getText().toString()));
		origItem.setIconUri(cloneItem.getIconUri());
		origItem.setImageUri(cloneItem.getImageUri());
		origItem.setCategory((String) categorySpn.getSelectedItem());
		origItem.setImageTextOverlay(cloneItem.isImageTextOverlay());
		return origItem;
	}

	public void cancel() {

	}

	private void pickPortrait() {
		final PortraitChooserDialog pdialog = new PortraitChooserDialog(getActivity());

		List<Integer> itemIcons = DsaTabApplication.getInstance().getConfiguration().getItemIcons();

		List<Uri> portraitPaths = new ArrayList<Uri>(itemIcons.size());
		for (Integer resId : itemIcons) {
			portraitPaths.add(Util.getUriForResourceId(resId));
		}

		pdialog.setImages(portraitPaths);
		pdialog.setScaleType(ScaleType.FIT_CENTER);
		pdialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				if (pdialog.getImageUri() != null) {
					cloneItem.setIconUri(pdialog.getImageUri());

					itemView.setItem(cloneItem, itemSpecification);

					if (cloneItem.getIconUri() != null)
						iconView.setImageURI(cloneItem.getIconUri());
					else
						iconView.setImageResource(itemSpecification.getResourceId());
				}
			}
		});
		pdialog.show();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.CompoundButton.OnCheckedChangeListener#onCheckedChanged
	 * (android.widget.CompoundButton, boolean)
	 */
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.popup_edit_overlay:
			cloneItem.setImageTextOverlay(isChecked);
			imageView.setItem(cloneItem);
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
			Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
			photoPickerIntent.setType("image/*");
			startActivityForResult(Intent.createChooser(photoPickerIntent, "Bild auswählen"), ACTION_PHOTO);
			break;
		case R.id.popup_edit_icon:
			pickPortrait();
			break;
		}

	}

}
