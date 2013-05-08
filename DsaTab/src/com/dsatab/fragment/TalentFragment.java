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
package com.dsatab.fragment;

import java.util.List;

import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.dsatab.R;
import com.dsatab.activity.DsaTabActivity;
import com.dsatab.data.Attribute;
import com.dsatab.data.CombatMeleeAttribute;
import com.dsatab.data.CombatTalent;
import com.dsatab.data.Hero;
import com.dsatab.data.MetaTalent;
import com.dsatab.data.Talent;
import com.dsatab.data.Value;
import com.dsatab.data.adapter.ExpandableTalentAdapter;
import com.dsatab.data.enums.AttributeType;
import com.dsatab.data.modifier.Modificator;
import com.dsatab.util.Util;
import com.dsatab.view.FilterSettings;
import com.dsatab.view.FilterSettings.FilterType;
import com.dsatab.view.ListFilterSettings;
import com.dsatab.view.listener.HeroChangedListener;
import com.gandulf.guilib.util.Debug;

/**
 * 
 * 
 */
public class TalentFragment extends BaseListFragment implements HeroChangedListener, OnItemLongClickListener {

	private static final String PREF_KEY_GROUP_EXPANDED = "GROUP_EXPANDED";

	private ExpandableListView talentList = null;

	private ExpandableTalentAdapter talentAdapter = null;

	private final class TalentActionMode implements ActionMode.Callback {
		@Override
		public boolean onActionItemClicked(ActionMode mode, com.actionbarsherlock.view.MenuItem item) {
			boolean notifyChanged = false;

			SparseBooleanArray checkedPositions = talentList.getCheckedItemPositions();
			if (checkedPositions != null) {
				for (int i = checkedPositions.size() - 1; i >= 0; i--) {
					if (checkedPositions.valueAt(i)) {
						Object obj = talentList.getItemAtPosition(checkedPositions.keyAt(i));
						if (obj instanceof Talent) {
							Talent talent = (Talent) obj;
							switch (item.getItemId()) {
							case R.id.option_edit_talent:
								DsaTabActivity.showEditPopup(getActivity(), talent);
								mode.finish();
								return true;
							case R.id.option_mark_favorite_talent:
								talent.setFavorite(true);
								notifyChanged = true;
								break;
							case R.id.option_mark_unused_talent:
								talent.setUnused(true);
								notifyChanged = true;
								break;
							case R.id.option_unmark_talent:
								talent.setFavorite(false);
								talent.setUnused(false);
								notifyChanged = true;
								break;
							default:
								return false;
							}
						} else {
							return false;
						}
					}

				}
				if (notifyChanged) {
					talentAdapter.notifyDataSetChanged();
				}
			}
			mode.finish();
			return true;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			mode.getMenuInflater().inflate(R.menu.talent_popupmenu, menu);
			mode.setTitle("Talente");
			return true;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			mMode = null;
			talentList.clearChoices();
			talentAdapter.notifyDataSetChanged();
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			SparseBooleanArray checkedPositions = talentList.getCheckedItemPositions();
			int selected = 0;
			boolean metaTalent = false;
			if (checkedPositions != null) {
				for (int i = checkedPositions.size() - 1; i >= 0; i--) {
					if (checkedPositions.valueAt(i)) {
						selected++;
						if (!metaTalent) {
							Object obj = talentList.getItemAtPosition(checkedPositions.keyAt(i));
							if (obj instanceof MetaTalent) {
								metaTalent = true;
							}
						}
					}
				}
			}

			mode.setSubtitle(selected + " ausgewählt");

			if (metaTalent || selected != 1) {
				if (menu.findItem(R.id.option_edit_talent).isEnabled()) {
					menu.findItem(R.id.option_edit_talent).setEnabled(false);
					return true;
				} else {
					return false;
				}
			} else {
				if (!menu.findItem(R.id.option_edit_talent).isEnabled()) {
					menu.findItem(R.id.option_edit_talent).setEnabled(true);
					return true;
				} else {
					return false;
				}
			}

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
		mCallback = new TalentActionMode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = configureContainerView(inflater.inflate(R.layout.sheet_talent, container, false));

		talentList = (ExpandableListView) root.findViewById(R.id.talent_list);
		talentList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		talentList.setOnItemLongClickListener(this);
		talentList.setGroupIndicator(getResources().getDrawable(
				Util.getThemeResourceId(getActivity(), R.attr.imgExpander)));
		talentList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
				if (mMode == null) {
					Talent talent = talentAdapter.getChild(groupPosition, childPosition);
					getBaseActivity().checkProbe(talent);
				} else {
					int pos = talentList.getPositionForView(v);
					talentList.setItemChecked(pos, !talentList.isItemChecked(pos));
					TalentFragment.this.onItemClick(parent, v, pos, id);
				}
				return true;
			}
		});

		talentList.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

			@Override
			public void onGroupCollapse(int groupPosition) {
				Editor edit = preferences.edit();
				edit.putBoolean(PREF_KEY_GROUP_EXPANDED + groupPosition, false);
				edit.commit();
			}
		});
		talentList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

			@Override
			public void onGroupExpand(int groupPosition) {
				Editor edit = preferences.edit();
				edit.putBoolean(PREF_KEY_GROUP_EXPANDED + groupPosition, true);
				edit.commit();
			}
		});

		return root;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.dsatab.activity.BaseMenuActivity#onHeroLoaded(com.dsatab.data.Hero)
	 */
	@Override
	public void onHeroLoaded(Hero hero) {
		loadHeroTalents(hero);
	}

	public void onValueChanged(Value value) {
		if (value == null) {
			return;
		}

		if (value instanceof Attribute) {
			Attribute attribute = (Attribute) value;
			if (attribute.getType() == AttributeType.Behinderung) {
				talentAdapter.notifyDataSetChanged();
			}
		} else if (value instanceof Talent || value instanceof CombatMeleeAttribute || value instanceof CombatTalent) {
			talentAdapter.notifyDataSetChanged();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.dsatab.fragment.BaseFragment#onModifierAdded(com.dsatab.data.modifier
	 * .Modificator)
	 */
	@Override
	public void onModifierAdded(Modificator value) {
		talentAdapter.notifyDataSetChanged();
		super.onModifierAdded(value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.dsatab.fragment.BaseFragment#onModifierChanged(com.dsatab.data.modifier
	 * .Modificator)
	 */
	@Override
	public void onModifierChanged(Modificator value) {
		talentAdapter.notifyDataSetChanged();
		super.onModifierChanged(value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.dsatab.fragment.BaseFragment#onModifierRemoved(com.dsatab.data.modifier
	 * .Modificator)
	 */
	@Override
	public void onModifierRemoved(Modificator value) {
		talentAdapter.notifyDataSetChanged();
		super.onModifierRemoved(value);
	}

	protected ListFilterSettings getFilterSettings() {
		return (ListFilterSettings) super.getFilterSettings();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.fragment.BaseFragment#onModifiersChanged(java.util.List)
	 */
	@Override
	public void onModifiersChanged(List<Modificator> values) {
		talentAdapter.notifyDataSetChanged();
		super.onModifiersChanged(values);
	}

	private void loadHeroTalents(Hero hero) {

		talentAdapter = new ExpandableTalentAdapter(getActivity(), hero, getFilterSettings());

		talentAdapter.setProbeListener(getBaseActivity().getProbeListener());
		talentAdapter.setEditListener(getBaseActivity().getEditListener());
		talentList.setAdapter(talentAdapter);

		for (int i = 0; i < talentAdapter.getGroupCount(); i++) {
			if (preferences.getBoolean(PREF_KEY_GROUP_EXPANDED + i, true))
				talentList.expandGroup(i);
			else
				talentList.collapseGroup(i);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.fragment.BaseFragment#onFilterChanged(com.dsatab.view.
	 * FilterSettings.FilterType, com.dsatab.view.FilterSettings)
	 */
	@Override
	public void onFilterChanged(FilterType type, FilterSettings settings) {
		if (talentAdapter != null && (type == null || type == FilterType.Talent)
				&& settings instanceof ListFilterSettings) {
			talentAdapter.filter((ListFilterSettings) settings);

			Debug.verbose("talent filter " + settings);
		}
	}

}
