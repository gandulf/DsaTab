package com.dsatab.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.TabInfo;
import com.dsatab.data.adapter.ListItemConfigAdapter;
import com.dsatab.data.adapter.SpinnerSimpleAdapter;
import com.dsatab.fragment.BaseFragment;
import com.dsatab.fragment.ListableFragment;
import com.dsatab.util.Util;
import com.dsatab.view.ListSettings;
import com.dsatab.view.ListSettings.ListItem;
import com.dsatab.view.ListSettings.ListItemType;
import com.dsatab.view.PortraitChooserDialog;
import com.gandulf.guilib.data.OpenArrayAdapter;
import com.gandulf.guilib.util.DefaultTextWatcher;
import com.haarman.listviewanimations.itemmanipulation.AnimateAdapter;
import com.haarman.listviewanimations.itemmanipulation.OnAnimateCallback;
import com.haarman.listviewanimations.itemmanipulation.OnDismissCallback;
import com.haarman.listviewanimations.itemmanipulation.SwipeDismissAdapter;
import com.haarman.listviewanimations.view.DynamicListView;

public class TabEditActivity extends BaseFragmentActivity implements OnItemClickListener, OnItemSelectedListener,
		OnCheckedChangeListener, OnClickListener, OnAnimateCallback {

	public static final String DATA_INTENT_TAB_INDEX = "tab.tabIndex";

	private Spinner spinner1, spinner2;

	private ImageView iconView;

	private CheckBox diceslider, attribteList;
	private EditText editTitle;

	private LinearLayout addons[] = new LinearLayout[TabInfo.MAX_TABS_PER_PAGE];

	private TabInfo currentInfo = null;

	private DynamicListView tabsList;
	private TabsAdapter tabsAdapter;
	private AnimateAdapter<TabInfo> animateAdapter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(DsaTabApplication.getInstance().getCustomTheme());
		applyPreferencesToTheme();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sheet_edit_tab);

		TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);
		tabHost.setup();
		TabWidget tabWidget = (TabWidget) findViewById(android.R.id.tabs);

		// See more at: http://android-holo-colors.com/faq.html#tabwidget
		// View tabIndicator = LayoutInflater.from(this).inflate(R.layout.tab_indicator_holo, tabHost.getTabWidget(),
		// false);
		// TextView title = (TextView) tabIndicator.findViewById(android.R.id.title); title.setText("Tab 1");

		TabHost.TabSpec spec = tabHost.newTabSpec("General");
		spec.setContent(R.id.tab1);
		spec.setIndicator("Allgemein");
		tabHost.addTab(spec);
		spec = tabHost.newTabSpec("Primary");
		spec.setContent(R.id.tab2);
		spec.setIndicator("Primär");
		tabHost.addTab(spec);
		spec = tabHost.newTabSpec("Secondary");
		spec.setContent(R.id.tab3);
		spec.setIndicator("Sekundär");
		tabHost.addTab(spec);

		diceslider = (CheckBox) findViewById(R.id.popup_edit_diceslider);
		diceslider.setOnCheckedChangeListener(this);

		attribteList = (CheckBox) findViewById(R.id.popup_edit_attributelist);
		attribteList.setOnCheckedChangeListener(this);

		addons[0] = (LinearLayout) findViewById(R.id.popup_edit_primary_addon);
		addons[1] = (LinearLayout) findViewById(R.id.popup_edit_secondary_addon);

		spinner1 = (Spinner) findViewById(R.id.popup_edit_primary);

		editTitle = (EditText) findViewById(R.id.popup_edit_title);
		editTitle.addTextChangedListener(new DefaultTextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (currentInfo != null) {
					currentInfo.setTitle(s.toString());
				}
			}

		});

		tabsList = (DynamicListView) findViewById(R.id.popup_tab_list);
		tabsList.setOnItemClickListener(this);
		tabsList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

		List<TabInfo> tabs;
		if (DsaTabApplication.getInstance().getHero() != null
				&& DsaTabApplication.getInstance().getHero().getHeroConfiguration() != null) {
			tabs = new ArrayList<TabInfo>(DsaTabApplication.getInstance().getHero().getHeroConfiguration().getTabs());
		} else {
			tabs = new ArrayList<TabInfo>();
		}
		tabsAdapter = new TabsAdapter(this, tabs);

		SwipeDismissAdapter swipeAdapter = new SwipeDismissAdapter(tabsAdapter, this);
		swipeAdapter.setAbsListView(tabsList);
		animateAdapter = new AnimateAdapter<TabInfo>(swipeAdapter, this);
		animateAdapter.setAbsListView(tabsList);
		tabsList.setAdapter(animateAdapter);

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

			int index = getIntent().getExtras().getInt(DATA_INTENT_TAB_INDEX, 0);
			if (tabsAdapter.getCount() > index)
				selectTabInfo(tabsAdapter.getItem(index));
			else
				selectTabInfo(tabsAdapter.getItem(0));
		} else {
			selectTabInfo(null);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
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
			animateAdapter.animateShow(tabsAdapter.getCount());
			tabsAdapter.add(info);
			selectTabInfo(info);
			break;
		case R.id.option_tab_delete:
			animateAdapter.animateDismiss(tabsList.getCheckedItemPosition());
			if (tabsList.getCheckedItemPosition() != AdapterView.INVALID_POSITION
					&& tabsList.getCheckedItemPosition() < tabsAdapter.getCount()) {
				selectTabInfo(tabsAdapter.getItem(tabsList.getCheckedItemPosition()));
			} else {
				tabsList.clearChoices();
				selectTabInfo(null);
			}
			break;
		case R.id.option_tab_reset:
			List<Integer> pos = new ArrayList<Integer>(tabsAdapter.getCount());
			for (int i = 0; i < tabsAdapter.getCount(); i++)
				pos.add(i);
			animateAdapter.animateDismiss(pos);
			tabsAdapter.addAll(DsaTabApplication.getInstance().getHero().getHeroConfiguration().getDefaultTabs(null));
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

			int pos = tabsAdapter.indexOf(info);
			tabsList.setItemChecked(pos, true);
			tabsList.smoothScrollToPosition(pos);

			editTitle.setText(info.getTitle());
		} else {
			editTitle.setText(null);
		}

		spinner1.setEnabled(info != null);
		spinner2.setEnabled(info != null);
		diceslider.setEnabled(info != null);
		attribteList.setEnabled(info != null);
		iconView.setEnabled(info != null);

		updateTabInfoSettings(info);

		supportInvalidateOptionsMenu();
	}

	private void pickIcon() {
		final PortraitChooserDialog pdialog = new PortraitChooserDialog(this);

		List<Integer> itemIcons = DsaTabApplication.getInstance().getConfiguration().getTabIcons();

		List<Uri> portraitPaths = new ArrayList<Uri>(itemIcons.size());
		for (Integer resId : itemIcons) {
			portraitPaths.add(Util.getUriForResourceId(resId));
		}
		pdialog.setTitle("Wähle ein Icon...");
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

	protected void updateTabInfoSettings(final TabInfo info) {
		CheckBox check;
		if (info != null && info.getListSettings() != null) {
			for (int i = 0; i < info.getListSettings().length; i++) {

				addons[i].removeAllViews();

				if (info.getListSettings()[i] != null) {
					final ListSettings listSettings = info.getListSettings()[i];

					getLayoutInflater().inflate(R.layout.popup_edit_tab_list, addons[i]);

					check = (CheckBox) addons[i].findViewById(R.id.popup_edit_show_normal);
					check.setTag(listSettings);
					check.setOnCheckedChangeListener(this);

					check.setChecked(listSettings.isShowNormal());

					check = (CheckBox) addons[i].findViewById(R.id.popup_edit_show_favorites);
					check.setTag(listSettings);
					check.setOnCheckedChangeListener(this);
					check.setChecked(listSettings.isShowFavorite());

					check = (CheckBox) addons[i].findViewById(R.id.popup_edit_show_unused);
					check.setTag(listSettings);
					check.setOnCheckedChangeListener(this);
					check.setChecked(listSettings.isShowUnused());

					check = (CheckBox) addons[i].findViewById(R.id.popup_edit_include_modifiers);
					check.setTag(listSettings);
					check.setOnCheckedChangeListener(this);
					check.setChecked(listSettings.isIncludeModifiers());

					View listTitle = addons[i].findViewById(R.id.popup_edit_list_title);

					final DynamicListView list = (DynamicListView) addons[i].findViewById(android.R.id.list);

					if (info.getActivityClazz(i) == ListableFragment.class) {
						listTitle.setVisibility(View.VISIBLE);
						list.setVisibility(View.VISIBLE);
						final ListItemConfigAdapter listAdapter = new ListItemConfigAdapter(this, DsaTabApplication
								.getInstance().getHero(), listSettings.getListItems());

						SwipeDismissAdapter swipeAdapter = new SwipeDismissAdapter(listAdapter,
								new OnDismissCallback() {
									@Override
									public void onDismiss(AbsListView list, int[] reverseSortedPositions) {
										for (int position : reverseSortedPositions) {
											listAdapter.remove(position);
											listSettings.getListItems().remove(position);
										}
									}
								});

						swipeAdapter.setAbsListView(list);
						final AnimateAdapter<ListItem> animateAdapter = new AnimateAdapter<ListSettings.ListItem>(
								swipeAdapter, this);
						animateAdapter.setAbsListView(list);
						list.setDivider(null);
						list.setAdapter(animateAdapter);
						list.setOnItemClickListener(this);

						final Spinner listItemType = (Spinner) addons[i].findViewById(R.id.popup_edit_list_type);
						SpinnerSimpleAdapter<ListItemType> typeAdapter = new SpinnerSimpleAdapter<ListSettings.ListItemType>(
								this, ListItemType.values());
						listItemType.setAdapter(typeAdapter);
						ImageButton listItemAdd = (ImageButton) addons[i].findViewById(R.id.popup_edit_list_add);
						listItemAdd.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								ListItem newListItem = new ListItem((ListItemType) listItemType.getSelectedItem());
								animateAdapter.animateShow(listAdapter.getCount());
								listAdapter.add(newListItem);
								listSettings.getListItems().add(newListItem);
							}
						});
					} else {
						listTitle.setVisibility(View.GONE);
						list.setVisibility(View.GONE);
					}
				}
			}
		} else {
			for (int i = 0; i < addons.length; i++) {
				addons[i].removeAllViews();
			}
		}

	}

	@Override
	public void onDismiss(AbsListView list, int[] reverseSortedPositions) {
		if (list == tabsList) {
			for (int position : reverseSortedPositions) {
				if (tabsAdapter.getCount() > position) {
					tabsAdapter.remove(position);
				}
			}

			if (tabsList.getCheckedItemPosition() != AdapterView.INVALID_POSITION
					&& tabsList.getCheckedItemPosition() < tabsAdapter.getCount()) {
				selectTabInfo(tabsAdapter.getItem(tabsList.getCheckedItemPosition()));
			} else {
				tabsList.clearChoices();
				selectTabInfo(null);
			}
		}

	}

	@Override
	public void onShow(AbsListView list, int[] pos) {
		if (pos != null && pos.length > 0) {
			list.smoothScrollToPosition(pos[0]);
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
			DsaTabApplication.getInstance().getHero().getHeroConfiguration().setTabs(tabsAdapter.getItems());
		}

		setResult(RESULT_OK);
		super.finish();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.CompoundButton.OnCheckedChangeListener#onCheckedChanged (android.widget.CompoundButton,
	 * boolean)
	 */
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (currentInfo != null) {
			ListSettings listFilterSettings;
			switch (buttonView.getId()) {

			case R.id.popup_edit_diceslider:
				currentInfo.setDiceSlider(isChecked);
				break;
			case R.id.popup_edit_attributelist:
				currentInfo.setAttributeList(isChecked);
				break;
			case R.id.popup_edit_show_favorites:
				listFilterSettings = (ListSettings) buttonView.getTag();
				listFilterSettings.setShowFavorite(isChecked);
				break;
			case R.id.popup_edit_show_unused:
				listFilterSettings = (ListSettings) buttonView.getTag();
				listFilterSettings.setShowUnused(isChecked);
				break;
			case R.id.popup_edit_show_normal:
				listFilterSettings = (ListSettings) buttonView.getTag();
				listFilterSettings.setShowNormal(isChecked);
				break;
			case R.id.popup_edit_include_modifiers:
				listFilterSettings = (ListSettings) buttonView.getTag();
				listFilterSettings.setIncludeModifiers(isChecked);
				break;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.AdapterView.OnItemSelectedListener#onItemSelected(android .widget.AdapterView,
	 * android.view.View, int, long)
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
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget .AdapterView, android.view.View,
	 * int, long)
	 */
	@Override
	public void onItemClick(final AdapterView<?> parent, View view, int position, long id) {
		switch (parent.getId()) {
		// list of tabs on the left
		case R.id.popup_tab_list:
			TabInfo info = tabsAdapter.getItem(position);
			selectTabInfo(info);
			break;
		// list of config items for list
		case android.R.id.list:
			final ListItem listItem = (ListItem) parent.getItemAtPosition(position);
			if (listItem.getType() == ListItemType.Header) {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Titel eingeben");
				final EditText editText = new EditText(this);
				editText.setText(listItem.getName());
				builder.setView(editText);

				DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case DialogInterface.BUTTON_POSITIVE:
							listItem.setName(editText.getText().toString());
							Util.hideKeyboard(editText);
							Util.notifyDatasetChanged(parent);
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
			break;
		}
	}

	static class TabsAdapter extends OpenArrayAdapter<TabInfo> {

		/**
		 * 
		 */
		public TabsAdapter(Context context, List<TabInfo> objects) {
			super(context, 0, objects);

		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public long getItemId(int position) {
			return getItem(position).hashCode();
		}

		public List<TabInfo> getItems() {
			return mObjects;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.item_drag_tab, parent, false);
			}
			ImageView imageButton = (ImageView) convertView.findViewById(R.id.gen_tab);
			TabInfo info = getItem(position);

			imageButton.setFocusable(false);
			imageButton.setClickable(false);
			imageButton.setImageURI(info.getIconUri());

			Util.applyRowStyle(convertView, position);

			return convertView;
		}
	}
}