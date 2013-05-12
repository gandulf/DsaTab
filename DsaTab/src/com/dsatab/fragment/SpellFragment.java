package com.dsatab.fragment;

import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.dsatab.R;
import com.dsatab.activity.DsaTabActivity;
import com.dsatab.data.Hero;
import com.dsatab.data.Spell;
import com.dsatab.data.Value;
import com.dsatab.data.adapter.SpellAdapter;
import com.dsatab.view.FilterSettings;
import com.dsatab.view.FilterSettings.FilterType;
import com.dsatab.view.ListFilterSettings;
import com.dsatab.view.SpellInfoDialog;
import com.dsatab.view.listener.HeroChangedListener;

/**
 * 
 * 
 */
public class SpellFragment extends BaseListFragment implements OnItemClickListener, HeroChangedListener {

	private ListView spellList;

	private SpellAdapter spellAdapter;

	private final class SpellActionMode implements ActionMode.Callback {
		@Override
		public boolean onActionItemClicked(ActionMode mode, com.actionbarsherlock.view.MenuItem item) {
			boolean notifyChanged = false;

			SparseBooleanArray checkedPositions = spellList.getCheckedItemPositions();
			if (checkedPositions != null) {
				for (int i = checkedPositions.size() - 1; i >= 0; i--) {
					if (checkedPositions.valueAt(i)) {
						Spell spell = spellAdapter.getItem(checkedPositions.keyAt(i));

						switch (item.getItemId()) {
						case R.id.option_edit_spell:
							DsaTabActivity.showEditPopup(getActivity(), spell);
							mode.finish();
							return true;
						case R.id.option_view_spell:
							showInfo(spell);
							mode.finish();
							return true;
						case R.id.option_mark_favorite_spell:
							spell.setFavorite(true);
							notifyChanged = true;
							break;
						case R.id.option_mark_unused_spell:
							spell.setUnused(true);
							notifyChanged = true;
							break;
						case R.id.option_unmark_spell:
							spell.setFavorite(false);
							spell.setUnused(false);
							notifyChanged = true;
							break;
						default:
							return false;
						}
					}
				}
				if (notifyChanged) {
					spellAdapter.notifyDataSetChanged();
				}
			}

			mode.finish();
			return true;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			mode.getMenuInflater().inflate(R.menu.spell_popupmenu, menu);
			mode.setTitle("Zauber");
			return true;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			mMode = null;
			spellList.clearChoices();
			spellAdapter.notifyDataSetChanged();
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			int selected = 0;
			boolean marked = false;
			SparseBooleanArray checkedPositions = spellList.getCheckedItemPositions();
			if (checkedPositions != null) {
				for (int i = checkedPositions.size() - 1; i >= 0; i--) {
					if (checkedPositions.valueAt(i)) {
						selected++;

						Spell spell = spellAdapter.getItem(checkedPositions.keyAt(i));

						marked |= spell.isFavorite() || spell.isUnused();
					}
				}
			}

			mode.setSubtitle(selected + " ausgewählt");

			boolean changed = false;

			if (selected != 1) {
				if (menu.findItem(R.id.option_edit_spell).isEnabled()) {
					menu.findItem(R.id.option_edit_spell).setEnabled(false);
					changed = true;
				}

				if (menu.findItem(R.id.option_view_spell).isEnabled()) {
					menu.findItem(R.id.option_view_spell).setEnabled(false);
					changed = true;
				}
			} else {
				if (!menu.findItem(R.id.option_edit_spell).isEnabled()) {
					menu.findItem(R.id.option_edit_spell).setEnabled(true);
					changed = true;
				}
				if (!menu.findItem(R.id.option_view_spell).isEnabled()) {
					menu.findItem(R.id.option_view_spell).setEnabled(true);
					changed = true;
				}
			}

			if (marked) {
				if (!menu.findItem(R.id.option_unmark_spell).isEnabled()) {
					menu.findItem(R.id.option_unmark_spell).setEnabled(true);
					changed = true;
				}
			} else {
				if (menu.findItem(R.id.option_unmark_spell).isEnabled()) {
					menu.findItem(R.id.option_unmark_spell).setEnabled(false);
					changed = true;
				}
			}

			return changed;
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

		mCallback = new SpellActionMode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = configureContainerView(inflater.inflate(R.layout.sheet_spell, container, false));

		spellList = (ListView) root.findViewById(R.id.spell_list);

		return root;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		// registerForContextMenu(spellList);
		spellList.setOnItemClickListener(this);
		spellList.setOnItemLongClickListener(this);
		spellList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

		super.onActivityCreated(savedInstanceState);
	}

	/**
	 * @param probe
	 */
	private void showInfo(Spell probe) {
		SpellInfoDialog spellInfo = new SpellInfoDialog(getBaseActivity(), getHero());
		spellInfo.setSpell(probe);
		spellInfo.show();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.activity.BaseMenuActivity#onHeroLoaded(com.dsatab.data.Hero)
	 */
	@Override
	public void onHeroLoaded(Hero hero) {
		loadHeroSpells(hero);

		if (hero.getSpells().isEmpty()) {
			findViewById(android.R.id.empty).setVisibility(View.VISIBLE);
			spellList.setVisibility(View.GONE);
		} else {
			findViewById(android.R.id.empty).setVisibility(View.GONE);
			spellList.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onValueChanged(Value value) {
		if (value == null) {
			return;
		}

		if (value instanceof Spell) {
			spellAdapter.notifyDataSetChanged();
		}

	}

	@Override
	protected ListFilterSettings getFilterSettings() {
		return (ListFilterSettings) super.getFilterSettings();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget .AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		if (mMode == null) {
			Spell spell = spellAdapter.getItem(position);
			if (spell != null) {
				getBaseActivity().checkProbe(spell);
			}
			spellList.setItemChecked(position, false);
		} else {
			super.onItemClick(parent, v, position, id);
		}
	}

	private void loadHeroSpells(Hero hero2) {
		spellAdapter = new SpellAdapter(getBaseActivity(), getHero(), getHero().getSpells().values(),
				getFilterSettings());

		spellList.setAdapter(spellAdapter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.fragment.BaseFragment#onFilterChanged(com.dsatab.view. FilterSettings.FilterType, com.dsatab.view.FilterSettings)
	 */
	@Override
	public void onFilterChanged(FilterType type, FilterSettings settings) {
		if (spellAdapter != null && (type == null || type == FilterType.Spell)
				&& settings instanceof ListFilterSettings) {
			spellAdapter.filter((ListFilterSettings) settings);
		}
	}

}
