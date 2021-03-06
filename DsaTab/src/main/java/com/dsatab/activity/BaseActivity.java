package com.dsatab.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.cloudrail.si.CloudRail;
import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.util.Hint;
import com.dsatab.util.Util;
import com.dsatab.util.ViewUtils;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

import pl.tajchert.nammu.Nammu;

public class BaseActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final int REQUEST_CODE_STORAGE = 1;

    private static final String BROWSABLE = "android.intent.category.BROWSABLE";

    private static final String INTENT ="intent";

    protected Toolbar toolbar;
    protected CollapsingToolbarLayout toolbarCollapse;
    protected AppBarLayout appBarLayout;

    private boolean toolbarRefreshing = false;

    private boolean inflateSaveButton;
    private View.OnClickListener saveListener;

    private FloatingActionButton fab;

    protected SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferences = DsaTabApplication.getPreferences();


        setTheme(DsaTabPreferenceActivity.getCustomTheme());
        applyPreferencesToTheme();
        super.onCreate(savedInstanceState);
        DsaTabApplication.getPreferences().registerOnSharedPreferenceChangeListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Util.getThemeColors(this, R.attr.colorPrimaryDark));
        }

        if (getIntent() == null && savedInstanceState!=null) {
            Intent intent = savedInstanceState.getParcelable(INTENT);
            setIntent(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        DsaTabApplication.getPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (DsaTabPreferenceActivity.KEY_STYLE_BG_PATH.equals(key)) {
            applyPreferencesToTheme();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.FragmentActivity#onPostCreate(android.os.Bundle)
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Hint.showRandomHint(getClass().getSimpleName(), this);

        fab = (FloatingActionButton) findViewById(R.id.fab);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);

        if (inflateSaveButton) {
            getMenuInflater().inflate(R.menu.menuitem_save, menu);
        }

        ViewUtils.menuIcons(toolbar.getContext(), menu);

        return result;
    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (getIntent() !=null) {
            outState.putParcelable(INTENT,getIntent());
        }

    }

    protected FloatingActionButton getFab() {
        return fab;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        ViewUtils.menuIcons(toolbar.getContext(), menu);

        final MenuItem refreshItem = menu.findItem(R.id.option_refresh);
        if (refreshItem != null) {
            if (toolbarRefreshing) {
                refreshItem.setActionView(R.layout._toolbar_progress);
            } else {
                refreshItem.setActionView(null);
            }
        }

        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.option_save:
                if (saveListener != null)
                    saveListener.onClick(null);
            default:
                return super.onOptionsItemSelected(item);
        }


    }

    public boolean isToolbarRefreshing() {
        return toolbarRefreshing;
    }

    public void setToolbarRefreshing(boolean toolbarRefreshing) {
        this.toolbarRefreshing = toolbarRefreshing;
        View loading = findViewById(R.id.loading);
        if (loading != null) {

            if (toolbarRefreshing) {
                loading.setVisibility(View.VISIBLE);
                loading.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onToolbarRefreshingClicked();
                    }
                });
            } else {
                loading.setVisibility(View.GONE);
                loading.setOnClickListener(null);
            }
        }
        supportInvalidateOptionsMenu();
    }

    public void onToolbarRefreshingClicked() {
        setToolbarRefreshing(false);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarCollapse = (CollapsingToolbarLayout) findViewById(R.id.toolbar_collapse);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
    }

    private void applyPreferencesToTheme() {

        SharedPreferences pref = DsaTabApplication.getPreferences();
        String bgPath = pref.getString(DsaTabPreferenceActivity.KEY_STYLE_BG_PATH, null);

        if (bgPath != null) {
            getWindow().setBackgroundDrawable(Drawable.createFromPath(bgPath));
        } else {
            getWindow().setBackgroundDrawableResource(Util.getThemeResourceId(this, android.R.attr.windowBackground));
        }
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public CollapsingToolbarLayout getToolbarCollapse() {
        return toolbarCollapse;
    }

    public void setToolbarTitle(int title) {
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(title);

        if (toolbarCollapse != null)
            toolbarCollapse.setTitle(getText(title));

        if (toolbar != null)
            toolbar.setTitle(title);

    }

    public void setToolbarTitle(CharSequence title) {
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(title);

        if (toolbarCollapse != null)
            toolbarCollapse.setTitle(title);

        if (toolbar != null)
            toolbar.setTitle(title);

    }

    public void inflateSaveAndDiscard(View.OnClickListener saveListener, View.OnClickListener discardListener) {
        inflateSave(saveListener);
        inflateDiscard(discardListener);
    }

    public void inflateSave(View.OnClickListener saveListener) {
        inflateSaveButton = true;
        this.saveListener = saveListener;
        supportInvalidateOptionsMenu();
    }

    public void inflateDiscard(View.OnClickListener listener) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        int iconColor = Util.getThemeColors(getToolbar().getContext(), R.attr.colorControlNormal);
        Drawable close = MaterialDrawableBuilder.with(this).setIcon(MaterialDrawableBuilder.IconValue.CLOSE).setToActionbarSize().setColor(iconColor).build();

        getToolbar().setNavigationIcon(close);
        getToolbar().setNavigationOnClickListener(listener);
    }

    public void inflateDone(View.OnClickListener listener) {
        int iconColor = Util.getThemeColors(getToolbar().getContext(), R.attr.colorControlNormal);
        Drawable check = MaterialDrawableBuilder.with(this).setIcon(MaterialDrawableBuilder.IconValue.ARROW_LEFT).setToActionbarSize().setColor(iconColor).build();

        getToolbar().setNavigationIcon(check);
        getToolbar().setNavigationOnClickListener(listener);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Nammu.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if(intent !=null && intent.getCategories()!=null && intent.getCategories().contains(BROWSABLE)) {
            // Here we pass the response to the SDK which will automatically
            // complete the authentication process
            CloudRail.setAuthenticationResponse(intent);
        }
        super.onNewIntent(intent);
    }
}
