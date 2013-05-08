/**
 *  This file is part of DsaTab.
 *
 *  DsaTab is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  DsaTab is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with DsaTab.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.dsatab.view;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.dsatab.R;

/**
 * @author Ganymede
 * 
 */
public class PreferenceWithButton extends Preference {

	private Button mButton;

	private OnClickListener buttonClickListener;

	private int widgetVisibility = View.VISIBLE;

	public PreferenceWithButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setWidgetLayoutResource(R.layout.widget_switch_button);
	}

	public PreferenceWithButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		setWidgetLayoutResource(R.layout.widget_switch_button);
	}

	public PreferenceWithButton(Context context) {
		super(context);
		setWidgetLayoutResource(R.layout.widget_switch_button);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.preference.Preference#onBindView(android.view.View)
	 */
	@Override
	protected void onBindView(View view) {
		super.onBindView(view);

		mButton = (Button) view.findViewById(R.id.pref_btn);
		if (mButton != null) {
			mButton.setOnClickListener(buttonClickListener);
			mButton.setFocusable(false);
			mButton.setTag(this);
			mButton.setVisibility(widgetVisibility);
		}
	}

	public Button getButton() {
		return mButton;
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
		if (mButton != null) {
			mButton.setVisibility(widgetVisibility);
		}
	}

}
