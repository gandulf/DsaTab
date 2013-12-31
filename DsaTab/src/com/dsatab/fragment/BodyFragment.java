package com.dsatab.fragment;

import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.activity.DsaTabActivity;
import com.dsatab.activity.DsaTabPreferenceActivity;
import com.dsatab.activity.ItemViewActivity;
import com.dsatab.data.ArmorAttribute;
import com.dsatab.data.Attribute;
import com.dsatab.data.Hero;
import com.dsatab.data.Value;
import com.dsatab.data.WoundAttribute;
import com.dsatab.data.adapter.EquippedItemAdapter;
import com.dsatab.data.items.EquippedItem;
import com.dsatab.data.items.Item;
import com.dsatab.data.items.ItemContainer;
import com.dsatab.util.Util;
import com.dsatab.view.BodyLayout;
import com.dsatab.view.listener.HeroInventoryChangedListener;

public class BodyFragment extends BaseFragment implements OnClickListener, HeroInventoryChangedListener {

	private BodyLayout bodyLayout;

	TextView totalRs, totalBe;

	ImageView bodyBackground;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.fragment.BaseFragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.actionbarsherlock.app.SherlockFragment#onCreateOptionsMenu(com. actionbarsherlock.view.Menu,
	 * com.actionbarsherlock.view.MenuInflater)
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		if (menu.findItem(R.id.option_set) == null) {
			inflater.inflate(R.menu.body_menu, menu);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup,
	 * android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = configureContainerView(inflater.inflate(R.layout.sheet_body, container, false));

		bodyLayout = (BodyLayout) root.findViewById(R.id.body_layout);

		bodyBackground = (ImageView) root.findViewById(R.id.body_background);

		totalRs = (TextView) root.findViewById(R.id.body_total_rs);
		totalBe = (TextView) root.findViewById(R.id.body_total_be);

		return root;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		bodyLayout.setOnArmorClickListener(this);
		bodyLayout.setOnArmorLongClickListener(getEditListener());
		bodyLayout.setOnWoundClickListener(this);

		updateBackground();

		super.onActivityCreated(savedInstanceState);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.fragment.BaseFragment#onSharedPreferenceChanged(android.content .SharedPreferences,
	 * java.lang.String)
	 */
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		super.onSharedPreferenceChanged(sharedPreferences, key);

		if (DsaTabPreferenceActivity.KEY_STYLE_BG_WOUNDS_PATH.equals(key)) {
			updateBackground();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.fragment.BaseFragment#onDestroyView()
	 */
	@Override
	public void onDestroyView() {

		bodyLayout.setOnArmorClickListener(null);
		bodyLayout.setOnArmorLongClickListener(null);
		bodyLayout.setOnWoundClickListener(null);

		super.onDestroyView();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.activity.BaseMenuActivity#onHeroLoaded(com.dsatab.data.Hero)
	 */
	@Override
	public void onHeroLoaded(Hero hero) {
		bodyLayout.setWoundAttributes(hero.getWounds());
		bodyLayout.setArmorAttributes(hero.getArmorAttributes());
		updateView();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.activity.BaseMainActivity#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {

		// wounds
		if (v.getTag() instanceof WoundAttribute) {

			ImageView iv = (ImageButton) v;
			WoundAttribute attribute = (WoundAttribute) v.getTag();

			if (iv.isSelected()) {
				attribute.setValue(attribute.getValue() - 1);
			} else {
				attribute.setValue(attribute.getValue() + 1);
			}
			iv.setSelected(!iv.isSelected());
			iv.setBackgroundResource(R.drawable.icon_wound_btn);
		}
		// armor
		else if (v.getTag() instanceof ArmorAttribute) {
			ArmorAttribute value = (ArmorAttribute) v.getTag();

			final List<EquippedItem> equippedItems = getHero().getArmor(value.getPosition());

			if (equippedItems.isEmpty()) {
				Toast.makeText(getActivity(), "Keine Einträge gefunden", Toast.LENGTH_SHORT).show();
			} else if (equippedItems.size() == 1) {
				ItemViewActivity.view(getActivity(), getHero(), equippedItems.get(0));
			} else {
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle("Rüstung");
				final EquippedItemAdapter adapter = new EquippedItemAdapter(getActivity(), equippedItems);
				builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						ItemViewActivity.view(getActivity(), getHero(), adapter.getItem(which));
					}
				});
				builder.show();
			}

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == DsaTabActivity.ACTION_PREFERENCES) {
			updateView();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.fragment.BaseFragment#onActiveSetChanged(int, int)
	 */
	@Override
	public void onActiveSetChanged(int newSet, int oldSet) {
		updateView();
		bodyLayout.setArmorAttributes(getHero().getArmorAttributes());
		if (getActivity() != null) {
			getActivity().supportInvalidateOptionsMenu();
		}
	}

	private void updateView() {
		totalRs.setText(Util.toString(getHero().getArmorRs()));
		totalBe.setText(Util.toString(getHero().getArmorBe()));
	}

	private void updateBackground() {
		SharedPreferences preferences = DsaTabApplication.getPreferences();
		if (preferences.contains(DsaTabPreferenceActivity.KEY_STYLE_BG_WOUNDS_PATH)) {
			String filePath = preferences.getString(DsaTabPreferenceActivity.KEY_STYLE_BG_WOUNDS_PATH, null);
			bodyBackground.setImageDrawable(Drawable.createFromPath(filePath));
		} else {
			bodyBackground.setImageResource(Util.getThemeResourceId(getActivity(), R.attr.imgCharacter));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.fragment.BaseFragment#onValueChanged(com.dsatab.data.Value)
	 */
	@Override
	public void onValueChanged(Value value) {
		if (value instanceof ArmorAttribute) {
			ArmorAttribute rs = (ArmorAttribute) value;
			bodyLayout.setArmorAttribute(rs);
		} else if (value instanceof Attribute) {
			Attribute attribute = (Attribute) value;

			switch (attribute.getType()) {
			case Behinderung:
				totalBe.setText(Util.toString(getHero().getArmorBe()));
				break;
			default:
				// do nothing
				break;
			}
		}
		super.onValueChanged(value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.fragment.BaseFragment#onItemEquipped(com.dsatab.data.items .EquippedItem)
	 */
	@Override
	public void onItemEquipped(EquippedItem item) {
		if (item.isArmor()) {
			totalRs.setText(Util.toString(getHero().getArmorRs()));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.fragment.BaseFragment#onItemUnequipped(com.dsatab.data.items .EquippedItem)
	 */
	@Override
	public void onItemUnequipped(EquippedItem item) {
		if (item.isArmor()) {
			totalRs.setText(Util.toString(getHero().getArmorRs()));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.view.listener.HeroInventoryChangedListener#onItemAdded(com .dsatab.data.items.Item)
	 */
	@Override
	public void onItemAdded(Item item) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.view.listener.HeroInventoryChangedListener#onItemRemoved(com .dsatab.data.items.Item)
	 */
	@Override
	public void onItemRemoved(Item item) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.view.listener.HeroInventoryChangedListener#onItemChanged(com .dsatab.data.items.Item)
	 */
	@Override
	public void onItemChanged(Item item) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.view.listener.HeroInventoryChangedListener#onItemChanged(com .dsatab.data.items.EquippedItem)
	 */
	@Override
	public void onItemChanged(EquippedItem item) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.view.listener.HeroInventoryChangedListener#onItemContainerAdded
	 * (com.dsatab.data.items.ItemContainer)
	 */
	@Override
	public void onItemContainerAdded(ItemContainer itemContainer) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.view.listener.HeroInventoryChangedListener#onItemContainerRemoved
	 * (com.dsatab.data.items.ItemContainer)
	 */
	@Override
	public void onItemContainerRemoved(ItemContainer itemContainer) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.view.listener.HeroInventoryChangedListener#onItemContainerChanged
	 * (com.dsatab.data.items.ItemContainer)
	 */
	@Override
	public void onItemContainerChanged(ItemContainer itemContainer) {
		// TODO Auto-generated method stub

	}

}
