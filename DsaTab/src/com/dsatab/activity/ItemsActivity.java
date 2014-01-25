package com.dsatab.activity;

import java.util.ArrayList;
import java.util.Collection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SlidingPaneLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.actionbarsherlock.view.MenuItem;
import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.data.enums.ItemType;
import com.dsatab.data.items.Item;
import com.dsatab.fragment.ItemChooserFragment;
import com.dsatab.fragment.ItemViewFragment;

public class ItemsActivity extends BaseFragmentActivity implements OnItemClickListener {

	private static final String DATA_INTENT_ITEM_TYPES = "itemTypes";
	public static final String DATA_INTENT_ITEM_ID = "itemId";

	private ItemChooserFragment itemsFragment;

	private ItemViewFragment itemViewFragment;

	private SlidingPaneLayout slidingPaneLayout;

	public static void view(Context context) {
		Intent intent = new Intent(context, ItemsActivity.class);
		intent.setAction(Intent.ACTION_VIEW);
		context.startActivity(intent);
	}

	public static void pick(Activity context, Collection<ItemType> itemTypes, int requestCode) {
		Intent intent = new Intent(context, ItemsActivity.class);
		intent.setAction(Intent.ACTION_PICK);
		if (itemTypes != null) {
			intent.putExtra(DATA_INTENT_ITEM_TYPES, new ArrayList<ItemType>(itemTypes));
		}
		context.startActivityForResult(intent, requestCode);
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
		setContentView(R.layout.main_items);

		itemsFragment = (ItemChooserFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_item_chooser);
		itemViewFragment = (ItemViewFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_item);

		slidingPaneLayout = (SlidingPaneLayout) findViewById(R.id.slidepanel);
		slidingPaneLayout.setParallaxDistance(100);
		slidingPaneLayout.openPane();

		if (slidingPaneLayout.isSlideable()) {
			itemsFragment.setHasOptionsMenu(false);
			itemViewFragment.setHasOptionsMenu(true);
		}

		slidingPaneLayout.setPanelSlideListener(new SlidingPaneLayout.PanelSlideListener() {

			@Override
			public void onPanelSlide(View arg0, float arg1) {

			}

			@Override
			public void onPanelOpened(View arg0) {
				itemsFragment.setHasOptionsMenu(true);
				itemViewFragment.setHasOptionsMenu(false);

			}

			@Override
			public void onPanelClosed(View arg0) {
				itemsFragment.setHasOptionsMenu(false);
				itemViewFragment.setHasOptionsMenu(true);
			}
		});

		Object itemType = getIntent().getSerializableExtra(DATA_INTENT_ITEM_TYPES);
		if (itemType instanceof ItemType) {
			itemsFragment.getItemTypes().add((ItemType) itemType);
		} else if (itemType instanceof Collection) {
			itemsFragment.getItemTypes().addAll((Collection<ItemType>) itemType);
		}

		itemsFragment.setOnItemClickListener(this);

		if (getIntent().getAction() == Intent.ACTION_PICK) {
			setTitle("Gegenstand auswählen");
			getSupportActionBar().setDisplayShowTitleEnabled(true);
		} else {
			getSupportActionBar().setDisplayShowTitleEnabled(false);
		}

		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayUseLogoEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.actionbarsherlock.app.SherlockFragmentActivity#onOptionsItemSelected
	 * (com.actionbarsherlock.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> list, View view, int position, long id) {
		Item item = itemsFragment.getItem(position);
		if (item != null) {

			if (getIntent().getAction() == Intent.ACTION_PICK) {
				Intent data = getIntent();
				data.putExtra(DATA_INTENT_ITEM_ID, item.getId());

				setResult(Activity.RESULT_OK, data);
				finish();
			} else {
				itemViewFragment.setItem(item, null);
				slidingPaneLayout.closePane();
			}
		}
	}

}
