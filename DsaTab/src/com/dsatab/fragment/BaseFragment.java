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

import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.bugsense.trace.BugSenseHandler;
import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.TabInfo;
import com.dsatab.activity.DsaTabActivity;
import com.dsatab.data.Hero;
import com.dsatab.data.Value;
import com.dsatab.data.modifier.Modificator;
import com.dsatab.util.Debug;
import com.dsatab.util.Hint;
import com.dsatab.util.Util;
import com.dsatab.view.FilterSettings;
import com.dsatab.view.FilterSettings.FilterType;
import com.dsatab.view.listener.FilterChangedListener;
import com.dsatab.view.listener.HeroChangedListener;
import com.dsatab.view.listener.HeroInventoryChangedListener;
import com.dsatab.view.listener.HeroLoader;

/**
 * @author Ganymede
 * 
 */
public abstract class BaseFragment extends SherlockFragment implements HeroLoader, HeroChangedListener,
		FilterChangedListener, OnSharedPreferenceChangeListener {

	public static final String TAB_POSITION = "TAB_POSITION";
	public static final String TAB_INFO = "TAB_INFO";

	public static List<String> activities;
	public static List<Class<? extends BaseFragment>> activityValues;

	static {
		activities = Arrays.asList("Keine", "Charakter", "Talente", "Zauber", "Künste", "Wunden", "Kampf",
				"Ausrüstung (Bilder)", "Ausrüstung (Liste)", "Notizen", "Geldbörse", "Karte", "Dokumente");

		activityValues = Arrays.asList(null, CharacterFragment.class, TalentFragment.class, SpellFragment.class,
				ArtFragment.class, BodyFragment.class, FightFragment.class, ItemsFragment.class,
				ItemsListFragment.class, NotesFragment.class, PurseFragment.class, MapFragment.class,
				DocumentsFragment.class);
	}

	public static String getFragmentTitle(Class<? extends BaseFragment> fragmentClass) {
		int index = activityValues.indexOf(fragmentClass);
		if (index >= 0)
			return activities.get(index);
		else
			return null;
	}

	protected SharedPreferences preferences;

	protected FilterSettings filterSettings;

	/**
	 * 
	 */
	public BaseFragment() {

	}

	protected void customizeActionModeCloseButton() {
		int buttonId = Resources.getSystem().getIdentifier("action_mode_close_button", "id", "android");
		View v = getActivity().findViewById(buttonId);
		if (v == null) {
			buttonId = R.id.abs__action_mode_close_button;
			v = getActivity().findViewById(buttonId);
		}
		if (v == null)
			return;
		LinearLayout ll = (LinearLayout) v;
		if (ll.getChildCount() > 1 && ll.getChildAt(1) != null) {
			TextView tv = (TextView) ll.getChildAt(1);
			tv.setTextColor(getResources().getColor(android.R.color.white));
			tv.setBackgroundResource(Util.getThemeResourceId(getActivity(), R.attr.actionBarItemBackground));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
	 */
	@Override
	public void onAttach(Activity activity) {
		// Debug.verbose(getClass().getName() + " attached");

		// if we reattach a fragment there is already a view present
		if (getView() != null) {
			Hero hero = getHero();
			if (hero != null) {
				Debug.verbose("Loading hero in " + getClass() + " onAttach " + hero.getName());
				loadHero(hero);
			}
		}
		super.onAttach(activity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();

		if (getUserVisibleHint()) {
			showRandomHint();
		}
	}

	protected void onAttachListener(Hero hero) {
		if (hero != null) {

			if (this instanceof HeroChangedListener) {
				hero.addHeroChangedListener((HeroChangedListener) this);
			}
			if (this instanceof HeroInventoryChangedListener) {
				hero.addHeroInventoryChangedListener((HeroInventoryChangedListener) this);
			}
		}
	}

	protected void onDetachListener(Hero hero) {
		if (hero != null) {
			if (this instanceof HeroChangedListener) {
				hero.removeHeroChangedListener((HeroChangedListener) this);
			}
			if (this instanceof HeroInventoryChangedListener) {
				hero.addHeroInventoryChangedListener((HeroInventoryChangedListener) this);
			}
		}
	}

	protected boolean showRandomHint() {
		return Hint.showRandomHint(getClass().getSimpleName(), getActivity());

	}

	protected int getTabPosition() {
		int pos = -1;
		if (getArguments() != null) {
			pos = getArguments().getInt(BaseFragment.TAB_POSITION, -1);
		}
		return pos;
	}

	protected TabInfo getTabInfo() {
		TabInfo tabInfo = null;
		if (getArguments() != null) {
			tabInfo = getArguments().getParcelable(BaseFragment.TAB_INFO);
		}
		return tabInfo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.fragment.FilterChangedListener#onFilterChanged(com.dsatab. view.FilterSettings.FilterType, com.dsatab.view.FilterSettings)
	 */
	@Override
	public void onFilterChanged(FilterType type, FilterSettings settings) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onDestroyView()
	 */
	@Override
	public void onDestroyView() {
		super.onDestroyView();

		// if (getView() != null) {
		// Debug.verbose("Unbinding drawbale to free memory from fragment");
		// Util.unbindDrawables(getView());
		// }

		// Debug.verbose(getClass().getName() + " destroyView");

		Hero hero = getHero();
		if (hero != null) {
			unloadHero(hero);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Debug.verbose(getClass().getName() + " createView");
		View view = super.onCreateView(inflater, container, savedInstanceState);
		return view;
	}

	public static View configureContainerView(View view) {
		LinearLayout.LayoutParams params;

		if (view.getLayoutParams() instanceof LinearLayout.LayoutParams) {
			params = ((LinearLayout.LayoutParams) view.getLayoutParams());
		} else {
			params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			view.setLayoutParams(params);
		}

		if (params.weight > 0)
			params.width = 0;

		return view;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (getTabPosition() >= 0 && getTabInfo() != null) {
			// update filter settings to be correct type just to be sure
			getTabInfo().updateFilterSettings();
			filterSettings = getTabInfo().getFilterSettings(getTabPosition());
			if (filterSettings != null) {
				onFilterChanged(null, filterSettings);
			}
		}

		Hero hero = getHero();
		if (hero != null) {
			if (getActivity() != null) {
				Debug.verbose("Loading hero in " + getClass() + " onActivityCreated " + hero.getName());
				loadHero(hero);
			} else {
				BugSenseHandler.sendException(new IllegalArgumentException(
						"getActivity was null in onActivityCreated of BaseFragment"));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		preferences = DsaTabApplication.getPreferences();
	}

	public Hero getHero() {
		return DsaTabApplication.getInstance().getHero();
	}

	public final void loadHero(Hero hero) {
		onHeroLoaded(hero);
		onAttachListener(hero);
	}

	public final void unloadHero(Hero hero) {
		onDetachListener(hero);
	}

	public abstract void onHeroLoaded(Hero hero);

	protected DsaTabActivity getBaseActivity() {
		if (getActivity() instanceof DsaTabActivity)
			return (DsaTabActivity) getActivity();
		else
			return null;
	}

	protected View findViewById(int id) {
		if (getView() != null)
			return getView().findViewById(id);
		else
			return null;
	}

	@Override
	public void onValueChanged(Value value) {

	}

	@Override
	public void onModifierAdded(Modificator value) {

	}

	@Override
	public void onModifierRemoved(Modificator value) {

	}

	@Override
	public void onModifierChanged(Modificator value) {

	}

	@Override
	public void onModifiersChanged(List<Modificator> values) {

	}

	@Override
	public void onPortraitChanged() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.SharedPreferences.OnSharedPreferenceChangeListener# onSharedPreferenceChanged(android.content.SharedPreferences, java.lang.String)
	 */
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

	}

	protected FilterSettings getFilterSettings() {
		return filterSettings;
	}
}
