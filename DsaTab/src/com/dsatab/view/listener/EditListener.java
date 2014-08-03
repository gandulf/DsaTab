package com.dsatab.view.listener;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.view.View;

import com.dsatab.R;
import com.dsatab.data.CombatMeleeTalent;
import com.dsatab.data.Value;
import com.dsatab.data.enums.AttributeType;
import com.dsatab.fragment.BaseFragment;
import com.dsatab.view.dialog.InlineEditDialog;
import com.dsatab.view.dialog.InlineEditFightDialog;

public class EditListener implements View.OnClickListener, View.OnLongClickListener {

	private WeakReference<BaseFragment> mFragment;

	public EditListener(BaseFragment context) {
		this.mFragment = new WeakReference<BaseFragment>(context);
	}

	@Override
	public void onClick(View v) {
		BaseFragment baseFragment = mFragment.get();
		if (baseFragment == null)
			return;

		Value value = null;
		if (v.getTag(R.id.TAG_KEY_VALUE) instanceof Value) {
			value = (Value) v.getTag(R.id.TAG_KEY_VALUE);
		} else if (v.getTag() instanceof Value) {
			value = (Value) v.getTag();
		} else if (v.getTag(R.id.TAG_KEY_VALUE) instanceof AttributeType) {
			AttributeType type = (AttributeType) v.getTag(R.id.TAG_KEY_VALUE);
			if (baseFragment.getBeing() != null)
				value = baseFragment.getBeing().getAttribute(type);
		} else if (v.getTag() instanceof AttributeType) {
			AttributeType type = (AttributeType) v.getTag();
			if (baseFragment.getBeing() != null)
				value = baseFragment.getBeing().getAttribute(type);
		}

		if (value != null && baseFragment.getActivity() != null) {
			showEditPopup(baseFragment.getActivity(), value);
		}

	}

	@Override
	public boolean onLongClick(View v) {
		BaseFragment baseFragment = mFragment.get();
		if (baseFragment == null)
			return false;

		Value value = null;
		if (v.getTag(R.id.TAG_KEY_VALUE) instanceof Value) {
			value = (Value) v.getTag(R.id.TAG_KEY_VALUE);
		} else if (v.getTag() instanceof Value) {
			value = (Value) v.getTag();
		} else if (v.getTag(R.id.TAG_KEY_VALUE) instanceof AttributeType) {
			AttributeType type = (AttributeType) v.getTag(R.id.TAG_KEY_VALUE);
			if (baseFragment.getBeing() != null)
				value = baseFragment.getBeing().getAttribute(type);
		} else if (v.getTag() instanceof AttributeType) {
			AttributeType type = (AttributeType) v.getTag();
			if (baseFragment.getBeing() != null)
				value = baseFragment.getBeing().getAttribute(type);
		}

		if (value != null && baseFragment.getActivity() != null) {
			showEditPopup(baseFragment.getActivity(), value);
			return true;
		}
		return false;

	}

	public static void showEditPopup(Context context, Value value) {

		if (value instanceof CombatMeleeTalent) {
			InlineEditFightDialog inlineEditFightdialog = new InlineEditFightDialog(context, (CombatMeleeTalent) value);
			inlineEditFightdialog.setTitle(value.getName());
			inlineEditFightdialog.show();
		} else if (value != null) {
			InlineEditDialog inlineEditdialog = new InlineEditDialog(context, value);
			inlineEditdialog.setTitle(value.getName());
			inlineEditdialog.show();
		}

	}

}
