package com.dsatab.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SlidingPaneLayout;
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
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.config.TabInfo;
import com.dsatab.fragment.TabListableConfigFragment;
import com.dsatab.fragment.dialog.ImageChooserDialog;
import com.dsatab.util.Util;
import com.gandulf.guilib.data.OpenArrayAdapter;
import com.gandulf.guilib.util.DefaultTextWatcher;
import com.gandulf.guilib.view.DynamicListViewEx;
import com.nhaarman.listviewanimations.itemmanipulation.dragdrop.TouchViewDraggableManager;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.SwipeDismissAdapter;

public class TabEditActivity extends BaseActivity implements OnItemClickListener, OnClickListener,
		OnCheckedChangeListener, OnDismissCallback {

	public static final String DATA_INTENT_TAB_INDEX = "tab.tabIndex";

	private ImageView iconView;

	private CheckBox diceslider;
	private EditText editTitle;

	private TabInfo currentInfo = null;

	private DynamicListViewEx tabsList;
	private TabsAdapter tabsAdapter;

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
		setTheme(DsaTabApplication.getInstance().getCustomTheme(false));
		applyPreferencesToTheme();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sheet_edit_tab);

		SlidingPaneLayout slidingPaneLayout = (SlidingPaneLayout) findViewById(R.id.slidepanel);
		// slidingPaneLayout.setParallaxDistance(100);
		slidingPaneLayout.openPane();

		TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);
		tabHost.setup();
		TabWidget tabWidget = (TabWidget) findViewById(android.R.id.tabs);

		// See more at: http://android-holo-colors.com/faq.html#tabwidget
		// View tabIndicator = LayoutInflater.from(this).inflate(R.layout.tab_indicator_holo, tabHost.getTabWidget(),
		// false);
		// TextView title = (TextView) tabIndicator.findViewById(android.R.id.title); title.setText("Tab 1");

		TabHost.TabSpec spec = tabHost.newTabSpec("Primary");
		spec.setContent(R.id.tab2);
		spec.setIndicator(getString(R.string.tab_primary));
		tabHost.addTab(spec);
		spec = tabHost.newTabSpec("Secondary");
		spec.setContent(R.id.tab3);
		spec.setIndicator(getString(R.string.tab_secondary));
		tabHost.addTab(spec);

		diceslider = (CheckBox) findViewById(R.id.popup_edit_diceslider);
		diceslider.setOnCheckedChangeListener(this);

		editTitle = (EditText) findViewById(R.id.popup_edit_title);
		editTitle.addTextChangedListener(new DefaultTextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (currentInfo != null) {
					currentInfo.setTitle(s.toString());
				}
			}

		});

		tabsList = (DynamicListViewEx) findViewById(R.id.popup_tab_list);
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

		tabsList.setAdapter(tabsAdapter);
		tabsList.enableSwipeToDismiss(this);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			tabsList.enableDragAndDrop();
			tabsList.setDraggableManager(new TouchViewDraggableManager(R.id.drag));
		}

		iconView = (ImageView) findViewById(R.id.popup_edit_icon);
		iconView.setOnClickListener(this);

		// Inflate a "Done" custom action bar view to serve as the "Up"
		// affordance.
		LayoutInflater inflater = LayoutInflater.from(getActionBar().getThemedContext());
		final View customActionBarView = inflater.inflate(R.layout.actionbar_custom_view_done_discard_left, null);
		customActionBarView.findViewById(R.id.actionbar_done).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				accept();
			}
		});

		customActionBarView.findViewById(R.id.actionbar_discard).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				cancel();
			}
		});

		// Show the custom action bar view and hide the normal Home icon and
		// title.
		final ActionBar actionBar = getActionBar();
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

		list1 = (TabListableConfigFragment) getFragmentManager().findFragmentByTag("list1");
		list2 = (TabListableConfigFragment) getFragmentManager().findFragmentByTag("list2");

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
		getMenuInflater().inflate(R.menu.menuitem_add, menu);
		getMenuInflater().inflate(R.menu.menuitem_delete, menu);
		getMenuInflater().inflate(R.menu.menuitem_reset, menu);
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
		MenuItem item = menu.findItem(R.id.option_delete);
		if (item != null) {
			item.setEnabled(currentInfo != null);
		}

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.option_add:
			TabInfo info = new TabInfo();
			info.setIconUri(Util.getUriForResourceId(R.drawable.dsa_armor_fist));
			tabsAdapter.add(info);
			selectTabInfo(info);
			break;
		case R.id.option_delete:
			tabsList.dismiss(tabsList.getCheckedItemPosition());
			break;
		case R.id.option_reset:
			List<Integer> pos = new ArrayList<Integer>(tabsAdapter.getCount());
			for (int i = 0; i < tabsAdapter.getCount(); i++) {
				pos.add(i);
			}
			tabsAdapter.setNotifyOnChange(false);
			tabsAdapter.clear();
			tabsAdapter.addAll(DsaTabApplication.getInstance().getHero().getHeroConfiguration().getDefaultTabs(null));
			tabsAdapter.notifyDataSetChanged();

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
		iconView.setEnabled(info != null);

		list1.setTabInfo(info, 0);
		list2.setTabInfo(info, 1);

		invalidateOptionsMenu();
	}

	private void pickIcon() {

		ImageChooserDialog.pickIcons(null, getFragmentManager(), new ImageChooserDialog.OnImageSelectedListener() {

			@Override
			public void onImageSelected(Uri imageUri) {
				currentInfo.setIconUri(imageUri);
				iconView.setImageURI(imageUri);
			}
		}, 0);

	}

	@Override
	public void onDismiss(ViewGroup list, int[] reverseSortedPositions) {
		if (list == tabsList) {
			removeTabInfo(reverseSortedPositions);
		}

	}

	private void removeTabInfo(int... reverseSortedPositions) {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.FragmentActivity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		cancel();
	}

	protected void accept() {
		Util.hideKeyboard(tabsList);

		if (list1 != null)
			list1.accept();
		if (list2 != null)
			list2.accept();
		if (DsaTabApplication.getInstance().getHero() != null
				&& DsaTabApplication.getInstance().getHero().getHeroConfiguration() != null) {
			DsaTabApplication.getInstance().getHero().getHeroConfiguration().setTabs(tabsAdapter.getItems());
		}
		setResult(RESULT_OK);

		finish();
	}

	protected void cancel() {
		setResult(RESULT_CANCELED);
		finish();
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
			if (position >= 0 && position < getCount()) {
				return getItem(position).hashCode();
			} else {
				return AdapterView.INVALID_ROW_ID;
			}
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