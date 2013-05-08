package com.dsatab.view;

import java.util.Collections;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.dsatab.R;
import com.dsatab.data.adapter.EquippedItemAdapter;
import com.dsatab.data.items.EquippedItem;
import com.dsatab.data.items.Weapon;

public class EquippedItemChooserDialog extends AlertDialog implements AdapterView.OnItemClickListener,
		DialogInterface.OnClickListener {

	private EquippedItemAdapter itemAdapter = null;

	private ListView itemList;

	private CheckBox bhKampf;

	private EquippedItem selectedItem = null;

	private List<EquippedItem> equippedItems = Collections.emptyList();

	public OnAcceptListener onAcceptListener;

	public interface OnAcceptListener {
		public void onAccept(EquippedItem item, boolean bhKampf);
	}

	public EquippedItemChooserDialog(Context context) {
		super(context);
		init();
	}

	public EquippedItemChooserDialog(Context context, int theme) {
		super(context, theme);
		init();

	}

	@Override
	protected void onStart() {
		super.onStart();

		itemAdapter = new EquippedItemAdapter(getContext(), equippedItems);
		itemList.setAdapter(itemAdapter);
		itemList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		if (selectedItem != null) {
			itemList.setItemChecked(itemAdapter.getPosition(selectedItem), true);
		}
		refreshBeidhändigerKampf();
	}

	public List<EquippedItem> getEquippedItems() {
		return equippedItems;
	}

	public void setEquippedItems(List<EquippedItem> equippedItems) {
		this.equippedItems = equippedItems;
	}

	public OnAcceptListener getOnAcceptListener() {
		return onAcceptListener;
	}

	public void setOnAcceptListener(OnAcceptListener onAcceptListener) {
		this.onAcceptListener = onAcceptListener;
	}

	public EquippedItem getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(EquippedItem selectedItem) {
		this.selectedItem = selectedItem;
	}

	public boolean isBeidhändigerKampf() {
		return bhKampf.isChecked();
	}

	private void init() {
		setTitle("Wähle einen Gegenstand...");

		setCanceledOnTouchOutside(true);

		RelativeLayout popupcontent = (RelativeLayout) LayoutInflater.from(getContext()).inflate(
				R.layout.popup_equipped_item_chooser, null, false);
		popupcontent.setLayoutParams(new LayoutParams(android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
				android.widget.LinearLayout.LayoutParams.WRAP_CONTENT));
		setView(popupcontent);

		setButton(BUTTON_POSITIVE, getContext().getString(android.R.string.ok), this);
		setButton(BUTTON_NEGATIVE, getContext().getString(android.R.string.cancel), this);

		itemList = (ListView) popupcontent.findViewById(R.id.popup_equipped_item_list);
		itemList.setOnItemClickListener(this);

		bhKampf = (CheckBox) popupcontent.findViewById(R.id.popup_equipped_item_bk);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.content.DialogInterface.OnClickListener#onClick(android.content
	 * .DialogInterface, int)
	 */
	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		case BUTTON_POSITIVE:
			if (onAcceptListener != null) {
				onAcceptListener.onAccept(selectedItem, bhKampf.isChecked() && bhKampf.isEnabled());
			}
			EquippedItemChooserDialog.this.dismiss();
			break;
		case BUTTON_NEGATIVE:
			selectedItem = null;
			EquippedItemChooserDialog.this.cancel();
			break;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		selectedItem = itemAdapter.getItem(position);
		refreshBeidhändigerKampf();
	}

	/**
	 * 
	 */
	private void refreshBeidhändigerKampf() {
		bhKampf.setEnabled((selectedItem == null || selectedItem.getItemSpecification() instanceof Weapon));
	}

}
