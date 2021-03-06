package com.dsatab.fragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.activity.BaseEditActivity;
import com.dsatab.data.CustomProbe;
import com.dsatab.data.Probe.ProbeType;
import com.dsatab.data.adapter.SpinnerSimpleAdapter;
import com.dsatab.data.modifier.RulesModificator.ModificatorType;
import com.dsatab.fragment.dialog.ImageChooserDialog;
import com.dsatab.util.Debug;
import com.dsatab.util.ResUtil;
import com.dsatab.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomProbeEditFragment extends BaseEditFragment implements OnClickListener, OnItemSelectedListener {

	public static void insert(Fragment fragment, int requestCode) {
		Intent intent = new Intent(fragment.getActivity(), BaseEditActivity.class);
		intent.setAction(Intent.ACTION_INSERT);
		intent.putExtra(BaseEditActivity.EDIT_FRAGMENT_CLASS, CustomProbeEditFragment.class);
		fragment.startActivityForResult(intent, requestCode);
	}

	public static void edit(Fragment fragment, CustomProbe probe, int requestCode) {
		Intent intent = new Intent(fragment.getActivity(), BaseEditActivity.class);
		intent.setAction(Intent.ACTION_EDIT);
		intent.putExtra(BaseEditActivity.EDIT_FRAGMENT_CLASS, CustomProbeEditFragment.class);
		intent.putExtra(INTENT_PROBE_CHOOSER_ID, probe.getId());
		fragment.startActivityForResult(intent, requestCode);
	}

	public static final String INTENT_PROBE_CHOOSER_ID = "com.dsatab.data.intent.customProbeId";

	private static final String ICON_URI = "ICON_URI";

	private EditText editName, editDescription, editFooter;
	private EditText editAttrs, editBe, editValue;
	private Spinner editProbeType, editModType;

	private SpinnerSimpleAdapter<ModificatorType> modTypeAdapter;
	private SpinnerSimpleAdapter<ProbeType> probeTypeAdapter;

	private ImageView iconView;

	private Uri iconUri;

	private ModificatorType modificationType;
	private ProbeType probeType;

	private CustomProbe customProbe;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.sheet_edit_custom_probe, container, false);

		editName = (EditText) root.findViewById(R.id.popup_edit_name);
		editDescription = (EditText) root.findViewById(R.id.popup_edit_description);
		editFooter = (EditText) root.findViewById(R.id.popup_edit_footer);

		editAttrs = (EditText) root.findViewById(R.id.popup_edit_attributes);
		editBe = (EditText) root.findViewById(R.id.popup_edit_be);
		editValue = (EditText) root.findViewById(R.id.popup_edit_value);

		editProbeType = (Spinner) root.findViewById(R.id.popup_edit_probe_type);
		List<ProbeType> probeTypes = new ArrayList<ProbeType>(Arrays.asList(ProbeType.values()));
		probeTypeAdapter = new SpinnerSimpleAdapter<ProbeType>(getActivity(), android.R.layout.simple_spinner_item,
				probeTypes);
		editProbeType.setAdapter(probeTypeAdapter);

		editModType = (Spinner) root.findViewById(R.id.popup_edit_modificator_type);
		List<ModificatorType> categories = new ArrayList<ModificatorType>(Arrays.asList(ModificatorType.values()));
		modTypeAdapter = new SpinnerSimpleAdapter<ModificatorType>(getActivity(), android.R.layout.simple_spinner_item,
				categories);
		editModType.setAdapter(modTypeAdapter);

		iconView = (ImageView) root.findViewById(R.id.popup_edit_icon);
		iconView.setOnClickListener(this);

		if (savedInstanceState != null && savedInstanceState.containsKey(ICON_URI)) {
			iconUri = Uri.parse(savedInstanceState.getString(ICON_URI));
            Drawable icon =  ResUtil.getDrawableByUri(iconView.getContext(),iconUri);
			iconView.setImageDrawable(icon);
		}

		return root;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		CustomProbe probe = null;
		Bundle extra = getExtra();
		if (extra != null && extra.containsKey(INTENT_PROBE_CHOOSER_ID)) {
			long containerId = extra.getInt(INTENT_PROBE_CHOOSER_ID,-1);
			if (containerId != -1) {
				probe = DsaTabApplication.getInstance().getHero().getHeroConfiguration().getCustomProbe(containerId);
			}
		}
		setCustomProbe(probe);

		updateView();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		if (iconUri != null) {
			outState.putString(ICON_URI, iconUri.toString());
		}
	}

	public void setCustomProbe(CustomProbe probe) {
		if (probe == null) {
			probe = new CustomProbe(DsaTabApplication.getInstance().getHero());
		}
		this.customProbe = probe;

		modificationType = customProbe.getModificatorType();
		probeType = customProbe.getProbeType();
		iconUri = customProbe.getIconUri();

		updateView();
	}

	private void updateView() {
		if (customProbe == null)
			return;

		if (editName != null) {
			editName.setText(customProbe.getName());
		}
		if (editDescription != null) {
			editDescription.setText(customProbe.getDescription());
		}
		if (editFooter != null) {
			editFooter.setText(customProbe.getFooter());
		}
		if (editAttrs != null && customProbe.getProbeInfo() != null) {
			editAttrs.setText(customProbe.getProbeInfo().getAttributesString());
		} else {
			editAttrs.setText(null);
		}
		if (editBe != null && customProbe.getProbeInfo() != null) {
			editBe.setText(customProbe.getProbeInfo().getBe());
		} else {
			editBe.setText(null);
		}
		if (editValue != null && customProbe.getValue() != null) {
			editValue.setText(Integer.toString(customProbe.getValue()));
		} else {
			editValue.setText(null);
		}

		if (modificationType != null) {
			int index = modTypeAdapter.getPosition(modificationType);
			if (editModType.getSelectedItemPosition() != index)
				editModType.setSelection(index);
		}
		editModType.setOnItemSelectedListener(this);

		List<ProbeType> probeTypes = new ArrayList<ProbeType>(Arrays.asList(ProbeType.values()));
		probeTypeAdapter = new SpinnerSimpleAdapter<ProbeType>(getActivity(), android.R.layout.simple_spinner_item,
				probeTypes);
		editProbeType.setAdapter(probeTypeAdapter);
		editProbeType.setOnItemSelectedListener(null);

		if (probeType != null) {
			int index = probeTypeAdapter.getPosition(probeType);
			if (editProbeType.getSelectedItemPosition() != index)
				editProbeType.setSelection(index);
		}
		editProbeType.setOnItemSelectedListener(this);

		if (iconView != null) {
            Drawable icon = ResUtil.getDrawableByUri(iconView.getContext(),iconUri);
			iconView.setImageDrawable(icon);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget .AdapterView, android.view.View,
	 * int, long)
	 */
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

		if (parent.getId() == R.id.popup_edit_modificator_type) {
			modificationType = (ModificatorType) editModType.getSelectedItem();
		} else if (parent.getId() == R.id.popup_edit_probe_type) {
			probeType = (ProbeType) editProbeType.getSelectedItem();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.AdapterView.OnItemSelectedListener#onNothingSelected(android .widget.AdapterView)
	 */
	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		if (parent.getId() == R.id.popup_edit_modificator_type) {
			modificationType = null;
		} else if (parent.getId() == R.id.popup_edit_probe_type) {
			probeType = null;
		}
	}

	/**
	 * 
	 */
	public void cancel() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.popup_edit_icon:
			pickIcon();
			break;

		default:
			break;
		}

	}

	/**
	 * 
	 */
	public Bundle accept() {
		Util.hideKeyboard(editName);
		customProbe.setName(editName.getText().toString());
		customProbe.setDescription(editDescription.getText().toString());
		customProbe.setFooter(editFooter.getText().toString());
		customProbe.getProbeInfo().applyBePattern(editBe.getText().toString());
		customProbe.getProbeInfo().applyProbePattern(editAttrs.getText().toString());
		customProbe.setValue(Util.parseInteger(editValue.getText().toString()));
		customProbe.setProbeType((ProbeType) editProbeType.getSelectedItem());
		customProbe.setModificatorType((ModificatorType) editModType.getSelectedItem());
		customProbe.setIconUri(iconUri);

		if (Intent.ACTION_INSERT.equals(getActivity().getIntent().getAction())) {
			DsaTabApplication.getInstance().getHero().getHeroConfiguration().addCustomProbe(customProbe);
		}

        Debug.logCustomEvent("Save CustomProbe","name", customProbe.getName());

		Bundle data = new Bundle();
		// TODO fill data
		return data;
	}

	private void pickIcon() {
		ImageChooserDialog.pickIcons(this, new ImageChooserDialog.OnImageSelectedListener() {

			@Override
			public void onImageSelected(Uri imageUri) {
                iconUri = imageUri;
                Drawable icon = ResUtil.getDrawableByUri(iconView.getContext(),iconUri);
				iconView.setImageDrawable(icon);
			}
		}, 0);

	}

}
