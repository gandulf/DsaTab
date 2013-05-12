package com.dsatab.fragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.ActionMode.Callback;
import com.actionbarsherlock.view.Menu;
import com.commonsware.cwac.merge.MergeAdapter;
import com.commonsware.cwac.sacklist.SackOfViewsAdapter;
import com.dsatab.R;
import com.dsatab.activity.DsaTabActivity;
import com.dsatab.activity.ItemEditActivity;
import com.dsatab.activity.ItemViewActivity;
import com.dsatab.activity.ModificatorEditActivity;
import com.dsatab.common.StyleableSpannableStringBuilder;
import com.dsatab.data.Attribute;
import com.dsatab.data.CombatTalent;
import com.dsatab.data.CustomModificator;
import com.dsatab.data.Hero;
import com.dsatab.data.Value;
import com.dsatab.data.adapter.FightEquippedItemAdapter;
import com.dsatab.data.adapter.FightModificatorAdapter;
import com.dsatab.data.adapter.ValueWheelAdapter;
import com.dsatab.data.enums.AttributeType;
import com.dsatab.data.enums.FeatureType;
import com.dsatab.data.enums.TalentType;
import com.dsatab.data.items.DistanceWeapon;
import com.dsatab.data.items.EquippedItem;
import com.dsatab.data.items.Hand;
import com.dsatab.data.items.Item;
import com.dsatab.data.items.ItemContainer;
import com.dsatab.data.items.Shield;
import com.dsatab.data.items.Weapon;
import com.dsatab.data.modifier.AbstractModificator;
import com.dsatab.data.modifier.Modificator;
import com.dsatab.util.Debug;
import com.dsatab.util.Util;
import com.dsatab.view.ArcheryChooserDialog;
import com.dsatab.view.EquippedItemChooserDialog;
import com.dsatab.view.EvadeChooserDialog;
import com.dsatab.view.FightFilterSettings;
import com.dsatab.view.FilterSettings;
import com.dsatab.view.FilterSettings.FilterType;
import com.dsatab.view.ItemListItem;
import com.dsatab.view.listener.HeroInventoryChangedListener;
import com.dsatab.xml.DataManager;

public class FightFragment extends BaseListFragment implements OnLongClickListener, OnClickListener,
		OnItemClickListener, HeroInventoryChangedListener {

	private static final String KEY_PICKER_TYPE = "pickerType";

	private WheelView fightNumberPicker;
	private ValueWheelAdapter fightNumberAdapter;
	private LinearLayout fightLpLayout;

	private ItemListItem fightausweichen;

	private ListView fightList;

	private FightEquippedItemAdapter fightItemAdapter;
	private FightModificatorAdapter fightModificatorAdapter;
	private SackOfViewsAdapter evadeAdapter;
	private MergeAdapter fightMergeAdapter;

	private EvadeChooserDialog ausweichenModificationDialog;

	private final class ModifierActionMode implements ActionMode.Callback {
		@Override
		public boolean onActionItemClicked(ActionMode mode, com.actionbarsherlock.view.MenuItem item) {
			boolean notifyChanged = false;

			SparseBooleanArray checkedPositions = fightList.getCheckedItemPositions();
			if (checkedPositions != null) {
				for (int i = checkedPositions.size() - 1; i >= 0; i--) {
					if (checkedPositions.valueAt(i)) {
						Object obj = fightList.getItemAtPosition(checkedPositions.keyAt(i));

						if (obj instanceof CustomModificator) {
							CustomModificator modificator = (CustomModificator) obj;

							switch (item.getItemId()) {
							case R.id.option_edit:
								Intent intent = new Intent(getActivity(), ModificatorEditActivity.class);
								intent.putExtra(ModificatorEditActivity.INTENT_ID, modificator.getId());
								intent.putExtra(ModificatorEditActivity.INTENT_NAME, modificator.getModificatorName());
								intent.putExtra(ModificatorEditActivity.INTENT_RULES, modificator.getRules());
								intent.putExtra(ModificatorEditActivity.INTENT_COMMENT, modificator.getComment());
								intent.putExtra(ModificatorEditActivity.INTENT_ACTIVE, modificator.isActive());
								getActivity().startActivityForResult(intent, DsaTabActivity.ACTION_EDIT_MODIFICATOR);
								mode.finish();
								return true;
							case R.id.option_delete:
								getHero().removeModificator(modificator);
								break;
							}
						}
					}

				}
				if (notifyChanged) {
					fightModificatorAdapter.notifyDataSetChanged();
				}
			}
			mode.finish();
			return true;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {

			boolean hasItems = false;
			boolean hasModifiers = false;

			SparseBooleanArray checkedPositions = fightList.getCheckedItemPositions();
			if (checkedPositions != null) {
				for (int i = checkedPositions.size() - 1; i >= 0; i--) {
					if (checkedPositions.valueAt(i)) {

						Object obj = fightList.getItemAtPosition(checkedPositions.keyAt(i));

						if (obj instanceof CustomModificator) {
							hasModifiers = true;
						}
						if (obj instanceof EquippedItem) {
							hasItems = true;
						}

					}
				}
			}

			if (hasModifiers && !hasItems) {
				mode.getMenuInflater().inflate(R.menu.modifikator_popupmenu, menu);
			} else if (hasItems && !hasModifiers) {
				mode.getMenuInflater().inflate(R.menu.equipped_item_popupmenu, menu);
			}

			return true;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			mMode = null;
			fightList.clearChoices();
			fightModificatorAdapter.notifyDataSetChanged();
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			SparseBooleanArray checkedPositions = fightList.getCheckedItemPositions();
			if (checkedPositions != null) {
				for (int i = checkedPositions.size() - 1; i >= 0; i--) {
					if (checkedPositions.valueAt(i)) {
						Object obj = fightList.getItemAtPosition(checkedPositions.keyAt(i));

						if (obj instanceof CustomModificator) {
							if (!menu.findItem(R.id.option_edit).isEnabled()) {
								menu.findItem(R.id.option_edit).setEnabled(true);
								return true;
							} else {
								return false;
							}
						} else {
							if (menu.findItem(R.id.option_edit).isEnabled()) {
								menu.findItem(R.id.option_edit).setEnabled(false);
								return true;
							} else {
								return false;
							}
						}

					}
				}
			}

			return false;
		}
	}

	private final class EquippedItemActionMode implements ActionMode.Callback {

		@Override
		public boolean onActionItemClicked(ActionMode mode, com.actionbarsherlock.view.MenuItem item) {
			boolean notifyChanged = false;
			boolean refill = false;

			SparseBooleanArray checkedPositions = fightList.getCheckedItemPositions();
			if (checkedPositions != null) {
				for (int i = checkedPositions.size() - 1; i >= 0; i--) {
					if (checkedPositions.valueAt(i)) {
						Object obj = fightList.getItemAtPosition(checkedPositions.keyAt(i));

						if (obj instanceof EquippedItem) {

							final EquippedItem equippedItem = (EquippedItem) obj;

							switch (item.getItemId()) {
							case R.id.option_edit:
								ItemEditActivity.edit(getActivity(), getHero(), equippedItem);
								break;
							case R.id.option_view:
								ItemViewActivity.view(getActivity(), getHero(), equippedItem);
								break;
							case R.id.option_assign_secondary: {
								final EquippedItem equippedPrimaryWeapon = equippedItem;
								final EquippedItemChooserDialog bkDialog = new EquippedItemChooserDialog(getActivity());

								bkDialog.setEquippedItems(getHero().getEquippedItems(Weapon.class, Shield.class));
								for (Iterator<EquippedItem> iter = bkDialog.getEquippedItems().iterator(); iter
										.hasNext();) {
									EquippedItem eq = iter.next();
									if (eq.getItemSpecification() instanceof Weapon) {
										Weapon weapon = (Weapon) eq.getItemSpecification();
										if (weapon.isTwoHanded())
											iter.remove();
									}
								}
								bkDialog.setSelectedItem(equippedItem.getSecondaryItem());
								// do not select item itself
								bkDialog.getEquippedItems().remove(equippedPrimaryWeapon);
								bkDialog.setOnAcceptListener(new EquippedItemChooserDialog.OnAcceptListener() {

									@Override
									public void onAccept(EquippedItem item, boolean bhKampf) {
										if (item != null) {
											EquippedItem equippedSecondaryWeapon = item;

											equippedPrimaryWeapon.setHand(Hand.rechts);
											equippedSecondaryWeapon.setHand(Hand.links);

											// remove 2way relation if old
											// secondary item existed
											if (equippedSecondaryWeapon.getSecondaryItem() != null
													&& equippedSecondaryWeapon.getSecondaryItem().getSecondaryItem() != null) {
												Debug.verbose("Removing old weapon sec item "
														+ equippedSecondaryWeapon.getSecondaryItem());
												equippedSecondaryWeapon.getSecondaryItem().setSecondaryItem(null);
											}
											if (equippedPrimaryWeapon.getSecondaryItem() != null
													&& equippedPrimaryWeapon.getSecondaryItem().getSecondaryItem() != null) {
												Debug.verbose("Removing old shield sec item "
														+ equippedSecondaryWeapon.getSecondaryItem());
												equippedPrimaryWeapon.getSecondaryItem().setSecondaryItem(null);
											}

											equippedPrimaryWeapon.setSecondaryItem(equippedSecondaryWeapon);
											equippedSecondaryWeapon.setSecondaryItem(equippedPrimaryWeapon);

											equippedPrimaryWeapon.setBeidhändigerKampf(bhKampf);
											equippedSecondaryWeapon.setBeidhändigerKampf(bhKampf);

											fillFightItemDescriptions();

										}

									}
								});

								bkDialog.show();
								break;
							}
							case R.id.option_unassign: {

								final EquippedItem equippedPrimaryWeapon = equippedItem;
								EquippedItem equippedSecondaryWeapon = equippedPrimaryWeapon.getSecondaryItem();
								equippedPrimaryWeapon.setSecondaryItem(null);
								if (equippedSecondaryWeapon != null) {
									equippedSecondaryWeapon.setSecondaryItem(null);
								}

								refill = true;
								break;
							}
							case R.id.option_select_version: {
								AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
								List<String> specInfo = equippedItem.getItem().getSpecificationNames();
								builder.setItems(specInfo.toArray(new String[0]),
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
												equippedItem.setItemSpecification(getActivity(), equippedItem.getItem()
														.getSpecifications().get(which));
												dialog.dismiss();
											}
										});

								builder.setTitle("Wähle eine Variante...");
								builder.show();
								break;
							}
							case R.id.option_select_talent: {

								final List<TalentType> specInfo = new ArrayList<TalentType>();
								final List<String> specName = new ArrayList<String>();
								if (equippedItem.getItemSpecification() instanceof Weapon) {
									Weapon weapon = (Weapon) equippedItem.getItemSpecification();
									for (TalentType type : weapon.getTalentTypes()) {
										specInfo.add(type);
										specName.add(type.xmlName());
									}
								} else if (equippedItem.getItemSpecification() instanceof Shield) {
									Shield shield = (Shield) equippedItem.getItemSpecification();
									for (TalentType type : shield.getTalentTypes()) {
										specInfo.add(type);
										specName.add(type.xmlName());
									}
								}
								if (!specInfo.isEmpty()) {
									AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
									builder.setItems(specName.toArray(new String[0]),
											new DialogInterface.OnClickListener() {
												@Override
												public void onClick(DialogInterface dialog, int which) {
													TalentType talentType = specInfo.get(which);
													CombatTalent talent = getHero().getCombatTalent(talentType);
													if (talent != null) {
														equippedItem.setTalent(talent);
														getHero().fireItemChangedEvent(equippedItem);
													}
													dialog.dismiss();
												}
											});

									builder.setTitle("Wähle ein Talent...");
									builder.show();
								}
								break;
							}
							case R.id.option_assign_hunting: {
								getHero().setHuntingWeapon(equippedItem);
								notifyChanged = true;
								break;
							}
							case R.id.option_delete: {
								getHero().removeEquippedItem(equippedItem);
								break;
							}
							}
						}
					}
				}
				if (refill) {
					fillFightItemDescriptions();
				} else if (notifyChanged) {
					fightItemAdapter.notifyDataSetChanged();
				}
			}
			mode.finish();
			return true;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			mode.getMenuInflater().inflate(R.menu.equipped_item_popupmenu, menu);
			return true;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			mMode = null;
			fightList.clearChoices();
			fightItemAdapter.notifyDataSetChanged();
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			SparseBooleanArray checkedPositions = fightList.getCheckedItemPositions();
			int selected = 0;
			if (checkedPositions != null) {
				for (int i = checkedPositions.size() - 1; i >= 0; i--) {
					if (checkedPositions.valueAt(i)) {
						selected++;
						Object obj = fightList.getItemAtPosition(checkedPositions.keyAt(i));

						if (obj instanceof EquippedItem) {
							EquippedItem equippedItem = (EquippedItem) obj;

							menu.findItem(R.id.option_assign_secondary).setVisible(false);
							if (equippedItem.getItemSpecification() instanceof Weapon) {
								Weapon weapon = (Weapon) equippedItem.getItemSpecification();
								if (!weapon.isTwoHanded()) {
									menu.findItem(R.id.option_assign_secondary).setVisible(true);
									if (selected == 1) {
										List<EquippedItem> items = getHero().getEquippedItems(Weapon.class,
												Shield.class);
										items.remove(equippedItem);
										menu.findItem(R.id.option_assign_secondary).setEnabled(!items.isEmpty());
									}
								}
							}

							menu.findItem(R.id.option_assign_hunting).setVisible(
									equippedItem.getItemSpecification() instanceof DistanceWeapon);

							menu.findItem(R.id.option_unassign).setVisible(equippedItem.getSecondaryItem() != null);

							menu.findItem(R.id.option_select_version).setVisible(
									equippedItem.getItem().getSpecifications().size() > 1);

							boolean hasMultipleTalentTypes = false;
							if (equippedItem.getItemSpecification() instanceof Weapon) {
								Weapon weapon = (Weapon) equippedItem.getItemSpecification();
								if (weapon.getTalentTypes().size() > 1) {
									hasMultipleTalentTypes = true;
								}
							} else if (equippedItem.getItemSpecification() instanceof Shield) {
								Shield shield = (Shield) equippedItem.getItemSpecification();
								if (shield.getTalentTypes().size() > 1) {
									hasMultipleTalentTypes = true;
								}
							}
							menu.findItem(R.id.option_select_talent).setVisible(hasMultipleTalentTypes);
						}
					}
				}
			}

			menu.findItem(R.id.option_view).setEnabled(selected == 1);
			if (menu.findItem(R.id.option_assign_secondary).isEnabled())
				menu.findItem(R.id.option_assign_secondary).setEnabled(selected == 1);
			menu.findItem(R.id.option_assign_hunting).setEnabled(selected == 1);
			menu.findItem(R.id.option_select_version).setEnabled(selected == 1);
			menu.findItem(R.id.option_select_talent).setEnabled(selected == 1);

			return true;
		}
	}

	public static class TargetListener implements View.OnClickListener {

		private WeakReference<DsaTabActivity> mActivity;

		/**
		 * 
		 */
		public TargetListener(DsaTabActivity activity) {
			this.mActivity = new WeakReference<DsaTabActivity>(activity);
		}

		@Override
		public void onClick(View v) {
			if (v.getTag() instanceof EquippedItem) {
				EquippedItem item = (EquippedItem) v.getTag();

				DsaTabActivity mainActivity = mActivity.get();
				if (mainActivity != null) {
					ArcheryChooserDialog targetChooserDialog = new ArcheryChooserDialog(mainActivity);
					targetChooserDialog.setWeapon(item);
					targetChooserDialog.show();
				}
			}
		}
	}

	private TargetListener targetListener;

	private Button fightPickerButton;
	private AttributeType fightPickerType = AttributeType.Lebensenergie_Aktuell;

	private List<AttributeType> fightPickerTypes;

	private Callback mItemsCallback = new EquippedItemActionMode();

	private Callback mModifiersCallback = new ModifierActionMode();

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == DsaTabActivity.ACTION_ADD_MODIFICATOR) {

			if (resultCode == Activity.RESULT_OK) {

				CustomModificator modificator = new CustomModificator(getHero());
				modificator.setModificatorName(data.getStringExtra(ModificatorEditActivity.INTENT_NAME));
				modificator.setRules(data.getStringExtra(ModificatorEditActivity.INTENT_RULES));
				modificator.setComment(data.getStringExtra(ModificatorEditActivity.INTENT_COMMENT));
				modificator.setActive(data.getBooleanExtra(ModificatorEditActivity.INTENT_ACTIVE, true));

				getHero().addModificator(modificator);

			}
		} else if (requestCode == DsaTabActivity.ACTION_EDIT_MODIFICATOR) {
			if (resultCode == Activity.RESULT_OK) {

				UUID id = (UUID) data.getSerializableExtra(ModificatorEditActivity.INTENT_ID);

				for (Modificator modificator : getHero().getUserModificators()) {
					if (modificator instanceof CustomModificator) {
						CustomModificator customModificator = (CustomModificator) modificator;
						if (customModificator.getId().equals(id)) {
							customModificator.setModificatorName(data
									.getStringExtra(ModificatorEditActivity.INTENT_NAME));
							customModificator.setRules(data.getStringExtra(ModificatorEditActivity.INTENT_RULES));
							customModificator.setActive(data.getBooleanExtra(ModificatorEditActivity.INTENT_ACTIVE,
									true));
							customModificator.setComment(data.getStringExtra(ModificatorEditActivity.INTENT_COMMENT));
						}
					}
				}

			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected Callback getActionModeCallback(List<Object> objects) {
		boolean hasItems = false;
		boolean hasModifiers = false;
		for (Object o : objects) {
			if (o instanceof EquippedItem)
				hasItems = true;
			else if (o instanceof CustomModificator)
				hasModifiers = true;
		}

		if (hasItems && !hasModifiers) {
			return mItemsCallback;
		} else if (hasModifiers && !hasItems)
			return mModifiersCallback;
		else
			return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.fragment.BaseFragment#onFilterChanged(com.dsatab.view. FilterSettings.FilterType, com.dsatab.view.FilterSettings)
	 */
	@Override
	public void onFilterChanged(FilterType type, FilterSettings settings) {
		if (fightMergeAdapter != null && (type == FilterType.Fight || type == null)
				&& settings instanceof FightFilterSettings) {

			Debug.verbose("fight filter " + settings);

			FightFilterSettings newSettings = (FightFilterSettings) settings;

			fightItemAdapter.filter(newSettings);
			fightMergeAdapter.setActive(fightModificatorAdapter, newSettings.isShowModifier());
			updateAusweichen();

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.actionbarsherlock.app.SherlockFragment#onCreateOptionsMenu(com. actionbarsherlock.view.Menu, com.actionbarsherlock.view.MenuInflater)
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, com.actionbarsherlock.view.MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		menu.removeItem(R.id.option_set);
		inflater.inflate(R.menu.fight_menu, menu);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.actionbarsherlock.app.SherlockFragment#onOptionsItemSelected(com. actionbarsherlock.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {

		if (item.getItemId() == R.id.option_modifier_add) {
			getActivity().startActivityForResult(new Intent(getActivity(), ModificatorEditActivity.class),
					DsaTabActivity.ACTION_ADD_MODIFICATOR);
			return true;
		} else {
			return super.onOptionsItemSelected(item);
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

		mCallback = new ModifierActionMode();
	}

	private void updateAusweichen() {
		if (evadeAdapter == null || fightMergeAdapter == null)
			return;

		fightMergeAdapter.setActive(evadeAdapter, getFilterSettings().isShowEvade());

		if (getFilterSettings().isShowEvade()) {

			Attribute ausweichen = getHero().getAttribute(AttributeType.Ausweichen);
			TextView text1 = (TextView) fightausweichen.findViewById(android.R.id.text1);

			StyleableSpannableStringBuilder title = new StyleableSpannableStringBuilder();
			title.append(ausweichen.getName());

			Util.appendValue(getHero(), title, ausweichen, null, getFilterSettings() != null ? getFilterSettings()
					.isIncludeModifiers() : true);

			text1.setText(title);
			final TextView text2 = (TextView) fightausweichen.findViewById(android.R.id.text2);
			text2.setText("Modifikator " + Util.toProbe(ausweichen.getProbeInfo().getErschwernis()));

			// fightausweichen.setBackgroundResource(getFightItemBackgroundResource(fightausweichen));

			ImageButton iconLeft = (ImageButton) fightausweichen.findViewById(android.R.id.icon1);
			iconLeft.setTag(ausweichen);

			Util.applyRowStyle(fightausweichen, fightItemAdapter.getCount());
		}

	}

	@Override
	protected FightFilterSettings getFilterSettings() {
		return (FightFilterSettings) super.getFilterSettings();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnLongClickListener#onLongClick(android.view.View)
	 */
	@Override
	public boolean onLongClick(View v) {
		switch (v.getId()) {
		case R.id.fight_btn_picker:
			if (fightPickerType == AttributeType.Initiative_Aktuell) {
				getBaseActivity().checkProbe(getHero().getAttribute(AttributeType.ini));
				return true;
			}
			break;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.activity.BaseMainActivity#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.fight_btn_picker:

			int index = fightPickerTypes.indexOf(fightPickerType);
			int next = (index + 1) % fightPickerTypes.size();
			fightPickerType = fightPickerTypes.get(next);

			Attribute attr = getHero().getAttribute(fightPickerType);

			updateNumberPicker(attr);
			break;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = configureContainerView(inflater.inflate(R.layout.sheet_fight, container, false));

		fightList = (ListView) root.findViewById(R.id.fight_list);
		fightPickerButton = (Button) root.findViewById(R.id.fight_btn_picker);
		fightNumberPicker = (WheelView) root.findViewById(R.id.fight_picker);
		fightLpLayout = (LinearLayout) root.findViewById(R.id.fight_le_layout);
		return root;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		fightList.setOnItemLongClickListener(this);
		fightList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
		fightList.setOnItemClickListener(this);

		targetListener = new TargetListener((DsaTabActivity) getActivity());

		fightausweichen = (ItemListItem) getLayoutInflater(savedInstanceState).inflate(R.layout.item_listitem, null,
				false);

		ImageButton iconLeft = (ImageButton) fightausweichen.findViewById(android.R.id.icon1);
		iconLeft.setOnClickListener(getBaseActivity().getProbeListener());
		iconLeft.setOnLongClickListener(getBaseActivity().getEditListener());
		iconLeft.setImageResource(R.drawable.icon_ausweichen);
		iconLeft.setVisibility(View.VISIBLE);
		iconLeft.setFocusable(true);
		ImageButton iconRight = (ImageButton) fightausweichen.findViewById(android.R.id.icon2);
		iconRight.setImageResource(R.drawable.icon_target);
		iconRight.setVisibility(View.VISIBLE);
		iconRight.setFocusable(true);
		iconRight.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (ausweichenModificationDialog == null) {
					ausweichenModificationDialog = new EvadeChooserDialog(getBaseActivity());
				}
				ausweichenModificationDialog.show();
			}
		});

		fightPickerButton.setOnClickListener(this);
		fightPickerButton.setOnLongClickListener(this);

		fightNumberAdapter = new ValueWheelAdapter(getActivity());
		fightNumberPicker.setViewAdapter(fightNumberAdapter);

		fightNumberPicker.setOrientation(WheelView.HORIZONTAL);
		fightNumberPicker.setOnWheelChangedListeners(new OnWheelChangedListener() {

			@Override
			public void onWheelChanged(WheelView wheel, int oldIndex, int newIndex) {
				Attribute attr = getHero().getAttribute(fightPickerType);
				attr.setValue(fightNumberAdapter.getItem(newIndex));
			}
		});

		fightLpLayout.setOnClickListener(getBaseActivity().getEditListener());

		SharedPreferences pref = getActivity().getPreferences(Context.MODE_PRIVATE);
		try {
			String typeString = pref.getString(KEY_PICKER_TYPE, AttributeType.Lebensenergie_Aktuell.name());
			setFightPickerType(AttributeType.valueOf(typeString));
		} catch (Exception e) {
			Debug.error(e);
			setFightPickerType(AttributeType.Lebensenergie_Aktuell);
		}

		super.onActivityCreated(savedInstanceState);
	}

	protected AttributeType getFightPickerTotalType() {
		switch (fightPickerType) {
		case Lebensenergie_Aktuell:
			return AttributeType.Lebensenergie;
		case Ausdauer_Aktuell:
			return AttributeType.Ausdauer;
		case Astralenergie_Aktuell:
			return AttributeType.Astralenergie;
		case Karmaenergie_Aktuell:
			return AttributeType.Karmaenergie;
		default:
			return null;
		}
	}

	protected AttributeType getFightPickerType() {
		return fightPickerType;
	}

	protected void setFightPickerType(AttributeType fightPickerType) {
		this.fightPickerType = fightPickerType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.activity.BaseMainActivity#onPause()
	 */
	@Override
	public void onPause() {
		super.onPause();

		SharedPreferences pref = getActivity().getPreferences(Context.MODE_PRIVATE);

		Editor edit = pref.edit();
		edit.putString(KEY_PICKER_TYPE, fightPickerType.name());
		edit.commit();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.activity.BaseMenuActivity#onHeroLoaded(com.dsatab.data.Hero)
	 */
	@Override
	public void onHeroLoaded(Hero hero) {
		ausweichenModificationDialog = null;

		fightItemAdapter = new FightEquippedItemAdapter(getActivity(), getHero(), getHero().getEquippedItems(),
				getFilterSettings());
		fightItemAdapter.setProbeListener(getBaseActivity().getProbeListener());
		fightItemAdapter.setTargetListener(targetListener);
		addWaffenloseTalente();
		fightMergeAdapter = new MergeAdapter();
		fightMergeAdapter.addAdapter(fightItemAdapter);

		List<View> evadeViews = new ArrayList<View>(1);
		evadeViews.add(fightausweichen);
		evadeAdapter = new SackOfViewsAdapter(evadeViews);

		fightMergeAdapter.addAdapter(evadeAdapter);
		fightMergeAdapter.setActive(evadeAdapter, getFilterSettings().isShowEvade());

		fightModificatorAdapter = new FightModificatorAdapter(getActivity(), hero.getUserModificators());
		fightMergeAdapter.addAdapter(fightModificatorAdapter);
		fightMergeAdapter.setActive(fightModificatorAdapter, getFilterSettings().isShowModifier());

		fightPickerTypes = new ArrayList<AttributeType>(6);
		fightPickerTypes.add(AttributeType.Lebensenergie_Aktuell);
		fightPickerTypes.add(AttributeType.Ausdauer_Aktuell);

		if (hero.getAttributeValue(AttributeType.Astralenergie) != null
				&& hero.getAttributeValue(AttributeType.Astralenergie) > 0) {
			fightPickerTypes.add(AttributeType.Astralenergie_Aktuell);
		}
		if (hero.getAttributeValue(AttributeType.Karmaenergie) != null
				&& hero.getAttributeValue(AttributeType.Karmaenergie) > 0) {
			fightPickerTypes.add(AttributeType.Karmaenergie_Aktuell);
			fightPickerTypes.add(AttributeType.Entrueckung);
		}
		if (getHero().hasFeature(FeatureType.Mondsüchtig)) {
			fightPickerTypes.add(AttributeType.Verzueckung);
		}

		fightPickerTypes.add(AttributeType.Initiative_Aktuell);
		fightPickerTypes.add(AttributeType.Erschoepfung);

		if (!fightPickerTypes.contains(fightPickerType)) {
			fightPickerType = fightPickerTypes.get(0);
		}
		// fight
		Attribute attr = hero.getAttribute(fightPickerType);
		updateNumberPicker(attr);

		updateAusweichen();

		fightList.setAdapter(fightMergeAdapter);
	}

	/**
	 * 
	 */

	@Override
	public void onModifierAdded(Modificator value) {
		fightModificatorAdapter.add(value);
		fightItemAdapter.notifyDataSetChanged();
		updateAusweichen();
		updateNumberPicker();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.view.listener.ModifierChangedListener#onPortraitChanged()
	 */
	@Override
	public void onPortraitChanged() {

	}

	@Override
	public void onModifierRemoved(Modificator value) {
		fightModificatorAdapter.remove(value);
		fightItemAdapter.notifyDataSetChanged();
		updateAusweichen();
		updateNumberPicker();
	}

	@Override
	public void onModifierChanged(Modificator value) {
		fightModificatorAdapter.notifyDataSetChanged();
		fightItemAdapter.notifyDataSetChanged();
		updateAusweichen();
		updateNumberPicker();
	}

	@Override
	public void onModifiersChanged(List<Modificator> values) {
		fightModificatorAdapter.notifyDataSetChanged();
		fightItemAdapter.notifyDataSetChanged();
		updateAusweichen();
	}

	private void updateNumberPicker() {
		if (fightPickerType != null) {
			Attribute attr = getHero().getAttribute(fightPickerType);
			updateNumberPicker(attr);
		}
	}

	private void updateNumberPicker(Attribute value) {
		fightNumberPicker.setTag(value);
		fightNumberAdapter.setAttribute(value);
		if (value.getValue() != null) {
			fightNumberPicker.setCurrentItem(fightNumberAdapter.getPosition(value.getValue()));
		} else {
			fightNumberPicker.setCurrentItem(0);
		}
		fightPickerButton.setText(value.getType().code());
	}

	@Override
	public void onValueChanged(Value value) {
		if (value == null) {
			return;
		}

		if (value instanceof Attribute) {
			Attribute attr = (Attribute) value;

			if (attr.getType() == fightPickerType) {
				updateNumberPicker(attr);
			} else if (attr.getType() == getFightPickerTotalType()) {
				updateNumberPicker();
			}

			switch (attr.getType()) {
			case Behinderung:
				updateAusweichen();
				break;
			case Ausweichen: {
				updateAusweichen();
				break;
			}
			case Körperkraft:
				fightItemAdapter.notifyDataSetChanged();
				break;
			default:
				// do nothing
				break;
			}
		}

	}

	private void fillFightItemDescriptions() {
		List<EquippedItem> items = getHero().getEquippedItems();
		Util.sort(items);

		fightItemAdapter.setNotifyOnChange(false);
		fightItemAdapter.clear();
		for (EquippedItem equippedItem : items) {
			fightItemAdapter.add(equippedItem);
		}

		addWaffenloseTalente();

		fightItemAdapter.notifyDataSetChanged();
	}

	private void addWaffenloseTalente() {
		Item raufen = DataManager.getItemByName(TalentType.Raufen.xmlName());
		Weapon raufenSpec = (Weapon) raufen.getSpecifications().get(0);
		EquippedItem raufenEquipped = new EquippedItem(getHero(),
				getHero().getCombatTalent(raufenSpec.getTalentType()), raufen, raufenSpec);
		fightItemAdapter.add(raufenEquipped);

		Item ringen = DataManager.getItemByName(TalentType.Ringen.xmlName());
		Weapon ringenSpec = (Weapon) ringen.getSpecifications().get(0);
		EquippedItem ringenEquipped = new EquippedItem(getHero(),
				getHero().getCombatTalent(ringenSpec.getTalentType()), ringen, ringenSpec);
		fightItemAdapter.add(ringenEquipped);

		if (getHero().hasFeature(FeatureType.WaffenloserKampfstilHruruzat)) {
			Item hruruzat = DataManager.getItemByName("Hruruzat");
			Weapon hruruzatSpec = (Weapon) ringen.getSpecifications().get(0);
			EquippedItem hruruzatEquipped = new EquippedItem(getHero(), getHero().getCombatTalent(
					ringenSpec.getTalentType()), hruruzat, hruruzatSpec);
			fightItemAdapter.add(hruruzatEquipped);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget .AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		if (parent == fightList) {
			if (mMode == null) {
				Object object = fightMergeAdapter.getItem(position);

				if (object instanceof AbstractModificator) {
					AbstractModificator modificator = (AbstractModificator) object;
					modificator.setActive(!modificator.isActive());
					fightModificatorAdapter.notifyDataSetChanged();
				}
				fightList.setItemChecked(position, false);
			} else {
				super.onItemClick(parent, v, position, id);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.fragment.BaseFragment#onActiveSetChanged(int, int)
	 */
	@Override
	public void onActiveSetChanged(int newSet, int oldSet) {
		fillFightItemDescriptions();
		if (getActivity() != null) {
			getActivity().supportInvalidateOptionsMenu();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.view.listener.InventoryChangedListener#onItemAdded(com.dsatab .data.items.Item)
	 */
	@Override
	public void onItemAdded(Item item) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.view.listener.InventoryChangedListener#onItemRemoved(com.dsatab .data.items.Item)
	 */
	@Override
	public void onItemRemoved(Item item) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.view.listener.InventoryChangedListener#onItemChanged(com.dsatab .data.items.EquippedItem)
	 */
	@Override
	public void onItemChanged(EquippedItem item) {
		if (item.getSet() == getHero().getActiveSet())
			fightItemAdapter.notifyDataSetChanged();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.view.listener.InventoryChangedListener#onItemEquipped(com. dsatab.data.items.EquippedItem)
	 */
	@Override
	public void onItemEquipped(EquippedItem item) {
		if (item.getSet() == getHero().getActiveSet()) {
			fightItemAdapter.add(item);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.view.listener.InventoryChangedListener#onItemUnequipped(com .dsatab.data.items.EquippedItem)
	 */
	@Override
	public void onItemUnequipped(EquippedItem item) {
		if (item.getSet() == getHero().getActiveSet()) {
			fightItemAdapter.remove(item);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.view.listener.HeroInventoryChangedListener#onItemChanged(com .dsatab.data.items.Item)
	 */
	@Override
	public void onItemChanged(Item item) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.view.listener.HeroInventoryChangedListener#onItemContainerAdded (com.dsatab.data.items.ItemContainer)
	 */
	@Override
	public void onItemContainerAdded(ItemContainer itemContainer) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.view.listener.HeroInventoryChangedListener#onItemContainerRemoved (com.dsatab.data.items.ItemContainer)
	 */
	@Override
	public void onItemContainerRemoved(ItemContainer itemContainer) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.view.listener.HeroInventoryChangedListener#onItemContainerChanged (com.dsatab.data.items.ItemContainer)
	 */
	@Override
	public void onItemContainerChanged(ItemContainer itemContainer) {
		// TODO Auto-generated method stub

	}

}
