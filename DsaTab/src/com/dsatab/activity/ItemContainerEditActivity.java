package com.dsatab.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.data.items.ItemContainer;
import com.dsatab.fragment.ItemContainerEditFragment;

public class ItemContainerEditActivity extends BaseFragmentActivity {

	public static final String INTENT_ITEM_CHOOSER_ID = "com.dsatab.data.intent.itemContainerId";

	private ItemContainerEditFragment fragment;

	public static void insert(Activity activity) {
		Intent intent = new Intent(activity, ItemContainerEditActivity.class);
		intent.setAction(Intent.ACTION_INSERT);
		activity.startActivity(intent);
	}

	public static void edit(Activity activity, ItemContainer itemContainer) {
		Intent intent = new Intent(activity, ItemContainerEditActivity.class);
		intent.setAction(Intent.ACTION_EDIT);
		intent.putExtra(INTENT_ITEM_CHOOSER_ID, itemContainer.getId());
		activity.startActivity(intent);
	}

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
		LayoutInflater inflater = LayoutInflater.from(getSupportActionBar().getThemedContext());
		final View customActionBarView = inflater.inflate(R.layout.actionbar_custom_view_done_discard, null);
		customActionBarView.findViewById(R.id.actionbar_done).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ItemContainer itemContainer = fragment.accept();
				if (itemContainer.getId() == ItemContainer.INVALID_ID) {
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

		//

		ItemContainer itemContainer = null;
		Bundle extra = getIntent().getExtras();
		if (extra != null) {
			int containerId = extra.getInt(INTENT_ITEM_CHOOSER_ID, -1);
			if (containerId >= 0) {
				itemContainer = DsaTabApplication.getInstance().getHero().getItemContainer(containerId);
			}
		}
		fragment.setItemContainer(itemContainer);
	}

}
