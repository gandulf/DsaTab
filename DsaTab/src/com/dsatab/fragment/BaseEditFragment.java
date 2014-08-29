package com.dsatab.fragment;

import android.os.Bundle;

import com.dsatab.activity.FragmentEditActivity;
import com.dsatab.data.Hero;

public abstract class BaseEditFragment extends BaseFragment {

	public BaseEditFragment() {
	}

	@Override
	public void onHeroLoaded(Hero hero) {

	}

	protected String getAction() {

		if (getActivity() != null && getActivity().getIntent() != null) {
			return getActivity().getIntent().getAction();
		} else {
			return null;
		}

	}

	protected Bundle getExtra() {

		Bundle extra = null;
		if (getActivity() != null && getActivity().getIntent() != null) {
			extra = getActivity().getIntent().getExtras();
		} else {
			extra = getArguments();
		}

		return extra;
	}

	public abstract Bundle accept();

	public abstract void cancel();

	public FragmentEditActivity getEditActivity() {
		if (getActivity() instanceof FragmentEditActivity) {
			return (FragmentEditActivity) getActivity();
		} else {
			return null;
		}
	}
}
