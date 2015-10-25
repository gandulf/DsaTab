package com.dsatab.fragment;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AbsListView;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.activity.DsaTabActivity;
import com.dsatab.activity.DsaTabPreferenceActivity;
import com.dsatab.activity.ItemsActivity;
import com.dsatab.data.Art;
import com.dsatab.data.ArtInfo;
import com.dsatab.data.Attribute;
import com.dsatab.data.CombatTalent;
import com.dsatab.data.CustomProbe;
import com.dsatab.data.Hero;
import com.dsatab.data.Markable;
import com.dsatab.data.MetaTalent;
import com.dsatab.data.Probe;
import com.dsatab.data.Purse.Currency;
import com.dsatab.data.Spell;
import com.dsatab.data.SpellInfo;
import com.dsatab.data.Talent;
import com.dsatab.data.TalentGroup;
import com.dsatab.data.Value;
import com.dsatab.data.adapter.BaseRecyclerAdapter;
import com.dsatab.data.adapter.ListableItemAdapter;
import com.dsatab.data.enums.AttributeType;
import com.dsatab.data.enums.EventCategory;
import com.dsatab.data.enums.FeatureType;
import com.dsatab.data.enums.Hand;
import com.dsatab.data.enums.TalentGroupType;
import com.dsatab.data.enums.TalentType;
import com.dsatab.data.items.DistanceWeapon;
import com.dsatab.data.items.EquippedItem;
import com.dsatab.data.items.Item;
import com.dsatab.data.items.ItemContainer;
import com.dsatab.data.items.Shield;
import com.dsatab.data.items.Weapon;
import com.dsatab.data.listable.FileListable;
import com.dsatab.data.listable.HeaderListItem;
import com.dsatab.data.listable.PurseListable;
import com.dsatab.data.listable.WoundListItem;
import com.dsatab.data.modifier.AbstractModificator;
import com.dsatab.data.modifier.CustomModificator;
import com.dsatab.data.modifier.Modificator;
import com.dsatab.data.notes.Connection;
import com.dsatab.data.notes.Event;
import com.dsatab.data.notes.NotesItem;
import com.dsatab.db.DataManager;
import com.dsatab.fragment.dialog.DiceSliderFragment;
import com.dsatab.fragment.dialog.DirectoryChooserDialog;
import com.dsatab.fragment.dialog.DirectoryChooserDialog.OnDirectoryChooserListener;
import com.dsatab.fragment.dialog.EquippedItemChooserDialog;
import com.dsatab.util.Debug;
import com.dsatab.util.Util;
import com.dsatab.util.ViewUtils;
import com.dsatab.view.ListSettings;
import com.dsatab.view.ListSettings.ListItem;
import com.dsatab.view.ListSettings.ListItemType;
import com.dsatab.view.listener.EditListener;
import com.dsatab.view.listener.HeroInventoryChangedListener;
import com.dsatab.view.listener.OnClickActionListenerDelegate;
import com.gandulf.guilib.util.FileFileFilter;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.h6ah4i.android.widget.advrecyclerview.selectable.RecyclerViewSelectionManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.RecyclerViewAdapterUtils;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.wnafee.vector.compat.ResourcesCompat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class ListableFragment extends BaseRecyclerFragment implements HeroInventoryChangedListener,
        com.dsatab.view.listener.OnActionListener {

    private static final int MENU_FILTER_GROUP = 97;

    private ListableItemAdapter mAdapter;

    private FloatingActionMenu fabMenu;

    protected static final class ModifierActionMode extends BaseListableActionMode<ListableFragment> {

        public ModifierActionMode(ListableFragment fragment, RecyclerView listView, RecyclerViewSelectionManager manager) {
            super(fragment, listView, manager);
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            RecyclerView list = listView.get();
            ListableFragment fragment = listFragment.get();
            if (list == null || fragment == null)
                return false;

            ListableItemAdapter adapter = WrapperAdapterUtils.findWrappedAdapter(list.getAdapter(), ListableItemAdapter.class);
            for (int index : getManager().getSelectedPositions()) {

                Object obj = adapter.getItem(index);

                if (obj instanceof CustomModificator) {
                    CustomModificator modificator = (CustomModificator) obj;

                    switch (item.getItemId()) {
                        case R.id.option_edit:
                            ModificatorEditFragment.edit(fragment.getActivity(), modificator,
                                    DsaTabActivity.ACTION_EDIT_MODIFICATOR);
                            mode.finish();
                            return true;
                        case R.id.option_delete:
                            DsaTabApplication.getInstance().getHero().removeModificator(modificator);
                            break;
                    }
                }
            }

            mode.finish();
            return true;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            RecyclerView list = listView.get();
            ListableFragment fragment = listFragment.get();
            if (list == null || fragment == null)
                return false;

            boolean hasItems = false;
            boolean hasModifiers = false;

            ListableItemAdapter adapter = WrapperAdapterUtils.findWrappedAdapter(list.getAdapter(), ListableItemAdapter.class);
            for (int index : getManager().getSelectedPositions()) {
                Object obj = adapter.getItem(index);
                if (obj instanceof CustomModificator) {
                    hasModifiers = true;
                }
                if (obj instanceof EquippedItem) {
                    hasItems = true;
                }
            }

            if (hasModifiers && !hasItems) {
                mode.getMenuInflater().inflate(R.menu.menuitem_edit, menu);
                mode.getMenuInflater().inflate(R.menu.menuitem_delete, menu);
            } else if (hasItems && !hasModifiers) {
                mode.getMenuInflater().inflate(R.menu.equipped_item_popupmenu, menu);
            }

            return super.onCreateActionMode(mode,menu);
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            RecyclerView list = listView.get();
            ListableFragment fragment = listFragment.get();
            if (list == null || fragment == null)
                return false;

            MenuItem edit = menu.findItem(R.id.option_edit);

            ListableItemAdapter adapter = WrapperAdapterUtils.findWrappedAdapter(list.getAdapter(), ListableItemAdapter.class);
            for (int index : getManager().getSelectedPositions()) {
                Object obj = adapter.getItem(index);

                if (obj instanceof CustomModificator) {
                    return ViewUtils.menuIconState(edit,true);
                } else {
                    return ViewUtils.menuIconState(edit,false);
                }
            }

            return false;
        }
    }

    protected static final class CustomProbeActionMode extends BaseListableActionMode<ListableFragment> {

        public CustomProbeActionMode(ListableFragment fragment, RecyclerView listView, RecyclerViewSelectionManager manager) {
            super(fragment, listView, manager);
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            boolean notifyChanged = false;

            RecyclerView list = listView.get();
            ListableFragment fragment = listFragment.get();
            if (list == null || fragment == null)
                return false;

            ListableItemAdapter adapter = WrapperAdapterUtils.findWrappedAdapter(list.getAdapter(), ListableItemAdapter.class);
            for (int index : getManager().getSelectedPositions()) {
                Object obj = adapter.getItem(index);

                if (obj instanceof CustomProbe) {
                    CustomProbe modificator = (CustomProbe) obj;

                    switch (item.getItemId()) {
                        case R.id.option_edit:
                            CustomProbeEditFragment.edit(fragment.getActivity(), modificator,
                                    DsaTabActivity.ACTION_EDIT_CUSTOM_PROBES);
                            mode.finish();
                            return true;
                        case R.id.option_delete:
                            DsaTabApplication.getInstance().getHero().getHeroConfiguration()
                                    .removeCustomProbe(modificator);
                            notifyChanged = true;
                            break;
                    }
                }


                if (notifyChanged) {
                    fragment.fillListItems(fragment.getHero());
                }
            }
            mode.finish();
            return true;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            RecyclerView list = listView.get();
            ListableFragment fragment = listFragment.get();
            if (list == null || fragment == null)
                return false;

            boolean hasModifiers = false;

            ListableItemAdapter adapter = WrapperAdapterUtils.findWrappedAdapter(list.getAdapter(), ListableItemAdapter.class);
            for (int index : getManager().getSelectedPositions()) {
                Object obj = adapter.getItem(index);

                if (obj instanceof CustomProbe) {
                    hasModifiers = true;
                    break;
                }

            }


            if (hasModifiers) {
                mode.getMenuInflater().inflate(R.menu.menuitem_edit, menu);
                mode.getMenuInflater().inflate(R.menu.menuitem_delete, menu);
            }

            return super.onCreateActionMode(mode,menu);
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            RecyclerView list = listView.get();
            ListableFragment fragment = listFragment.get();
            if (list == null || fragment == null)
                return false;

            MenuItem edit = menu.findItem(R.id.option_edit);

            ListableItemAdapter adapter = WrapperAdapterUtils.findWrappedAdapter(list.getAdapter(), ListableItemAdapter.class);
            for (int index : getManager().getSelectedPositions()) {
                Object obj = adapter.getItem(index);

                if (obj instanceof CustomProbe) {
                    return ViewUtils.menuIconState(edit,true);
                } else {
                    return ViewUtils.menuIconState(edit,false);
                }
            }

            return false;
        }
    }

    protected static final class EquippedItemActionMode extends BaseListableActionMode<ListableFragment> {

        public EquippedItemActionMode(ListableFragment fragment, RecyclerView listView, RecyclerViewSelectionManager manager) {
            super(fragment, listView, manager);
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            RecyclerView list = listView.get();
            final ListableFragment fragment = listFragment.get();
            if (list == null || fragment == null)
                return false;

            boolean refill = false;

            ListableItemAdapter adapter = WrapperAdapterUtils.findWrappedAdapter(list.getAdapter(), ListableItemAdapter.class);
            for (int index : getManager().getSelectedPositions()) {
                Object obj = adapter.getItem(index);

                if (obj instanceof EquippedItem) {

                    final EquippedItem equippedItem = (EquippedItem) obj;

                    switch (item.getItemId()) {
                        case R.id.option_edit:
                            ItemsActivity.edit(fragment.getActivity(), getHero(), equippedItem,
                                    ItemsActivity.ACTION_EDIT);
                            break;
                        case R.id.option_view:
                            ItemsActivity.view(fragment.getActivity(), getHero(), equippedItem);
                            break;
                        case R.id.option_assign_secondary: {
                            final EquippedItem equippedPrimaryWeapon = equippedItem;

                            List<EquippedItem> equippedItems = new ArrayList<EquippedItem>(getHero()
                                    .getEquippedItems(Weapon.class, Shield.class));
                            for (Iterator<EquippedItem> iter = equippedItems.iterator(); iter.hasNext(); ) {
                                EquippedItem eq = iter.next();
                                if (eq.getItemSpecification() instanceof Weapon) {
                                    Weapon weapon = (Weapon) eq.getItemSpecification();
                                    if (weapon.isTwoHanded())
                                        iter.remove();
                                }
                            }
                            // do not select item itself
                            equippedItems.remove(equippedPrimaryWeapon);

                            EquippedItemChooserDialog.OnAcceptListener acc = new EquippedItemChooserDialog.OnAcceptListener() {

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

                                        fragment.fillListItems(getHero());

                                    }

                                }
                            };

                            EquippedItemChooserDialog.show(fragment, equippedItems,
                                    equippedItem.getSecondaryItem(), acc, 0);

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
                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                    fragment.getActivity());

                            List<String> specInfo = equippedItem.getItem().getSpecificationNames();
                            builder.setItems(specInfo.toArray(new String[0]),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            equippedItem.setItemSpecification(fragment.getActivity(), equippedItem
                                                    .getItem().getSpecifications().get(which));
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
                                AlertDialog.Builder builder = new AlertDialog.Builder(
                                        fragment.getActivity());
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
                            adapter.notifyItemChanged(index);
                            break;
                        }
                        case R.id.option_delete: {
                            getHero().removeEquippedItem(equippedItem);
                            break;
                        }
                    }
                } else if (obj instanceof Talent) {
                    Talent talent = (Talent) obj;
                    switch (item.getItemId()) {
                        case R.id.option_edit:
                            EditListener.showEditPopup(fragment, talent);
                            mode.finish();
                            return true;
                        case R.id.option_mark_favorite:
                            talent.setFavorite(true);
                            adapter.notifyItemChanged(index);
                            break;
                        case R.id.option_mark_unused:
                            talent.setUnused(true);
                            adapter.notifyItemChanged(index);
                            break;
                        case R.id.option_unmark:
                            talent.setFavorite(false);
                            talent.setUnused(false);
                            adapter.notifyItemChanged(index);
                            break;
                        default:
                            return false;
                    }


                }
                if (refill) {
                    fragment.fillListItems(getHero());
                }
            }
            mode.finish();
            return true;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.equipped_item_popupmenu, menu);

            return super.onCreateActionMode(mode,menu);
        }


        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            RecyclerView list = listView.get();
            ListableFragment fragment = listFragment.get();
            if (list == null || fragment == null)
                return false;

            int selected = 0;
            ListableItemAdapter adapter = WrapperAdapterUtils.findWrappedAdapter(list.getAdapter(), ListableItemAdapter.class);
            for (int index : getManager().getSelectedPositions()) {
                Object obj = adapter.getItem(index);
                selected++;
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
                                ViewUtils.menuIconState(menu.findItem(R.id.option_assign_secondary), !items.isEmpty());
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
                    MenuItem talentMenu = menu.findItem(R.id.option_select_talent);
                    talentMenu.setVisible(hasMultipleTalentTypes);
                }
            }

            boolean changed = ViewUtils.menuIconState(menu.findItem(R.id.option_view),selected ==1);
            changed |= ViewUtils.menuIconState(menu.findItem(R.id.option_assign_secondary),selected ==1);
            changed |= ViewUtils.menuIconState(menu.findItem(R.id.option_assign_hunting),selected ==1);
            changed |= ViewUtils.menuIconState(menu.findItem(R.id.option_select_version),selected ==1);
            changed |= ViewUtils.menuIconState(menu.findItem(R.id.option_select_talent),selected ==1);

            return changed;
        }

        protected Hero getHero() {
            return DsaTabApplication.getInstance().getHero();
        }
    }

    protected static final class ArtActionMode extends BaseListableActionMode<ListableFragment> {

        public ArtActionMode(ListableFragment fragment, RecyclerView listView, RecyclerViewSelectionManager manager) {
            super(fragment, listView, manager);
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            RecyclerView list = listView.get();
            ListableFragment fragment = listFragment.get();
            if (list == null || fragment == null)
                return false;

            ListableItemAdapter adapter = WrapperAdapterUtils.findWrappedAdapter(list.getAdapter(), ListableItemAdapter.class);
            for (int index : getManager().getSelectedPositions()) {
                Object obj = adapter.getItem(index);

                if (obj instanceof Art) {
                    Art art = (Art) obj;

                    switch (item.getItemId()) {
                        case R.id.option_mark_favorite:
                            art.setFavorite(true);
                            adapter.notifyItemChanged(index);
                            break;
                        case R.id.option_mark_unused:
                            art.setUnused(true);
                            adapter.notifyItemChanged(index);
                            break;
                        case R.id.option_unmark:
                            art.setFavorite(false);
                            art.setUnused(false);
                            adapter.notifyItemChanged(index);
                            break;
                        case R.id.option_view:
                            ArtInfoFragment.view(fragment.getActivity(), art, DsaTabActivity.ACTION_VIEW_ART);
                            mode.finish();
                            return true;
                        case R.id.option_edit:
                            ArtInfoFragment.edit(fragment.getActivity(), art, DsaTabActivity.ACTION_VIEW_ART);
                            mode.finish();
                            return true;
                        default:
                            return false;

                    }
                }
            }
            mode.finish();
            return true;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menuitem_view, menu);
            mode.getMenuInflater().inflate(R.menu.menuitem_edit, menu);
            mode.getMenuInflater().inflate(R.menu.menuitem_mark, menu);
            mode.setTitle("Künste");
            return super.onCreateActionMode(mode,menu);
        }


        /*
         * (non-Javadoc)
         *
         * @see ActionMode.Callback#onPrepareActionMode (ActionMode, Menu)
         */
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            RecyclerView list = listView.get();
            ListableFragment fragment = listFragment.get();
            if (list == null || fragment == null)
                return false;

            int selected = 0;
            boolean marked = false;
            ListableItemAdapter adapter = WrapperAdapterUtils.findWrappedAdapter(list.getAdapter(), ListableItemAdapter.class);
            for (int index : getManager().getSelectedPositions()) {
                selected++;
                Object obj = adapter.getItem(index);
                if (obj instanceof Art) {
                    Art art = (Art) obj;
                    marked |= art.isFavorite() || art.isUnused();
                }
            }
            mode.setSubtitle(selected + " ausgewählt");

            boolean changed = false;

            changed |= ViewUtils.menuIconState(menu.findItem(R.id.option_view),selected ==1);
            changed |= ViewUtils.menuIconState(menu.findItem(R.id.option_unmark),marked);

            return changed;
        }
    }

    protected static final class SpellActionMode extends BaseListableActionMode<ListableFragment> {

        public SpellActionMode(ListableFragment fragment, RecyclerView listView, RecyclerViewSelectionManager manager) {
            super(fragment, listView, manager);
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            RecyclerView list = listView.get();
            ListableFragment fragment = listFragment.get();
            if (list == null || fragment == null)
                return false;

            ListableItemAdapter adapter = WrapperAdapterUtils.findWrappedAdapter(list.getAdapter(), ListableItemAdapter.class);
            for (int index : getManager().getSelectedPositions()) {
                Object obj = adapter.getItem(index);

                if (obj instanceof Spell) {
                    Spell spell = (Spell) obj;

                    switch (item.getItemId()) {
                        case R.id.option_view:
                            SpellInfoFragment.view(fragment.getActivity(), spell, DsaTabActivity.ACTION_VIEW_SPELL);
                            mode.finish();
                            return true;
                        case R.id.option_edit:
                            SpellInfoFragment.edit(fragment.getActivity(), spell, DsaTabActivity.ACTION_VIEW_SPELL);
                            mode.finish();
                            return true;
                        case R.id.option_mark_favorite:
                            spell.setFavorite(true);
                            adapter.notifyItemChanged(index);
                            break;
                        case R.id.option_mark_unused:
                            spell.setUnused(true);
                            adapter.notifyItemChanged(index);
                            break;
                        case R.id.option_unmark:
                            spell.setFavorite(false);
                            spell.setUnused(false);
                            adapter.notifyItemChanged(index);
                            break;
                        default:
                            return false;
                    }
                }
            }

            mode.finish();
            return true;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menuitem_view, menu);
            mode.getMenuInflater().inflate(R.menu.menuitem_edit, menu);
            mode.getMenuInflater().inflate(R.menu.menuitem_mark, menu);
            mode.setTitle("Zauber");
            return super.onCreateActionMode(mode,menu);
        }


        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

            RecyclerView list = listView.get();
            ListableFragment fragment = listFragment.get();
            if (list == null || fragment == null)
                return false;

            int selected = 0;
            boolean marked = false;
            ListableItemAdapter adapter = WrapperAdapterUtils.findWrappedAdapter(list.getAdapter(), ListableItemAdapter.class);
            for (int index : getManager().getSelectedPositions()) {
                selected++;
                Object obj = adapter.getItem(index);
                if (obj instanceof Spell) {
                    Spell spell = (Spell) obj;
                    marked |= spell.isFavorite() || spell.isUnused();
                }
            }

            mode.setSubtitle(selected + " ausgewählt");

            boolean changed = false;
            changed |= ViewUtils.menuIconState(menu.findItem(R.id.option_view),selected==1);
            changed |= ViewUtils.menuIconState(menu.findItem(R.id.option_unmark),marked);

            return changed;
        }
    }

    protected static final class TalentActionMode extends BaseListableActionMode<ListableFragment> {

        public TalentActionMode(ListableFragment fragment, RecyclerView listView, RecyclerViewSelectionManager manager) {
            super(fragment, listView, manager);
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            RecyclerView list = listView.get();
            ListableFragment fragment = listFragment.get();
            if (list == null || fragment == null)
                return false;

            ListableItemAdapter adapter = WrapperAdapterUtils.findWrappedAdapter(list.getAdapter(), ListableItemAdapter.class);
            for (int index : getManager().getSelectedPositions()) {
                Object obj = adapter.getItem(index);
                if (obj instanceof Talent) {
                    Talent talent = (Talent) obj;
                    switch (item.getItemId()) {
                        case R.id.option_edit:
                            EditListener.showEditPopup(fragment, talent);
                            mode.finish();
                            return true;
                        case R.id.option_mark_favorite:
                            talent.setFavorite(true);
                            adapter.notifyItemChanged(index);
                            break;
                        case R.id.option_mark_unused:
                            talent.setUnused(true);
                            adapter.notifyItemChanged(index);
                            break;
                        case R.id.option_unmark:
                            talent.setFavorite(false);
                            talent.setUnused(false);
                            adapter.notifyItemChanged(index);
                            break;
                        default:
                            return false;
                    }
                } else {
                    return false;
                }
            }

            mode.finish();
            return true;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menuitem_edit, menu);
            mode.getMenuInflater().inflate(R.menu.menuitem_mark, menu);
            mode.setTitle("Talente");

            return super.onCreateActionMode(mode,menu);
        }


        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            RecyclerView list = listView.get();
            if (list == null)
                return false;

            int selected = 0;
            boolean metaTalent = false;
            boolean marked = false;
            ListableItemAdapter adapter = WrapperAdapterUtils.findWrappedAdapter(list.getAdapter(), ListableItemAdapter.class);
            for (int index : getManager().getSelectedPositions()) {

                selected++;
                if (!metaTalent) {
                    Object obj = adapter.getItem(index);
                    if (obj instanceof MetaTalent) {
                        metaTalent = true;
                    }

                    if (obj instanceof Markable) {
                        Markable mark = (Markable) obj;
                        marked |= mark.isFavorite() || mark.isUnused();
                    }
                }
            }

            mode.setSubtitle(selected + " ausgewählt");

            boolean changed = ViewUtils.menuIconState(menu.findItem(R.id.option_unmark),marked);
            changed |= ViewUtils.menuIconState(menu.findItem(R.id.option_edit),!metaTalent && selected == 1);

            return changed;
        }
    }

    protected static final class NoteActionMode extends BaseListableActionMode<ListableFragment> {

        public NoteActionMode(ListableFragment fragment, RecyclerView listView, RecyclerViewSelectionManager manager) {
            super(fragment, listView, manager);
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            RecyclerView list = listView.get();
            ListableFragment fragment = listFragment.get();
            if (list == null || fragment == null)
                return false;

            ListableItemAdapter adapter = WrapperAdapterUtils.findWrappedAdapter(list.getAdapter(), ListableItemAdapter.class);
            for (int index : getManager().getSelectedPositions()) {
                Object obj = adapter.getItem(index);
                if (obj instanceof Event) {
                    Event event = (Event) obj;
                    if (item.getItemId() == R.id.option_delete) {
                        if (event.isDeletable()) {
                            fragment.getHero().removeEvent(event);
                            adapter.remove(event);
                        }
                    } else if (item.getItemId() == R.id.option_edit) {
                        NotesEditFragment.edit(event, null, fragment.getActivity(),
                                DsaTabActivity.ACTION_EDIT_NOTES);
                        mode.finish();
                        break;
                    }
                } else if (obj instanceof Connection) {
                    Connection connection = (Connection) obj;
                    if (item.getItemId() == R.id.option_delete) {
                        fragment.getHero().removeConnection(connection);
                        adapter.remove(connection);
                    } else if (item.getItemId() == R.id.option_edit) {
                        NotesEditFragment.edit(connection, fragment.getActivity(),
                                DsaTabActivity.ACTION_EDIT_NOTES);
                        mode.finish();
                        break;
                    }

                }
            }
            mode.finish();
            return true;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menuitem_edit, menu);
            mode.getMenuInflater().inflate(R.menu.menuitem_delete, menu);
            mode.setTitle("Notizen");
            return super.onCreateActionMode(mode,menu);
        }


        /*
         * (non-Javadoc)
         *
         * @see ActionMode.Callback#onPrepareActionMode (ActionMode, Menu)
         */
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            RecyclerView list = listView.get();
            ListableFragment fragment = listFragment.get();
            if (list == null || fragment == null)
                return false;


            MenuItem view = menu.findItem(R.id.option_delete);
            int selected = 0;
            boolean allDeletable = true;
            ListableItemAdapter adapter = WrapperAdapterUtils.findWrappedAdapter(list.getAdapter(), ListableItemAdapter.class);
            for (int index : getManager().getSelectedPositions()) {
                Object obj = adapter.getItem(index);
                selected++;
                if (obj instanceof Event) {
                    Event event = (Event) obj;
                    allDeletable &= event.isDeletable();
                }
            }
            mode.setSubtitle(selected + " ausgewählt");

            return ViewUtils.menuIconState(view,allDeletable);
        }
    }

    private Callback mItemsCallback;
    private Callback mModifiersCallback;
    private Callback mCustomProbeCallback;
    private Callback mTalentCallback;
    private Callback mSpellCallback;
    private Callback mArtCallback;
    private Callback mNotesCallback;

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
                modificator.setModificatorName(data.getStringExtra(ModificatorEditFragment.INTENT_NAME));
                modificator.setRules(data.getStringExtra(ModificatorEditFragment.INTENT_RULES));
                modificator.setComment(data.getStringExtra(ModificatorEditFragment.INTENT_COMMENT));
                modificator.setActive(data.getBooleanExtra(ModificatorEditFragment.INTENT_ACTIVE, true));

                getHero().addModificator(modificator);

            }
        } else if (requestCode == DsaTabActivity.ACTION_EDIT_MODIFICATOR) {
            if (resultCode == Activity.RESULT_OK) {

                UUID id = (UUID) data.getSerializableExtra(ModificatorEditFragment.INTENT_ID);

                for (Modificator modificator : getHero().getUserModificators()) {
                    if (modificator instanceof CustomModificator) {
                        CustomModificator customModificator = (CustomModificator) modificator;
                        if (customModificator.getId().equals(id)) {
                            customModificator.setModificatorName(data
                                    .getStringExtra(ModificatorEditFragment.INTENT_NAME));
                            customModificator.setRules(data.getStringExtra(ModificatorEditFragment.INTENT_RULES));
                            customModificator.setActive(data.getBooleanExtra(ModificatorEditFragment.INTENT_ACTIVE,
                                    true));
                            customModificator.setComment(data.getStringExtra(ModificatorEditFragment.INTENT_COMMENT));
                        }
                    }
                }

            }
        } else if (requestCode == DsaTabActivity.ACTION_EDIT_NOTES) {
            if (resultCode == Activity.RESULT_OK) {
                fillListItems(getHero());
            }
        } else if (requestCode == DsaTabActivity.ACTION_EDIT_CUSTOM_PROBES) {
            if (resultCode == Activity.RESULT_OK) {
                fillListItems(getHero());
            }
        } else if (requestCode == DsaTabActivity.ACTION_EDIT_TABS) {
            if (resultCode == Activity.RESULT_OK) {
                fillListItems(getHero());
            }
        } else if (requestCode == DsaTabActivity.ACTION_VIEW_ART) {
            if (resultCode == Activity.RESULT_OK) {
                ArtInfo artInfo = (ArtInfo) data.getSerializableExtra(ArtInfoFragment.DATA_INTENT_ART_INFO);
                if (artInfo != null) {
                    int value = data.getIntExtra(ArtInfoFragment.DATA_INTENT_ART_VALUE, Integer.MIN_VALUE);

                    Art art = getHero().getArt(artInfo.getName());

                    if (art != null) {

                        RuntimeExceptionDao<ArtInfo, Long> dao = DsaTabApplication.getInstance().getDBHelper()
                                .getRuntimeExceptionDao(ArtInfo.class);
                        dao.createOrUpdate(artInfo);

                        if (value != Integer.MIN_VALUE) {
                            art.setValue(value);
                        }
                        art.setInfo(artInfo);
                        art.setProbePattern(artInfo.getProbe());
                        art.fireValueChangedEvent();

                        ViewUtils.snackbar(getActivity(), "Kunstinformationen wurden gespeichert", Snackbar.LENGTH_SHORT)
                                ;
                    }
                }
            }
        } else if (requestCode == DsaTabActivity.ACTION_VIEW_SPELL) {
            if (resultCode == Activity.RESULT_OK) {
                SpellInfo spellInfo = (SpellInfo) data.getSerializableExtra(SpellInfoFragment.DATA_INTENT_SPELL_INFO);
                if (spellInfo != null) {
                    int value = data.getIntExtra(SpellInfoFragment.DATA_INTENT_SPELL_VALUE, Integer.MIN_VALUE);
                    String comment = data.getStringExtra(SpellInfoFragment.DATA_INTENT_SPELL_COMMENT);
                    String variant = data.getStringExtra(SpellInfoFragment.DATA_INTENT_SPELL_VARIANT);
                    Spell spell = getHero().getSpell(spellInfo.getName());

                    if (spell != null) {

                        RuntimeExceptionDao<SpellInfo, Long> dao = DsaTabApplication.getInstance().getDBHelper()
                                .getRuntimeExceptionDao(SpellInfo.class);
                        dao.createOrUpdate(spellInfo);

                        if (value != Integer.MIN_VALUE) {
                            spell.setValue(value);
                        }
                        spell.setComments(comment);
                        spell.setVariant(variant);
                        spell.setInfo(spellInfo);
                        spell.setProbePattern(spellInfo.getProbe());
                        spell.fireValueChangedEvent();

                        ViewUtils.snackbar(getActivity(), "Zauberinformationen wurden gespeichert", Snackbar.LENGTH_SHORT)
                                ;

                    }
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected Callback getActionModeCallback(List<Object> objects) {

        for (Object o : objects) {
            if (o instanceof EquippedItem)
                return mItemsCallback;
            else if (o instanceof CustomModificator)
                return mModifiersCallback;
            else if (o instanceof CustomProbe)
                return mCustomProbeCallback;
            else if (o instanceof Talent)
                return mTalentCallback;
            else if (o instanceof Spell)
                return mSpellCallback;
            else if (o instanceof Art)
                return mArtCallback;
            else if (o instanceof NotesItem)
                return mNotesCallback;
        }

        return null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        ListSettings listSettings = getListSettings();
        if (listSettings != null) {
            if (listSettings.hasListItem(ListItemType.EquippedItem) && menu.findItem(R.id.option_set) == null) {
                inflater.inflate(R.menu.menuitem_set, menu);
            }

            if (listSettings.hasListItem(ListItemType.Document) && menu.findItem(R.id.option_documents_choose) == null) {
                inflater.inflate(R.menu.documents_menu, menu);
            }

            if (listSettings.hasListItem(ListItemType.Notes) && menu.findItem(R.id.option_note_filter) == null) {
                inflater.inflate(R.menu.note_list_menu, menu);

                if (menu.findItem(R.id.option_note_filter) != null) {
                    SubMenu filterSet = menu.findItem(R.id.option_note_filter).getSubMenu();
                    EventCategory[] eventCategory = EventCategory.values();

                    for (int i = 0; i < eventCategory.length; i++) {
                        MenuItem item = filterSet.add(MENU_FILTER_GROUP, i, Menu.NONE, eventCategory[i].name())
                                .setIcon(eventCategory[i].getDrawableId());
                        item.setCheckable(true);
                        item.setChecked(getListSettings().getEventCategories()
                                .contains(eventCategory[item.getItemId()]));
                    }
                }

            }
        }

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (getListSettings() != null) {
            if (menu.findItem(R.id.option_set) != null) {
                menu.findItem(R.id.option_set).setVisible(
                        !isDrawerOpened() && getListSettings().hasListItem(ListItemType.EquippedItem));
            }
            if (menu.findItem(R.id.option_documents_choose) != null) {
                menu.findItem(R.id.option_documents_choose).setVisible(
                        !isDrawerOpened() && getListSettings().hasListItem(ListItemType.Document));
            }

            if (menu.findItem(R.id.option_note_filter) != null) {
                menu.findItem(R.id.option_note_filter).setVisible(!isDrawerOpened());
                SubMenu filterSet = menu.findItem(R.id.option_note_filter).getSubMenu();
                EventCategory[] eventCategory = EventCategory.values();
                for (int i = 0; i < filterSet.size(); i++) {
                    MenuItem item = filterSet.getItem(i);
                    item.setChecked(getListSettings().getEventCategories().contains(eventCategory[item.getItemId()]));
                }
            }
        }

    }

    public boolean onAction(int actionId) {
        fabMenu.close(true);

        switch (actionId) {
            case ACTION_NOTES_RECORD: {
                recordEvent();
                return true;
            }
            case ACTION_NOTES_ADD: {
                NotesEditFragment.insert(getActivity(), DsaTabActivity.ACTION_EDIT_NOTES);
                return true;
            }
            case ACTION_CUSTOM_PROBE_ADD: {
                CustomProbeEditFragment.insert(getActivity(), DsaTabActivity.ACTION_EDIT_CUSTOM_PROBES);
                return true;
            }
            case ACTION_MODIFICATOR_ADD: {
                ModificatorEditFragment.insert(getActivity(), DsaTabActivity.ACTION_ADD_MODIFICATOR);
                return true;
            }
            case ACTION_DOCUMENTS_CHOOSE: {
                OnDirectoryChooserListener resultListener = new OnDirectoryChooserListener() {
                    /*
                     * (non-Javadoc)
                     *
                     * @see com.dsatab.view.DirectoryChooserDialogHelper.Result# onChooseDirectory(java.lang.String)
                     */
                    @Override
                    public void onChooseDirectory(String dir) {

                        File directory = new File(dir);
                        if (directory.exists()) {
                            if (getHero() != null) {
                                getHero().getHeroConfiguration().setProperty(
                                        DsaTabPreferenceActivity.KEY_CUSTOM_DIRECTORY + DsaTabPreferenceActivity.DIR_PDFS,
                                        dir);
                            }
                            fillListItems(getHero());
                        } else {
                            ViewUtils.snackbar(getActivity(), "Verzeichnis existiert nicht. Wähle bitte ein anderes aus.",
                                    Snackbar.LENGTH_LONG);
                        }
                    }
                };

                File docFile = null;
                if (getHero() != null) {
                    String dir = getHero().getHeroConfiguration().getProperty(
                            DsaTabPreferenceActivity.KEY_CUSTOM_DIRECTORY + DsaTabPreferenceActivity.DIR_PDFS);
                    if (!TextUtils.isEmpty(dir)) {
                        docFile = new File(dir);
                    }
                }

                if (docFile == null || !docFile.isDirectory()) {
                    docFile = DsaTabApplication.getDirectory(DsaTabPreferenceActivity.DIR_PDFS);
                }
                DirectoryChooserDialog.show(this, docFile.getAbsolutePath(), resultListener, 0);
                return true;
            }

        }
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.actionbarsherlock.app.SherlockFragment#onOptionsItemSelected(com. actionbarsherlock.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getGroupId() == MENU_FILTER_GROUP) {

            item.setChecked(!item.isChecked());

            EventCategory category = EventCategory.values()[item.getItemId()];
            if (item.isChecked())
                getListSettings().getEventCategories().add(category);
            else
                getListSettings().getEventCategories().remove(category);

            mAdapter.filter(getListSettings());
            return true;
        }

        if (item.getItemId() == R.id.option_documents_choose) {
            return onAction(ACTION_DOCUMENTS_CHOOSE);
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
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = configureContainerView(inflater.inflate(R.layout.sheet_list, container, false));

        recyclerView = (RecyclerView) root.findViewById(android.R.id.list);

        fabMenu = (FloatingActionMenu) root.findViewById(R.id.fab_menu);

        return root;
    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);

        recyclerView.setHasFixedSize(false);

        mAdapter = new ListableItemAdapter(this, null, getListSettings());
        mAdapter.setProbeListener(getProbeListener());
        mAdapter.setTargetListener(getTargetListener());
        mAdapter.setEditListener(getEditListener());
        mAdapter.setActionListener(this);
        mAdapter.setEventListener(this);

        initRecyclerView(recyclerView, mAdapter, false, false, true);

        mRecyclerViewSelectionManager.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

        mTalentCallback = new TalentActionMode(this, recyclerView, mRecyclerViewSelectionManager);
        mSpellCallback = new SpellActionMode(this, recyclerView, mRecyclerViewSelectionManager);
        mArtCallback = new ArtActionMode(this, recyclerView, mRecyclerViewSelectionManager);
        mModifiersCallback = new ModifierActionMode(this, recyclerView, mRecyclerViewSelectionManager);
        mItemsCallback = new EquippedItemActionMode(this, recyclerView, mRecyclerViewSelectionManager);
        mCustomProbeCallback = new CustomProbeActionMode(this, recyclerView, mRecyclerViewSelectionManager);
        mCallback = new NoteActionMode(this, recyclerView, mRecyclerViewSelectionManager);
    }



    @Override
    public void onPause() {
        //mRecyclerViewDragDropManager.cancelDrag();
        super.onPause();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mAdapter = null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.dsatab.activity.BaseMenuActivity#onHeroLoaded(com.dsatab.data.Hero)
     */
    @Override
    public void onHeroLoaded(Hero hero) {

        mAdapter.setHero(hero);
        fillListItems(hero);
        mAdapter.filter(getListSettings());

        mNotesCallback = new NoteActionMode(this, recyclerView, mRecyclerViewSelectionManager);

        refreshEmptyView(mAdapter);
    }

    @Override
    public void onModifierAdded(Modificator value) {
        if (getListSettings().hasListItem(ListItemType.Modificator)) {
            mAdapter.add(value);
        }
        // fightItemAdapter.sort(AbstractModificator.NAME_COMPARATOR);
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
        if (getListSettings().hasListItem(ListItemType.Modificator)) {
            mAdapter.remove(value);
        }

    }

    @Override
    public void onModifierChanged(Modificator value) {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onModifiersChanged(List<Modificator> values) {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onValueChanged(Value value) {
        if (value == null) {
            return;
        }

        if (getListSettings() != null && getListSettings().isAffected(value)) {
            mAdapter.notifyDataSetChanged();
        } else {

            if (value instanceof Attribute) {
                Attribute attr = (Attribute) value;

                if (attr.getType() != null) {
                    switch (attr.getType()) {
                        case Behinderung: {
                            mAdapter.notifyDataSetChanged();
                            break;
                        }
                        case Körperkraft:
                            if (getListSettings() != null && getListSettings().hasListItem(ListItemType.EquippedItem)) {
                                mAdapter.notifyDataSetChanged();
                            }
                            break;
                        default:
                            // do nothing
                            break;
                    }
                }
            }
        }
    }

    private void fillListItems(Hero hero) {
        fabMenu.removeAllMenuButtons();
        int fabItems =0;
        mAdapter.clear();
        if (getListSettings() != null && getListSettings().getListItems() != null) {
            for (ListItem listItem : getListSettings().getListItems()) {
                switch (listItem.getType()) {
                    case Talent:
                        if (TextUtils.isEmpty(listItem.getName())) {

                            for (TalentGroupType talentGroupType : TalentGroupType.values()) {
                                TalentGroup talentGroup = hero.getTalentGroup(talentGroupType);

                                if (talentGroup != null && !talentGroup.getTalents().isEmpty()) {
                                    mAdapter.add(new HeaderListItem(talentGroupType.name()));
                                    mAdapter.addAll(talentGroup.getTalents());
                                }
                            }

                        } else {
                            try {
                                TalentGroupType talentGroupType = TalentGroupType.valueOf(listItem.getName());
                                TalentGroup talentGroup = hero.getTalentGroup(talentGroupType);
                                if (talentGroup != null && !talentGroup.getTalents().isEmpty()) {
                                    mAdapter.addAll(talentGroup.getTalents());
                                }
                            } catch (IllegalArgumentException e) {
                                // if its no talentgrouptype name try adding talent by name
                                Talent talent = hero.getTalent(listItem.getName());
                                if (talent != null) {
                                    mAdapter.add(talent);
                                }
                            }
                        }
                        break;
                    case Spell:
                        if (TextUtils.isEmpty(listItem.getName())) {
                            mAdapter.addAll(hero.getSpells().values());
                        } else {
                            Spell spell = hero.getSpell(listItem.getName());
                            if (spell != null) {
                                mAdapter.add(spell);
                            }
                        }
                        break;
                    case Art:
                        if (TextUtils.isEmpty(listItem.getName())) {
                            mAdapter.addAll(hero.getArts().values());
                        } else {
                            Art art = hero.getArt(listItem.getName());
                            if (art != null) {
                                mAdapter.add(art);
                            }
                        }
                        break;
                    case Attribute:
                        if (TextUtils.isEmpty(listItem.getName())) {
                            mAdapter.add(new HeaderListItem(ListItemType.Attribute));
                            mAdapter.addAll(hero.getAttributes().values());
                        } else {
                            try {
                                AttributeType attributeType = AttributeType.valueOf(listItem.getName());
                                Attribute attr = hero.getAttribute(attributeType);
                                if (attr != null) {

                                    switch (attr.getType()) {
                                        case Astralenergie_Aktuell:
                                        case Astralenergie:
                                            Integer ae = hero.getAttributeValue(AttributeType.Astralenergie);
                                            if (ae != null && ae > 0) {
                                                mAdapter.add(attr);
                                            }
                                            break;
                                        case Karmaenergie_Aktuell:
                                        case Karmaenergie:
                                            Integer ke = hero.getAttributeValue(AttributeType.Karmaenergie);
                                            if (ke != null && ke > 0) {
                                                mAdapter.add(attr);
                                            }
                                            break;
                                        default:
                                            mAdapter.add(attr);
                                    }

                                }
                            } catch (IllegalArgumentException e) {
                                Debug.error(e);
                            }
                        }
                        break;
                    case EquippedItem:
                        if (TextUtils.isEmpty(listItem.getName())) {
                            mAdapter.add(new HeaderListItem(ListItemType.EquippedItem));
                            mAdapter.addAll(hero.getEquippedItems());

                            addWaffenloseTalente();
                        } else {
                            EquippedItem item = hero.getEquippedItem(getHero().getActiveSet(), listItem.getName());
                            if (item != null) {
                                mAdapter.add(item);
                            }
                        }
                        break;
                    case Modificator:
                        if (TextUtils.isEmpty(listItem.getName())) {
                            mAdapter.add(new HeaderListItem(ListItemType.Modificator));
                            mAdapter.addAll(hero.getUserModificators());
                        } else {
                            mAdapter.add(hero.getUserModificators(listItem.getName()));
                        }
                        // mAdapter.add(new FooterListItem(ListItemType.Modificator));

                        FloatingActionButton modAdd = new FloatingActionButton(getActivity());
                        modAdd.setColorPressed(getResources().getColor(R.color.white_pressed));
                        modAdd.setColorNormal(getResources().getColor(R.color.white));
                        modAdd.setImageDrawable(ResourcesCompat.getDrawable(modAdd.getContext(),R.drawable.vd_orb_direction));
                        modAdd.setOnClickListener(new OnClickActionListenerDelegate(ACTION_MODIFICATOR_ADD, this));
                        fabMenu.addMenuButton(modAdd);
                        fabItems++;

                        break;
                    case Header:
                        try {
                            ListItemType subType = ListItemType.valueOf(listItem.getName());
                            mAdapter.add(new HeaderListItem(subType.title(), subType));
                        } catch (Exception e) {
                            mAdapter.add(new HeaderListItem(listItem.getName()));
                        }
                        break;
                    case Wound:
                        mAdapter.add(new HeaderListItem(ListItemType.Wound));
                        mAdapter.add(new WoundListItem());
                        break;
                    case Document:
                        mAdapter.add(new HeaderListItem(ListItemType.Document));
                        File pdfsDir = null;

                        String dir = getHero().getHeroConfiguration().getProperty(
                                DsaTabPreferenceActivity.KEY_CUSTOM_DIRECTORY + DsaTabPreferenceActivity.DIR_PDFS);
                        if (!TextUtils.isEmpty(dir)) {
                            pdfsDir = new File(dir);
                        }
                        if (pdfsDir == null || !pdfsDir.isDirectory()) {
                            pdfsDir = DsaTabApplication.getDirectory(DsaTabPreferenceActivity.DIR_PDFS);
                        }

                        if (pdfsDir != null && pdfsDir.exists() && pdfsDir.isDirectory()) {
                            File[] pdfFiles = pdfsDir.listFiles(new FileFileFilter());
                            List<File> documents;
                            if (pdfFiles != null) {
                                documents = Arrays.asList(pdfFiles);
                                Collections.sort(documents, new Util.FileNameComparator());
                            } else {
                                documents = Collections.emptyList();
                                String path = pdfsDir.getAbsolutePath();
                                ViewUtils.snackbar(getActivity(),
                                        Util.getText(R.string.message_documents_empty, path).toString(), Snackbar.LENGTH_SHORT)
                                        ;
                            }
                            for (File file : documents) {
                                mAdapter.add(new FileListable(file));
                            }
                        }

                        // mAdapter.add(new FooterListItem(ListItemType.Document));
                        break;
                    case Notes:
                        if (TextUtils.isEmpty(listItem.getName())) {
                            mAdapter.add(new HeaderListItem(ListItemType.Notes));
                            mAdapter.addAll(getHero().getEvents());
                            mAdapter.addAll(getHero().getConnections());
                        } else {
                            for (NotesItem notesItem : getHero().getEvents()) {
                                if (notesItem.getCategory().name().equals(listItem.getName())) {
                                    mAdapter.add(notesItem);
                                }
                            }
                            for (NotesItem notesItem : getHero().getConnections()) {
                                if (notesItem.getCategory().name().equals(listItem.getName())) {
                                    mAdapter.add(notesItem);
                                }
                            }
                        }

                        FloatingActionButton notesRecord = new FloatingActionButton(getActivity());
                        notesRecord.setColorPressed(getResources().getColor(R.color.white_pressed));
                        notesRecord.setColorNormal(getResources().getColor(R.color.white));
                        notesRecord.setImageDrawable(ResourcesCompat.getDrawable(notesRecord.getContext(), R.drawable.vd_nothing_to_say));
                        notesRecord.setOnClickListener(new OnClickActionListenerDelegate(ACTION_NOTES_RECORD, this));
                        fabMenu.addMenuButton(notesRecord);
                        fabItems++;

                        FloatingActionButton notesAdd = new FloatingActionButton(getActivity());
                        notesAdd.setColorPressed(getResources().getColor(R.color.white_pressed));
                        notesAdd.setColorNormal(getResources().getColor(R.color.white));
                        notesAdd.setImageDrawable(ResourcesCompat.getDrawable(notesAdd.getContext(), R.drawable.vd_tied_scroll));
                        notesAdd.setOnClickListener(new OnClickActionListenerDelegate(ACTION_NOTES_ADD, this));
                        fabMenu.addMenuButton(notesAdd);
                        fabItems++;
                        // mAdapter.add(new FooterListItem(ListItemType.Notes));
                        break;
                    case Purse:
                        if (TextUtils.isEmpty(listItem.getName())) {
                            mAdapter.add(new HeaderListItem(ListItemType.Purse));
                            PurseListable purseListable = new PurseListable(null);
                            mAdapter.add(purseListable);
                        } else {
                            Currency currency = Currency.valueOf(listItem.getName());
                            PurseListable purseListable = new PurseListable(currency);
                            mAdapter.add(purseListable);
                        }
                        break;

                    case Probe:
                        if (TextUtils.isEmpty(listItem.getName())) {
                            mAdapter.add(new HeaderListItem(ListItemType.Probe));
                            mAdapter.addAll(hero.getHeroConfiguration().getCustomProbes());
                            // mAdapter.add(new FooterListItem(ListItemType.Probe));
                        } else {
                            CustomProbe probe = hero.getHeroConfiguration().getCustomProbe(listItem.getName());
                            if (probe != null) {
                                mAdapter.add(probe);
                            }
                        }

                        FloatingActionButton probeAdd = new FloatingActionButton(getActivity());
                        probeAdd.setColorPressed(getResources().getColor(R.color.white_pressed));
                        probeAdd.setColorNormal(getResources().getColor(R.color.white));
                        probeAdd.setImageDrawable(ResourcesCompat.getDrawable(probeAdd.getContext(), R.drawable.vd_dice_six_faces_two));
                        probeAdd.setOnClickListener(new OnClickActionListenerDelegate(ACTION_CUSTOM_PROBE_ADD, this));
                        fabMenu.addMenuButton(probeAdd);
                        fabItems++;

                        break;
                }

            }
        }

        if (fabItems == 0 ) {
            fabMenu.setVisibility(View.GONE);
        } else
            fabMenu.setVisibility(View.VISIBLE);
    }

    private void recordEvent() {
        try {

            File recordingsDir = DsaTabApplication.getDirectory(DsaTabApplication.DIR_RECORDINGS);

            final File currentAudio = new File(recordingsDir, "last.3gp");

            final MediaRecorder mediaRecorder = new MediaRecorder();

            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            mediaRecorder.setOutputFile(currentAudio.getAbsolutePath());
            mediaRecorder.prepare();
            mediaRecorder.start(); // Recording is now started

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.recording);
            builder.setMessage(R.string.recording_message);
            builder.setPositiveButton(R.string.label_save, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (mediaRecorder != null) {
                        try {
                            mediaRecorder.stop();
                        } catch (IllegalStateException e) {
                            Debug.warning("Couldn't stop mediaRecorder something went wrong.", e);
                        } finally {
                            mediaRecorder.reset();
                        }
                    }

                    File nowAudio = new File(DsaTabApplication.getDirectory(DsaTabApplication.DIR_RECORDINGS), System
                            .currentTimeMillis() + ".3gp");
                    currentAudio.renameTo(nowAudio);

                    NotesEditFragment.edit(null, nowAudio.getAbsolutePath(), getActivity(),
                            DsaTabActivity.ACTION_EDIT_NOTES);
                }
            });

            builder.setNegativeButton(R.string.label_cancel, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (mediaRecorder != null) {
                        try {
                            mediaRecorder.stop();
                        } catch (IllegalStateException e) {
                            Debug.warning("Couldn't stop mediaRecorder something went wrong.", e);
                        } finally {
                            mediaRecorder.reset();
                        }
                    }
                    currentAudio.delete();
                }
            });

            AlertDialog dialog = builder.show();

            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (mediaRecorder != null) {
                        mediaRecorder.release();
                    }
                }
            });

        } catch (IllegalStateException e) {
            Debug.error(e);
        } catch (IOException e) {
            Debug.error(e);
        }
    }

    private void addWaffenloseTalente() {
        Item raufen = DataManager.getItemByName(TalentType.Raufen.xmlName());
        Weapon raufenSpec = (Weapon) raufen.getSpecification(Weapon.class);
        if (raufenSpec != null) {
            EquippedItem raufenEquipped = new EquippedItem(getHero(), getHero().getCombatTalent(
                    raufenSpec.getTalentType()), raufen, raufenSpec);
            mAdapter.add(raufenEquipped);
        }
        Item ringen = DataManager.getItemByName(TalentType.Ringen.xmlName());
        Weapon ringenSpec = (Weapon) ringen.getSpecification(Weapon.class);
        if (ringenSpec != null) {
            EquippedItem ringenEquipped = new EquippedItem(getHero(), getHero().getCombatTalent(
                    ringenSpec.getTalentType()), ringen, ringenSpec);
            mAdapter.add(ringenEquipped);
        }

        if (getHero().hasFeature(FeatureType.WaffenloserKampfstilHruruzat)) {
            Item hruruzat = DataManager.getItemByName("Hruruzat");
            Weapon hruruzatSpec = (Weapon) hruruzat.getSpecification(Weapon.class);
            if (hruruzatSpec != null) {
                EquippedItem hruruzatEquipped = new EquippedItem(getHero(), getHero().getCombatTalent(
                        hruruzatSpec.getTalentType()), hruruzat, hruruzatSpec);
                mAdapter.add(hruruzatEquipped);
            }
        }
    }

    @Override
    public void onItemClicked(BaseRecyclerAdapter adapter, int position, View v) {
        if (mMode == null) {
            Object object = mAdapter.getItem(position);

            if (object instanceof AbstractModificator) {
                AbstractModificator modificator = (AbstractModificator) object;
                modificator.setActive(!modificator.isActive());
            } else if (object instanceof Probe) {
                DiceSliderFragment.show(this, getBeing(), (Probe) object, true, 0);
            } else if (object instanceof FileListable) {
                FileListable fileListable = (FileListable) object;
                File file = fileListable.getFile();

                if (file.exists() && file.isFile()) {
                    Uri path = Uri.fromFile(file);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    String ext = MimeTypeMap.getFileExtensionFromUrl(path.toString());
                    if (ext != null)
                        intent.setDataAndType(path, MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext));
                    else {
                        intent.setData(path);
                    }
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        ViewUtils.snackbar(getActivity(),
                                "Keine App zum Betrachten von " + file.getName() + " gefunden", Snackbar.LENGTH_SHORT)
                                ;
                    }
                }
            } else if (object instanceof Event) {
                Event event = (Event) object;

                if (event.getAudioPath() != null) {
                    try {

                        MediaPlayer mediaPlayer = new MediaPlayer();
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                        mediaPlayer.setDataSource(event.getAudioPath());
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                mp.stop();
                                mp.reset();
                                mp.release();
                            }
                        });
                    } catch (IllegalArgumentException e) {
                        Debug.error(e);
                    } catch (IllegalStateException e) {
                        Debug.error(e);
                    } catch (IOException e) {
                        Debug.error(e);
                    }

                }
            }
            RecyclerView.ViewHolder holder = RecyclerViewAdapterUtils.getViewHolder(v);
            mRecyclerViewSelectionManager.setSelected(holder, false);
        } else {
            super.onItemClicked(adapter, position,v);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.dsatab.fragment.BaseFragment#onActiveSetChanged(int, int)
     */
    @Override
    public void onActiveSetChanged(int newSet, int oldSet) {
        fillListItems(getHero());
        if (getActivity() != null) {
            getActivity().invalidateOptionsMenu();
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
            mAdapter.notifyDataSetChanged();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.dsatab.view.listener.InventoryChangedListener#onItemEquipped(com. dsatab.data.items.EquippedItem)
     */
    @Override
    public void onItemEquipped(EquippedItem item) {
        if (item.getSet() == getHero().getActiveSet()) {
            mAdapter.add(item);
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
            mAdapter.remove(item);
        }

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
