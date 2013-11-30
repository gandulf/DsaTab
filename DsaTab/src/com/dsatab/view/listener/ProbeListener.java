package com.dsatab.view.listener;

import java.lang.ref.WeakReference;

import android.view.View;

import com.dsatab.R;
import com.dsatab.data.Probe;
import com.dsatab.data.enums.AttributeType;
import com.dsatab.fragment.BaseFragment;

public class ProbeListener implements View.OnClickListener, View.OnLongClickListener {

	private WeakReference<BaseFragment> mFragment;

	/**
	 * 
	 */
	public ProbeListener(BaseFragment context) {
		this.mFragment = new WeakReference<BaseFragment>(context);
	}

	@Override
	public void onClick(View v) {
		BaseFragment baseFragment = mFragment.get();
		if (baseFragment == null)
			return;

		Probe probe = null;

		if (v.getTag(R.id.TAG_KEY_PROBE) instanceof Probe) {
			probe = (Probe) v.getTag(R.id.TAG_KEY_PROBE);
		} else if (v.getTag() instanceof Probe) {
			probe = (Probe) v.getTag();
		} else if (v.getTag(R.id.TAG_KEY_PROBE) instanceof AttributeType) {
			AttributeType type = (AttributeType) v.getTag(R.id.TAG_KEY_PROBE);
			if (baseFragment.getBeing() != null)
				probe = baseFragment.getBeing().getAttribute(type);
		} else if (v.getTag() instanceof AttributeType) {
			AttributeType type = (AttributeType) v.getTag();
			if (baseFragment.getBeing() != null)
				probe = baseFragment.getBeing().getAttribute(type);
		}

		if (probe != null) {
			baseFragment.checkProbe(probe);
		}

	}

	@Override
	public boolean onLongClick(View v) {
		BaseFragment baseFragment = mFragment.get();
		if (baseFragment == null)
			return false;

		Probe probe = null;

		if (v.getTag(R.id.TAG_KEY_PROBE) instanceof Probe) {
			probe = (Probe) v.getTag(R.id.TAG_KEY_PROBE);
		} else if (v.getTag() instanceof Probe) {
			probe = (Probe) v.getTag();
		} else if (v.getTag(R.id.TAG_KEY_PROBE) instanceof AttributeType) {
			AttributeType type = (AttributeType) v.getTag(R.id.TAG_KEY_PROBE);
			if (baseFragment.getBeing() != null)
				probe = baseFragment.getBeing().getAttribute(type);
		} else if (v.getTag() instanceof AttributeType) {
			AttributeType type = (AttributeType) v.getTag();
			if (baseFragment.getBeing() != null)
				probe = baseFragment.getBeing().getAttribute(type);
		}

		if (probe != null) {
			baseFragment.checkProbe(probe);
			return true;
		}
		return false;
	}

}