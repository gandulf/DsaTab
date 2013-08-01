package com.dsatab.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.dsatab.R;
import com.dsatab.activity.ItemEditActivity;
import com.dsatab.data.adapter.ItemCursorAdapter;
import com.dsatab.data.adapter.SpinnerSimpleAdapter;
import com.dsatab.data.enums.ItemType;
import com.dsatab.data.items.Item;
import com.dsatab.util.Util;
import com.dsatab.xml.DataManager;

public class ItemChooserDialog extends AlertDialog implements android.view.View.OnClickListener, OnItemSelectedListener {

	private ItemCursorAdapter itemAdapter = null;

	private ListView itemList;

	private Spinner categorySpinner;

	private SpinnerSimpleAdapter<ItemType> categoryAdapter;

	private Collection<ItemType> itemTypes = null;

	private ImageButton searchButton;
	private Button newButton;
	private EditText searchText;

	public ItemChooserDialog(Context context) {
		super(context);
		init();
	}

	@Override
	protected void onStart() {
		super.onStart();

		itemAdapter = new ItemCursorAdapter(getContext(), DataManager.getItemsCursor(null, itemTypes, null));

		if (searchText.getVisibility() == View.VISIBLE) {
			// do not show category spinner we are in search mode
		} else {
			categorySpinner.setVisibility(View.VISIBLE);
		}

		itemList.setAdapter(itemAdapter);

		if (itemTypes != null)
			categorySpinner.setSelection(categoryAdapter.getPosition(itemTypes.iterator().next()));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Dialog#onStop()
	 */
	@Override
	protected void onStop() {
		super.onStop();

		if (itemAdapter.getCursor() != null) {
			itemAdapter.getCursor().close();
		}

	}

	public void filter(Collection<ItemType> type, String category, String constraint) {
		if (itemAdapter != null) {
			itemAdapter.changeCursor(DataManager.getItemsCursor(constraint, type, category));
		}
	}

	public void show(Collection<ItemType> types) {
		closeSearch();
		setItemTypes(types);
		show();
	}

	public Collection<ItemType> getItemTypes() {
		return itemTypes;
	}

	public void setItemTypes(Collection<ItemType> itemType) {
		this.itemTypes = itemType;
	}

	private void toggleSearch() {
		if (searchText.getVisibility() == View.VISIBLE) {
			closeSearch();
			Util.hideKeyboard(searchText);
		} else
			openSearch();
	}

	private void openSearch() {
		searchText.setText("");
		searchText.setVisibility(View.VISIBLE);
		categorySpinner.setVisibility(View.INVISIBLE);
		searchText.requestFocus();

		searchButton.setSelected(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Dialog#onSearchRequested()
	 */
	@Override
	public boolean onSearchRequested() {
		toggleSearch();
		return false;
	}

	private void closeSearch() {
		categorySpinner.setVisibility(View.VISIBLE);
		searchText.setVisibility(View.INVISIBLE);
		searchText.clearFocus();
		searchButton.setSelected(false);

		ItemType type = (ItemType) categorySpinner.getSelectedItem();
		if (type != null)
			filter(Arrays.asList(type), null, null);
	}

	private void init() {

		itemTypes = new ArrayList<ItemType>();
		itemTypes.add(ItemType.Waffen);

		setTitle("Wähle einen Gegenstand...");

		setCanceledOnTouchOutside(true);

		RelativeLayout popupcontent = (RelativeLayout) LayoutInflater.from(getContext()).inflate(
				R.layout.popup_item_chooser, null, false);

		popupcontent.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));

		setView(popupcontent);

		itemList = (ListView) popupcontent.findViewById(R.id.popup_item_list);

		categorySpinner = (Spinner) popupcontent.findViewById(R.id.popup_item_category);
		categoryAdapter = new SpinnerSimpleAdapter<ItemType>(this.getContext(), ItemType.values());
		categorySpinner.setAdapter(categoryAdapter);
		categorySpinner.setSelection(categoryAdapter.getPosition(itemTypes.iterator().next()));
		categorySpinner.setOnItemSelectedListener(this);

		searchButton = (ImageButton) popupcontent.findViewById(R.id.popup_search_button);
		searchButton.setOnClickListener(this);
		newButton = (Button) popupcontent.findViewById(R.id.popup_item_new);
		newButton.setOnClickListener(this);

		searchText = (EditText) popupcontent.findViewById(R.id.popup_autosearch);

		searchText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					closeSearch();
				}
			}
		});
		searchText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				filter(null, null, s.toString());
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.AdapterView.OnItemSelectedListener#onItemSelected(android .widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		if (parent.getId() == R.id.popup_item_category) {
			ItemType type = (ItemType) categorySpinner.getItemAtPosition(position);
			filter(Arrays.asList(type), null, null);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.AdapterView.OnItemSelectedListener#onNothingSelected(android .widget.AdapterView)
	 */
	@Override
	public void onNothingSelected(AdapterView<?> arg0) {

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.popup_search_button:
			toggleSearch();
			break;
		case R.id.popup_item_new:
			getContext().startActivity(new Intent(getContext(), ItemEditActivity.class));
			dismiss();
			break;
		}

	}

	public AdapterView.OnItemClickListener getOnItemClickListener() {
		return itemList.getOnItemClickListener();
	}

	public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
		itemList.setOnItemClickListener(onItemClickListener);
	}

	public Item getItem(int position) {
		if (!itemAdapter.getCursor().isClosed())
			return DataManager.getItemByCursor((Cursor) itemAdapter.getItem(position));
		else
			return null;
	}

}