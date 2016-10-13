package com.dsatab.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.fragment.preference.BasePreferenceFragment;
import com.dsatab.util.ViewUtils;

public class DsaTabPreferenceActivity extends BaseActivity implements BasePreferenceFragment.DsaTabSettings, PreferenceFragmentCompat.OnPreferenceStartScreenCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_blank);

        if (savedInstanceState == null) {
            BasePreferenceFragment fragment = new BasePreferenceFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content, fragment);
            ft.commit();
        }
    }

    @Override
    public boolean onPreferenceStartScreen(PreferenceFragmentCompat preferenceFragmentCompat, PreferenceScreen preferenceScreen) {
        BasePreferenceFragment fragment = new BasePreferenceFragment();
        Bundle args = new Bundle();
        args.putString(PreferenceFragmentCompat.ARG_PREFERENCE_ROOT, preferenceScreen.getKey());
        fragment.setArguments(args);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.replace(R.id.content, fragment, preferenceScreen.getKey());
        ft.addToBackStack(preferenceScreen.getKey());
        ft.commit();

        return true;
    }

    public static int getCustomTheme() {
        String theme = DsaTabApplication.getPreferences().getString(DsaTabPreferenceActivity.KEY_THEME, DsaTabApplication.THEME_DEFAULT);

        if (DsaTabApplication.THEME_LIGHT_PLAIN.equals(theme)) {
            return R.style.DsaTabTheme_Light;
        } else if (DsaTabApplication.THEME_DARK_PLAIN.equals(theme)) {
            return R.style.DsaTabTheme_Dark;
        } else {
            return R.style.DsaTabTheme_Light;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu_preferences, menu);
        ViewUtils.menuIcons(toolbar.getContext(), menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.inflateMenu(R.menu.menu_preferences);
            ViewUtils.menuIcons(toolbar.getContext(), toolbar.getMenu());

            toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goBack();
                }
            });
        }
    }

    protected boolean goBack() {
        BasePreferenceFragment fragment = (BasePreferenceFragment) getSupportFragmentManager().findFragmentById(R.id.content);
        if (fragment!=null && fragment.getArguments()!=null) {
            if (fragment.getArguments().get(PreferenceFragmentCompat.ARG_PREFERENCE_ROOT)!=null) {
                getSupportFragmentManager().popBackStack();
                return true;
            }
        }
        setResult(RESULT_OK);
        finish();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                return goBack();
            case R.id.option_about:
                Intent aboutIntent = new Intent(this, AboutActivity.class);
                startActivity(aboutIntent);
                return true;
            case R.id.option_donate:
                Intent donateIntent = new Intent();
                donateIntent.setAction(Intent.ACTION_VIEW);
                donateIntent.setData(Uri.parse("https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=gandulf.k@gmx.net&lc=DE&item_name=Gandulf&item_number=DsaTab&currency_code=EUR&bn=PP-DonationsBF:btn_donateCC_LG.gif:NonHostedGuest"));
                startActivity(donateIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}