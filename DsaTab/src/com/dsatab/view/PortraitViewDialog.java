package com.dsatab.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.dsatab.R;
import com.squareup.picasso.Picasso;

public class PortraitViewDialog extends AlertDialog implements DialogInterface.OnClickListener {

	private ImageView imageView;

	public PortraitViewDialog(Context context, String title, Uri portraitUri) {
		super(context);
		init(title);
		load(portraitUri);
	}

	public PortraitViewDialog(Context context, String title, Bitmap portrait) {
		super(context);
		init(title);
		load(portrait);
	}

	private void init(String title) {
		setTitle(title);

		setCanceledOnTouchOutside(true);

		RelativeLayout popupcontent = (RelativeLayout) LayoutInflater.from(getContext()).inflate(
				R.layout.popup_portrait_view, null, false);
		popupcontent.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

		imageView = (ImageView) popupcontent.findViewById(R.id.portrait_view);

		setView(popupcontent);

		setButton(BUTTON_NEUTRAL, getContext().getString(R.string.label_ok), this);

	}

	private void load(Uri portraitUri) {
		Picasso.with(getContext()).load(portraitUri).skipMemoryCache().placeholder(R.drawable.profile_picture)
				.into(imageView);
	}

	private void load(Bitmap portrait) {
		if (portrait != null)
			imageView.setImageBitmap(portrait);
		else
			imageView.setImageResource(R.drawable.profile_picture);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.DialogInterface.OnClickListener#onClick(android.content .DialogInterface, int)
	 */
	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (which == BUTTON_NEUTRAL)
			this.dismiss();

	}

}
