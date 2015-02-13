package com.dsatab.cloud;

import java.io.InputStream;
import java.io.OutputStream;

import org.jdom2.Document;
import org.jdom2.Element;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.activity.DsaTabPreferenceActivity;
import com.dsatab.data.Hero;
import com.dsatab.data.HeroFileInfo.FileType;
import com.dsatab.util.Debug;
import com.dsatab.util.Util;
import com.dsatab.xml.HeldenXmlParser;
import com.dsatab.xml.Xml;

public class HeroSaveTask extends AsyncTask<Void, Void, Boolean> {

	private Exception exception;

	private Activity context;
	private Hero hero;

	public HeroSaveTask(Activity context, Hero hero) {
		this.context = context;
		this.hero = hero;
	}

	@Override
	protected Boolean doInBackground(Void... paramVarArgs) {

		try {
			HeroExchange exchange = DsaTabApplication.getInstance().getExchange();

			InputStream fis = exchange.getInputStream(hero.getFileInfo(), FileType.Hero);
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

			OutputStream out = exchange.getOutputStream(hero.getFileInfo(), FileType.Hero);
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

			OutputStream outConfig = exchange.getOutputStream(hero.getFileInfo(), FileType.Config);
			if (outConfig == null) {
				Debug.warning("Unable to write config file for hero: " + hero.getFileInfo());
				return false;
			}

			try {
				outConfig.write(hero.getHeroConfiguration().toJSONObject().toString().getBytes());
			} finally {
				Util.close(outConfig);
			}

			exchange.upload(hero.getFileInfo());

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
				if (context != null && context.getWindow() != null)
					Toast.makeText(context, context.getString(R.string.hero_saved, hero.getName()), Toast.LENGTH_SHORT)
							.show();
			}
		} else {
			if (context != null && context.getWindow() != null)
				Toast.makeText(context, R.string.message_save_hero_failed, Toast.LENGTH_LONG).show();

		}

		super.onPostExecute(result);
	}

	public Exception getException() {
		return exception;
	}

}
