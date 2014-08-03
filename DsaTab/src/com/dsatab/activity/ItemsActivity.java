package com.dsatab.activity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SlidingPaneLayout;
import android.view.MenuItem;
import android.view.View;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.data.Hero;
import com.dsatab.data.enums.ItemType;
import com.dsatab.data.items.EquippedItem;
import com.dsatab.data.items.Item;
import com.dsatab.data.items.ItemCard;
import com.dsatab.data.items.ItemSpecification;
import com.dsatab.db.DataManager;
import com.dsatab.fragment.item.ItemEditFragment;
import com.dsatab.fragment.item.ItemListFragment;
import com.dsatab.fragment.item.ItemListFragment.OnItemSelectedListener;
import com.dsatab.fragment.item.ItemViewFragment;

public class ItemsActivity extends BaseActivity implements OnItemSelectedListener {

	public static final int ACTION_EDIT = 1014;
	public static final int ACTION_CREATE = 1015;

	public static final String INTENT_EXTRA_HERO_KEY = "heroKey";
	public static final String INTENT_EXTRA_ITEM_ID = "itemId";
	public static final String INTENT_EXTRA_EQUIPPED_ITEM_ID = "equippedItemId";
	private static final String INTENT_EXTRA_ITEM_TYPES = "itemTypes";

	private ItemListFragment itemListFragment;

	private ItemViewFragment itemViewFragment;
	private ItemEditFragment itemEditFragment;

	private SlidingPaneLayout slidingPaneLayout;

	private boolean viewMode;

	public static void view(Context context) {
		Intent intent = new Intent(context, ItemsActivity.class);
		intent.setAction(Intent.ACTION_VIEW);
		context.startActivity(intent);
	}

	public static void view(Context context, Hero hero, ItemCard itemCard) {
		if (itemCard != null && context != null) {

			Item item = itemCard.getItem();

			Intent intent = new Intent(context, ItemsActivity.class);
			intent.setAction(Intent.ACTION_VIEW);
			if (itemCard instanceof EquippedItem) {
				intent.putExtra(INTENT_EXTRA_EQUIPPED_ITEM_ID, ((EquippedItem) itemCard).getId());
			} else {
				intent.putExtra(INTENT_EXTRA_ITEM_ID, item.getId());
			}
			if (hero != null) {
				intent.putExtra(INTENT_EXTRA_HERO_KEY, hero.getFileInfo().getKey());
			}

			context.startActivity(intent);
		}
	}

	public static void pick(Activity context, Collection<ItemType> itemTypes, int requestCode) {
		Intent intent = new Intent(context, ItemsActivity.class);
		intent.setAction(Intent.ACTION_PICK);
		if (itemTypes != null) {
			intent.putExtra(INTENT_EXTRA_ITEM_TYPES, new ArrayList<ItemType>(itemTypes));
		}
		context.startActivityForResult(intent, requestCode);
	}

	public static void insert(Activity context, String heroKey, int requestCode) {

		Intent intent = new Intent(context, ItemsActivity.class);
		intent.setAction(Intent.ACTION_INSERT);
		if (heroKey != null) {
			intent.putExtra(ItemEditFragment.INTENT_EXTRA_HERO_KEY, heroKey);
		}
		context.startActivityForResult(intent, requestCode);

	}

	public static void edit(Activity context, String heroKey, ItemCard itemCard, int requestCode) {
		if (itemCard != null) {
			Item item = itemCard.getItem();
			Intent intent = new Intent(context, ItemsActivity.class);
			intent.setAction(Intent.ACTION_EDIT);
			if (itemCard instanceof EquippedItem) {
				intent.putExtra(ItemEditFragment.INTENT_EXTRA_EQUIPPED_ITEM_ID, ((EquippedItem) itemCard).getId());
			} else {
				intent.putExtra(ItemEditFragment.INTENT_EXTRA_ITEM_ID, item.getId());
			}
			if (heroKey != null) {
				intent.putExtra(ItemEditFragment.INTENT_EXTRA_HERO_KEY, heroKey);
			}
			context.startActivityForResult(intent, requestCode);
		}
	}

	public static void edit(Activity context, Hero hero, ItemCard itemCard, int requestCode) {
		if (hero != null)
			edit(context, hero.getFileInfo().getKey(), itemCard, requestCode);
		else
			edit(context, (String) null, itemCard, requestCode);
	}

	public static void edit(Activity context, String heroKey, UUID itemID, UUID equippedItemId, int requestCode) {
		Intent intent = new Intent(context, ItemsActivity.class);
		intent.setAction(Intent.ACTION_EDIT);
		if (equippedItemId != null) {
			intent.putExtra(ItemEditFragment.INTENT_EXTRA_EQUIPPED_ITEM_ID, equippedItemId);
		}
		if (itemID != null) {
			intent.putExtra(ItemEditFragment.INTENT_EXTRA_ITEM_ID, itemID);
		}
		if (heroKey != null) {
			intent.putExtra(ItemEditFragment.INTENT_EXTRA_HERO_KEY, heroKey);
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

		itemListFragment = (ItemListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_item_chooser);

		slidingPaneLayout = (SlidingPaneLayout) findViewById(R.id.slidepanel);
		// slidingPaneLayout.setParallaxDistance(100);

		slidingPaneLayout.setPanelSlideListener(new SlidingPaneLayout.PanelSlideListener() {

			@Override
			public void onPanelSlide(View arg0, float arg1) {

			}

			@Override
			public void onPanelOpened(View arg0) {
				itemListFragment.setHasOptionsMenu(true);

				if (itemViewFragment != null)
					itemViewFragment.setHasOptionsMenu(false);
				if (itemEditFragment != null)
					itemEditFragment.setHasOptionsMenu(false);

			}

			@Override
			public void onPanelClosed(View arg0) {
				itemListFragment.setHasOptionsMenu(false);

				if (itemViewFragment != null)
					itemViewFragment.setHasOptionsMenu(true);
				if (itemEditFragment != null)
					itemEditFragment.setHasOptionsMenu(true);
			}
		});

		Object itemType = getIntent().getSerializableExtra(INTENT_EXTRA_ITEM_TYPES);
		if (itemType instanceof ItemType) {
			itemListFragment.getItemTypes().add((ItemType) itemType);
		} else if (itemType instanceof Collection) {
			itemListFragment.getItemTypes().addAll((Collection<ItemType>) itemType);
		}

		itemListFragment.setOnItemSelectedListener(this);

		if (Intent.ACTION_PICK.equals(getIntent().getAction())) {
			setTitle(R.string.choose_item);
			getSupportActionBar().setDisplayShowTitleEnabled(true);
		} else {
			getSupportActionBar().setDisplayShowTitleEnabled(false);
		}

		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayUseLogoEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		if (Intent.ACTION_EDIT.equals(getIntent().getAction()) || Intent.ACTION_INSERT.equals(getIntent().getAction()))
			initEditFragment();
		else
			initViewFragment();

		if (getIntent().hasExtra(INTENT_EXTRA_ITEM_ID) || getIntent().hasExtra(INTENT_EXTRA_EQUIPPED_ITEM_ID)) {
			slidingPaneLayout.closePane();
		} else {
			slidingPaneLayout.openPane();
		}

		if (slidingPaneLayout.isSlideable()) {
			itemListFragment.setHasOptionsMenu(slidingPaneLayout.isOpen());
			if (itemViewFragment != null)
				itemViewFragment.setHasOptionsMenu(!slidingPaneLayout.isOpen());
			if (itemEditFragment != null)
				itemEditFragment.setHasOptionsMenu(!slidingPaneLayout.isOpen());
		}
	}

	protected void initViewFragment() {

		Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.details);
		if (currentFragment instanceof ItemViewFragment) {
			itemViewFragment = (ItemViewFragment) currentFragment;
		} else {
			itemViewFragment = new ItemViewFragment();

			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			if (currentFragment == null)
				ft.add(R.id.details, itemViewFragment);
			else
				ft.replace(R.id.details, itemViewFragment);

			ft.commit();
		}

		Bundle extra = getIntent().getExtras();

		Item item = null;
		ItemSpecification itemSpecification = null;

		Hero hero = DsaTabApplication.getInstance().getHero();

		if (extra != null) {
			UUID itemId = (UUID) extra.getSerializable(INTENT_EXTRA_ITEM_ID);

			UUID equippedItemId = (UUID) extra.getSerializable(INTENT_EXTRA_EQUIPPED_ITEM_ID);
			if (equippedItemId != null && hero != null) {
				EquippedItem equippedItem = hero.getEquippedItem(equippedItemId);
				if (equippedItem != null) {
					item = equippedItem.getItem();
					itemSpecification = equippedItem.getItemSpecification();
				}
			}

			if (item == null && itemId != null) {
				if (hero != null) {
					item = hero.getItem(itemId);
				}
				if (item == null) {
					item = DataManager.getItemById(itemId);
				}
				if (item != null) {
					itemSpecification = item.getSpecifications().get(0);
				}
			}
		}

		itemViewFragment.setItem(item, itemSpecification);

		viewMode = true;
	}

	public void viewItem(Item item, ItemSpecification itemSpecification) {
		if (!viewMode) {

			if (slidingPaneLayout.isOpen()) {
				slidingPaneLayout.closePane();
			}

			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

			if (itemEditFragment != null)
				ft.hide(itemEditFragment);

			if (itemViewFragment == null) {
				itemViewFragment = new ItemViewFragment();
				ft.add(R.id.details, itemViewFragment);
			} else {
				ft.show(itemViewFragment);
			}
			ft.commit();
		}

		itemViewFragment.setItem(item, itemSpecification);

		viewMode = true;
	}

	public void editItem(Item item, ItemSpecification itemSpecification) {
		if (viewMode) {

			if (slidingPaneLayout.isOpen()) {
				slidingPaneLayout.closePane();
			}

			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

			if (itemViewFragment != null)
				ft.hide(itemViewFragment);

			if (itemEditFragment == null) {
				itemEditFragment = new ItemEditFragment();
				ft.add(R.id.details, itemEditFragment);
			} else {
				ft.show(itemEditFragment);
			}
			ft.commit();
		}

		itemEditFragment.setItem(item, itemSpecification);

		viewMode = false;
	}

	protected void initEditFragment() {

		Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.details);
		if (currentFragment instanceof ItemEditFragment) {
			itemEditFragment = (ItemEditFragment) currentFragment;
		} else {
			itemEditFragment = new ItemEditFragment();

			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			if (currentFragment == null)
				ft.add(R.id.details, itemEditFragment);
			else
				ft.replace(R.id.details, itemEditFragment);

			ft.commit();
		}

		Bundle extra = getIntent().getExtras();

		Item item = null;
		ItemSpecification itemSpecification = null;

		Hero hero = DsaTabApplication.getInstance().getHero();

		if (extra != null) {
			UUID itemId = (UUID) extra.getSerializable(INTENT_EXTRA_ITEM_ID);

			UUID equippedItemId = (UUID) extra.getSerializable(INTENT_EXTRA_EQUIPPED_ITEM_ID);
			if (equippedItemId != null && hero != null) {
				EquippedItem equippedItem = hero.getEquippedItem(equippedItemId);
				if (equippedItem != null) {
					item = equippedItem.getItem();
					itemSpecification = equippedItem.getItemSpecification();
				}
			}

			if (item == null && itemId != null) {
				if (hero != null) {
					item = hero.getItem(itemId);
				}
				if (item == null) {
					item = DataManager.getItemById(itemId);
				}
				if (item != null) {
					itemSpecification = item.getSpecifications().get(0);
				}
			}
		}

		itemEditFragment.setItem(item, itemSpecification);

		viewMode = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.actionbarsherlock.app.SherlockFragmentActivity#onOptionsItemSelected
	 * (com.actionbarsherlock.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.option_search:
			slidingPaneLayout.openPane();
			return false;
		case R.id.option_edit:
			editItem(itemViewFragment.getItem(), itemViewFragment.getItemSpecification());
			return true;
		case R.id.option_ok:
			itemEditFragment.accept();
			itemListFragment.refresh();
			viewItem(itemEditFragment.getItem(), itemEditFragment.getItemSpecification());
			return true;
		case R.id.option_cancel:
			itemEditFragment.cancel();
			viewItem(itemEditFragment.getItem(), itemEditFragment.getItemSpecification());
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onItemSelected(Item item) {
		if (item != null) {
			if (Intent.ACTION_PICK.equals(getIntent().getAction())) {
				Intent data = getIntent();
				data.putExtra(INTENT_EXTRA_ITEM_ID, item.getId());

				setResult(Activity.RESULT_OK, data);
				finish();
				return true;
			} else {
				viewItem(item, null);
				slidingPaneLayout.closePane();
				return false;
			}
		}
		return false;
	}

}
