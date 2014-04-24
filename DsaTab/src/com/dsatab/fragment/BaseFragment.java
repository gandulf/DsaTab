package com.dsatab.fragment;

import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.dsatab.util.Util;
import com.dsatab.view.listener.EditListener;
import com.dsatab.view.listener.HeroChangedListener;
import com.dsatab.view.listener.HeroInventoryChangedListener;
import com.dsatab.view.listener.HeroLoader;
import com.dsatab.view.listener.ProbeListener;
import com.dsatab.view.listener.TargetListener;

public abstract class BaseFragment extends Fragment implements HeroLoader, HeroChangedListener,
		OnSharedPreferenceChangeListener {

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

	private SharedPreferences preferences;

	private ProbeListener probeListener;

	private EditListener editListener;

	private TargetListener targetListener;

	protected void customizeActionModeCloseButton() {
		int buttonId = Resources.getSystem().getIdentifier("action_mode_close_button", "id", "android");
		View v = getActivity().findViewById(buttonId);
		if (v == null)
			return;
		LinearLayout ll = (LinearLayout) v;
		if (ll.getChildCount() > 1 && ll.getChildAt(1) != null) {
			TextView tv = (TextView) ll.getChildAt(1);
			tv.setTextColor(getResources().getColor(android.R.color.white));
			tv.setBackgroundResource(Util.getThemeResourceId(getActivity(), android.R.attr.actionBarItemBackground));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// if we reattach a fragment there is already a view present
		if (getView() != null && getActivity() != null) {
			Hero hero = getHero();

			if (hero != null) {
				Debug.verbose("Loading hero in " + getClass() + " onAttach " + hero.getName());
				loadHero(hero);
			}
		}

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
			Hint.showRandomHint(getClass().getSimpleName(), getActivity());
		}
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

	public View configureContainerView(View view) {
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

	@Override
	public final void loadHero(Hero hero) {
		onHeroLoaded(hero);
		onAttachListener(hero);
	}

	@Override
	public final void unloadHero(Hero hero) {
		onDetachListener(hero);
	}

	public boolean checkProbe(Probe probe) {
		if (getBaseActivity() != null)
			return getBaseActivity().checkProbe(probe);
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
			getBaseActivity().notifyTabsChanged();
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

	public ActionBarActivity getActionBarActivity() {
		if (getActivity() instanceof ActionBarActivity) {
			return (ActionBarActivity) getActivity();
		} else {
			return null;
		}
	}
}
