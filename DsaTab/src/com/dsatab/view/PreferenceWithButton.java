package com.dsatab.view;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;

import com.dsatab.R;

public class PreferenceWithButton extends Preference {

	private View mView;

	private OnClickListener buttonClickListener;

	private int widgetVisibility = View.VISIBLE;

	public PreferenceWithButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setWidgetLayoutResource(R.layout.widget_delete_button);
	}

	public PreferenceWithButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		setWidgetLayoutResource(R.layout.widget_delete_button);
	}

	public PreferenceWithButton(Context context) {
		super(context);
		setWidgetLayoutResource(R.layout.widget_delete_button);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.preference.Preference#onBindView(android.view.View)
	 */
	@Override
	@SuppressWarnings("WrongConstant")
	protected void onBindView(View view) {
		super.onBindView(view);

		mView = view.findViewById(R.id.pref_btn);
		if (mView != null) {
			mView.setOnClickListener(buttonClickListener);
			mView.setFocusable(false);
			mView.setTag(this);
			mView.setVisibility(widgetVisibility);
		}
	}

	public View getButton() {
		return mView;
	}

	public OnClickListener getButtonClickListener() {
		return buttonClickListener;
	}

	public void setButtonClickListener(OnClickListener buttonClickListener) {
		this.buttonClickListener = buttonClickListener;
	}

	public int getWidgetVisibility() {
		return widgetVisibility;
	}

	public void setWidgetVisibility(int widgetVisibility) {
		this.widgetVisibility = widgetVisibility;
		if (mView != null) {
			mView.setVisibility(widgetVisibility);
		}
	}

}
