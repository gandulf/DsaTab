/**
 *  This file is part of DsaTab.
 *
 *  DsaTab is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  DsaTab is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with DsaTab.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.dsatab.activity;

import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

/**
 * @author Ganymede
 * 
 */
public class SearchableActivity extends BaseFragmentActivity {

	public static final String INTENT_TAB_INFO = "tabInfo";

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
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
