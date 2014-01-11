package com.dsatab.activity;

import java.io.File;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.TabInfo;
import com.dsatab.activity.menu.TabListener;
import com.dsatab.data.Hero;
import com.dsatab.data.HeroConfiguration;
import com.dsatab.data.HeroLoaderTask;
import com.dsatab.data.Probe;
import com.dsatab.data.adapter.TabAdapter;
import com.dsatab.data.adapter.TabPagerAdapter;
import com.dsatab.fragment.AttributeListFragment;
import com.dsatab.fragment.BaseFragment;
import com.dsatab.util.Debug;
import com.dsatab.util.Util;
import com.dsatab.view.ChangeLogDialog;
import com.dsatab.view.DiceSlider;
import com.dsatab.view.MyViewPager;
import com.dsatab.view.listener.ShakeListener;

public class DsaTabActivity extends BaseFragmentActivity implements OnClickListener, OnPageChangeListener,
		LoaderManager.LoaderCallbacks<Hero>, OnSharedPreferenceChangeListener {

	public static boolean newsShown = false;

	protected static final String INTENT_TAB_INFO = "tabInfo";

	public static final String PREF_LAST_HERO = "LAST_HERO";

	private static final String KEY_HERO_PATH = "HERO_PATH";

	public static final int ACTION_PREFERENCES = 1000;
	public static final int ACTION_ADD_MODIFICATOR = 1003;
	protected static final int ACTION_CHOOSE_HERO = 1004;
	public static final int ACTION_EDIT_MODIFICATOR = 1005;
	public static final int ACTION_EDIT_TABS = 1006;

	private static final String KEY_TAB_INFO = "tabInfo";

	protected SharedPreferences preferences;

	private DiceSlider diceSlider;

	private AttributeListFragment attributeFragment;

	private ShakeListener mShaker;

	private MyViewPager viewPager;

	private TabInfo tabInfo;

	private View loadingView;

	private TabPagerAdapter viewPagerAdapter;

	public Hero getHero() {
		return DsaTabApplication.getInstance().getHero();
	}

	public final void loadHero(String heroPath) {

		loadingView.setVisibility(View.VISIBLE);
		loadingView.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
		viewPager.setEnabled(false);

		Bundle args = new Bundle();
		args.putString(KEY_HERO_PATH, heroPath);
		getSupportLoaderManager().restartLoader(0, args, this);
	}

	@Override
	public boolean onSearchRequested() {
		Bundle appData = new Bundle();
		appData.putParcelable(SearchableActivity.INTENT_TAB_INFO, tabInfo);
		startSearch(null, false, appData, false);
		return true;
	}

	public void showDiceSlider() {
		if (diceSlider != null) {
			diceSlider.setSliderVisible(true);
		}
	}

	public void hideDiceSlider() {
		if (diceSlider != null) {
			if (diceSlider.isOpened()) {
				diceSlider.close();
			}
			diceSlider.setSliderVisible(false);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.view.ViewPager.OnPageChangeListener#onPageScrolled (int, float, int)
	 */
	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.view.ViewPager.OnPageChangeListener# onPageScrollStateChanged(int)
	 */
	@Override
	public void onPageScrollStateChanged(int state) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.view.ViewPager.OnPageChangeListener#onPageSelected (int)
	 */
	@Override
	public void onPageSelected(int position) {

		if (getSupportActionBar().getSelectedNavigationIndex() != position) {
			getSupportActionBar().setSelectedNavigationItem(position);
			supportInvalidateOptionsMenu();
		}
		if (getHeroConfiguration() != null) {
			tabInfo = getHeroConfiguration().getTab(position);
			updatePage(tabInfo);
		}

		Fragment fragment = viewPagerAdapter.getFragment(position);
		if (fragment != null) {
			fragment.setUserVisibleHint(true);
		}
	}

	private void updatePage(TabInfo tabInfo) {
		if (tabInfo.isDiceSlider())
			showDiceSlider();
		else
			hideDiceSlider();

		if (attributeFragment != null && attributeFragment.isAdded() && attributeFragment.getView() != null) {
			if (tabInfo.isAttributeList()) {
				if (attributeFragment.getView().getVisibility() == View.GONE) {
					attributeFragment.getView().setVisibility(View.VISIBLE);

					Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_top);
					attributeFragment.getView().startAnimation(animation);
				}

			} else {
				if (attributeFragment.getView().getVisibility() == View.VISIBLE) {
					Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_out_top);
					attributeFragment.getView().startAnimation(animation);
					attributeFragment.getView().setVisibility(View.GONE);
				}
			}
		}
	}

	private void loadHero() {
		// in case of orientation change the hero is already loaded, just recreate the menu etc...
		if (DsaTabApplication.getInstance().getHero() != null) {
			onHeroLoaded(DsaTabApplication.getInstance().getHero());
		} else {
			String heroPath = preferences.getString(PREF_LAST_HERO, null);
			if (heroPath != null && new File(heroPath).exists()) {
				loadHero(heroPath);
			} else {
				showHeroChooser();
			}
		}
	}

	@Override
	public Loader<Hero> onCreateLoader(int id, Bundle args) {
		// Debug.verbose("Creating loader for " + args.getString(KEY_HERO_PATH));
		return new HeroLoaderTask(this, args.getString(KEY_HERO_PATH));
	}

	@Override
	public void onLoadFinished(Loader<Hero> loader, Hero hero) {
		loadingView.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
		loadingView.setVisibility(View.GONE);
		viewPager.setEnabled(true);

		// Swap the new cursor in. (The framework will take care of closing the
		// old cursor once we return.)
		if (loader instanceof HeroLoaderTask) {
			HeroLoaderTask heroLoader = (HeroLoaderTask) loader;

			if (heroLoader.getException() != null) {
				Toast.makeText(this, heroLoader.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
			}
		}
		DsaTabApplication.getInstance().setHero(hero);

		if (hero != null) {
			Toast.makeText(this, getString(R.string.hero_loaded, hero.getName()), Toast.LENGTH_SHORT).show();
		}
		onHeroLoaded(hero);
	}

	@Override
	public void onLoaderReset(Loader<Hero> loader) {
		// This is called when the last Cursor provided to onLoadFinished()
		// above is about to be closed. We need to make sure we are no
		// longer using it.
		// mAdapter.swapCursor(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == ACTION_CHOOSE_HERO) {

			if (resultCode == RESULT_OK) {
				String heroPath = data.getStringExtra(HeroChooserActivity.INTENT_NAME_HERO_PATH);
				Debug.verbose("HeroChooserActivity returned with path:" + heroPath);
				loadHero(heroPath);
			} else if (resultCode == RESULT_CANCELED) {
				if (getHero() == null) {
					finish();
				}
			}
		} else if (requestCode == ACTION_PREFERENCES) {

			SharedPreferences preferences = DsaTabApplication.getPreferences();

			if (preferences.getBoolean(DsaTabPreferenceActivity.KEY_PROBE_SHAKE_ROLL_DICE, false)) {
				registerShakeDice();
			} else {
				unregisterShakeDice();
			}
		} else if (requestCode == ACTION_EDIT_TABS) {
			if (resultCode == Activity.RESULT_OK) {
				setupTabs();
			}
		}

		// notify other listeners (fragments, heroes)
		if (viewPagerAdapter != null) {
			Fragment fragment = viewPagerAdapter.getFragment(viewPager.getCurrentItem());
			if (fragment != null) {
				fragment.onActivityResult(requestCode, resultCode, data);
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(DsaTabApplication.getInstance().getCustomTheme());
		applyPreferencesToTheme();
		super.onCreate(savedInstanceState);

		preferences = DsaTabApplication.getPreferences();

		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setDisplayShowHomeEnabled(false);

		DsaTabApplication.getPreferences().registerOnSharedPreferenceChangeListener(this);

		// start tracing to "/sdcard/calc.trace"
		// android.os.Debug.startMethodTracing("dsatab");

		setContentView(R.layout.main_tab_view);

		viewPager = (MyViewPager) findViewById(R.id.viewpager);
		loadingView = findViewById(R.id.loading);

		String orientation = preferences.getString(DsaTabPreferenceActivity.KEY_SCREEN_ORIENTATION,
				DsaTabPreferenceActivity.DEFAULT_SCREEN_ORIENTATION);

		Configuration configuration = getResources().getConfiguration();

		if (savedInstanceState != null) {
			tabInfo = savedInstanceState.getParcelable(KEY_TAB_INFO);
		}

		if (tabInfo == null && getIntent() != null) {
			tabInfo = getIntent().getParcelableExtra(INTENT_TAB_INFO);
		}

		diceSlider = (DiceSlider) findViewById(R.id.SlidingDrawer);
		diceSlider.setSlideHandleButton(findViewById(R.id.slideHandleButton));

		attributeFragment = (AttributeListFragment) getSupportFragmentManager().findFragmentByTag(
				AttributeListFragment.TAG);

		// Debug.verbose("onCreate Orientation =" + configuration.orientation);
		if (DsaTabPreferenceActivity.SCREEN_ORIENTATION_LANDSCAPE.equals(orientation)
				&& configuration.orientation != Configuration.ORIENTATION_LANDSCAPE) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			return;
		} else if (DsaTabPreferenceActivity.SCREEN_ORIENTATION_PORTRAIT.equals(orientation)
				&& configuration.orientation != Configuration.ORIENTATION_PORTRAIT) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			return;
		} else if (DsaTabPreferenceActivity.SCREEN_ORIENTATION_AUTO.equals(orientation)
				&& getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_SENSOR
				&& getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
			return;
		}

		// TODO make sure the viewpager fragments are already initialized here!!!
		loadHero();

		showNewsInfoPopup();
	}

	/**
	 * 
	 */
	private void setupTabs() {
		if (viewPagerAdapter == null) {
			viewPagerAdapter = new TabPagerAdapter(getSupportFragmentManager(), getHeroConfiguration());
			viewPager.setAdapter(viewPagerAdapter);
		} else {
			viewPagerAdapter.setHeroConfiguration(getSupportFragmentManager(), getHeroConfiguration());
		}

		viewPager.setOnPageChangeListener(null);

		ActionBar bar = getSupportActionBar();
		List<TabInfo> tabs;
		if (getHeroConfiguration() != null)
			tabs = getHeroConfiguration().getTabs();
		else
			tabs = Collections.emptyList();

		if (Util.getWidth(this) > 900) {
			// TABS INIT
			bar.removeAllTabs();
			for (int i = 0; i < tabs.size(); i++) {
				TabInfo tabInfo = tabs.get(i);
				Drawable d = Util.getDrawableByUri(tabInfo.getIconUri());
				d.setBounds(0, 0, getResources().getDimensionPixelSize(R.dimen.abs__dropdownitem_icon_width),
						getResources().getDimensionPixelSize(R.dimen.abs__dropdownitem_icon_width));

				bar.addTab(bar.newTab().setIcon(d).setTabListener(new TabListener(this, i)));
			}
			getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		} else {
			// LIST INIT
			/** Create an array adapter to populate dropdownlist */
			Context context = getSupportActionBar().getThemedContext();
			TabAdapter adapter = new TabAdapter(context, tabs);

			/** Defining Navigation listener */
			ActionBar.OnNavigationListener navigationListener = new OnNavigationListener() {
				@Override
				public boolean onNavigationItemSelected(int itemPosition, long itemId) {
					viewPager.setCurrentItem(itemPosition);
					return true;
				}
			};
			bar.setListNavigationCallbacks(adapter, navigationListener);
			getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		}

		viewPager.setOnPageChangeListener(this);
		for (int i = 0; i < tabs.size(); i++) {
			TabInfo tabInfo = tabs.get(i);
			if (tabInfo == this.tabInfo) {
				viewPager.setCurrentItem(i);
				break;
			}
		}
	}

	private void showNewsInfoPopup() {
		if (newsShown)
			return;

		ChangeLogDialog logDialog = new ChangeLogDialog(this);
		logDialog.show();
		newsShown = true;
	}

	private HeroConfiguration getHeroConfiguration() {
		HeroConfiguration tabConfig = null;
		if (getHero() != null) {
			tabConfig = getHero().getHeroConfiguration();
		}
		return tabConfig;
	}

	protected boolean showNextTab() {
		if (tabInfo != null && getHeroConfiguration() != null) {
			int index = getHeroConfiguration().getTabs().indexOf(tabInfo);
			return showTab(index + 1);
		} else {
			return false;
		}
	}

	protected boolean showPreviousTab() {
		if (tabInfo != null && getHeroConfiguration() != null) {
			int index = getHeroConfiguration().getTabs().indexOf(tabInfo);
			return showTab(index - 1);
		} else {
			return false;
		}
	}

	/**
	 * 
	 */
	private TabInfo refreshTabInfo() {

		if (getHeroConfiguration() == null || getHeroConfiguration().getTabs().isEmpty()) {
			tabInfo = null;
			return null;
		}

		List<TabInfo> tabs = getHeroConfiguration().getTabs();

		// if we have an existing tabinfo check if it's upto date
		if (tabInfo != null) {
			// check wether tabinfo is uptodate (within current tabconfig
			if (tabs.contains(tabInfo)) {
				return tabInfo;
			}

			// look for tabinfo with same activities
			for (TabInfo tab : tabs) {

				if (Util.equalsOrNull(tab.getActivityClazz(0), tabInfo.getActivityClazz(0))
						&& Util.equalsOrNull(tab.getActivityClazz(1), tabInfo.getActivityClazz(1))) {
					return tab;
				}
			}

			// if we have portraitmode and a secondary clazz this means we
			// switched from landscape here, in this case we look for a tabinfo
			// with the primary activityclass and use this one
			if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT
					&& tabInfo.getActivityClazz(1) != null) {

				Class<? extends BaseFragment> activityClazz = tabInfo.getActivityClazz(0);
				if (activityClazz != null) {
					for (TabInfo tab : tabs) {
						if (activityClazz.equals(tab.getActivityClazz(0))) {
							return tab;
						}
					}
				}
			}

			// if we have landscape mode and a empty secondary activity look for
			// one with one
			if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE
					&& tabInfo.getActivityClazz(1) == null) {

				Class<? extends BaseFragment> activityClazz = tabInfo.getActivityClazz(0);
				if (activityClazz != null) {
					for (TabInfo tab : tabs) {
						if (activityClazz.equals(tab.getActivityClazz(0))
								|| activityClazz.equals(tab.getActivityClazz(1))) {
							return tab;

						}
					}
				}
			}
		}

		// last resort set tabinfo to first one if no matching one is found
		return tabs.get(0);
	}

	public boolean showTab(int index) {

		if (getHeroConfiguration() != null && index >= 0 && index < getHeroConfiguration().getTabs().size()) {
			if (viewPager.getCurrentItem() != index)
				viewPager.setCurrentItem(index, false);
			return true;
		} else {
			return false;
		}
	}

	protected boolean showTab(TabInfo newTabInfo) {

		if (newTabInfo != null && getHeroConfiguration() != null) {
			viewPager.setCurrentItem(getHeroConfiguration().getTabs().indexOf(newTabInfo));
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void onClick(View v) {

		if (v.getTag() instanceof TabInfo) {
			TabInfo tabInfo = (TabInfo) v.getTag();

			// no need to reselect the current tab
			if (this.tabInfo != tabInfo) {
				showTab(tabInfo);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {

		if (diceSlider != null && diceSlider.isOpened()) {
			diceSlider.animateClose();
		} else {
			super.onBackPressed();
		}
	}

	protected void onHeroLoaded(Hero hero) {

		if (hero == null) {
			Toast.makeText(this, "Error: Trying to load empty hero. Please contact developer!", Toast.LENGTH_LONG)
					.show();
			return;
		}

		TabInfo oldInfo = tabInfo;
		tabInfo = refreshTabInfo();
		setupTabs();

		if (tabInfo != oldInfo) {
			showTab(tabInfo);
		}

		if (attributeFragment != null && attributeFragment.isAdded())
			attributeFragment.loadHero(hero);

		updatePage(tabInfo);
	}

	public boolean checkProbe(Probe probe) {
		if (diceSlider != null) {
			if (probe != null && getHero() != null) {
				diceSlider.checkProbe(getHero(), probe);
				return true;
			}
		}
		return false;
	}

	private void unregisterShakeDice() {
		try {
			if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER)) {
				if (mShaker != null) {
					mShaker.setOnShakeListener(null);
					mShaker = null;
				}
			}
		} catch (UnsupportedOperationException e) {
			mShaker = null;
			Debug.warning(e);
		}
	}

	private void registerShakeDice() {
		try {
			if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER)) {
				if (mShaker == null) {
					final Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
					mShaker = new ShakeListener(this);
					mShaker.setOnShakeListener(new ShakeListener.OnShakeListener() {
						@Override
						public void onShake() {
							vibe.vibrate(100);
							if (diceSlider != null)
								diceSlider.rollDice20();
						}
					});

				}
			}
		} catch (UnsupportedOperationException e) {
			mShaker = null;
			Debug.warning(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		unregisterShakeDice();
		DsaTabApplication.getPreferences().unregisterOnSharedPreferenceChangeListener(this);

		// android.os.Debug.stopMethodTracing();
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		try {
			if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER)) {
				if (mShaker != null)
					mShaker.pause();
			}
		} catch (UnsupportedOperationException e) {
			mShaker = null;
			Debug.warning(e);
		}

		super.onPause();
	}

	@Override
	protected void onResume() {
		try {
			if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER)) {
				if (preferences.getBoolean(DsaTabPreferenceActivity.KEY_PROBE_SHAKE_ROLL_DICE, false)) {
					if (mShaker == null)
						registerShakeDice();
					else
						mShaker.resume();
				}
			}
		} catch (UnsupportedOperationException e) {
			mShaker = null;
			Debug.warning(e);
		}
		super.onResume();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		com.actionbarsherlock.view.MenuItem item = menu.add(Menu.NONE, R.id.option_load_hero, Menu.NONE, "Helden");
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
		item.setIcon(R.drawable.ic_menu_archive);

		item = menu.add(Menu.NONE, R.id.option_save_hero, Menu.NONE, "Held speichern");
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
		item.setIcon(R.drawable.ic_menu_save);

		item = menu.add(Menu.NONE, R.id.option_tabs, Menu.NONE, "Tabs anpassen");
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
		item.setIcon(R.drawable.ic_menu_account_list);

		item = menu.add(Menu.NONE, R.id.option_items, Menu.NONE, "Gegenstände");
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
		item.setIcon(Util.getThemeResourceId(this, R.attr.imgSwordAdd));

		item = menu.add(Menu.NONE, R.id.option_settings, 99, "Einstellungen");
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
		item.setIcon(R.drawable.ic_menu_preferences);

		return super.onCreateOptionsMenu(menu);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.actionbarsherlock.app.SherlockFragmentActivity#onPrepareOptionsMenu (com.actionbarsherlock.view.Menu)
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		com.actionbarsherlock.view.MenuItem item = menu.findItem(R.id.option_set);
		if (item != null) {
			switch (getHero().getActiveSet()) {
			case 0:
				item.setIcon(Util.getThemeResourceId(this, R.attr.imgBarSet1));
				break;
			case 1:
				item.setIcon(Util.getThemeResourceId(this, R.attr.imgBarSet2));
				break;
			case 2:
				item.setIcon(Util.getThemeResourceId(this, R.attr.imgBarSet3));
				break;
			}
		}

		item = menu.findItem(R.id.option_save_hero);
		if (item != null) {
			item.setEnabled(getHero() != null);
		}
		item = menu.findItem(R.id.option_export_hero);
		if (item != null) {
			item.setEnabled(getHero() != null);
		}
		item = menu.findItem(R.id.option_tabs);
		if (item != null) {
			item.setEnabled(getHero() != null);
		}

		item = menu.findItem(R.id.option_set);
		if (getHero() != null) {

			if (item != null) {
				item.setEnabled(true);
			}
			switch (getHero().getActiveSet()) {
			case 0:
				if (menu.findItem(R.id.option_set1) != null) {
					menu.findItem(R.id.option_set1).setChecked(true);
				}
				break;
			case 1:
				if (menu.findItem(R.id.option_set2) != null) {
					menu.findItem(R.id.option_set2).setChecked(true);
				}
				break;
			case 2:
				if (menu.findItem(R.id.option_set3) != null) {
					menu.findItem(R.id.option_set3).setChecked(true);
				}
				break;
			default:
				break;
			}
		} else {
			if (item != null) {
				item.setEnabled(false);
			}
		}

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.option_load_hero:
			showHeroChooser();
			return true;
		case R.id.option_save_hero:
			if (getHero() != null) {
				DsaTabApplication.getInstance().saveHero();
			}
			return true;
		case R.id.option_settings:
			DsaTabPreferenceActivity.startPreferenceActivity(this);
			return true;
		case R.id.option_set1:
			if (getHero() != null) {
				getHero().setActiveSet(0);
				item.setChecked(true);
			}
			return true;
		case R.id.option_set2:
			if (getHero() != null) {
				getHero().setActiveSet(1);
				item.setChecked(true);
			}
			return true;
		case R.id.option_set3:
			if (getHero() != null) {
				getHero().setActiveSet(2);
				item.setChecked(true);
			}
			return true;
		case R.id.option_tabs:
			if (getHero() != null) {
				Intent editTab = new Intent(this, TabEditActivity.class);
				editTab.putExtra(TabEditActivity.DATA_INTENT_TAB_INDEX, viewPager.getCurrentItem());
				startActivityForResult(editTab, ACTION_EDIT_TABS);
			}
			return true;
		case R.id.option_items:
			startActivity(new Intent(this, ItemChooserActivity.class));
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onSaveInstanceState(android.os .Bundle)
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// super.onSaveInstanceState(outState);
		outState.putParcelable(KEY_TAB_INFO, tabInfo);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onRestoreInstanceState(android.os.Bundle)
	 */
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// super.onRestoreInstanceState(savedInstanceState);
		if (savedInstanceState.containsKey(KEY_TAB_INFO))
			tabInfo = savedInstanceState.getParcelable(KEY_TAB_INFO);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.SharedPreferences.OnSharedPreferenceChangeListener#
	 * onSharedPreferenceChanged(android.content.SharedPreferences, java.lang.String)
	 */
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		// Debug.verbose(key + " changed");

		if (DsaTabPreferenceActivity.KEY_STYLE_BG_PATH.equals(key)) {
			applyPreferencesToTheme();
		}

		if (DsaTabPreferenceActivity.KEY_SCREEN_ORIENTATION.equals(key)) {
			String orientation = sharedPreferences.getString(DsaTabPreferenceActivity.KEY_SCREEN_ORIENTATION,
					DsaTabPreferenceActivity.DEFAULT_SCREEN_ORIENTATION);

			if (DsaTabPreferenceActivity.SCREEN_ORIENTATION_LANDSCAPE.equals(orientation)) {
				if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
					// You need to check if your desired orientation isn't
					// already set because setting orientation restarts your
					// Activity which takes long
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				}
			} else if (DsaTabPreferenceActivity.SCREEN_ORIENTATION_PORTRAIT.equals(orientation)) {
				if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				}
			} else if (DsaTabPreferenceActivity.SCREEN_ORIENTATION_AUTO.equals(orientation)) {
				if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_SENSOR) {
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
				}
			}
		}

		if (DsaTabPreferenceActivity.KEY_FULLSCREEN.equals(key)) {
			updateFullscreenStatus(preferences.getBoolean(DsaTabPreferenceActivity.KEY_FULLSCREEN, false));
		}

		// notify other listeners (fragments, heroes)
		if (viewPagerAdapter != null) {
			for (Fragment fragment : viewPagerAdapter.getFragments()) {
				if (fragment instanceof OnSharedPreferenceChangeListener) {
					((OnSharedPreferenceChangeListener) fragment).onSharedPreferenceChanged(sharedPreferences, key);
				}
			}
		}

		if (attributeFragment != null && attributeFragment.isAdded())
			attributeFragment.onSharedPreferenceChanged(sharedPreferences, key);

		Hero hero = DsaTabApplication.getInstance().getHero();
		if (hero != null) {
			hero.onSharedPreferenceChanged(sharedPreferences, key);
		}
	}

	protected void showHeroChooser() {
		startActivityForResult(new Intent(DsaTabActivity.this, HeroChooserActivity.class), ACTION_CHOOSE_HERO);
	}
}
