package com.dsatab.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.fragment.dialog.ChangeLogDialog;
import com.gandulf.guilib.util.ResUtil;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.aboutlibraries.LibsConfiguration;
import com.mikepenz.aboutlibraries.entity.Library;
import com.mikepenz.aboutlibraries.ui.LibsFragment;

public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_blank);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        LibsFragment fragment = new LibsBuilder()
                .withActivityTheme(DsaTabPreferenceActivity.getCustomTheme())
                .withLibraries("game_icons_net", "ulisses_fan_project")
                .withAboutSpecial1("Source Code")
                .withAboutSpecial1Description("https://github.com/gandulf/DsaTab")
                .withAboutSpecial2("Changelog")
                .withAboutSpecial2Description("Hinweise zu den Veränderungen der aktuellen Version")
                .withAboutSpecial3("Helden-Software " + DsaTabApplication.HS_VERSION)
                .withAboutSpecial3Description("http://www.helden-software.de/")
                .withListener(new LibsConfiguration.LibsListener() {
                    @Override
                    public void onIconClicked(View view) {

                    }

                    @Override
                    public boolean onLibraryAuthorClicked(View view, Library library) {
                        return false;
                    }

                    @Override
                    public boolean onLibraryContentClicked(View view, Library library) {
                        if ("ulisses_fan_project".equals(library.getDefinedName())) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(AboutActivity.this);
                            builder.setTitle(R.string.title_credits);
                            builder.setCancelable(true);
                            WebView webView = new WebView(builder.getContext());
                            webView.getSettings().setDefaultTextEncodingName("utf-8");
                            String summary = ResUtil.loadResToString(R.raw.ulisses_license, getResources());
                            webView.loadDataWithBaseURL(null, summary, "text/html", "utf-8", null);
                            builder.setView(webView);
                            builder.setPositiveButton(R.string.label_ok, null);
                            builder.show();
                            return true;
                        }

                        return false;
                    }

                    @Override
                    public boolean onLibraryBottomClicked(View view, Library library) {
                        return false;
                    }

                    @Override
                    public boolean onExtraClicked(View view, Libs.SpecialButton specialButton) {
                        if (specialButton == Libs.SpecialButton.SPECIAL1) {
                            String url = "https://github.com/gandulf/DsaTab";
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(url));
                            startActivity(i);
                            return true;
                        } else if (specialButton == Libs.SpecialButton.SPECIAL2) {
                            ChangeLogDialog.show(AboutActivity.this, true);
                            return true;
                        } else if (specialButton == Libs.SpecialButton.SPECIAL3) {
                            String url = "http://www.helden-software.de/";
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(url));
                            startActivity(i);
                            return true;
                        }
                        return false;
                    }

                    @Override
                    public boolean onIconLongClicked(View view) {
                        return false;
                    }

                    @Override
                    public boolean onLibraryAuthorLongClicked(View view, Library library) {
                        return false;
                    }

                    @Override
                    public boolean onLibraryContentLongClicked(View view, Library library) {
                        return false;
                    }

                    @Override
                    public boolean onLibraryBottomLongClicked(View view, Library library) {
                        return false;
                    }
                })
                .fragment();

        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.content, fragment);
        ft.commit();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
