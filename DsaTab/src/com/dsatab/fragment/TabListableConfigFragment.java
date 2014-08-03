package com.dsatab.fragment;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import com.haarman.listviewanimations.itemmanipulation.AnimateAdapter;
import com.haarman.listviewanimations.itemmanipulation.OnAnimateCallback;
import com.haarman.listviewanimations.itemmanipulation.OnDismissCallback;
import com.haarman.listviewanimations.itemmanipulation.SwipeDismissAdapter;
import com.haarman.listviewanimations.view.DynamicListView;

public class TabListableConfigFragment extends Fragment implements View.OnClickListener, OnItemClickListener,
		OnItemSelectedListener, OnCheckedChangeListener, OnAnimateCallback {

	private TabInfo info;
	private ListSettings listSettings;
	private int index;

	private Spinner spinner;
	private LinearLayout settingsLayout;

	private CheckBox normal, favorites, unused, modifier;

	private ImageButton addListItem;

	private DynamicListView listItemList;
	private ListItemConfigAdapter listItemAdapter;
	private AnimateAdapter<ListItem> animateListItemAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View root = inflater.inflate(R.layout._edit_tabinfo_list, container, false);

		spinner = (Spinner) root.findViewById(R.id.popup_tab_type);
		spinner.setOnItemSelectedListener(this);
		settingsLayout = (LinearLayout) root.findViewById(R.id.popup_tab_content);

		normal = (CheckBox) root.findViewById(R.id.popup_edit_show_normal);
		favorites = (CheckBox) root.findViewById(R.id.popup_edit_show_favorites);
		unused = (CheckBox) root.findViewById(R.id.popup_edit_show_unused);
		modifier = (CheckBox) root.findViewById(R.id.popup_edit_include_modifiers);

		listItemList = (DynamicListView) root.findViewById(android.R.id.list);
		listItemList.setDivider(null);

		addListItem = (ImageButton) root.findViewById(R.id.popup_edit_add_list_item);
		addListItem.setOnClickListener(this);

		spinner.setAdapter(new SpinnerSimpleAdapter<String>(getActivity(), BaseFragment.activities));

		listItemAdapter = new ListItemConfigAdapter(getActivity(), DsaTabApplication.getInstance().getHero(),
				new ArrayList<ListSettings.ListItem>());

		SwipeDismissAdapter swipeAdapter = new SwipeDismissAdapter(listItemAdapter, new OnDismissCallback() {
			@Override
			public void onDismiss(AbsListView list, int[] reverseSortedPositions) {
				for (int position : reverseSortedPositions) {
					listItemAdapter.remove(position);
					listSettings.getListItems().remove(position);
				}
			}
		});

		swipeAdapter.setAbsListView(listItemList);
		animateListItemAdapter = new AnimateAdapter<ListSettings.ListItem>(swipeAdapter, this);
		animateListItemAdapter.setAbsListView(listItemList);

		listItemList.setAdapter(animateListItemAdapter);
		listItemList.setOnItemClickListener(this);

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

	@Override
	public void onShow(AbsListView list, int[] pos) {
		if (pos != null && pos.length > 0) {
			list.smoothScrollToPosition(pos[0]);
		}
	}

	@Override
	public void onDismiss(AbsListView arg0, int[] arg1) {

	}

	protected void editListItem(final ListItem listItem) {
		if (listItem.getType() == ListItemType.Header) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(R.string.title_insert_title);
			final EditText editText = new EditText(getActivity());
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.popup_edit_add_list_item:

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

			ArrayAdapter<ListItemType> typeAdapter = new ArrayAdapter<ListItemType>(getActivity(),
					android.R.layout.simple_list_item_1, ListItemType.values());
			builder.setTitle("Typ auswählen");
			builder.setAdapter(typeAdapter, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					ListItem newListItem = new ListItem((ListItemType) ListItemType.values()[which]);
					animateListItemAdapter.animateShow(listItemAdapter.getCount());
					listItemAdapter.add(newListItem);
					listSettings.getListItems().add(newListItem);

					editListItem(newListItem);
				}
			});

			builder.show().setCanceledOnTouchOutside(true);
			break;
		}
	}
}
