package com.dsatab.activity;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import com.dsatab.R;
import com.dsatab.data.Hero;
import com.dsatab.data.enums.ItemType;
import com.dsatab.data.items.EquippedItem;
import com.dsatab.data.items.Item;
import com.dsatab.data.items.ItemCard;
import com.dsatab.fragment.item.ItemEditFragment;
import com.dsatab.fragment.item.ItemViewFragment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public abstract class BaseItemActivity extends BaseActivity {

	public static final int ACTION_EDIT = 1014;
	public static final int ACTION_CREATE = 1015;

	public static final String INTENT_EXTRA_HERO_KEY = "heroKey";
	public static final String INTENT_EXTRA_ITEM_ID = "itemId";
	public static final String INTENT_EXTRA_EQUIPPED_ITEM_ID = "equippedItemId";
	public static final String INTENT_EXTRA_ITEM_TYPES = "itemTypes";

	protected ItemViewFragment itemViewFragment;

    /**
     * Default view actions lists all items
     * @param context
     */
	public static void view(Context context) {
		Intent intent = new Intent(context, ItemsActivity.class);
		context.startActivity(intent);
	}

	public static void view(Context context, Hero hero, ItemCard itemCard) {
		if (itemCard != null && context != null) {

			Item item = itemCard.getItem();

			Intent intent = new Intent(context, BaseEditActivity.class);
            intent.putExtra(BaseEditActivity.EDIT_FRAGMENT_CLASS, ItemViewFragment.class);
            intent.putExtra(BaseEditActivity.EDIT_TITLE,item.getTitle());
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

	public static void pick(Fragment fragment, Collection<ItemType> itemTypes, int requestCode) {
		Intent intent = new Intent(fragment.getActivity(), ItemsActivity.class);
		intent.setAction(Intent.ACTION_PICK);
		if (itemTypes != null) {
			intent.putExtra(INTENT_EXTRA_ITEM_TYPES, new ArrayList<ItemType>(itemTypes));
		}
		fragment.startActivityForResult(intent, requestCode);
	}

	public static void insert(Activity context, String heroKey, int requestCode) {

        Intent intent = new Intent(context, BaseEditActivity.class);
        intent.putExtra(BaseEditActivity.EDIT_FRAGMENT_CLASS, ItemEditFragment.class);
        intent.putExtra(BaseEditActivity.EDIT_TITLE,context.getString(R.string.label_create));
		intent.setAction(Intent.ACTION_INSERT);
		if (heroKey != null) {
			intent.putExtra(ItemEditFragment.INTENT_EXTRA_HERO_KEY, heroKey);
		}
		context.startActivityForResult(intent, requestCode);

	}

	public static void edit(Fragment fragment, String heroKey, ItemCard itemCard, int requestCode) {
		if (itemCard != null) {
			Item item = itemCard.getItem();
            Intent intent = new Intent(fragment.getActivity(), BaseEditActivity.class);
            intent.putExtra(BaseEditActivity.EDIT_FRAGMENT_CLASS, ItemEditFragment.class);
            intent.putExtra(BaseEditActivity.EDIT_TITLE,fragment.getString(R.string.label_edit));
			intent.setAction(Intent.ACTION_EDIT);
			if (itemCard instanceof EquippedItem) {
				intent.putExtra(ItemEditFragment.INTENT_EXTRA_EQUIPPED_ITEM_ID, ((EquippedItem) itemCard).getId());
			} else {
				intent.putExtra(ItemEditFragment.INTENT_EXTRA_ITEM_ID, item.getId());
			}
			if (heroKey != null) {
				intent.putExtra(ItemEditFragment.INTENT_EXTRA_HERO_KEY, heroKey);
			}
			fragment.startActivityForResult(intent, requestCode);
		}
	}

	public static void edit(Fragment context, Hero hero, ItemCard itemCard, int requestCode) {
		if (hero != null)
			edit(context, hero.getFileInfo().getKey(), itemCard, requestCode);
		else
			edit(context, (String) null, itemCard, requestCode);
	}

	public static void edit(Fragment fragment, String heroKey, UUID itemID, UUID equippedItemId, int requestCode) {
        Intent intent = new Intent(fragment.getActivity(), BaseEditActivity.class);
        intent.putExtra(BaseEditActivity.EDIT_FRAGMENT_CLASS, ItemEditFragment.class);
        intent.putExtra(BaseEditActivity.EDIT_TITLE,fragment.getString(R.string.label_edit));
		intent.setAction(Intent.ACTION_EDIT);
        intent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
		if (equippedItemId != null) {
			intent.putExtra(ItemEditFragment.INTENT_EXTRA_EQUIPPED_ITEM_ID, equippedItemId);
		}
		if (itemID != null) {
			intent.putExtra(ItemEditFragment.INTENT_EXTRA_ITEM_ID, itemID);
		}
		if (heroKey != null) {
			intent.putExtra(ItemEditFragment.INTENT_EXTRA_HERO_KEY, heroKey);
		}
		fragment.startActivityForResult(intent, requestCode);
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
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (itemViewFragment != null)
			itemViewFragment.onActivityResult(requestCode, resultCode, data);

	}

}
