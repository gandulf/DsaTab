package com.dsatab.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.data.Hero;
import com.dsatab.data.enums.ItemType;
import com.dsatab.data.items.Item;
import com.dsatab.data.items.ItemSpecification;
import com.dsatab.fragment.item.ItemListFragment;
import com.dsatab.fragment.item.ItemListFragment.OnItemSelectedListener;
import com.dsatab.fragment.item.ItemViewFragment;

import java.util.Collection;

public class ItemsActivity extends BaseItemActivity implements OnItemSelectedListener {

    private ItemListFragment itemListFragment;

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_items);

        itemListFragment = (ItemListFragment) getFragmentManager().findFragmentById(R.id.fragment_item_list);

        Object itemType = getIntent().getSerializableExtra(INTENT_EXTRA_ITEM_TYPES);
        if (itemType instanceof ItemType) {
            itemListFragment.setItemType((ItemType) itemType);
        } else if (itemType instanceof Collection) {
            itemListFragment.setItemTypes((Collection<ItemType>) itemType);
        }
        itemListFragment.setOnItemSelectedListener(this);

        if (Intent.ACTION_PICK.equals(getIntent().getAction())) {
            setToolbarTitle(R.string.choose_item);
        }

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        itemViewFragment =(ItemViewFragment) getFragmentManager().findFragmentById(R.id.fragment_item_view);

    }

    public void viewItem(Item item, ItemSpecification itemSpecification) {
        if (itemViewFragment !=null) {
            itemViewFragment.setItem(item, itemSpecification);
        } else {
            Hero hero = DsaTabApplication.getInstance().getHero();
            BaseItemActivity.view(this,hero,item);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (itemListFragment != null) {
            itemListFragment.onActivityResult(requestCode, resultCode, data);
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
                return false;
            }
        }
        return false;
    }

}
