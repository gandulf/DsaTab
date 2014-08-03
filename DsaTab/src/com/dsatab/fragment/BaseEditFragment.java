package com.dsatab.fragment;

import android.os.Bundle;

import com.dsatab.data.Hero;

public abstract class BaseEditFragment extends BaseFragment {

	public BaseEditFragment() {
	}

	@Override
	public void onHeroLoaded(Hero hero) {

	}

	protected Bundle getExtra() {

		Bundle extra = null;
		if (getActivity().getIntent() != null) {
			extra = getActivity().getIntent().getExtras();
		} else {
			extra = getArguments();
		}

		return extra;
	}

	public abstract Bundle accept();

	public abstract void cancel();

}
