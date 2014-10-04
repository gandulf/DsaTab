package com.dsatab.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.TabInfo;
import com.dsatab.data.adapter.ListItemConfigAdapter;
import com.dsatab.data.adapter.SpinnerSimpleAdapter;
import com.dsatab.util.Util;
import com.dsatab.view.ListSettings;
import com.dsatab.view.ListSettings.ListItem;
import com.dsatab.view.ListSettings.ListItemType;
import com.gandulf.guilib.view.DynamicListViewEx;
import com.nhaarman.listviewanimations.itemmanipulation.dragdrop.TouchViewDraggableManager;

public class TabListableConfigFragment extends Fragment implements View.OnClickListener, OnItemClickListener,
		OnItemSelectedListener, OnCheckedChangeListener {

	private TabInfo info;
	private ListSettings listSettings;
	private int index;

	private Spinner spinner;
	private LinearLayout settingsLayout;

	private CheckBox normal, favorites, unused, modifier;

	private ImageButton addListItem;

	private DynamicListViewEx listItemList;
	private ListItemConfigAdapter listItemAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View root = inflater.inflate(R.layout._edit_tabinfo_list, container, false);

		spinner = (Spinner) root.findViewById(R.id.popup_tab_type);
		spinner.setOnItemSelectedListener(this);
		settingsLayout = (LinearLayout) root.findViewById(R.id.popup_tab_content);

		listItemList = (DynamicListViewEx) root.findViewById(android.R.id.list);

		addListItem = (ImageButton) root.findViewById(R.id.popup_edit_add_list_item);
		addListItem.setOnClickListener(this);

		spinner.setAdapter(new SpinnerSimpleAdapter<String>(getActivity(), BaseFragment.activities));

		listItemAdapter = new ListItemConfigAdapter(getActivity(), DsaTabApplication.getInstance().getHero(),
				new ArrayList<ListSettings.ListItem>());
		ViewGroup checkboxes = (ViewGroup) inflater.inflate(R.layout._edit_tabinfo_list_checkboxes, container);

		normal = (CheckBox) checkboxes.findViewById(R.id.popup_edit_show_normal);
		favorites = (CheckBox) checkboxes.findViewById(R.id.popup_edit_show_favorites);
		unused = (CheckBox) checkboxes.findViewById(R.id.popup_edit_show_unused);
		modifier = (CheckBox) checkboxes.findViewById(R.id.popup_edit_include_modifiers);

		listItemList.addHeaderView(checkboxes);

		listItemList.setAdapter(listItemAdapter);
		listItemList.setOnItemClickListener(this);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			listItemList.enableDragAndDrop();
			listItemList.setDraggableManager(new TouchViewDraggableManager(R.id.drag));
		}

		return root;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.AdapterView.OnItemSelectedListener#onItemSelected(android .widget.AdapterView,
	 * android.view.View, int, long)
	 */
	@Override
	public void onItemSelected(AdapterView<?> adapter, View view, int position, long id) {
		if (info != null && adapter == spinner) {
			Class<? extends BaseFragment> clazz = BaseFragment.activityValues.get(adapter.getSelectedItemPosition());
			info.setActivityClazz(index, clazz);
			updateListSettings();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.CompoundButton.OnCheckedChangeListener#onCheckedChanged (android.widget.CompoundButton,
	 * boolean)
	 */
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (info != null) {
			switch (buttonView.getId()) {
			case R.id.popup_edit_show_favorites:
				listSettings.setShowFavorite(isChecked);
				break;
			case R.id.popup_edit_show_unused:
				listSettings.setShowUnused(isChecked);
				break;
			case R.id.popup_edit_show_normal:
				listSettings.setShowNormal(isChecked);
				break;
			case R.id.popup_edit_include_modifiers:
				listSettings.setIncludeModifiers(isChecked);
				break;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.AdapterView.OnItemSelectedListener#onNothingSelected(android .widget.AdapterView)
	 */
	@Override
	public void onNothingSelected(AdapterView<?> adapter) {
		if (info != null && adapter == spinner) {
			info.setActivityClazz(index, null);
			updateListSettings();
		}
	}

	public void setTabInfo(TabInfo info, int index) {
		this.info = info;
		this.index = index;
		spinner.setEnabled(info != null);
		if (info != null) {
			this.listSettings = info.getListSettings(index);

			Class<? extends BaseFragment> clazz = info.getActivityClazz(index);
			spinner.setSelection(BaseFragment.activityValues.indexOf(clazz));
		} else {
			this.listSettings = null;
		}

		updateListSettings();
	}

	private void updateListSettings() {

		if (info != null && info.getListSettings(index) != null) {
			listSettings = info.getListSettings(index);

			settingsLayout.setVisibility(View.VISIBLE);

			normal.setChecked(listSettings.isShowNormal());
			normal.setOnCheckedChangeListener(this);

			favorites.setChecked(listSettings.isShowFavorite());
			favorites.setOnCheckedChangeListener(this);

			unused.setChecked(listSettings.isShowUnused());
			unused.setOnCheckedChangeListener(this);

			modifier.setChecked(listSettings.isIncludeModifiers());
			modifier.setOnCheckedChangeListener(this);

			if (info.getActivityClazz(index) == ListableFragment.class) {
				listItemList.setVisibility(View.VISIBLE);
				listItemAdapter.clear();
				listItemAdapter.addAll(listSettings.getListItems());
			} else {
				settingsLayout.setVisibility(View.GONE);
				listItemList.setVisibility(View.GONE);
			}
		} else {
			settingsLayout.setVisibility(View.GONE);
			listItemList.setVisibility(View.GONE);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget .AdapterView, android.view.View,
	 * int, long)
	 */
	@Override
	public void onItemClick(final AdapterView<?> parent, View view, int position, long id) {
		switch (parent.getId()) {
		// list of config items for list
		case android.R.id.list:
			final ListItem listItem = (ListItem) parent.getItemAtPosition(position);
			editListItem(listItem);
			break;
		}
	}

	protected void editListItem(final ListItem listItem) {
		if (listItem.getType() == ListItemType.Header) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(R.string.title_insert_title);
			final EditText editText = new EditText(builder.getContext());
			editText.setText(listItem.getName());
			builder.setView(editText);

			DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case DialogInterface.BUTTON_POSITIVE:

						if (TextUtils.isEmpty(editText.getText()))
							listItem.setName(null);
						else
							listItem.setName(editText.getText().toString());

						Util.hideKeyboard(editText);
						listItemAdapter.notifyDataSetChanged();
						break;
					case DialogInterface.BUTTON_NEGATIVE:
						Util.hideKeyboard(editText);
						break;
					}
				}
			};

			builder.setPositiveButton(android.R.string.ok, clickListener);
			builder.setNegativeButton(android.R.string.cancel, clickListener);
			builder.show();
		}
	}

	public void accept() {
		if (listSettings != null && listItemAdapter != null) {
			listSettings.getListItems().clear();
			listSettings.getListItems().addAll(listItemAdapter.getItems());
		}
	}

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
		case R.id.popup_edit_add_list_item:

			List<String> listTypeTitles = new ArrayList<String>();
			for (ListItemType listItemType : ListItemType.values()) {
				listTypeTitles.add(listItemType.title());
			}

			PopupMenu popupMenu = new PopupMenu(getActivity(), v);
			for (int i = 0; i < listTypeTitles.size(); i++) {
				popupMenu.getMenu().add(0, i, i, listTypeTitles.get(i));
			}

			popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

				@Override
				public boolean onMenuItemClick(MenuItem item) {
					ListItem newListItem = new ListItem((ListItemType) ListItemType.values()[item.getItemId()]);
					listItemAdapter.add(newListItem);
					listSettings.getListItems().add(newListItem);

					editListItem(newListItem);
					return true;
				}
			});
			popupMenu.show();
			break;
		}
	}
}
