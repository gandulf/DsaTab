package com.dsatab.fragment;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dsatab.R;
import com.dsatab.config.TabInfo;
import com.dsatab.data.Hero;

import java.util.List;

/**
 * Created by Ganymedes on 25.10.2015.
 */
public class DualFragment extends BaseFragment {

    private List<BaseFragment> childFragments;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TabInfo tabInfo = getTabInfo();
        for (int i=0; i< tabInfo.getActivityClazzes().length;i++) {
            Class<? extends BaseFragment> clazz = tabInfo.getActivityClazzes()[i];
            childFragments.add(BaseFragment.newInstance(clazz,tabInfo,i));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = configureContainerView(inflater.inflate(R.layout.dual_fragment,container,false));
        FragmentManager childFragMan = getChildFragmentManager();
        FragmentTransaction childFragTrans = childFragMan.beginTransaction();

        for (BaseFragment childFragment : childFragments) {
            childFragTrans.add(R.id.viewpager, childFragment);
        }
        childFragTrans.commit();

        return root;
    }

    @Override
    public void onHeroLoaded(Hero hero) {
        for (BaseFragment childFragment : childFragments) {
            childFragment.onHeroLoaded(hero);
        }
    }
}
