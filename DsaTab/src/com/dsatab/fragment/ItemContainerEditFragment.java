package com.dsatab.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.activity.BaseEditActivity;
import com.dsatab.data.items.ItemContainer;
import com.dsatab.fragment.dialog.ImageChooserDialog;
import com.dsatab.util.Util;

public class ItemContainerEditFragment extends BaseEditFragment implements OnClickListener {

	public static void insert(Fragment fragment) {
		Intent intent = new Intent(fragment.getActivity(), BaseEditActivity.class);
		intent.setAction(Intent.ACTION_INSERT);
		intent.putExtra(BaseEditActivity.EDIT_FRAGMENT_CLASS, ItemContainerEditFragment.class);
		fragment.startActivity(intent);
	}

	public static void edit(Fragment fragment, ItemContainer itemContainer) {
		Intent intent = new Intent(fragment.getActivity(), BaseEditActivity.class);
		intent.setAction(Intent.ACTION_EDIT);
		intent.putExtra(BaseEditActivity.EDIT_FRAGMENT_CLASS, ItemContainerEditFragment.class);
		intent.putExtra(INTENT_ITEM_CHOOSER_ID, itemContainer.getId());
		fragment.startActivity(intent);
	}

	public static final String INTENT_ITEM_CHOOSER_ID = "com.dsatab.data.intent.itemContainerId";

	private EditText editCapacity;
	private EditText editName;
	private ImageView iconView;

	private Uri iconUri;

	private ItemContainer itemContainer;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.sheet_edit_item_container, container, false);

		editName = (EditText) root.findViewById(R.id.popup_edit_name);
		editCapacity = (EditText) root.findViewById(R.id.popup_edit_capacity);
		iconView = (ImageView) root.findViewById(R.id.popup_edit_icon);
		iconView.setOnClickListener(this);

		return root;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		Bundle extra = getExtra();
		if (extra != null) {
			int containerId = extra.getInt(INTENT_ITEM_CHOOSER_ID, -1);
			if (containerId >= 0) {
				itemContainer = DsaTabApplication.getInstance().getHero().getItemContainer(containerId);
			}
		}
		setItemContainer(itemContainer);
	}

	public void setItemContainer(ItemContainer itemContainer) {
		if (itemContainer == null) {
			itemContainer = new ItemContainer();
		}
		this.itemContainer = itemContainer;
	}

	@Override
	public void onResume() {
		super.onResume();
		updateView();
	}

	private void updateView() {
		if (editCapacity != null) {
			if (itemContainer.getCapacity() > 0) {
				editCapacity.setText(Integer.toString(itemContainer.getCapacity()));
			} else {
				editCapacity.setText(null);
			}
		}
		if (editName != null) {
			editName.setText(itemContainer.getName());
		}
		if (iconView != null) {
			iconView.setImageURI(itemContainer.getIconUri());
		}

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
			pickIcon();
			break;

		default:
			break;
		}

	}

	/**
	 * 
	 */
	public Bundle accept() {
		Util.hideKeyboard(editName);
		itemContainer.setCapacity(Util.parseInt(editCapacity.getText().toString(), 0));
		itemContainer.setName(editName.getText().toString());
		itemContainer.setIconUri(iconUri);

		if (itemContainer.getId() == ItemContainer.INVALID_ID) {
			DsaTabApplication.getInstance().getHero().addItemContainer(itemContainer);
		} else {
			DsaTabApplication.getInstance().getHero().fireItemContainerChangedEvent(itemContainer);
		}

		Bundle data = new Bundle();
		// TODO fill bundle
		return data;
	}

	private void pickIcon() {
		ImageChooserDialog.pickIcons(this, new ImageChooserDialog.OnImageSelectedListener() {

			@Override
			public void onImageSelected(Uri imageUri) {
				iconView.setImageURI(imageUri);
			}
		}, 0);
	}

}
