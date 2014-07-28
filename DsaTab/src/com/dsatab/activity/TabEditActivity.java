package com.dsatab.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.TabInfo;
import com.dsatab.fragment.TabListableConfigFragment;
import com.dsatab.util.Util;
import com.dsatab.view.ImageChooserDialog;
import com.gandulf.guilib.data.OpenArrayAdapter;
import com.gandulf.guilib.util.DefaultTextWatcher;
import com.haarman.listviewanimations.itemmanipulation.AnimateAdapter;
import com.haarman.listviewanimations.itemmanipulation.OnAnimateCallback;
import com.haarman.listviewanimations.itemmanipulation.SwipeDismissAdapter;
import com.haarman.listviewanimations.view.DynamicListView;

public class TabEditActivity extends BaseFragmentActivity implements OnItemClickListener, OnClickListener,
		OnCheckedChangeListener, OnAnimateCallback {

	public static final String DATA_INTENT_TAB_INDEX = "tab.tabIndex";

	private ImageView iconView;

	private CheckBox diceslider, attribteList;
	private EditText editTitle;

	private TabInfo currentInfo = null;

	private DynamicListView tabsList;
	private TabsAdapter tabsAdapter;
	private AnimateAdapter<TabInfo> animateAdapter;

	private TabListableConfigFragment list1;
	private TabListableConfigFragment list2;

	public static void edit(Activity activity, int tabIndex, int requestCode) {
		Intent editTab = new Intent(activity, TabEditActivity.class);
		editTab.putExtra(TabEditActivity.DATA_INTENT_TAB_INDEX, tabIndex);
		editTab.setAction(Intent.ACTION_EDIT);
		activity.startActivityForResult(editTab, requestCode);
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(DsaTabApplication.getInstance().getCustomTheme());
		applyPreferencesToTheme();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sheet_edit_tab);

		SlidingPaneLayout slidingPaneLayout = (SlidingPaneLayout) findViewById(R.id.slidepanel);
		slidingPaneLayout.setParallaxDistance(100);
		slidingPaneLayout.openPane();

		TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);
		tabHost.setup();
		TabWidget tabWidget = (TabWidget) findViewById(android.R.id.tabs);

		// See more at: http://android-holo-colors.com/faq.html#tabwidget
		// View tabIndicator = LayoutInflater.from(this).inflate(R.layout.tab_indicator_holo, tabHost.getTabWidget(),
		// false);
		// TextView title = (TextView) tabIndicator.findViewById(android.R.id.title); title.setText("Tab 1");

		TabHost.TabSpec spec = tabHost.newTabSpec("General");
		spec.setContent(R.id.tab1);
		spec.setIndicator(getString(R.string.tab_general));
		tabHost.addTab(spec);
		spec = tabHost.newTabSpec("Primary");
		spec.setContent(R.id.tab2);
		spec.setIndicator(getString(R.string.tab_primary));
		tabHost.addTab(spec);
		spec = tabHost.newTabSpec("Secondary");
		spec.setContent(R.id.tab3);
		spec.setIndicator(getString(R.string.tab_secondary));
		tabHost.addTab(spec);

		diceslider = (CheckBox) findViewById(R.id.popup_edit_diceslider);
		diceslider.setOnCheckedChangeListener(this);

		attribteList = (CheckBox) findViewById(R.id.popup_edit_attributelist);
		attribteList.setOnCheckedChangeListener(this);

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

		iconView = (ImageView) findViewById(R.id.popup_edit_icon);
		iconView.setOnClickListener(this);

		// Inflate a "Done" custom action bar view to serve as the "Up"
		// affordance.
		LayoutInflater inflater = LayoutInflater.from(getSupportActionBar().getThemedContext());
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
			Toast.makeText(this, R.string.message_can_only_edit_tabs_if_hero_loaded, Toast.LENGTH_SHORT).show();
			setResult(RESULT_CANCELED);
			super.finish();
			return;
		}

		list1 = (TabListableConfigFragment) getSupportFragmentManager().findFragmentByTag("list1");
		list2 = (TabListableConfigFragment) getSupportFragmentManager().findFragmentByTag("list2");

		if (tabsAdapter.getCount() > 0) {

			int index = getIntent().getExtras().getInt(DATA_INTENT_TAB_INDEX, 0);
			if (index >= 0 && index < tabsAdapter.getCount())
				selectTabInfo(tabsAdapter.getItem(index));
			else
				selectTabInfo(tabsAdapter.getItem(0));
		} else {
			selectTabInfo(null);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem item = menu.add(Menu.NONE, R.id.option_tab_add, Menu.NONE, R.string.option_create_tab);
		MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM
				| MenuItemCompat.SHOW_AS_ACTION_WITH_TEXT);
		item.setIcon(R.drawable.ic_menu_add);

		item = menu.add(Menu.NONE, R.id.option_tab_delete, Menu.NONE, R.string.label_delete);
		MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM
				| MenuItemCompat.SHOW_AS_ACTION_WITH_TEXT);
		item.setIcon(Util.getThemeResourceId(this, R.attr.imgBarDelete));

		item = menu.add(Menu.NONE, R.id.option_tab_reset, Menu.NONE, R.string.option_reset_tabs);
		MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM
				| MenuItemCompat.SHOW_AS_ACTION_WITH_TEXT);
		item.setIcon(R.drawable.ic_menu_revert);

		return true;
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
			switch (buttonView.getId()) {
			case R.id.popup_edit_diceslider:
				currentInfo.setDiceSlider(isChecked);
				break;
			case R.id.popup_edit_attributelist:
				currentInfo.setAttributeList(isChecked);
				break;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.actionbarsherlock.app.SherlockFragmentActivity#onPrepareOptionsMenu (Menu)
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
			info.setIconUri(Util.getUriForResourceId(R.drawable.dsa_armor_fist));
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

		editTitle.setEnabled(info != null);
		diceslider.setEnabled(info != null);
		attribteList.setEnabled(info != null);
		iconView.setEnabled(info != null);

		list1.setTabInfo(info, 0);
		list2.setTabInfo(info, 1);

		supportInvalidateOptionsMenu();
	}

	private void pickIcon() {
		final ImageChooserDialog pdialog = new ImageChooserDialog(this);

		pdialog.setTitle(R.string.title_choose_icon);
		pdialog.setImageIds(DsaTabApplication.getInstance().getConfiguration().getDsaIcons());
		pdialog.setGridColumnWidth(getResources().getDimensionPixelSize(R.dimen.icon_button_size));
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
			ImageView imageView = (ImageView) convertView.findViewById(R.id.gen_tab);
			TextView text1 = (TextView) convertView.findViewById(android.R.id.text1);
			TabInfo info = getItem(position);

			text1.setText(info.getTitle());
			imageView.setImageURI(info.getIconUri());

			Util.applyRowStyle(convertView, position);

			return convertView;
		}
	}
}