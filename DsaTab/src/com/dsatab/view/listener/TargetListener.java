package com.dsatab.view.listener;

import java.lang.ref.WeakReference;

import android.view.View;

import com.dsatab.activity.DsaTabActivity;
import com.dsatab.data.items.EquippedItem;
import com.dsatab.fragment.BaseFragment;
import com.dsatab.view.ArcheryChooserDialog;

public class TargetListener implements View.OnClickListener {

	private WeakReference<BaseFragment> mFragment;

	/**
	 * 
	 */
	public TargetListener(BaseFragment fragment) {
		this.mFragment = new WeakReference<BaseFragment>(fragment);
	}

	@Override
	public void onClick(View v) {
		if (v.getTag() instanceof EquippedItem) {
			EquippedItem item = (EquippedItem) v.getTag();

			BaseFragment fragment = mFragment.get();

			if (fragment != null && fragment.getActivity() instanceof DsaTabActivity) {
				ArcheryChooserDialog targetChooserDialog = new ArcheryChooserDialog(
						(DsaTabActivity) fragment.getActivity());
				targetChooserDialog.setWeapon(item);
				targetChooserDialog.show();
			}
		}
	}
}
