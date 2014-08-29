package com.dsatab.view.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;

import com.dsatab.R;
import com.dsatab.data.adapter.SpinnerSimpleAdapter;
import com.dsatab.db.DataManager;

public class WebInfoDialog extends AlertDialog implements DialogInterface.OnClickListener, OnItemSelectedListener {

	private static WebInfoDialog dialog;

	private WebView popupcontent = null;
	private Spinner spinner;
	private SpinnerSimpleAdapter<String> infoAdapters;

	private String url;
	private String data;

	public WebInfoDialog(Context context) {
		this(context, null, null);
	}

	public WebInfoDialog(Context context, String url, String data) {
		super(context);
		this.url = url;
		this.data = data;
		init();
	}

	public static boolean show(Context context, CharSequence tag) {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}

		dialog = new WebInfoDialog(context);
		if (tag != null) {
			dialog.setTag(tag.toString());
		}
		dialog.loadData();
		return dialog.showIfData();
	}

	public void setTag(String tag) {
		this.data = DataManager.getWebInfo(getContext(), tag.toString());
		this.spinner.setSelection(infoAdapters.getPosition(tag));
	}

	protected void loadData() {
		if (url != null)
			popupcontent.loadUrl(url);
		else if (data != null) {
			popupcontent.loadDataWithBaseURL("file:///android_asset/data/", data, "text/html", "UTF-8", null);
		}
	}

	public boolean showIfData() {
		if (url != null || data != null) {
			super.show();
			return true;
		} else {
			return false;
		}
	}

	private void init() {
		setCanceledOnTouchOutside(true);

		View root = getLayoutInflater().inflate(R.layout.popup_web_info, null, false);

		popupcontent = (WebView) root.findViewById(R.id.web);
		spinner = (Spinner) root.findViewById(R.id.spinner);

		infoAdapters = new SpinnerSimpleAdapter<String>(getContext(), DataManager.getWebInfos(getContext()));

		spinner.setAdapter(infoAdapters);
		spinner.setOnItemSelectedListener(this);

		setView(root);
		WebSettings settings = popupcontent.getSettings();
		settings.setDefaultTextEncodingName("utf-8");
		loadData();

		setButton(DialogInterface.BUTTON_POSITIVE, getContext().getString(R.string.label_ok), this);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		setTag(infoAdapters.getItem(position));
		loadData();
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {

	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		WebInfoDialog.this.dismiss();
	}

}
