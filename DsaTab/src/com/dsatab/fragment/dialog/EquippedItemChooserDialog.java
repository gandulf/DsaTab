package com.dsatab.fragment.dialog;

import java.util.Collections;
import java.util.List;

import uk.me.lewisdeane.ldialogs.CustomDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.data.adapter.EquippedItemAdapter;
import com.dsatab.data.items.EquippedItem;
import com.dsatab.data.items.Weapon;

public class EquippedItemChooserDialog extends DialogFragment implements AdapterView.OnItemClickListener,
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

		CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
		builder.setDarkTheme(DsaTabApplication.getInstance().isDarkTheme());

		builder.setTitle("Wähle einen Gegenstand...");

		View popupcontent = builder.setView(R.layout.popup_equipped_item_chooser);

		builder.setPositiveButton(android.R.string.ok, this);
		builder.setNegativeButton(android.R.string.cancel, this);

		itemList = (ListView) popupcontent.findViewById(R.id.popup_equipped_item_list);
		itemList.setOnItemClickListener(this);

		itemAdapter = new EquippedItemAdapter(builder.getContext(), equippedItems);

		itemList.setAdapter(itemAdapter);
		itemList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

		bhKampf = (CheckBox) popupcontent.findViewById(R.id.popup_equipped_item_bk);

		CustomDialog dialog = builder.create();

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
