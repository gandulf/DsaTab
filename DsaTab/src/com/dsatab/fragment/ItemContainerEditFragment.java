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

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.data.Hero;
import com.dsatab.data.items.ItemContainer;
import com.dsatab.util.Util;
import com.dsatab.view.PortraitChooserDialog;

/**
 * @author Seraphim
 * 
 */
public class ItemContainerEditFragment extends BaseFragment implements OnItemSelectedListener, OnClickListener {

	public static final String INTENT_ITEM_CHOOSER_ID = "com.dsatab.data.intent.itemContainerId";

	private EditText editCapacity;
	private EditText editName;
	private ImageView iconView;

	private Uri iconUri;

	private ItemContainer itemContainer;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return configureContainerView(inflater.inflate(R.layout.sheet_edit_item_container, container, false));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		editName = (EditText) findViewById(R.id.popup_edit_name);
		editCapacity = (EditText) findViewById(R.id.popup_edit_capacity);
		iconView = (ImageView) findViewById(R.id.popup_edit_icon);
		iconView.setOnClickListener(this);

		Bundle extra = getActivity().getIntent().getExtras();
		if (extra != null) {
			int containerId = extra.getInt(INTENT_ITEM_CHOOSER_ID, -1);
			if (containerId >= 0) {
				itemContainer = getHero().getItemContainer(containerId);
			}
		}

		if (itemContainer == null) {
			itemContainer = new ItemContainer();
		}
		editCapacity.setText(Integer.toString(itemContainer.getCapacity()));
		editName.setText(itemContainer.getName());
		iconView.setImageURI(itemContainer.getIconUri());

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget
	 * .AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

		if (parent.getId() == R.id.popup_edit_icon) {
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.AdapterView.OnItemSelectedListener#onNothingSelected(android
	 * .widget.AdapterView)
	 */
	@Override
	public void onNothingSelected(AdapterView<?> arg0) {

	}

	/**
	 * 
	 */
	public void cancel() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.popup_edit_icon:
			pickPortrait();
			break;

		default:
			break;
		}

	}

	/**
	 * 
	 */
	public ItemContainer accept() {
		Util.hideKeyboard(editName);
		itemContainer.setCapacity(Util.parseInt(editCapacity.getText().toString(), 0));
		itemContainer.setName(editName.getText().toString());
		itemContainer.setIconUri(iconUri);

		return itemContainer;
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
					iconUri = pdialog.getImageUri();
					iconView.setImageURI(iconUri);
				}
			}
		});
		pdialog.show();

	}

}
