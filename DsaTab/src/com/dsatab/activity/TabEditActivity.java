package com.dsatab.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.TabInfo;
import com.dsatab.data.adapter.SpinnerSimpleAdapter;
import com.dsatab.fragment.BaseFragment;
import com.dsatab.util.Util;
import com.dsatab.view.FightFilterSettings;
import com.dsatab.view.ListFilterSettings;
import com.dsatab.view.PortraitChooserDialog;
import com.mobeta.android.dslv.DragSortListView;
import com.mobeta.android.dslv.DragSortListView.DropListener;
import com.mobeta.android.dslv.DragSortListView.RemoveListener;

public class TabEditActivity extends BaseFragmentActivity implements OnItemClickListener, OnItemSelectedListener,
		DropListener, RemoveListener, OnCheckedChangeListener, OnClickListener {

	private Spinner spinner1, spinner2;
	private ImageView iconView;

	private CheckBox diceslider, attribteList;

	private LinearLayout addons[] = new LinearLayout[TabInfo.MAX_TABS_PER_PAGE];

	private TabInfo currentInfo = null;

	private DragSortListView tabsList;
	private TabsAdapter tabsAdapter;

	private List<TabInfo> tabs;

	/** Called when the activity is first created. */
	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(DsaTabApplication.getInstance().getCustomTheme());
		applyPreferencesToTheme();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sheet_edit_tab);

		diceslider = (CheckBox) findViewById(R.id.popup_edit_diceslider);
		diceslider.setOnCheckedChangeListener(this);

		attribteList = (CheckBox) findViewById(R.id.popup_edit_attributelist);
		attribteList.setOnCheckedChangeListener(this);

		addons[0] = (LinearLayout) findViewById(R.id.popup_edit_primary_addon);
		addons[1] = (LinearLayout) findViewById(R.id.popup_edit_secondary_addon);

		spinner1 = (Spinner) findViewById(R.id.popup_edit_primary);

		tabsList = (DragSortListView) findViewById(R.id.popup_tab_list);
		tabsList.setDropListener(this);
		tabsList.setRemoveListener(this);
		tabsList.setOnItemClickListener(this);
		tabsList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
		// tabsList.setItemHeightExpanded(tabsList.getItemHeightNormal() * 2);

		if (DsaTabApplication.getInstance().getHero() != null
				&& DsaTabApplication.getInstance().getHero().getHeroConfiguration() != null) {
			tabs = new ArrayList<TabInfo>(DsaTabApplication.getInstance().getHero().getHeroConfiguration().getTabs());
		} else {
			tabs = new ArrayList<TabInfo>();
		}

		tabsAdapter = new TabsAdapter(this, tabs);
		tabsList.setAdapter(tabsAdapter);

		SpinnerSimpleAdapter<String> adapter = new SpinnerSimpleAdapter<String>(this, BaseFragment.activities);
		spinner1.setAdapter(adapter);
		spinner1.setOnItemSelectedListener(this);

		spinner2 = (Spinner) findViewById(R.id.popup_edit_secondary);
		spinner2.setAdapter(adapter);
		spinner2.setOnItemSelectedListener(this);

		iconView = (ImageView) findViewById(R.id.popup_edit_icon);
		iconView.setOnClickListener(this);

		// Inflate a "Done" custom action bar view to serve as the "Up"
		// affordance.
		LayoutInflater inflater = LayoutInflater.from(this);
		final View customActionBarView = inflater.inflate(R.layout.actionbar_custom_view_done, null);
		customActionBarView.findViewById(R.id.actionbar_done).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		// Show the custom action bar view and hide the normal Home icon and
		// title.
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM, ActionBar.DISPLAY_SHOW_CUSTOM
				| ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
		actionBar.setCustomView(customActionBarView);

		setResult(RESULT_OK);

		if (DsaTabApplication.getInstance().getHero() == null) {
			Toast.makeText(this, "Tabs können erst editiert werden, wenn ein Held geladen wurde.", Toast.LENGTH_SHORT)
					.show();
			setResult(RESULT_CANCELED);
			super.finish();
			return;
		}

		if (tabsAdapter.getCount() > 0) {
			selectTabInfo(tabsAdapter.getItem(0));
		} else {
			selectTabInfo(null);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		com.actionbarsherlock.view.MenuItem item = menu
				.add(Menu.NONE, R.id.option_tab_add, Menu.NONE, "Tab hinzufügen");
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		item.setIcon(R.drawable.ic_menu_add);

		item = menu.add(Menu.NONE, R.id.option_tab_delete, Menu.NONE, "Tab entfernen");
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		item.setIcon(Util.getThemeResourceId(this, R.attr.imgBarDelete));

		item = menu.add(Menu.NONE, R.id.option_tab_reset, Menu.NONE, "Tabs zurücksetzen");
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		item.setIcon(R.drawable.ic_menu_revert);

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.actionbarsherlock.app.SherlockFragmentActivity#onPrepareOptionsMenu (com.actionbarsherlock.view.Menu)
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem item = menu.findItem(R.id.option_tab_delete);
		if (item != null) {
			item.setEnabled(currentInfo != null);
		}

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.option_tab_add:
			TabInfo info = new TabInfo();
			info.setIconUri(Util.getUriForResourceId(R.drawable.icon_fist));
			tabs.add(info);
			tabsAdapter.notifyDataSetChanged();
			selectTabInfo(info);
			break;
		case R.id.option_tab_delete:
			tabs.remove(currentInfo);
			tabsAdapter.notifyDataSetChanged();
			if (tabsList.getCheckedItemPosition() != AdapterView.INVALID_POSITION
					&& tabsList.getCheckedItemPosition() < tabsAdapter.getCount()) {
				selectTabInfo(tabsAdapter.getItem(tabsList.getCheckedItemPosition()));
			} else {
				tabsList.clearChoices();
				selectTabInfo(null);
			}
			break;
		case R.id.option_tab_reset:
			tabs = DsaTabApplication.getInstance().getHero().getHeroConfiguration().getDefaultTabs(tabs);
			tabsAdapter = new TabsAdapter(this, tabs);
			tabsList.setAdapter(tabsAdapter);
			selectTabInfo(null);
			break;
		case android.R.id.home:
			finish();
		}

		return false;
	}

	protected void selectTabInfo(TabInfo info) {
		currentInfo = info;
		if (info != null) {
			Class<? extends BaseFragment> clazz1 = info.getActivityClazz(0);
			spinner1.setSelection(BaseFragment.activityValues.indexOf(clazz1));

			Class<? extends BaseFragment> clazz2 = info.getActivityClazz(1);
			spinner2.setSelection(BaseFragment.activityValues.indexOf(clazz2));

			diceslider.setChecked(info.isDiceSlider());
			attribteList.setChecked(info.isAttributeList());

			iconView.setImageURI(info.getIconUri());

			int pos = tabsAdapter.getPosition(info);
			tabsList.setItemChecked(pos, true);
			tabsList.smoothScrollToPosition(pos);
		}

		spinner1.setEnabled(info != null);
		spinner2.setEnabled(info != null);
		diceslider.setEnabled(info != null);
		attribteList.setEnabled(info != null);
		iconView.setEnabled(info != null);

		updateTabInfoSettings(info);

		invalidateOptionsMenu();

	}

	private void pickIcon() {
		final PortraitChooserDialog pdialog = new PortraitChooserDialog(this);

		List<Integer> itemIcons = DsaTabApplication.getInstance().getConfiguration().getTabIcons();

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
					currentInfo.setIconUri(pdialog.getImageUri());
					iconView.setImageURI(pdialog.getImageUri());
				}
			}
		});
		pdialog.show();

	}

	protected void updateTabInfoSettings(TabInfo info) {
		CheckBox check;
		if (info != null && info.getFilterSettings() != null) {
			for (int i = 0; i < info.getFilterSettings().length; i++) {
				if (info.getFilterSettings()[i] instanceof ListFilterSettings) {
					ListFilterSettings listFilterSettings = (ListFilterSettings) info.getFilterSettings()[i];

					if (addons[i].findViewById(R.id.popup_edit_show_favorites) == null) {
						addons[i].removeAllViews();
						getLayoutInflater().inflate(R.layout.popup_edit_tab_list, addons[i]);
					}

					check = (CheckBox) addons[i].findViewById(R.id.popup_edit_show_normal);
					check.setTag(listFilterSettings);
					check.setOnCheckedChangeListener(this);

					check.setChecked(listFilterSettings.isShowNormal());

					check = (CheckBox) addons[i].findViewById(R.id.popup_edit_show_favorites);
					check.setTag(listFilterSettings);
					check.setOnCheckedChangeListener(this);
					check.setChecked(listFilterSettings.isShowFavorite());

					check = (CheckBox) addons[i].findViewById(R.id.popup_edit_show_unused);
					check.setTag(listFilterSettings);
					check.setOnCheckedChangeListener(this);
					check.setChecked(listFilterSettings.isShowUnused());

					check = (CheckBox) addons[i].findViewById(R.id.popup_edit_include_modifiers);
					check.setTag(listFilterSettings);
					check.setOnCheckedChangeListener(this);
					check.setChecked(listFilterSettings.isIncludeModifiers());

				} else if (info.getFilterSettings()[i] instanceof FightFilterSettings) {
					FightFilterSettings fightFilterSettings = (FightFilterSettings) info.getFilterSettings()[i];

					if (addons[i].findViewById(R.id.popup_edit_fight_show_armor) == null) {
						addons[i].removeAllViews();
						getLayoutInflater().inflate(R.layout.popup_edit_tab_fight, addons[i]);
					}

					check = (CheckBox) addons[i].findViewById(R.id.popup_edit_fight_include_modifier);
					check.setTag(fightFilterSettings);
					check.setOnCheckedChangeListener(this);
					check.setChecked(fightFilterSettings.isIncludeModifiers());

					check = (CheckBox) addons[i].findViewById(R.id.popup_edit_fight_show_armor);
					check.setTag(fightFilterSettings);
					check.setOnCheckedChangeListener(this);
					check.setChecked(fightFilterSettings.isShowArmor());

					check = (CheckBox) addons[i].findViewById(R.id.popup_edit_fight_show_evade);
					check.setTag(fightFilterSettings);
					check.setOnCheckedChangeListener(this);
					check.setChecked(fightFilterSettings.isShowEvade());

					check = (CheckBox) addons[i].findViewById(R.id.popup_edit_fight_show_modifier);
					check.setTag(fightFilterSettings);
					check.setOnCheckedChangeListener(this);
					check.setChecked(fightFilterSettings.isShowModifier());

				} else {
					addons[i].removeAllViews();
				}
			}
		} else {
			for (int i = 0; i < addons.length; i++) {
				addons[i].removeAllViews();
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.commonsware.cwac.tlv.TouchListView.DropListener#drop(int, int)
	 */
	@Override
	public void drop(int from, int to) {
		if (from != to) {
			TabInfo tab = tabs.remove(from);
			tabs.add(to, tab);
			tabsList.moveCheckState(from, to);
			tabsAdapter.notifyDataSetChanged();

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.commonsware.cwac.tlv.TouchListView.RemoveListener#remove(int)
	 */
	@Override
	public void remove(int which) {
		tabs.remove(which);
		tabsAdapter.notifyDataSetChanged();
		if (tabsList.getCheckedItemPosition() != AdapterView.INVALID_POSITION
				&& tabsList.getCheckedItemPosition() < tabsAdapter.getCount()) {
			selectTabInfo(tabsAdapter.getItem(tabsList.getCheckedItemPosition()));
		} else {
			tabsList.clearChoices();
			selectTabInfo(null);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#finish()
	 */
	@Override
	public void finish() {

		Util.hideKeyboard(tabsList);
		if (DsaTabApplication.getInstance().getHero() != null
				&& DsaTabApplication.getInstance().getHero().getHeroConfiguration() != null) {
			DsaTabApplication.getInstance().getHero().getHeroConfiguration().setTabs(tabs);
		}

		setResult(RESULT_OK);
		super.finish();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.CompoundButton.OnCheckedChangeListener#onCheckedChanged (android.widget.CompoundButton, boolean)
	 */
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (currentInfo != null) {
			ListFilterSettings listFilterSettings;
			FightFilterSettings fightFilterSettings;
			switch (buttonView.getId()) {

			case R.id.popup_edit_diceslider:
				currentInfo.setDiceSlider(isChecked);
				break;
			case R.id.popup_edit_attributelist:
				currentInfo.setAttributeList(isChecked);
				break;
			case R.id.popup_edit_show_favorites:
				listFilterSettings = (ListFilterSettings) buttonView.getTag();
				listFilterSettings.setShowFavorite(isChecked);
				break;
			case R.id.popup_edit_show_unused:
				listFilterSettings = (ListFilterSettings) buttonView.getTag();
				listFilterSettings.setShowUnused(isChecked);
				break;
			case R.id.popup_edit_show_normal:
				listFilterSettings = (ListFilterSettings) buttonView.getTag();
				listFilterSettings.setShowNormal(isChecked);
				break;
			case R.id.popup_edit_include_modifiers:
				listFilterSettings = (ListFilterSettings) buttonView.getTag();
				listFilterSettings.setIncludeModifiers(isChecked);
				break;
			case R.id.popup_edit_fight_include_modifier:
				fightFilterSettings = (FightFilterSettings) buttonView.getTag();
				fightFilterSettings.setIncludeModifiers(isChecked);
				break;
			case R.id.popup_edit_fight_show_modifier:
				fightFilterSettings = (FightFilterSettings) buttonView.getTag();
				fightFilterSettings.setShowModifiers(isChecked);
				break;
			case R.id.popup_edit_fight_show_armor:
				fightFilterSettings = (FightFilterSettings) buttonView.getTag();
				fightFilterSettings.setShowArmor(isChecked);
				break;
			case R.id.popup_edit_fight_show_evade:
				fightFilterSettings = (FightFilterSettings) buttonView.getTag();
				fightFilterSettings.setShowEvade(isChecked);
				break;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.AdapterView.OnItemSelectedListener#onItemSelected(android .widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemSelected(AdapterView<?> adapter, View view, int position, long id) {
		if (currentInfo != null) {
			if (adapter == spinner1) {
				Class<? extends BaseFragment> clazz1 = BaseFragment.activityValues.get(spinner1
						.getSelectedItemPosition());
				currentInfo.setActivityClazz(0, clazz1);
				updateTabInfoSettings(currentInfo);
			} else if (adapter == spinner2) {
				Class<? extends BaseFragment> clazz2 = BaseFragment.activityValues.get(spinner2
						.getSelectedItemPosition());
				currentInfo.setActivityClazz(1, clazz2);
				updateTabInfoSettings(currentInfo);
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
		if (currentInfo != null) {
			if (adapter == spinner1) {
				currentInfo.setActivityClazz(0, null);
				updateTabInfoSettings(currentInfo);
			} else if (adapter == spinner2) {
				currentInfo.setActivityClazz(1, null);
				updateTabInfoSettings(currentInfo);
			}
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
		case R.id.popup_edit_icon:
			pickIcon();
			break;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget .AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (parent == tabsList) {
			TabInfo info = tabsAdapter.getItem(position);
			selectTabInfo(info);
		}
	}

	static class TabsAdapter extends ArrayAdapter<TabInfo> {

		private LayoutInflater inflater;

		/**
		 * 
		 */
		public TabsAdapter(Context context, List<TabInfo> objects) {
			super(context, 0, objects);
			inflater = LayoutInflater.from(getContext());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;

			if (convertView instanceof LinearLayout) {
				view = convertView;
			} else {
				view = inflater.inflate(R.layout.item_drag_tab, parent, false);
			}
			ImageView imageButton = (ImageView) view.findViewById(R.id.gen_tab);
			TabInfo info = getItem(position);

			imageButton.setFocusable(false);
			imageButton.setClickable(false);
			imageButton.setImageURI(info.getIconUri());

			Util.applyRowStyle(view, position);

			return view;
		}
	}
}