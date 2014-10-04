package com.dsatab.activity;

import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class SearchableActivity extends BaseActivity {

	public static final String INTENT_TAB_INFO = "tabInfo";

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		String query = null;
		// Get the intent, verify the action and get the query
		Intent intent = getIntent();
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			query = intent.getStringExtra(SearchManager.QUERY);
		}

		// TabInfo tabInfo = null;
		// Bundle appData = intent.getBundleExtra(SearchManager.APP_DATA);
		// if (appData != null) {
		// tabInfo = appData.getParcelable(SearchableActivity.INTENT_TAB_INFO);
		// }

		Uri uriUrl = Uri.parse("http://www.wiki-aventurica.de/index.php?search=" + query);
		final Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
		launchBrowser.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(launchBrowser);
		finish();
	}

}
