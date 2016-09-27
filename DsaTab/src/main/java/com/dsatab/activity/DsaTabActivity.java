package com.dsatab.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.cloud.HeroSaveTask;
import com.dsatab.config.TabInfo;
import com.dsatab.data.AbstractBeing;
import com.dsatab.data.Hero;
import com.dsatab.data.HeroConfiguration;
import com.dsatab.data.HeroFileInfo;
import com.dsatab.data.HeroLoaderTask;
import com.dsatab.fragment.AnimalFragment;
import com.dsatab.fragment.BaseFragment;
import com.dsatab.fragment.CharacterFragment;
import com.dsatab.fragment.EmptyFragment;
import com.dsatab.fragment.HeroChooserFragment;
import com.dsatab.fragment.MapFragment;
import com.dsatab.fragment.dialog.ChangeLogDialog;
import com.dsatab.util.Debug;
import com.dsatab.util.Util;
import com.dsatab.util.ViewUtils;
import com.dsatab.view.listener.ShakeListener;
import com.gandulf.guilib.util.ResUtil;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;

public class DsaTabActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, ViewPager.OnPageChangeListener {

    private static final String TAB_INDEX = "TAB_INDEX";

    private static final int DRAWER_ID_SYSTEM = 1000;
    private static final int DRAWER_ID_HEROES = 1001;
    private static final int DRAWER_ID_ITEMS = 1002;
    private static final int DRAWER_ID_SETTINGS = 1003;
    private static final int DRAWER_ID_TABS = 1004;

    protected static final String INTENT_TAB_INFO = "tabInfo";

    public static final String PREF_LAST_HERO = "LAST_HERO_JSON";

    private static final String KEY_HERO_PATH = "HERO_PATH";

    public static final int ACTION_ADD_MODIFICATOR = 1003;
    protected static final int ACTION_CHOOSE_HERO = 1004;
    public static final int ACTION_EDIT_MODIFICATOR = 1005;
    public static final int ACTION_EDIT_TABS = 1006;
    public static final int ACTION_EDIT_NOTES = 1007;
    public static final int ACTION_EDIT_CUSTOM_PROBES = 1008;
    public static final int ACTION_VIEW_SPELL = 1009;
    public static final int ACTION_VIEW_ART = 1010;

    private static final String KEY_TAB_INFO = "tabInfo";

    private static final int LOADER_HERO = 2;



    private ShakeListener mShaker;

    private int oldTabInfoPosition = -1;
    private TabInfo tabInfo;

    private DrawerLayout mDrawerLayout;
    private NavigationView mDrawer;
    private View mDrawerHeaderItem;

    private ViewPager viewPager;
    private TabFragmentPagerAdapter tabPagerAdapter;

    private long backPressed;


    private static class HeroLoaderCallback implements LoaderManager.LoaderCallbacks<Hero> {

        private WeakReference<DsaTabActivity> contextRef;

        public HeroLoaderCallback(DsaTabActivity context) {
            this.contextRef = new WeakReference<DsaTabActivity>(context);
        }

        @Override
        public Loader<Hero> onCreateLoader(int id, Bundle args) {
            if (id == LOADER_HERO) {
                DsaTabActivity context = contextRef.get();
                if (context!=null) {
                    return new HeroLoaderTask(context, (HeroFileInfo) args.getSerializable(KEY_HERO_PATH), DsaTabApplication.getInstance().getExchange());
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }

        @Override
        public void onLoadFinished(Loader<Hero> loader, Hero hero) {
            Debug.verbose("loading of hero finished");
            DsaTabActivity context = contextRef.get();
            if (context!=null) {
                context.setToolbarRefreshing(false);

                // Swap the new cursor in. (The framework will take care of closing the
                // old cursor once we return.)
                if (loader instanceof HeroLoaderTask) {
                    HeroLoaderTask heroLoader = (HeroLoaderTask) loader;

                    if (heroLoader.getException() != null) {
                        Snackbar.make(context.mDrawerLayout, heroLoader.getException().getLocalizedMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                }
                DsaTabApplication.getInstance().setHero(hero);

                if (hero != null) {
                    context.checkHsVersion(hero);
                }
                if (!context.isFinishing())
                    context.onHeroLoaded(hero);

            }
        }

        @Override
        public void onLoaderReset(Loader<Hero> loader) {
            // This is called when the last Cursor provided to onLoadFinished()
            // above is about to be closed. We need to make sure we are no
            // longer using it.
            // mAdapter.swapCursor(null);
        }
    }

    public Hero getHero() {
        return DsaTabApplication.getInstance().getHero();
    }

    public final void loadHero(HeroFileInfo heroPath) {

        if (getHero() != null && preferences.getBoolean(DsaTabPreferenceActivity.KEY_AUTO_SAVE, true)) {
            HeroSaveTask heroSaveTask = new HeroSaveTask(this, getHero(),DsaTabApplication.getInstance().getExchange());
            heroSaveTask.execute();
        }
        setToolbarRefreshing(true);

        Bundle args = new Bundle();
        args.putSerializable(KEY_HERO_PATH, heroPath);
        getLoaderManager().restartLoader(LOADER_HERO, args, new HeroLoaderCallback(this));
    }

    @Override
    public boolean onSearchRequested() {
        Bundle appData = new Bundle();
        appData.putParcelable(SearchableActivity.INTENT_TAB_INFO, tabInfo);
        startSearch(null, false, appData, false);
        return true;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        tabInfo = tabPagerAdapter.getTabInfo(position);
        if (oldTabInfoPosition >= 0) {
            if (tabPagerAdapter.isOddPosition(position)) {
                if (oldTabInfoPosition < position)
                    viewPager.setCurrentItem(position + 1, true);
                else
                    viewPager.setCurrentItem(position - 1, true);
                return;
            }
        }
        updatePage(tabInfo);
        updateBackdrop(tabInfo);

        oldTabInfoPosition = position;
    }

    private void updatePage(TabInfo tabInfo) {

        Drawable d = null;
        if (tabInfo != null) {
            if (tabInfo.hasActivityClazz(MapFragment.class) || tabInfo.hasActivityClazz(AnimalFragment.class))
                setToolbarTitle("");
            else if (tabInfo.hasActivityClazz(CharacterFragment.class) && getHero() != null) {
                setToolbarTitle(getHero().getName());
            } else {
                setToolbarTitle(tabInfo.getTitle());
            }

            int tabIndex = tabPagerAdapter.indexOf(tabInfo);
            if (tabIndex >= 0) {
                mDrawer.setCheckedItem(tabIndex);
            }

            if (tabInfo.getIconUri() != null) {
                d = ResUtil.getDrawableByUri(this, tabInfo.getIconUri());
                d = d.mutate();
                android.support.v4.graphics.drawable.DrawableCompat.setTint(d, getResources().getColor(R.color.white));
                int size = getResources().getDimensionPixelSize(R.dimen.toolbar_icon_size);
                d.setBounds(0, 0, size, size);
            }

            if (!tabInfo.isToolbarExpandable()) {
                setToolbarExpanded(false, true);
            }
        } else {
            setToolbarTitle(getString(R.string.app_name));
        }

        if (d != null) {
            getToolbar().setLogo(d);
        } else {
            getToolbar().setLogo(R.mipmap.ic_launcher);
        }

    }

    private boolean initHero() {

        // in case of orientation change the hero is already loaded, just recreate the menu etc...
        if (DsaTabApplication.getInstance().getHero() != null) {
            onHeroLoaded(DsaTabApplication.getInstance().getHero());
            return true;
        } else {
            String heroFileInfoJson = preferences.getString(PREF_LAST_HERO, null);
            if (heroFileInfoJson != null) {
                try {
                    HeroFileInfo fileInfo = new HeroFileInfo(new JSONObject(heroFileInfoJson));
                    loadHero(fileInfo);
                    return true;
                } catch (Exception e) {
                    Debug.error(e);
                    return false;
                }
            } else {
                return false;
            }
        }
    }

    protected boolean checkHsVersion(Hero hero) {

        if (TextUtils.isEmpty(hero.getFileInfo().getVersion())) {
            Snackbar.make(mDrawerLayout, getString(R.string.hero_loaded, hero.getName()), Snackbar.LENGTH_SHORT).show();
            return false;
        }

        int version = hero.getFileInfo().getVersionInt();

        if (version < 0) {
            Snackbar.make(
                    mDrawerLayout,
                    "Warnung: Unbekannte Helden-Software Version. Es kann keine vollständige Kompatibilität gewährleistet werden.",
                    Snackbar.LENGTH_LONG).show();
            return false;
        } else if (version > DsaTabApplication.HS_VERSION_INT) {
            Snackbar.make(mDrawerLayout, "Hinweis: DsaTab wurde noch nicht an die aktuellste Helden-Software angepasst.",
                    Snackbar.LENGTH_LONG).show();
            return false;
            //} else if (version < DsaTabApplication.HS_VERSION_INT) {
            // ViewUtils.snackbar(this,
            // "Warnung: Die Helden Xml Datei wurde nicht mit der aktuellen Helden-Software erstellt.",
            // Snackbar.LENGTH_LONG);
            // return false;
        } else {
            Snackbar.make(mDrawerLayout, getString(R.string.hero_loaded, hero.getName()), Snackbar.LENGTH_SHORT).show();
            return true;
        }
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
                HeroFileInfo heroFileInfo = (HeroFileInfo) data
                        .getSerializableExtra(HeroChooserFragment.INTENT_NAME_HERO_FILE_INFO);
                Debug.verbose("HeroChooserActivity returned with path:" + heroFileInfo);
                loadHero(heroFileInfo);
            } else if (resultCode == RESULT_CANCELED) {
                if (getHero() == null) {
                    finish();
                }
            }

        } else if (requestCode == ACTION_EDIT_TABS) {
            if (resultCode == Activity.RESULT_OK) {
                setupDrawerItems(getHero());
                setupViewPager(getHero());
            }
        }

        // notify other listeners (fragments, heroes)
        for (int i = 0; i < TabInfo.MAX_TABS_PER_PAGE; i++) {
            if (getCurrentFragment(i) != null) {
                getCurrentFragment(i).onActivityResult(requestCode, resultCode, data);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    protected Fragment getCurrentFragment(int index) {
        if (tabInfo != null) {
            return getFragmentManager().findFragmentByTag(tabInfo.getId().toString() + index);
        } else {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_view);

        // start tracing to "/sdcard/calc.trace"
        // android.os.Debug.startMethodTracing("dsatab");

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        String orientation = preferences.getString(DsaTabPreferenceActivity.KEY_SCREEN_ORIENTATION,
                DsaTabPreferenceActivity.DEFAULT_SCREEN_ORIENTATION);

        Configuration configuration = getResources().getConfiguration();

        if (savedInstanceState != null) {
            tabInfo = savedInstanceState.getParcelable(KEY_TAB_INFO);
        }

        if (tabInfo == null && getIntent() != null) {
            tabInfo = getIntent().getParcelableExtra(INTENT_TAB_INFO);
        }

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

        initViewPager();
        initDrawer();

        if (preferences.getBoolean(DsaTabIntro.KEY_APP_INTRO, true)) {
            // we already ask inside intro need to ask again
            boolean heroLoaded = initHero();
            if (!heroLoaded) {
                showHeroChooser();
            }

            startActivity(new Intent(this, DsaTabIntro.class));

        } else {

            Nammu.askForPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, new PermissionCallback() {
                @Override
                public void permissionGranted() {
                    boolean heroLoaded = initHero();
                    if (!heroLoaded) {
                        showHeroChooser();
                    }
                }

                @Override
                public void permissionRefused() {
                    ViewUtils.snackbar(DsaTabActivity.this, "Ohne Schreibrechte kann DsaTab leider keine Heldendaten speichern.");
                }
            });
        }
    }

    private void initViewPager() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.addOnPageChangeListener(this);
        //viewPager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.card_margin));
        //viewPager.setPageMarginDrawable(new ColorDrawable(getResources().getColor(android.R.color.darker_gray)));
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        mDrawerLayout.closeDrawers();

        switch (menuItem.getItemId()) {
            case DRAWER_ID_HEROES:
                showHeroChooser();
                break;
            case DRAWER_ID_ITEMS:
                ItemsActivity.view(this);
                break;
            case DRAWER_ID_TABS:
                if (getHero() != null) {
                    TabEditActivity.list(this, tabInfo, getHeroConfiguration().getTabs().indexOf(tabInfo), ACTION_EDIT_TABS);
                }
                break;
            default:
                if (menuItem.getIntent() != null) {
                    startActivity(menuItem.getIntent());
                } else {
                    final TabInfo tabInfo = tabPagerAdapter.getTabInfoByIndex(menuItem.getOrder());
                    mDrawerLayout.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showTab(tabInfo, false);
                        }
                    }, 500);
                }
                break;
        }

        return true;
    }

    private void setupDrawerItems(Hero hero) {
        List<TabInfo> tabs;
        if (hero != null && hero.getHeroConfiguration() != null)
            tabs = hero.getHeroConfiguration().getActiveTabs(hero);
        else
            tabs = Collections.emptyList();

        int SYSTEM_GROUP = 0;
        int TAB_GROUP = 1;

        Menu menu = mDrawer.getMenu();
        menu.removeGroup(TAB_GROUP);

        MenuItem menuItem;
        int pos = 0;
        if (tabs != null) {

            for (TabInfo tabInfo : tabs) {
                menuItem = menu.add(TAB_GROUP, pos, pos, tabInfo.getTitle());
                menuItem.setIcon(ResUtil.getDrawableByUri(this, tabInfo.getIconUri()));
                pos++;
            }
        }
        menu.setGroupCheckable(TAB_GROUP, true, true);

        SubMenu subMenu = null;
        if (menu.findItem(DRAWER_ID_SYSTEM) != null) {
            subMenu = menu.findItem(DRAWER_ID_SYSTEM).getSubMenu();
        }
        if (subMenu == null) {
            subMenu = menu.addSubMenu(SYSTEM_GROUP, DRAWER_ID_SYSTEM, 100, "System");
        }

        if (subMenu.findItem(DRAWER_ID_HEROES) == null) {
            menuItem = subMenu.add(SYSTEM_GROUP, DRAWER_ID_HEROES, 1, "Helden");
            menuItem.setIcon(ViewUtils.toolbarIcon(mDrawer.getContext(), MaterialDrawableBuilder.IconValue.ACCOUNT));
        }
        if (subMenu.findItem(DRAWER_ID_TABS) == null && hero != null) {
            menuItem = subMenu.add(SYSTEM_GROUP, DRAWER_ID_TABS, 2, "Tabs anpassen");
            menuItem.setIcon(ViewUtils.toolbarIcon(mDrawer.getContext(), MaterialDrawableBuilder.IconValue.TAB));
        } else {
            if (hero == null) {
                subMenu.removeItem(DRAWER_ID_TABS);
            }
        }

        if (subMenu.findItem(DRAWER_ID_ITEMS) == null) {
            menuItem = subMenu.add(SYSTEM_GROUP, DRAWER_ID_ITEMS, 3, "Gegenstände");
            menuItem.setIcon(ViewUtils.toolbarIcon(mDrawer.getContext(), MaterialDrawableBuilder.IconValue.TSHIRT_CREW));
        }

        if (subMenu.findItem(DRAWER_ID_SETTINGS) == null) {
            menuItem = subMenu.add(SYSTEM_GROUP, DRAWER_ID_SETTINGS, 4, "Einstellungen");
            menuItem.setIntent(new Intent(this, DsaTabPreferenceActivity.class));
            menuItem.setIcon(ViewUtils.toolbarIcon(mDrawer.getContext(), MaterialDrawableBuilder.IconValue.SETTINGS));
        }
    }

    private void setupViewPager(Hero hero) {
        List<TabInfo> tabs;
        if (hero != null && hero.getHeroConfiguration() != null)
            tabs = hero.getHeroConfiguration().getActiveTabs(hero);
        else
            tabs = Collections.emptyList();

        tabPagerAdapter = new TabFragmentPagerAdapter(getFragmentManager(), tabs);
        viewPager.setAdapter(tabPagerAdapter);

        int defaultTabIndex = preferences.getInt(TAB_INDEX, 0);
        showTab(refreshTabInfo(defaultTabIndex), true);
    }

    private void initDrawer() {
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(ViewUtils.toolbarIcon(ab.getThemedContext(), MaterialDrawableBuilder.IconValue.MENU));
        ab.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setClickable(true);
        mDrawer = ((NavigationView) findViewById(R.id.drawer));

        mDrawerLayout.setStatusBarBackgroundColor(Util.getThemeColors(this, R.attr.colorPrimaryDark));
        mDrawer.setNavigationItemSelectedListener(this);

        if (mDrawerHeaderItem != null) {
            mDrawer.removeHeaderView(mDrawerHeaderItem);
        }
        mDrawerHeaderItem = mDrawer.inflateHeaderView(R.layout.drawer_header);

        mDrawerLayout.closeDrawers();
    }


    private HeroConfiguration getHeroConfiguration() {
        HeroConfiguration tabConfig = null;
        if (getHero() != null) {
            tabConfig = getHero().getHeroConfiguration();
        }
        return tabConfig;
    }

    public void notifyTabsChanged(TabInfo oldTabInfo) {
        setupDrawerItems(getHero());
        setupViewPager(getHero());
    }

    private TabInfo refreshTabInfo(int defaultTabIndex) {

        if (getHero() == null || getHeroConfiguration() == null) {
            tabInfo = null;
            return null;
        }

        List<TabInfo> tabs = getHeroConfiguration().getActiveTabs(getHero());

        if (tabs.isEmpty()) {
            tabInfo = null;
            return null;
        }

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
        if (defaultTabIndex >= 0 && defaultTabIndex < tabs.size())
            return tabs.get(defaultTabIndex);
        else
            return tabs.get(0);
    }

    protected boolean showTab(TabInfo newTabInfo, boolean forceRefresh) {
        if (newTabInfo != null && tabPagerAdapter != null) {
            int index = tabPagerAdapter.positionOf(newTabInfo);
            // if we area already on the right page fore a onPageSelected if force is set
            if (forceRefresh && viewPager.getCurrentItem() == index) {
                onPageSelected(index);
            } else if (viewPager.getCurrentItem() != index) {
                viewPager.setCurrentItem(index);
            }
            return true;
        } else {
            return false;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onBackPressed()
     */
    @Override
    public void onBackPressed() {

        boolean drawerOpen = mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mDrawer);

        if (drawerOpen) {
            mDrawerLayout.closeDrawers();
        } else {
            if (backPressed + 2000 > System.currentTimeMillis() || getFragmentManager().getBackStackEntryCount() > 0) {
                super.onBackPressed();
            } else {
                Snackbar.make(mDrawerLayout, "Erneut klicken um DsaTab zu schließen", Snackbar.LENGTH_SHORT).show();
                backPressed = System.currentTimeMillis();
            }

        }
    }

    protected void onHeroLoaded(Hero hero) {
        if (isFinishing())
            return ;

        setupDrawerItems(hero);
        setupViewPager(hero);
        updatePortrait(hero);

        if (hero == null) {
            Snackbar.make(mDrawerLayout, R.string.message_load_hero_failed, Snackbar.LENGTH_LONG).show();
        } else {
            if (DsaTabApplication.getInstance().isFirstRun() && mDrawerLayout != null) {
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        }
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


    public void updatePortrait(AbstractBeing being) {

        updateBackdrop(tabInfo);

        // update drawer profile
        if (being instanceof Hero) {
            Hero hero = (Hero) being;
            if (hero.getFileInfo() != null && mDrawerHeaderItem != null) {
                // -- header

                HeroFileInfo heroInfo = hero.getFileInfo();
                CircleImageView imageView = (CircleImageView) mDrawerHeaderItem.findViewById(R.id.profile_image);
                Util.setImage(imageView, heroInfo.getPortraitUri(), R.drawable.profile_picture);

                TextView textView = (TextView) mDrawerHeaderItem.findViewById(R.id.profile_title);
                if (textView != null)
                    textView.setText(heroInfo.getName());

                TextView descrView = (TextView) mDrawerHeaderItem.findViewById(R.id.profile_description);
                if (descrView != null)
                    descrView.setText(heroInfo.getVersion());
            }
        }
    }

    protected void updateBackdrop(TabInfo tabInfo) {
        ImageView portraitView = (ImageView) findViewById(R.id.toolbar_portrait);

        if (tabInfo != null) {
            if (mDrawerHeaderItem != null)
                mDrawerHeaderItem.setVisibility(View.VISIBLE);
            int backdropImage = tabInfo.getBackdropImage();

            if (backdropImage != 0) {
                portraitView.setImageResource(backdropImage);
            } else {
                Util.setImage(portraitView, getHero() != null ? getHero().getPortraitUri() : null, R.drawable.backdrop_dsa);
            }

            boolean hasDescription = tabInfo.hasActivityClazz(CharacterFragment.class) || tabInfo.hasActivityClazz(AnimalFragment.class);
            View description = findViewById(R.id.gen_description);
            View info = findViewById(R.id.toolbar_info);
            if (info != null) {
                if (!hasDescription || description == null) {
                    info.setVisibility(View.GONE);
                } else {
                    info.setVisibility(View.VISIBLE);
                }
            }
        } else {
            if (mDrawerHeaderItem != null)
                mDrawerHeaderItem.setVisibility(View.GONE);
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

//							DiceSliderFragment.show();
//							if (diceSliderFragment != null) {
//								diceSliderFragment.expandPanel();
//								diceSliderFragment.rollDice20();
                            //}
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
     * @see android.app.Activity#onDestroy()
     */
    @Override
    protected void onDestroy() {
        unregisterShakeDice();

        getLoaderManager().destroyLoader(LOADER_HERO);
        DsaTabApplication.getPreferences().unregisterOnSharedPreferenceChangeListener(this);

        if (getHero() != null && preferences.getBoolean(DsaTabPreferenceActivity.KEY_AUTO_SAVE, true)) {
            HeroSaveTask heroSaveTask = new HeroSaveTask(this, getHero(),DsaTabApplication.getInstance().getExchange());
            heroSaveTask.execute();
        }

        if (getHeroConfiguration() != null && getHeroConfiguration().getTabs() != null) {
            Editor edit = preferences.edit();

            int tabIndex = getHeroConfiguration().getActiveTabs(getHero()).indexOf(tabInfo);
            edit.putInt(TAB_INDEX, tabIndex);
            edit.commit();
        }

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

        ChangeLogDialog.show(this);

        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menuitem_search, menu);

        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        if (searchMenuItem != null) {
            android.support.v7.widget.SearchView searchViewAction = (android.support.v7.widget.SearchView) MenuItemCompat
                    .getActionView(searchMenuItem);
            if (searchViewAction != null) {
                SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
                searchViewAction.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
                searchViewAction.setIconifiedByDefault(true);
            }
        }
        inflater.inflate(R.menu.menuitem_savehero, menu);

        return super.onCreateOptionsMenu(menu);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.actionbarsherlock.app.SherlockFragmentActivity#onPrepareOptionsMenu (com.actionbarsherlock.view.Menu)
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.option_set);
        if (item != null) {
            ViewUtils.menuIconState(item, getHero() != null);
            if (getHero() != null) {
                switch (getHero().getActiveSet()) {
                    case 0:
                        item.setIcon(Util.getThemeResourceId(this, R.attr.imgBarSet1));
                        if (menu.findItem(R.id.option_set1) != null) {
                            menu.findItem(R.id.option_set1).setChecked(true);
                        }
                        break;
                    case 1:
                        item.setIcon(Util.getThemeResourceId(this, R.attr.imgBarSet2));
                        if (menu.findItem(R.id.option_set2) != null) {
                            menu.findItem(R.id.option_set2).setChecked(true);
                        }
                        break;
                    case 2:
                        item.setIcon(Util.getThemeResourceId(this, R.attr.imgBarSet3));
                        if (menu.findItem(R.id.option_set3) != null) {
                            menu.findItem(R.id.option_set3).setChecked(true);
                        }
                        break;
                }
            }
        }

        item = menu.findItem(R.id.option_save_hero);
        if (item != null) {
            item.setVisible(!preferences.getBoolean(DsaTabPreferenceActivity.KEY_AUTO_SAVE, true));
            ViewUtils.menuIconState(item, getHero() != null);
        }

        boolean result = super.onPrepareOptionsMenu(menu);

        // hide all actions if drawer is opened (android design guideline)
        if (isDrawerOpened()) {
            for (int i = 0; i < menu.size(); i++) {
                menu.getItem(i).setVisible(false);
            }
        }

        return result;
    }

    public void setToolbarExpanded(boolean expanded, boolean animate) {
        appBarLayout.setExpanded(expanded, animate);
    }

    public boolean isDrawerOpened() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mDrawer);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.option_save_hero:
                if (getHero() != null) {
                    HeroSaveTask heroSaveTask = new HeroSaveTask(this, getHero(),DsaTabApplication.getInstance().getExchange());
                    heroSaveTask.execute();
                }
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
        }

        return super.onOptionsItemSelected(item);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.FragmentActivity#onSaveInstanceState(android.os .Bundle)
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
        super.onSharedPreferenceChanged(sharedPreferences,key);

        // Debug.verbose(key + " changed");
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

        if (DsaTabPreferenceActivity.KEY_PROBE_SHAKE_ROLL_DICE.equals(key)) {
            if (sharedPreferences.getBoolean(DsaTabPreferenceActivity.KEY_PROBE_SHAKE_ROLL_DICE, false)) {
                registerShakeDice();
            } else {
                unregisterShakeDice();
            }
        }

        // notify other listeners (fragments, heroes)
        for (int i = 0; i < TabInfo.MAX_TABS_PER_PAGE; i++) {
            if (getCurrentFragment(i) instanceof OnSharedPreferenceChangeListener) {
                ((OnSharedPreferenceChangeListener) getCurrentFragment(i)).onSharedPreferenceChanged(sharedPreferences,
                        key);
            }
        }
    }

    protected void showHeroChooser() {
        Intent intent = new Intent(this, HeroChooserActivity.class);
        startActivityForResult(intent, ACTION_CHOOSE_HERO);
    }

    class TabFragmentPagerAdapter extends com.gandulf.guilib.view.adapter.FragmentPagerAdapter {

        private List<TabInfoPage> tabInfoPages;
        private List<TabInfo> tabInfos;

        private class TabInfoPage {
            private TabInfo tabInfo;
            private int index;

            public TabInfoPage(TabInfo info, int index) {
                this.tabInfo = info;
                this.index = index;
            }

            public float getPageWidth() {
                if (tabInfo != null)
                    return 1.0f / tabInfo.getTabCount();
                else
                    return 1.0f;
            }

            public Class<? extends BaseFragment> getActivityClazz() {
                if (tabInfo != null)
                    return tabInfo.getActivityClazz(index);
                else
                    return EmptyFragment.class;
            }

            public long getId() {
                if (tabInfo != null) {
                    int hash = tabInfo.getId().hashCode();
                    String itemId = hash + "" + index;
                    return Long.parseLong(itemId);
                } else {
                    return AdapterView.INVALID_ROW_ID;
                }
            }
        }

        public TabFragmentPagerAdapter(android.app.FragmentManager fm, List<TabInfo> tabs) {
            super(fm);

            tabInfoPages = new ArrayList<>();
            if (tabs != null)
                tabInfos = new ArrayList<>(tabs);
            else {
                tabInfos = new ArrayList<>();

            }
            for (TabInfo info : tabInfos) {
                for (int i = 0; i < info.getTabCount(); i++) {
                    tabInfoPages.add(new TabInfoPage(info, i));
                }
            }
        }

        @Override
        public float getPageWidth(int position) {
            TabInfoPage page = getTabInfoPage(position);
            if (page != null)
                return page.getPageWidth();
            else
                return 1.0f;
        }

        @Override
        public Fragment getItem(int position) {
            TabInfoPage infoPage = getTabInfoPage(position);
            if (infoPage != null && infoPage.tabInfo != null) {
                return BaseFragment.newInstance(infoPage.getActivityClazz(), infoPage.tabInfo, infoPage.index);
            } else {
                return BaseFragment.newInstance(EmptyFragment.class, null, 0);
            }
        }

        @Override
        public long getItemId(int position) {
            TabInfoPage infoPage = getTabInfoPage(position);
            if (infoPage != null) {
                return infoPage.getId();
            } else {
                return AdapterView.INVALID_ROW_ID;
            }
        }

        public TabInfo getTabInfoByIndex(int index) {
            if (index >= 0 && index < tabInfos.size())
                return tabInfos.get(index);
            else
                return null;
        }

        public TabInfoPage getTabInfoPage(int position) {
            if (position >= 0 && position < tabInfoPages.size())
                return tabInfoPages.get(position);
            else
                return null;
        }

        public TabInfo getTabInfo(int position) {
            TabInfoPage page = getTabInfoPage(position);
            if (page != null)
                return page.tabInfo;
            else
                return null;
        }

        public boolean isOddPosition(int position) {
            TabInfoPage page = tabInfoPages.get(position);
            if (page != null)
                return page.index != 0;
            else
                return false;
        }

        public int positionOf(TabInfo tabInfo) {
            if (tabInfo == null)
                return -1;

            for (int i = 0; i < tabInfoPages.size(); i++) {
                TabInfoPage page = tabInfoPages.get(i);
                if (page.tabInfo != null && page.tabInfo.equals(tabInfo)) {
                    return i;
                }
            }
            return -1;
        }

        public int indexOf(TabInfo tabInfo) {
            if (tabInfo == null)
                return -1;

            return tabInfos.indexOf(tabInfo);
        }

        @Override
        public int getCount() {
            return tabInfoPages.size();
        }
    }
}
