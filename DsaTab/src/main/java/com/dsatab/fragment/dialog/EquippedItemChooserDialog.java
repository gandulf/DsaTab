package com.dsatab.fragment.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;

import com.dsatab.R;
import com.dsatab.data.adapter.EquippedItemAdapter;
import com.dsatab.data.items.EquippedItem;
import com.dsatab.data.items.Weapon;

import java.util.Collections;
import java.util.List;

public class EquippedItemChooserDialog extends AppCompatDialogFragment implements AdapterView.OnItemClickListener,
		DialogInterface.OnClickListener {

	public static final String TAG = "EquippedItemChooserDialog";

	private EquippedItemAdapter itemAdapter = null;

	private ListView itemList;

	private CheckBox bhKampf;

	private EquippedItem selectedItem = null;

	private List<EquippedItem> equippedItems = Collections.emptyList();

	public OnAcceptListener onAcceptListener;

	public interface OnAcceptListener {
		public void onAccept(EquippedItem item, boolean bhKampf);
	}

	public static void show(Fragment parent, List<EquippedItem> equippedItems, EquippedItem selectedItem,
			OnAcceptListener acceptListener, int requestCode) {
		EquippedItemChooserDialog dialog = new EquippedItemChooserDialog();

		Bundle args = new Bundle();
		// TODO value should be set as argument
		dialog.equippedItems = equippedItems;
		dialog.selectedItem = selectedItem;
		dialog.onAcceptListener = acceptListener;
		dialog.setArguments(args);
		dialog.setTargetFragment(parent, requestCode);
		dialog.show(parent.getFragmentManager(), TAG);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Wähle einen Gegenstand...");

        LayoutInflater inflater =  LayoutInflater.from(builder.getContext());

		View popupcontent = inflater.inflate(R.layout.popup_equipped_item_chooser, null, false);
        builder.setView(popupcontent);

		builder.setPositiveButton(android.R.string.ok, this);
		builder.setNegativeButton(android.R.string.cancel, this);

		itemList = (ListView) popupcontent.findViewById(R.id.popup_equipped_item_list);
		itemList.setOnItemClickListener(this);

		itemAdapter = new EquippedItemAdapter(builder.getContext(), equippedItems);

		itemList.setAdapter(itemAdapter);
		itemList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

		bhKampf = (CheckBox) popupcontent.findViewById(R.id.popup_equipped_item_bk);

		AlertDialog dialog = builder.create();

		dialog.setCanceledOnTouchOutside(true);

		if (selectedItem != null) {
			itemList.setItemChecked(itemAdapter.getPosition(selectedItem), true);
		}
		refreshBeidhändigerKampf();

		return dialog;
	}

	public List<EquippedItem> getEquippedItems() {
		return equippedItems;
	}

	public OnAcceptListener getOnAcceptListener() {
		return onAcceptListener;
	}

	public EquippedItem getSelectedItem() {
		return selectedItem;
	}

	public boolean isBeidhändigerKampf() {
		return bhKampf.isChecked();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.DialogInterface.OnClickListener#onClick(android.content .DialogInterface, int)
	 */
	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		case DialogInterface.BUTTON_POSITIVE:
			if (onAcceptListener != null) {
				onAcceptListener.onAccept(selectedItem, bhKampf.isChecked() && bhKampf.isEnabled());
			}
			EquippedItemChooserDialog.this.dismiss();
			break;
		case DialogInterface.BUTTON_NEGATIVE:
			selectedItem = null;
			getDialog().cancel();
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
