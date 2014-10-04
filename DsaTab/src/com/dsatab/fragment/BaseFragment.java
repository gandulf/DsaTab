package com.dsatab.fragment;

import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.view.View;

import com.dsatab.DsaTabApplication;
import com.dsatab.TabInfo;
import com.dsatab.activity.DsaTabActivity;
import com.dsatab.data.AbstractBeing;
import com.dsatab.data.Hero;
import com.dsatab.data.Probe;
import com.dsatab.data.Value;
import com.dsatab.data.modifier.Modificator;
import com.dsatab.util.Debug;
import com.dsatab.util.Hint;
import com.dsatab.view.listener.EditListener;
import com.dsatab.view.listener.HeroChangedListener;
import com.dsatab.view.listener.HeroInventoryChangedListener;
import com.dsatab.view.listener.ProbeListener;
import com.dsatab.view.listener.TargetListener;

public abstract class BaseFragment extends Fragment implements HeroChangedListener, OnSharedPreferenceChangeListener {

	public static final String TAB_POSITION = "TAB_POSITION";
	public static final String TAB_INFO = "TAB_INFO";

	public static List<String> activities;
	public static List<Class<? extends BaseFragment>> activityValues;

	static {
		activities = Arrays.asList("Keine", "Charakter", "Liste", "Wunden", "Ausrüstung", "Karte", "Tiere");

		activityValues = Arrays.asList(null, CharacterFragment.class, ListableFragment.class, BodyFragment.class,
				ItemsFragment.class, MapFragment.class, AnimalFragment.class);
	}

	public static String getFragmentTitle(Class<? extends BaseFragment> fragmentClass) {
		int index = activityValues.indexOf(fragmentClass);
		if (index >= 0)
			return activities.get(index);
		else
			return null;
	}

	public static <T extends BaseFragment> T newInstance(Class<T> fragmentClazz, TabInfo tabInfo, int position) {
		T fragment = null;
		if (fragmentClazz != null) {
			try {
				fragment = fragmentClazz.newInstance();
				Bundle args = new Bundle();
				args.putParcelable(BaseFragment.TAB_INFO, tabInfo);
				args.putInt(BaseFragment.TAB_POSITION, position);
				fragment.setArguments(args);
			} catch (java.lang.InstantiationException e) {
				Debug.error(e);
			} catch (IllegalAccessException e) {
				Debug.error(e);
			}

		}
		return fragment;
	}

	private SharedPreferences preferences;

	private ProbeListener probeListener;

	private EditListener editListener;

	private TargetListener targetListener;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();

		Hint.showRandomHint(getClass().getSimpleName(), getActivity());
	}

	protected void onAttachListener(Hero hero) {
		if (hero != null) {

			if (this instanceof HeroChangedListener) {
				hero.addHeroChangedListener(this);
			}
			if (this instanceof HeroInventoryChangedListener) {
				hero.addHeroInventoryChangedListener((HeroInventoryChangedListener) this);
			}
		}
	}

	protected void onDetachListener(Hero hero) {
		if (hero != null) {
			if (this instanceof HeroChangedListener) {
				hero.removeHeroChangedListener(this);
			}
			if (this instanceof HeroInventoryChangedListener) {
				hero.removeHeroInventoryChangedListener((HeroInventoryChangedListener) this);
			}
		}
	}

	protected int getTabPosition() {
		int pos = -1;
		if (getArguments() != null) {
			pos = getArguments().getInt(BaseFragment.TAB_POSITION, -1);
		}
		return pos;
	}

	public TabInfo getTabInfo() {
		TabInfo tabInfo = null;
		if (getArguments() != null) {
			tabInfo = getArguments().getParcelable(BaseFragment.TAB_INFO);
		}
		return tabInfo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onDestroyView()
	 */
	@Override
	public void onDestroyView() {
		super.onDestroyView();

		Debug.trace(getClass().getName() + " destroyView");

		Hero hero = getHero();
		if (hero != null) {
			unloadHero(hero);
		}
	}

	public View configureContainerView(View view) {
		return view;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		Hero hero = getHero();
		if (hero != null) {
			if (getActivity() != null) {
				Debug.verbose("Loading hero in " + getClass() + " onActivityCreated " + hero.getName());
				loadHero(hero);
			} else {
				Debug.error(new IllegalArgumentException("getActivity was null in onActivityCreated of BaseFragment"));
			}
		}
	}

	public Hero getHero() {
		return DsaTabApplication.getInstance().getHero();
	}

	public AbstractBeing getBeing() {
		return getHero();
	}

	public final void loadHero(Hero hero) {
		onHeroLoaded(hero);
		onAttachListener(hero);
	}

	public final void unloadHero(Hero hero) {
		onDetachListener(hero);
	}

	public boolean checkProbe(Probe probe) {
		if (getBaseActivity() != null)
			return getBaseActivity().checkProbe(getBeing(), probe);
		else
			return false;
	}

	public boolean checkProbe(Probe probe, boolean autoRoll) {
		if (getBaseActivity() != null)
			return getBaseActivity().checkProbe(getBeing(), probe, autoRoll);
		else
			return false;
	}

	public abstract void onHeroLoaded(Hero hero);

	protected DsaTabActivity getBaseActivity() {
		if (getActivity() instanceof DsaTabActivity)
			return (DsaTabActivity) getActivity();
		else
			return null;
	}

	public void hideActionBarItems() {

	}

	public void showActionBarItems() {

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
	 * @see android.content.SharedPreferences.OnSharedPreferenceChangeListener#
	 * onSharedPreferenceChanged(android.content.SharedPreferences, java.lang.String)
	 */
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

	}

	protected void removeTab() {
		TabInfo tabInfo = null;
		for (TabInfo ti : getHero().getHeroConfiguration().getTabs()) {
			if (ti.getId().equals(getTabInfo().getId())) {
				tabInfo = ti;
			}
		}
		if (tabInfo != null) {
			tabInfo.setActivityClazz(getTabPosition(), null);

			if (tabInfo.isEmpty()) {
				getHero().getHeroConfiguration().getTabs().remove(tabInfo);
			}
			unloadHero(getHero());
			getBaseActivity().notifyTabsChanged(tabInfo);
		}
	}

	public SharedPreferences getPreferences() {
		if (preferences == null) {
			preferences = DsaTabApplication.getPreferences();
		}
		return preferences;
	}

	public ProbeListener getProbeListener() {
		if (probeListener == null)
			probeListener = new ProbeListener(this);

		return probeListener;
	}

	public EditListener getEditListener() {
		if (editListener == null)
			editListener = new EditListener(this);

		return editListener;
	}

	public TargetListener getTargetListener() {
		if (targetListener == null)
			targetListener = new TargetListener(this);

		return targetListener;
	}

	public Activity getActionBarActivity() {
		return getActivity();
	}

	public boolean isDrawerOpened() {
		return getBaseActivity() != null && getBaseActivity().isDrawerOpened();
	}
}
