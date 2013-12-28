package com.dsatab.view.listener;

import java.lang.ref.WeakReference;

import android.view.View;

import com.dsatab.activity.DsaTabActivity;
import com.dsatab.data.items.EquippedItem;
import com.dsatab.view.ArcheryChooserDialog;

public class TargetListener implements View.OnClickListener {

	private WeakReference<DsaTabActivity> mActivity;

	/**
	 * 
	 */
	public TargetListener(DsaTabActivity activity) {
		this.mActivity = new WeakReference<DsaTabActivity>(activity);
	}

	@Override
	public void onClick(View v) {
		if (v.getTag() instanceof EquippedItem) {
			EquippedItem item = (EquippedItem) v.getTag();

			DsaTabActivity mainActivity = mActivity.get();
			if (mainActivity != null) {
				ArcheryChooserDialog targetChooserDialog = new ArcheryChooserDialog(mainActivity);
				targetChooserDialog.setWeapon(item);
				targetChooserDialog.show();
			}
		}
	}
}
