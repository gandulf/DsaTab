package com.dsatab.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.dsatab.R;

public class PortraitViewDialog extends AlertDialog implements DialogInterface.OnClickListener {

	public PortraitViewDialog(Context context, String title, Bitmap portrait) {
		super(context);
		init(title, portrait);
	}

	private void init(String title, Bitmap portrait) {
		setTitle(title);

		setCanceledOnTouchOutside(true);

		RelativeLayout popupcontent = (RelativeLayout) LayoutInflater.from(getContext()).inflate(
				R.layout.popup_portrait_view, null, false);
		popupcontent.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		setView(popupcontent);

		final ImageView image = (ImageView) popupcontent.findViewById(R.id.portrait_view);
		if (portrait != null)
			image.setImageBitmap(portrait);
		else
			image.setImageResource(R.drawable.profile_picture);

		setButton(BUTTON_NEUTRAL, getContext().getString(R.string.label_ok), this);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.content.DialogInterface.OnClickListener#onClick(android.content
	 * .DialogInterface, int)
	 */
	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (which == BUTTON_NEUTRAL)
			this.dismiss();

	}

}
