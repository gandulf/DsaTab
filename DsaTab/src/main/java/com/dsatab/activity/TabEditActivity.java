package com.dsatab.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.config.TabInfo;
import com.dsatab.fragment.TabEditFragment;
import com.dsatab.fragment.TabListFragment;
import com.dsatab.util.ViewUtils;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

public class TabEditActivity extends BaseActivity implements TabListFragment.TabListListener {

    public static final int ACTION_EDIT = 1014;
    public static final int ACTION_ADD = 1015;

    public static final String DATA_INTENT_TAB_INDEX = "tab.index";
    public static final String DATA_INTENT_TABINFO = "tab.info";

    private TabListFragment listFragment;
    private TabEditFragment editFragment1;
    private TabEditFragment editFragment2;

    public static void list(Activity activity, TabInfo tabInfo, int tabIndex, int requestCode) {
        Intent intent = new Intent(activity, TabEditActivity.class);
        intent.putExtra(TabEditActivity.DATA_INTENT_TABINFO, tabInfo);
        intent.putExtra(TabEditActivity.DATA_INTENT_TAB_INDEX, tabIndex);
        intent.setAction(Intent.ACTION_EDIT);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void insert(Activity context, int tabIndex, int requestCode) {
        Intent intent = new Intent(context, BaseEditActivity.class);
        intent.putExtra(BaseEditActivity.EDIT_FRAGMENT_CLASS, TabEditFragment.class);
        intent.putExtra(BaseEditActivity.EDIT_TITLE, context.getString(R.string.label_create));
        intent.putExtra(TabEditActivity.DATA_INTENT_TAB_INDEX, tabIndex);
        intent.setAction(Intent.ACTION_INSERT);
        context.startActivityForResult(intent, requestCode);
    }

    public static void edit(Activity context, TabInfo tabInfo, int tabIndex, int requestCode) {
        if (tabInfo != null) {
            Intent intent = new Intent(context, BaseEditActivity.class);
            intent.putExtra(BaseEditActivity.EDIT_FRAGMENT_CLASS, TabEditFragment.class);
            intent.putExtra(BaseEditActivity.EDIT_TITLE, context.getString(R.string.label_edit));
            intent.putExtra(TabEditActivity.DATA_INTENT_TABINFO, tabInfo);
            intent.putExtra(TabEditActivity.DATA_INTENT_TAB_INDEX, tabIndex);
            intent.setAction(Intent.ACTION_EDIT);
            context.startActivityForResult(intent, requestCode);
        }
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_edit_tabs);

        // Inflate a "Done" custom action bar view to serve as the "Up"
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Drawable close = ViewUtils.toolbarIcon(getToolbar().getContext(), MaterialDrawableBuilder.IconValue.WINDOW_CLOSE);
        getToolbar().setNavigationIcon(close);
        getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

        setResult(RESULT_OK);

        if (DsaTabApplication.getInstance().getHero() == null) {
            ViewUtils.snackbar(this, R.string.message_can_only_edit_tabs_if_hero_loaded, Snackbar.LENGTH_SHORT);
            setResult(RESULT_CANCELED);
            super.finish();
            return;
        }

        editFragment1 = (TabEditFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_tab_edit1);
        editFragment2 = (TabEditFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_tab_edit2);
        listFragment = (TabListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_tab_list);
        listFragment.setTabListListener(this);
        int tabIndex = 0;
        if (getIntent() != null) {
            tabIndex = getIntent().getIntExtra(TabEditActivity.DATA_INTENT_TAB_INDEX, 0);
        }
        listFragment.selectTabInfo(tabIndex);


        setUpTabInfos();
    }

    private void setUpTabInfos() {
        TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);
        if (tabHost != null) {
            tabHost.setup();

            final TabWidget tabWidget = tabHost.getTabWidget();
            final FrameLayout tabContent = tabHost.getTabContentView();

            // Get the original tab textviews and remove them from the viewgroup.
            TextView[] originalTextViews = new TextView[tabWidget.getTabCount()];
            for (int index = 0; index < tabWidget.getTabCount(); index++) {
                originalTextViews[index] = (TextView) tabWidget.getChildTabViewAt(index);
            }
            tabWidget.removeAllViews();

            // Ensure that all tab content childs are not visible at startup.
            for (int index = 0; index < tabContent.getChildCount(); index++) {
                tabContent.getChildAt(index).setVisibility(View.GONE);
            }

            // Create the tabspec based on the textview childs in the xml file.
            // Or create simple tabspec instances in any other way...
            for (int index = 0; index < originalTextViews.length; index++) {
                final TextView tabWidgetTextView = originalTextViews[index];
                final View tabContentView = tabContent.getChildAt(index);
                TabHost.TabSpec tabSpec = tabHost.newTabSpec((String) tabWidgetTextView.getTag());
                tabSpec.setContent(new TabHost.TabContentFactory() {
                    @Override
                    public View createTabContent(String tag) {
                        return tabContentView;
                    }
                });
                if (tabWidgetTextView.getBackground() == null) {
                    tabSpec.setIndicator(tabWidgetTextView.getText());
                } else {
                    tabSpec.setIndicator(tabWidgetTextView.getText(), tabWidgetTextView.getBackground());
                }
                tabHost.addTab(tabSpec);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menuitem_save, menu);
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ACTION_EDIT && resultCode == Activity.RESULT_OK) {
            TabInfo info = data.getParcelableExtra(TabEditActivity.DATA_INTENT_TABINFO);
            int position = listFragment.getTabInfos().indexOf(info);
            listFragment.setTabInfo(position, info);
        } else if (requestCode == ACTION_ADD && resultCode == Activity.RESULT_OK) {
            TabInfo info = data.getParcelableExtra(TabEditActivity.DATA_INTENT_TABINFO);
            listFragment.addTabInfo(info);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.option_save:
                return accept();
            case android.R.id.home:
                finish();
        }

        return false;
    }


    @Override
    public void onTabInfoClicked(TabInfo tabInfo) {
        int index = 0;
        if (listFragment != null) {
            index = listFragment.getTabInfos().indexOf(tabInfo);
        }

        if (editFragment1 != null) {
            editFragment1.setTabInfo(tabInfo, 0);
            if (editFragment2 != null) {
                editFragment2.setTabInfo(tabInfo, 1);
            }
            invalidateOptionsMenu();
        } else {
            edit(this, tabInfo, index, ACTION_EDIT);
        }
    }

    @Override
    public void onTabInfoSelected(TabInfo info) {
        if (editFragment1 != null) {
            editFragment1.setTabInfo(info, 0);
            if (editFragment2 != null) {
                editFragment2.setTabInfo(info, 1);
            }
            invalidateOptionsMenu();
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
        cancel();
    }

    protected boolean accept() {
        if (editFragment1 != null) {
            Bundle data = editFragment1.accept();
            TabInfo info = data.getParcelable(TabEditActivity.DATA_INTENT_TABINFO);
            if (editFragment2 != null) {
                Bundle data2 = editFragment2.accept();
                TabInfo info2 = data2.getParcelable(TabEditActivity.DATA_INTENT_TABINFO);
                info.setActivityClazz(editFragment2.getTabInfoIndex(),info2.getActivityClazz(editFragment2.getTabInfoIndex()));
                info.setListSettings(editFragment2.getTabInfoIndex(), info2.getListSettings(editFragment2.getTabInfoIndex()));

            }

            if (info != null) {
                int position = listFragment.getTabInfos().indexOf(info);
                if (position >= 0) {
                    listFragment.getTabInfos().set(position, info);
                } else {
                    listFragment.addTabInfo(info);
                }
            }
        }
        if (DsaTabApplication.getInstance().getHero() != null
                && DsaTabApplication.getInstance().getHero().getHeroConfiguration() != null) {
            DsaTabApplication.getInstance().getHero().getHeroConfiguration().setTabs(listFragment.getTabInfos());
        }
        setResult(RESULT_OK);

        finish();
        return true;
    }

    protected void cancel() {
        setResult(RESULT_CANCELED);
        finish();
    }


}