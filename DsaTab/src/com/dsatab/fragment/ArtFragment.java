package com.dsatab.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.dsatab.R;
import com.dsatab.data.Art;
import com.dsatab.data.Hero;
import com.dsatab.data.Talent;
import com.dsatab.data.TalentGroup;
import com.dsatab.data.Value;
import com.dsatab.data.adapter.ArtAdapter;
import com.dsatab.data.enums.ArtGroupType;
import com.dsatab.data.enums.TalentGroupType;
import com.dsatab.data.enums.TalentType;
import com.dsatab.util.Util;
import com.dsatab.view.ArtInfoDialog;
import com.dsatab.view.FilterSettings;
import com.dsatab.view.FilterSettings.FilterType;
import com.dsatab.view.ListFilterSettings;
import com.dsatab.view.listener.HeroChangedListener;

/**
 * 
 * 
 */
public class ArtFragment extends BaseListFragment implements OnItemClickListener, HeroChangedListener {

	private ListView artList;
	private LinearLayout talentsView;

	private ArtAdapter artAdapter;

	private View empty;

	private final class ArtActionMode implements ActionMode.Callback {
		@Override
		public boolean onActionItemClicked(ActionMode mode, com.actionbarsherlock.view.MenuItem item) {
			boolean notifyChanged = false;

			SparseBooleanArray checkedPositions = artList.getCheckedItemPositions();
			if (checkedPositions != null) {
				for (int i = checkedPositions.size() - 1; i >= 0; i--) {
					if (checkedPositions.valueAt(i)) {
						Art art = artAdapter.getItem(checkedPositions.keyAt(i));

						switch (item.getItemId()) {
						case R.id.option_mark_favorite_art:
							art.setFavorite(true);
							notifyChanged = true;
							break;
						case R.id.option_mark_unused_art:
							art.setUnused(true);
							notifyChanged = true;
							break;
						case R.id.option_unmark_art:
							art.setFavorite(false);
							art.setUnused(false);
							notifyChanged = true;
							break;
						case R.id.option_view_art:
							showInfo(art);
							mode.finish();
							return true;
						default:
							return false;

						}
					}

				}
				if (notifyChanged) {
					artAdapter.notifyDataSetChanged();
				}
			}
			mode.finish();
			return true;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			mode.getMenuInflater().inflate(R.menu.art_popupmenu, menu);
			mode.setTitle("Künste");
			return true;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			mMode = null;
			artList.clearChoices();
			artAdapter.notifyDataSetChanged();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.actionbarsherlock.view.ActionMode.Callback#onPrepareActionMode (com.actionbarsherlock.view.ActionMode, com.actionbarsherlock.view.Menu)
		 */
		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			int selected = 0;
			boolean marked = false;
			SparseBooleanArray checkedPositions = artList.getCheckedItemPositions();
			if (checkedPositions != null) {
				for (int i = checkedPositions.size() - 1; i >= 0; i--) {
					if (checkedPositions.valueAt(i)) {
						selected++;
						Art art = artAdapter.getItem(checkedPositions.keyAt(i));
						marked |= art.isFavorite() || art.isUnused();
					}
				}
			}

			mode.setSubtitle(selected + " ausgewählt");

			boolean changed = false;

			if (selected != 1) {

				if (menu.findItem(R.id.option_view_art).isEnabled()) {
					menu.findItem(R.id.option_view_art).setEnabled(false);
					changed = true;
				}
			} else {

				if (!menu.findItem(R.id.option_view_art).isEnabled()) {
					menu.findItem(R.id.option_view_art).setEnabled(true);
					changed = true;
				}
			}

			if (marked) {
				if (!menu.findItem(R.id.option_unmark_art).isEnabled()) {
					menu.findItem(R.id.option_unmark_art).setEnabled(true);
					changed = true;
				}
			} else {
				if (menu.findItem(R.id.option_unmark_art).isEnabled()) {
					menu.findItem(R.id.option_unmark_art).setEnabled(false);
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

		mCallback = new ArtActionMode();
	}

	@Override
	protected ListFilterSettings getFilterSettings() {
		return (ListFilterSettings) super.getFilterSettings();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = configureContainerView(inflater.inflate(R.layout.sheet_art, container, false));
		artList = (ListView) root.findViewById(R.id.art_list);

		talentsView = (LinearLayout) root.findViewById(R.id.art_talents);
		empty = root.findViewById(android.R.id.empty);
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

		artList.setOnItemClickListener(this);
		artList.setOnItemLongClickListener(this);
		artList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

	}

	/**
	 * @param probe
	 */
	private void showInfo(Art probe) {
		ArtInfoDialog liturgieInfo = new ArtInfoDialog(getBaseActivity(), getHero());
		liturgieInfo.setArt(probe);
		liturgieInfo.show();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.activity.BaseMenuActivity#onHeroLoaded(com.dsatab.data.Hero)
	 */
	@Override
	public void onHeroLoaded(Hero hero) {
		loadHeroArts(hero);

		fillArtKenntnis(hero);

		if (hero.getArts().isEmpty()) {
			empty.setVisibility(View.VISIBLE);
			artList.setVisibility(View.GONE);
			talentsView.setVisibility(View.GONE);
		} else {
			empty.setVisibility(View.GONE);
			artList.setVisibility(View.VISIBLE);
			talentsView.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 
	 */
	private void fillArtKenntnis(Hero hero) {

		List<Talent> talents;
		TalentGroup gabenGroup = hero.getTalentGroup(TalentGroupType.Gaben);
		if (gabenGroup != null) {
			talents = gabenGroup.getTalents();
		} else {
			talents = Collections.emptyList();
		}

		boolean elfenLieder = false;
		for (Art art : hero.getArts().values()) {
			if (art.getGroupType() == ArtGroupType.Elfenlieder) {
				elfenLieder = true;
				break;
			}
		}

		if (elfenLieder) {
			talents = new ArrayList<Talent>(talents);
			Talent talent = hero.getTalent(TalentType.Singen);
			if (talent != null) {
				talents.add(talent);
			}

			talent = hero.getTalent(TalentType.Musizieren);
			if (talent != null) {
				talents.add(talent);
			}
		}
		// remove talentViews that are no longer needed

		LayoutInflater inflater = getActivity().getLayoutInflater();

		int count = 0;
		for (Talent talent : talents) {
			if (talent.getType() == TalentType.Gefahreninstinkt)
				continue;

			if (getFilterSettings() != null && !getFilterSettings().isVisible(talent))
				continue;

			View talentView = talentsView.getChildAt(count);
			if (talentView == null) {
				talentView = inflater.inflate(R.layout.talent_list_item, talentsView, false);
				talentsView.addView(talentView);
			}
			// name
			TextView text1 = (TextView) talentView.findViewById(R.id.talent_list_item_text1);
			// be
			TextView text2 = (TextView) talentView.findViewById(R.id.talent_list_item_text2);
			// probe
			TextView text3 = (TextView) talentView.findViewById(R.id.talent_list_item_text3);
			// value / at
			TextView text4 = (TextView) talentView.findViewById(R.id.talent_list_item_text4);
			// pa
			TextView text5 = (TextView) talentView.findViewById(R.id.talent_list_item_text5);

			text1.setText(talent.getName());

			String be = talent.getProbeInfo().getBe();

			if (TextUtils.isEmpty(be)) {
				Util.setVisibility(text2, false, text1);
			} else {
				Util.setVisibility(text2, true, text1);
				text2.setText(be);
			}
			text3.setText(talent.getProbeInfo().getAttributesString());

			int modifier = hero.getModifier(talent);
			Util.setText(text4, talent.getValue(), modifier, null);

			Util.setVisibility(text5, false, text1);
			talentView.setTag(talent);
			talentView.setOnClickListener(getBaseActivity().getProbeListener());

			Util.applyRowStyle(talent, talentView, count);

			count++;

		}

		int maxCount = talentsView.getChildCount();
		for (int i = maxCount - 1; i >= count; i--) {
			talentsView.removeViewAt(i);
		}

		if (talents.isEmpty() || talentsView.getChildCount() == 0) {
			talentsView.setVisibility(View.GONE);
		} else {
			talentsView.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onValueChanged(Value value) {
		if (value == null) {
			return;
		}

		if (value instanceof Art) {
			artAdapter.notifyDataSetChanged();
		}

		if (value instanceof Talent) {
			fillArtKenntnis(getHero());
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget .AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		if (mMode == null) {
			Art art = artAdapter.getItem(position);
			if (art != null) {
				getBaseActivity().checkProbe(art);
			}
			artList.setItemChecked(position, false);
		} else {
			super.onItemClick(parent, v, position, id);
		}
	}

	private void loadHeroArts(Hero hero) {
		artAdapter = new ArtAdapter(getActivity(), hero.getArts().values(), getFilterSettings());
		artList.setAdapter(artAdapter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.fragment.BaseFragment#onFilterChanged(com.dsatab.view. FilterSettings.FilterType, com.dsatab.view.FilterSettings)
	 */
	@Override
	public void onFilterChanged(FilterType type, FilterSettings settings) {
		if (artAdapter != null && (type == null || type == FilterType.Art) && settings instanceof ListFilterSettings) {
			artAdapter.filter((ListFilterSettings) settings);
		}
	}

}
