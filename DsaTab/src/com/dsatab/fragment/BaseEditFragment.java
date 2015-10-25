package com.dsatab.fragment;

import android.os.Bundle;
import android.view.View;

import com.dsatab.activity.BaseEditActivity;
import com.dsatab.data.Hero;

public abstract class BaseEditFragment extends BaseFragment implements EditFragment {


	@Override
	public void onHeroLoaded(Hero hero) {

	}

    public void inflateSaveAndDiscard() {

        if (getEditActivity()!=null) {
            getEditActivity().inflateSaveAndDiscard();
        } else {
            getBaseActivity().inflateSaveAndDiscard(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    accept();
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancel();
                }
            });
        }
    }

    public void inflateDone() {

        if (getEditActivity()!=null) {
            getEditActivity().inflateDone();
        } else {
            getBaseActivity().inflateDone(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancel();
                }
            });
        }
    }
	public abstract Bundle accept();

	public abstract void cancel();

	public BaseEditActivity getEditActivity() {
		if (getActivity() instanceof BaseEditActivity) {
			return (BaseEditActivity) getActivity();
		} else {
			return null;
		}
	}
}
