package com.dsatab.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.NinePatchDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.config.TabInfo;
import com.dsatab.data.adapter.TabInfoDraggableItemAdapter;
import com.dsatab.fragment.TabListableConfigFragment;
import com.dsatab.fragment.dialog.ImageChooserDialog;
import com.dsatab.util.Util;
import com.gandulf.guilib.data.OpenArrayAdapter;
import com.gandulf.guilib.util.DefaultTextWatcher;
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.SwipeDismissItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.ItemShadowDecorator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.selectable.RecyclerViewSelectionManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.h6ah4i.android.widget.advrecyclerview.touchguard.RecyclerViewTouchActionGuardManager;

import java.util.ArrayList;
import java.util.List;

public class TabEditActivity extends BaseActivity implements OnClickListener, OnCheckedChangeListener,
		TabInfoDraggableItemAdapter.EventListener {

	public static final String DATA_INTENT_TAB_INDEX = "tab.tabIndex";

	private ImageView iconView;

	private CheckBox diceslider;
	private EditText editTitle;

	private TabInfo currentInfo = null;

	private RecyclerView recyclerView;
	private TabInfoDraggableItemAdapter mAdapter;

	private RecyclerView.LayoutManager mLayoutManager;
	private RecyclerView.Adapter mWrappedAdapter;
	private RecyclerViewDragDropManager mRecyclerViewDragDropManager;
	private RecyclerViewSwipeManager mRecyclerViewSwipeManager;
	private RecyclerViewSelectionManager mRecyclerViewSelectionManager;
	private RecyclerViewTouchActionGuardManager mRecyclerViewTouchActionGuardManager;

	private TabListableConfigFragment list1;
	private TabListableConfigFragment list2;

	public static void edit(Activity activity, int tabIndex, int requestCode) {
		Intent editTab = new Intent(activity, TabEditActivity.class);
		editTab.putExtra(TabEditActivity.DATA_INTENT_TAB_INDEX, tabIndex);
		editTab.setAction(Intent.ACTION_EDIT);
		activity.startActivityForResult(editTab, requestCode);
	}

	protected void initRecycler(List<TabInfo> tabInfos) {
		recyclerView = (RecyclerView) findViewById(R.id.popup_tab_list);
		recyclerView.setHasFixedSize(false);

		// touch guard manager  (this class is required to suppress scrolling while swipe-dismiss animation is running)
		mRecyclerViewTouchActionGuardManager = new RecyclerViewTouchActionGuardManager();
		mRecyclerViewTouchActionGuardManager.setInterceptVerticalScrollingWhileAnimationRunning(true);
		mRecyclerViewTouchActionGuardManager.setEnabled(true);

		// drag & drop manager
		mRecyclerViewDragDropManager = new RecyclerViewDragDropManager();
		mRecyclerViewDragDropManager.setDraggingItemShadowDrawable(
				(NinePatchDrawable) getResources().getDrawable(R.drawable.material_shadow_z3));

		// swipe manager
		mRecyclerViewSwipeManager = new RecyclerViewSwipeManager();

		//selection manager
		mRecyclerViewSelectionManager = new RecyclerViewSelectionManager();

		mLayoutManager = new LinearLayoutManager(this);
		//adapter
		mAdapter = new TabInfoDraggableItemAdapter(this, tabInfos);
		mAdapter.setEventListener(this);

		mWrappedAdapter = mRecyclerViewDragDropManager.createWrappedAdapter(mAdapter);     // wrap for dragging
		mWrappedAdapter = mRecyclerViewSwipeManager.createWrappedAdapter(mWrappedAdapter);      // wrap for swiping
		mWrappedAdapter = mRecyclerViewSelectionManager.createWrappedAdapter(mWrappedAdapter);  // wrap for selection

		final GeneralItemAnimator animator = new SwipeDismissItemAnimator();

		recyclerView.setLayoutManager(mLayoutManager);
		recyclerView.setAdapter(mWrappedAdapter);  // requires *wrapped* adapter
		recyclerView.setItemAnimator(animator);

		// additional decorations
		//noinspection StatementWithEmptyBody
		if (supportsViewElevation()) {
			// Lollipop or later has native drop shadow feature. ItemShadowDecorator is not required.
		} else {
			recyclerView.addItemDecoration(new ItemShadowDecorator((NinePatchDrawable) getResources().getDrawable(R.drawable.material_shadow_z1)));
		}

		recyclerView.addItemDecoration(new SimpleListDividerDecorator(getResources().getDrawable(R.drawable.abc_list_divider_mtrl_alpha), true));

		// NOTE:
		// The initialization order is very important! This order determines the priority of touch event handling.
		//
		// priority: TouchActionGuard> Selection > Swipe > DragAndDrop
		mRecyclerViewTouchActionGuardManager.attachRecyclerView(recyclerView);
		mRecyclerViewSelectionManager.attachRecyclerView(recyclerView);
		mRecyclerViewSwipeManager.attachRecyclerView(recyclerView);
		mRecyclerViewDragDropManager.attachRecyclerView(recyclerView);



		if (mAdapter.getItemCount() > 0) {
			int index = getIntent().getExtras().getInt(DATA_INTENT_TAB_INDEX, 0);
			if (index >= 0 && index < mAdapter.getItemCount()) {
				mAdapter.setSelected(index);
			}else
				mAdapter.setSelected(0);
		} else {
			selectTabInfo(null);
		}

	}

	@Override
	public void onItemViewClicked(int position, View v) {
		selectTabInfo(mAdapter.get(position));
	}

	@Override
	public void onItemSelected(int position, boolean value) {

	}

	@Override
	public void onItemRemoved(int position) {

	}

	public int getScreenHeight() {
		return findViewById(android.R.id.content).getHeight();
	}

	private boolean supportsViewElevation() {
		return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(DsaTabApplication.getInstance().getCustomTheme());
		applyPreferencesToTheme();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_edit_tabs);

		SlidingPaneLayout slidingPaneLayout = (SlidingPaneLayout) findViewById(R.id.slidepanel);
		slidingPaneLayout.setCoveredFadeColor(0);
		slidingPaneLayout.setSliderFadeColor(0);
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

		iconView = (ImageView) findViewById(R.id.popup_edit_icon);
		iconView.setOnClickListener(this);

		// Inflate a "Done" custom action bar view to serve as the "Up"
		// affordance.
		LayoutInflater inflater = LayoutInflater.from(getSupportActionBar().getThemedContext());
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
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM, ActionBar.DISPLAY_SHOW_CUSTOM
				| ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
		actionBar.setCustomView(customActionBarView);

		setResult(RESULT_OK);

		if (DsaTabApplication.getInstance().getHero() == null) {
			Snackbar.make(slidingPaneLayout, R.string.message_can_only_edit_tabs_if_hero_loaded, Snackbar.LENGTH_SHORT).show();
			setResult(RESULT_CANCELED);
			super.finish();
			return;
		}

		list1 = (TabListableConfigFragment) getFragmentManager().findFragmentByTag("list1");
		list2 = (TabListableConfigFragment) getFragmentManager().findFragmentByTag("list2");

		// recylelist

		List<TabInfo> tabs;
		if (DsaTabApplication.getInstance().getHero() != null
				&& DsaTabApplication.getInstance().getHero().getHeroConfiguration() != null) {
			tabs = new ArrayList<TabInfo>(DsaTabApplication.getInstance().getHero().getHeroConfiguration().getTabs());
		} else {
			tabs = new ArrayList<TabInfo>();
		}

		// noinspection ConstantConditions

		// mRecyclerViewDragDropManager.setDraggingItemShadowDrawable((NinePatchDrawable) getResources().getDrawable(
		// R.drawable.material_shadow_z3));

		// adapter
		initRecycler(tabs);
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
			mAdapter.insert(info);
			mAdapter.setSelected(mAdapter.getItemCount() - 1);
			break;
		case R.id.option_delete:
			// for (Integer pos : mAdapter.getSelectedPositions()) {
			// mAdapter.remove(pos);
			// }
			break;
		case R.id.option_reset:
			mAdapter.clear();
			for (TabInfo tabInfo : DsaTabApplication.getInstance().getHero().getHeroConfiguration()
					.getDefaultTabs(null)) {
				mAdapter.insert(tabInfo);
			}
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

			// TODO
			// int pos = mAdapter.getIndex(info);
			// mAdapter.setSelected(pos, mAdapter.getItemId(pos), true);
			// ultimateRecyclerView.smoothScrollToPosition(pos);

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
		Util.hideKeyboard(recyclerView);

		if (list1 != null)
			list1.accept();
		if (list2 != null)
			list2.accept();
		if (DsaTabApplication.getInstance().getHero() != null
				&& DsaTabApplication.getInstance().getHero().getHeroConfiguration() != null) {
			DsaTabApplication.getInstance().getHero().getHeroConfiguration().setTabs(mAdapter.getItems());
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