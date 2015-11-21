package com.dsatab.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.fragment.preference.BasePreferenceFragment;
import com.dsatab.util.ViewUtils;

import java.util.List;

public class DsaTabPreferenceActivity extends AppCompatPreferenceActivity implements BasePreferenceFragment.DsaTabSettings, Toolbar.OnMenuItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(DsaTabApplication.getInstance().getCustomPreferencesTheme());
        super.onCreate(savedInstanceState);

        View rootView = getWindow().getDecorView().getRootView();
        ViewGroup contentView = (ViewGroup) rootView.findViewById(android.R.id.content);

        ViewGroup newContent = (ViewGroup) getLayoutInflater().inflate(R.layout.main_blank, contentView, false);

        ViewGroup newContainer = (ViewGroup) newContent.findViewById(R.id.content);
        for (int i = contentView.getChildCount() - 1; i >= 0; i--) {
            View v = contentView.getChildAt(i);
            contentView.removeView(v);
            newContainer.addView(v, newContainer.getChildCount());
        }
        contentView.addView(newContent);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.inflateMenu(R.menu.menu_preferences);
            ViewUtils.menuIcons(toolbar.getContext(), toolbar.getMenu());
            int titleResId = 0;
            if (getIntent() != null) {
                titleResId = getIntent().getIntExtra(EXTRA_SHOW_FRAGMENT_TITLE, 0);
            }
            if (titleResId > 0)
                toolbar.setTitle(titleResId);
            else
                toolbar.setTitle("Einstellungen");

            toolbar.setOnMenuItemClickListener(this);
            toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setResult(RESULT_OK);
                    finish();
                }
            });
        }
    }

    /**
     * Populate the activity with the top-level headers.
     */
    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(com.dsatab.R.xml.preferences_headers, target);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
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
                return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_OK);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return fragmentName.startsWith(DsaTabApplication.getInstance().getPackageName());
    }

}