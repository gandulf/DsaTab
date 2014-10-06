package com.dsatab.fragment.dialog;

import uk.me.lewisdeane.ldialogs.CustomDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;

import com.dsatab.R;
import com.dsatab.data.adapter.SpinnerSimpleAdapter;
import com.dsatab.db.DataManager;

public class WebInfoDialog extends DialogFragment implements OnItemSelectedListener {

	public static final String TAG = "WebInfoDialog";

	private static final String KEY_TAG = "tag";

	private WebView content = null;
	private Spinner spinner;
	private SpinnerSimpleAdapter<String> infoAdapters;

	private String url;
	private String data;

	public static void show(Fragment parent, CharSequence tag, int requestCode) {
		DialogFragment dialog = new WebInfoDialog();
		Bundle args = new Bundle();
		if (tag != null) {
			args.putString(KEY_TAG, tag.toString());
		}
		dialog.setArguments(args);
		dialog.setTargetFragment(parent, requestCode);
		dialog.show(parent.getFragmentManager(), TAG);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		Bundle args = getArguments();
		String tag = args.getString(KEY_TAG);

		CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());

		builder.setPositiveButton(android.R.string.ok, null);
		View popupContent = builder.setView(R.layout.popup_web_info);

		content = (WebView) popupContent.findViewById(R.id.web);
		spinner = (Spinner) popupContent.findViewById(R.id.spinner);

		infoAdapters = new SpinnerSimpleAdapter<String>(builder.getContext(), DataManager.getWebInfos(getActivity()));

		spinner.setAdapter(infoAdapters);
		spinner.setOnItemSelectedListener(this);

		WebSettings settings = content.getSettings();
		settings.setDefaultTextEncodingName("utf-8");
		setTag(tag);

		CustomDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(true);
		return dialog;

	}

	protected void setTag(String tag) {
		this.data = DataManager.getWebInfo(getActivity(), tag.toString());
		this.spinner.setSelection(infoAdapters.getPosition(tag));

		loadData();
	}

	protected void loadData() {
		if (url != null)
			content.loadUrl(url);
		else if (data != null) {
			content.loadDataWithBaseURL("file:///android_asset/data/", data, "text/html", "UTF-8", null);
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		setTag(infoAdapters.getItem(position));
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {

	}

}
