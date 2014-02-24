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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.data.Hero;
import com.dsatab.data.items.ItemContainer;
import com.dsatab.util.Util;
import com.dsatab.view.PictureChooserDialog;

public class ItemContainerEditFragment extends BaseFragment implements OnClickListener {

	private EditText editCapacity;
	private EditText editName;
	private ImageView iconView;

	private Uri iconUri;

	private ItemContainer itemContainer;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup,
	 * android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = configureContainerView(inflater.inflate(R.layout.sheet_edit_item_container, container, false));

		editName = (EditText) root.findViewById(R.id.popup_edit_name);
		editCapacity = (EditText) root.findViewById(R.id.popup_edit_capacity);
		iconView = (ImageView) root.findViewById(R.id.popup_edit_icon);
		iconView.setOnClickListener(this);

		return root;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.fragment.BaseFragment#onHeroLoaded(com.dsatab.data.Hero)
	 */
	@Override
	public void onHeroLoaded(Hero hero) {

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
		final PictureChooserDialog pdialog = new PictureChooserDialog(getActivity());

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
