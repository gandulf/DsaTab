package com.dsatab.cloud;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.activity.DsaTabPreferenceActivity;
import com.dsatab.data.Hero;
import com.dsatab.data.HeroFileInfo.FileType;
import com.dsatab.util.Debug;
import com.dsatab.util.Util;
import com.dsatab.util.ViewUtils;
import com.dsatab.xml.HeldenXmlParser;
import com.dsatab.xml.Xml;

import org.jdom2.Document;
import org.jdom2.Element;

import java.io.InputStream;
import java.io.OutputStream;

public class HeroSaveTask extends AsyncTask<Void, Void, Boolean> {

    private Exception exception;

    private HeroExchange heroExchange;
    private Activity context;
    private Hero hero;

    public HeroSaveTask(Activity context, Hero hero, HeroExchange heroExchange) {
        this.context = context;
        this.hero = hero;
        this.heroExchange = heroExchange;
    }

    @Override
    protected Boolean doInBackground(Void... paramVarArgs) {

        try {
            InputStream fis = heroExchange.getInputStream(hero.getFileInfo(), FileType.Hero);
            if (fis == null) {
                Debug.warning("Unable to read hero from input stream: " + hero.getFileInfo());
                return false;
            }

            Document dom = null;
            try {
                dom = HeldenXmlParser.readDocument(fis);
            } finally {
                Util.close(fis);
            }

            Element heroElement = dom.getRootElement().getChild(Xml.KEY_HELD);
            HeldenXmlParser.onPreHeroSaved(hero, heroElement);

            OutputStream out = heroExchange.getOutputStream(hero.getFileInfo(), FileType.Hero);
            if (out == null) {
                Debug.warning("Unable to write hero to output stream: " + hero.getFileInfo());
                return false;
            }

            try {
                HeldenXmlParser.writeHero(hero, dom, out);
                hero.onPostHeroSaved();
            } finally {
                Util.close(out);
            }

            OutputStream outConfig = heroExchange.getOutputStream(hero.getFileInfo(), FileType.Config);
            if (outConfig == null) {
                Debug.warning("Unable to write config file for hero: " + hero.getFileInfo());
                return false;
            }

            try {
                outConfig.write(hero.getHeroConfiguration().toJSONObject().toString().getBytes());
            } finally {
                Util.close(outConfig);
            }

            heroExchange.upload(hero.getFileInfo());

            return true;
        } catch (Exception e) {
            Debug.error(e);
            exception = e;
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {

        if (result != null && result) {
            if (!DsaTabApplication.getPreferences().getBoolean(DsaTabPreferenceActivity.KEY_AUTO_SAVE, true)) {
                ViewUtils.snackbar(context, R.string.hero_saved, Snackbar.LENGTH_SHORT, hero.getName());
            }
        } else {

            ViewUtils.snackbar(context, R.string.message_save_hero_failed, Snackbar.LENGTH_LONG);
        }

        super.onPostExecute(result);
    }

    public Exception getException() {
        return exception;
    }

}
