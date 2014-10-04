package com.dsatab.fragment;

import java.lang.ref.WeakReference;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.activity.DsaTabActivity;
import com.dsatab.activity.DsaTabPreferenceActivity;
import com.dsatab.activity.ItemsActivity;
import com.dsatab.data.ArmorAttribute;
import com.dsatab.data.Attribute;
import com.dsatab.data.Hero;
import com.dsatab.data.Value;
import com.dsatab.data.WoundAttribute;
import com.dsatab.data.adapter.EquippedItemAdapter;
import com.dsatab.data.items.EquippedItem;
import com.dsatab.data.items.Item;
import com.dsatab.data.items.ItemContainer;
import com.dsatab.fragment.dialog.InlineEditDialog;
import com.dsatab.util.Util;
import com.dsatab.view.BodyLayout;
import com.dsatab.view.dialog.TakeHitDialog;
import com.dsatab.view.listener.HeroInventoryChangedListener;

public class BodyFragment extends BaseFragment implements OnClickListener, OnLongClickListener,
		HeroInventoryChangedListener, OnCheckedChangeListener {

	private BodyLayout bodyLayout;

	private TextView totalRs, totalBe;

	private ImageView bodyBackground;

	protected ActionMode mMode;

	protected ActionMode.Callback mCallback;

	protected ArmorAttribute selectedArmorAttribute;

	private static final class BodyActionMode implements ActionMode.Callback {

		private WeakReference<BodyFragment> fragment;

		public BodyActionMode(BodyFragment fragment) {
			this.fragment = new WeakReference<BodyFragment>(fragment);
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			BodyFragment bodyFragment = fragment.get();
			if (bodyFragment == null || bodyFragment.getActivity() == null)
				return false;

			if (bodyFragment.getHero() != null) {
				mode.getMenuInflater().inflate(R.menu.body_popupmenu, menu);
				mode.setTitle("Wunden");
				if (bodyFragment.selectedArmorAttribute != null) {
					mode.setSubtitle(bodyFragment.selectedArmorAttribute.getName());
				}
			}
			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			BodyFragment bodyFragment = fragment.get();
			if (bodyFragment == null || bodyFragment.getActivity() == null)
				return false;

			if (bodyFragment.selectedArmorAttribute != null) {
				mode.setSubtitle(bodyFragment.selectedArmorAttribute.getName());
			} else {
				mode.setSubtitle(null);
			}
			return false;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem menuItem) {
			final BodyFragment bodyFragment = fragment.get();
			if (bodyFragment == null || bodyFragment.getActivity() == null)
				return false;

			switch (menuItem.getItemId()) {
			case R.id.option_list_items:
				if (bodyFragment.selectedArmorAttribute != null) {
					final List<EquippedItem> equippedItems = bodyFragment.getHero().getArmor(
							bodyFragment.selectedArmorAttribute.getPosition());

					if (equippedItems.isEmpty()) {
						Toast.makeText(bodyFragment.getActivity(), "Keine Einträge gefunden", Toast.LENGTH_SHORT)
								.show();
					} else if (equippedItems.size() == 1) {
						ItemsActivity.view(bodyFragment.getActivity(), bodyFragment.getHero(), equippedItems.get(0));
					} else {
						AlertDialog.Builder builder = new AlertDialog.Builder(bodyFragment.getActivity());
						builder.setTitle("Rüstung");
						final EquippedItemAdapter adapter = new EquippedItemAdapter(builder.getContext(), equippedItems);
						builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								ItemsActivity.view(bodyFragment.getActivity(), bodyFragment.getHero(),
										adapter.getItem(which));
							}
						});
						builder.show().setCanceledOnTouchOutside(true);
					}
				}
				mode.finish();
				return true;
			case R.id.option_take_hit:
				TakeHitDialog takeHitDialog = new TakeHitDialog(bodyFragment.getActivity(), bodyFragment.getBeing(),
						bodyFragment.selectedArmorAttribute.getPosition());
				takeHitDialog.show();
				mode.finish();
				return true;
			case R.id.option_edit:
				InlineEditDialog.show(bodyFragment, bodyFragment.selectedArmorAttribute, 0);
				mode.finish();
				return true;
			}
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode actionMode) {
			BodyFragment bodyFragment = fragment.get();
			if (bodyFragment != null && bodyFragment.getActivity() != null)
				bodyFragment.setSelectedArmorAttribute(null);

			bodyFragment.mMode = null;

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.fragment.BaseFragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		mCallback = new BodyActionMode(this);
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
			inflater.inflate(R.menu.menuitem_set, menu);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = configureContainerView(inflater.inflate(R.layout.sheet_body, container, false));

		bodyLayout = (BodyLayout) root.findViewById(R.id.body_layout);

		bodyBackground = (ImageView) root.findViewById(R.id.body_background);
		bodyBackground.setOnLongClickListener(this);

		totalRs = (TextView) root.findViewById(R.id.body_total_rs);
		totalBe = (TextView) root.findViewById(R.id.body_total_be);

		return root;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onActivityCreated(android.os.Bundle)
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

	@Override
	public boolean onLongClick(View v) {
		switch (v.getId()) {
		case R.id.body_background:
			Util.pickImage(this, DsaTabPreferenceActivity.ACTION_PICK_BG_WOUNDS_PATH);
			return true;
		}
		return false;
	}

	@Override
	public void onCheckedChanged(CompoundButton v, boolean checked) {
		// wounds
		if (v.getTag() instanceof WoundAttribute) {
			WoundAttribute attribute = (WoundAttribute) v.getTag();

			if (checked) {
				attribute.addValue(1);
			} else {
				attribute.addValue(-1);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.activity.BaseMainActivity#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {

		// armor
		if (v.getTag() instanceof ArmorAttribute) {

			// on click on already selected armorattribute uncheck it
			if (selectedArmorAttribute == v.getTag() && mMode != null) {
				mMode.finish();
				setSelectedArmorAttribute(null);
			} else {
				setSelectedArmorAttribute((ArmorAttribute) v.getTag());

				if (mMode == null) {
					mMode = getActionBarActivity().startActionMode(mCallback);
				}
				mMode.invalidate();

			}

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == DsaTabActivity.ACTION_PREFERENCES) {
			updateView();
		}

		if (resultCode == Activity.RESULT_OK) {

			if (requestCode == DsaTabPreferenceActivity.ACTION_PICK_BG_WOUNDS_PATH) {
				Uri uri = Util.retrieveBitmapUri(getActivity(), data);
				if (uri != null) {
					SharedPreferences preferences = DsaTabApplication.getPreferences();
					Editor edit = preferences.edit();
					edit.putString(DsaTabPreferenceActivity.KEY_STYLE_BG_WOUNDS_PATH, uri.toString());
					edit.commit();

					Toast.makeText(getActivity(), "Hintergrundbild wurde verändert.", Toast.LENGTH_SHORT).show();
					updateBackground();
				}
			}
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
			getActivity().invalidateOptionsMenu();
		}
	}

	protected void setSelectedArmorAttribute(ArmorAttribute selectedArmorAttribute) {
		this.selectedArmorAttribute = selectedArmorAttribute;

		bodyLayout.clearArmorAttributeChecked();
		if (selectedArmorAttribute != null) {
			bodyLayout.setArmorAttributeChecked(selectedArmorAttribute.getPosition(), true);
		}
	}

	private void updateView() {
		totalRs.setText(Util.toString(getHero().getArmorRs()));
		totalBe.setText(Util.toString(getHero().getArmorBe()));
	}

	private void updateBackground() {
		SharedPreferences preferences = DsaTabApplication.getPreferences();
		if (preferences.contains(DsaTabPreferenceActivity.KEY_STYLE_BG_WOUNDS_PATH)) {
			Uri uri = Uri.parse(preferences.getString(DsaTabPreferenceActivity.KEY_STYLE_BG_WOUNDS_PATH, null));
			bodyBackground.setImageURI(uri);
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
		} else if (value instanceof WoundAttribute) {
			WoundAttribute ws = (WoundAttribute) value;
			bodyLayout.setWoundAttribute(ws);
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

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.view.listener.HeroInventoryChangedListener#onItemContainerRemoved
	 * (com.dsatab.data.items.ItemContainer)
	 */
	@Override
	public void onItemContainerRemoved(ItemContainer itemContainer) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.view.listener.HeroInventoryChangedListener#onItemContainerChanged
	 * (com.dsatab.data.items.ItemContainer)
	 */
	@Override
	public void onItemContainerChanged(ItemContainer itemContainer) {

	}

}
