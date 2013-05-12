package com.dsatab.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.dsatab.R;

public class WebInfoDialog extends AlertDialog implements DialogInterface.OnClickListener {

	private WebView popupcontent = null;

	private String url;

	public WebInfoDialog(Context context, String url) {
		super(context);
		this.url = url;
		init();
	}

	private void init() {
		setCanceledOnTouchOutside(true);

		popupcontent = new WebView(getContext());
		popupcontent.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		setView(popupcontent);
		WebSettings settings = popupcontent.getSettings();
		settings.setDefaultTextEncodingName("utf-8");

		popupcontent.loadUrl(url);

		setButton(DialogInterface.BUTTON_POSITIVE, getContext().getString(R.string.label_ok), this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.AlertDialog#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		WebInfoDialog.this.dismiss();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Dialog#onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();
	}

}
