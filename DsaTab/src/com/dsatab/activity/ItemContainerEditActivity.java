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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.ActionBar;
import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.data.items.ItemContainer;
import com.dsatab.fragment.ItemContainerEditFragment;

public class ItemContainerEditActivity extends BaseFragmentActivity {

	private ItemContainerEditFragment fragment;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(DsaTabApplication.getInstance().getCustomTheme());
		applyPreferencesToTheme();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_item_container_edit);

		fragment = (ItemContainerEditFragment) getSupportFragmentManager().findFragmentById(
				R.id.fragment_item_container_edit);

		// Inflate a "Done/Discard" custom action bar view.
		LayoutInflater inflater = LayoutInflater.from(this);
		final View customActionBarView = inflater.inflate(R.layout.actionbar_custom_view_done_discard, null);
		customActionBarView.findViewById(R.id.actionbar_done).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ItemContainer itemContainer = fragment.accept();
				if (itemContainer.getId() == 0) {
					DsaTabApplication.getInstance().getHero().addItemContainer(itemContainer);
				} else {
					DsaTabApplication.getInstance().getHero().fireItemContainerChangedEvent(itemContainer);
				}
				setResult(RESULT_OK);
				finish();
			}
		});
		customActionBarView.findViewById(R.id.actionbar_discard).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				fragment.cancel();
				setResult(RESULT_CANCELED);
				finish();
			}
		});

		// Show the custom action bar view and hide the normal Home icon and
		// title.
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM, ActionBar.DISPLAY_SHOW_CUSTOM
				| ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
		actionBar.setCustomView(customActionBarView, new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));
	}

}
