package com.dsatab.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import com.dsatab.R;
import com.dsatab.activity.BaseEditActivity;
import com.dsatab.activity.DsaTabActivity;
import com.dsatab.data.modifier.CustomModificator;

public class ModificatorEditFragment extends BaseEditFragment {

	public static void edit(Fragment fragment, CustomModificator modificator, int requestCode) {
		Intent intent = new Intent(fragment.getActivity(), BaseEditActivity.class);
		intent.putExtra(BaseEditActivity.EDIT_FRAGMENT_CLASS, ModificatorEditFragment.class);

		intent.putExtra(ModificatorEditFragment.INTENT_ID, modificator.getId());
		intent.putExtra(ModificatorEditFragment.INTENT_NAME, modificator.getModificatorName());
		intent.putExtra(ModificatorEditFragment.INTENT_RULES, modificator.getRules());
		intent.putExtra(ModificatorEditFragment.INTENT_COMMENT, modificator.getComment());
		intent.putExtra(ModificatorEditFragment.INTENT_ACTIVE, modificator.isActive());
		fragment.startActivityForResult(intent, requestCode);
	}

	public static void insert(Fragment fragment, int actionAddModificator) {
		Intent intent = new Intent(fragment.getActivity(), BaseEditActivity.class);
		intent.setAction(Intent.ACTION_INSERT);
		intent.putExtra(BaseEditActivity.EDIT_FRAGMENT_CLASS, ModificatorEditFragment.class);
		fragment.startActivityForResult(intent, DsaTabActivity.ACTION_ADD_MODIFICATOR);

	}

	public static final String INTENT_ID = "id";
	public static final String INTENT_NAME = "name";
	public static final String INTENT_COMMENT = "comment";
	public static final String INTENT_RULES = "rules";
	public static final String INTENT_ACTIVE = "active";

	private CheckBox cbActive;

	private EditText etName, etRules, etComment;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.sheet_edit_modificator, container, false);

		cbActive = (CheckBox) root.findViewById(R.id.popup_edit_active);
		etName = (EditText) root.findViewById(R.id.popup_edit_name);
		etRules = (EditText) root.findViewById(R.id.popup_edit_info);
		etComment = (EditText) root.findViewById(R.id.popup_edit_comment);

		return root;

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		Bundle extra = getExtra();
		if (extra != null) {
			etName.setText(extra.getString(INTENT_NAME));
			etRules.setText(extra.getString(INTENT_RULES));
			etComment.setText(extra.getString(INTENT_COMMENT));
			cbActive.setChecked(extra.getBoolean(INTENT_ACTIVE, true));
		}

	}

	public Bundle accept() {

		Bundle data = new Bundle();

		Bundle extra = getExtra();
		if (extra != null) {
			data.putSerializable(INTENT_ID, extra.getSerializable(INTENT_ID));
		}
		String name = etName.getText().toString();
		String comment = etComment.getText().toString();
		String rules = etRules.getText().toString();

		if (TextUtils.isEmpty(rules)) {
			etRules.setError(getString(R.string.mandatory));
			return null;
		}
		data.putString(INTENT_NAME, name);
		data.putString(INTENT_COMMENT, comment);
		data.putString(INTENT_RULES, rules);
		data.putBoolean(INTENT_ACTIVE, cbActive.isChecked());

		return data;
	}

	public void cancel() {

	}

}