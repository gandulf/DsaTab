package com.dsatab.fragment;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.activity.DsaTabPreferenceActivity;
import com.dsatab.common.ClickSpan;
import com.dsatab.common.ClickSpan.OnSpanClickListener;
import com.dsatab.common.StyleableSpannableStringBuilder;
import com.dsatab.data.AbstractBeing;
import com.dsatab.data.Feature;
import com.dsatab.data.enums.AttributeType;
import com.dsatab.db.DataManager;
import com.dsatab.util.Debug;
import com.dsatab.util.PhotoPicker;
import com.dsatab.util.Util;
import com.dsatab.view.dialog.ImageChooserDialog;
import com.dsatab.view.dialog.PortraitViewDialog;
import com.dsatab.view.dialog.WebInfoDialog;
import com.gandulf.guilib.download.AbstractDownloader;
import com.gandulf.guilib.download.DownloaderWrapper;
import com.squareup.picasso.Picasso;

public abstract class BaseProfileFragment extends BaseAttributesFragment implements OnClickListener,
		OnLongClickListener {

	private static final String PREF_SHOW_FEATURE_COMMENTS = "SHOW_COMMENTS";

	private static final int ACTION_PHOTO = 1;
	private static final int ACTION_GALERY = 2;

	private TextView tfSpecialFeatures, tfSpecialFeaturesTitle, tfAdvantages, tfAdvantagesTitle, tfDisadvantages,
			tfDisadvantgesTitle;

	private ImageView portraitView;

	protected ActionMode mMode;

	protected ActionMode.Callback mCallback = new PortraitActionMode();

	private final class PortraitActionMode implements ActionMode.Callback {
		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			if (getBaseActivity() != null) {
				switch (item.getItemId()) {
				case R.id.option_take_photo:
					Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					startActivityForResult(camera, ACTION_PHOTO);
					break;
				case R.id.option_pick_image:
					Util.pickImage(BaseProfileFragment.this, ACTION_GALERY);
					break;
				case R.id.option_pick_avatar:
					ImageChooserDialog.pickPortrait(getBaseActivity(), getBeing());
					break;
				case R.id.option_download_avatars:
					AbstractDownloader downloader = DownloaderWrapper.getInstance(DsaTabApplication.getDsaTabPath(),
							getActivity());
					downloader.addPath(DsaTabPreferenceActivity.PATH_WESNOTH_PORTRAITS);
					downloader.downloadZip();
					Toast.makeText(getActivity(), R.string.message_download_started_in_background, Toast.LENGTH_SHORT)
							.show();
					break;
				}
			}
			mode.finish();
			return true;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			if (getHero() != null) {
				mode.getMenuInflater().inflate(R.menu.portrait_popupmenu, menu);
				mode.setTitle("Portrait");
				mode.setSubtitle(null);
			}
			return true;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			if (getActivity() != null) {
				resetPortaitView();
			}
			mMode = null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.actionbarsherlock.view.ActionMode.Callback#onPrepareActionMode
		 * (com.actionbarsherlock.view.ActionMode, com.actionbarsherlock.view.Menu)
		 */
		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			boolean portraits = ImageChooserDialog.hasPortraits();

			menu.findItem(R.id.option_download_avatars).setVisible(!portraits);
			menu.findItem(R.id.option_pick_avatar).setVisible(portraits);

			return true;
		}

	}

	@Override
	public View configureContainerView(View view) {
		view = super.configureContainerView(view);

		tfSpecialFeatures = (TextView) view.findViewById(R.id.gen_specialfeatures);
		tfSpecialFeaturesTitle = (TextView) view.findViewById(R.id.gen_specialfeatures_title);

		tfAdvantages = (TextView) view.findViewById(R.id.gen_advantages);
		tfAdvantagesTitle = (TextView) view.findViewById(R.id.gen_advantages_title);

		tfDisadvantages = (TextView) view.findViewById(R.id.gen_disadvantages);
		tfDisadvantgesTitle = (TextView) view.findViewById(R.id.gen_disadvantages_title);

		tfSpecialFeatures.setOnLongClickListener(this);
		tfSpecialFeaturesTitle.setOnLongClickListener(this);
		tfAdvantages.setOnLongClickListener(this);
		tfAdvantagesTitle.setOnLongClickListener(this);
		tfDisadvantages.setOnLongClickListener(this);
		tfDisadvantgesTitle.setOnLongClickListener(this);

		portraitView = (ImageView) view.findViewById(R.id.gen_portrait);

		portraitView.setOnLongClickListener(this);
		portraitView.setOnClickListener(this);

		return view;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.activity.BaseMainActivity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {
		case ACTION_GALERY:
			if (resultCode == Activity.RESULT_OK && getBeing() != null) {

				Uri uri = Util.retrieveBitmapUri(getActivity(), data);
				PhotoPicker picker = new PhotoPicker(uri, getActivity().getContentResolver());

				try {
					Bitmap bitmap = picker.getBitmap();
					if (bitmap != null) {

						String photoName = "photo"
								+ Util.convertNonAscii(getHero().getName() + "_" + getBeing().getName()) + ".jpg";

						File outputfile = Util.saveBitmap(bitmap, photoName);
						if (outputfile != null) {
							// set uri for currently selected player
							getBeing().setPortraitUri(outputfile.toURI());
							updatePortrait(getBeing());
						}
					} else {
						Toast.makeText(getActivity(),
								"Konnte Bild nicht öffnen. Verwende die Standard Galerie um eine Bild auszuwählen.",
								Toast.LENGTH_LONG).show();
					}
				} catch (IOException e) {
					Debug.error(e);
					Toast.makeText(getActivity(),
							"Konnte Bild nicht öffnen. Verwende die Standard Galerie um eine Bild auszuwählen.",
							Toast.LENGTH_LONG).show();
				}
			}

			break;
		case ACTION_PHOTO:

			if (resultCode == Activity.RESULT_OK && getBeing() != null) {

				// Retrieve image taking in camera activity
				Bundle b = data.getExtras();
				Bitmap pic = (Bitmap) b.get("data");

				if (pic != null) {

					String photoName = "photo" + Util.convertNonAscii(getHero().getName() + "_" + getBeing().getName())
							+ ".jpg";

					File outputfile = Util.saveBitmap(pic, photoName);
					if (outputfile != null) {
						// set uri for currently selected player
						getBeing().setPortraitUri(outputfile.toURI());

						updatePortrait(getBeing());
					}
				}
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	private void showPortrait() {
		Uri portraitUri = getBeing().getPortraitUri();

		if (portraitUri != null) {
			PortraitViewDialog viewDialog = new PortraitViewDialog(getActivity(), getBeing().getName(), portraitUri);
			viewDialog.show();
		} else {
			ImageChooserDialog.pickPortrait(getBaseActivity(), getBeing());
		}
	}

	protected void updatePortrait(AbstractBeing being) {

		Uri portraitUri = null;
		if (being != null) {
			portraitUri = being.getPortraitUri();
		}

		if (portraitUri != null)
			Picasso.with(getActivity()).load(portraitUri).placeholder(R.drawable.profile_picture).centerCrop()
					.resize(300, 300).skipMemoryCache().into(portraitView);
		else
			portraitView.setImageResource(R.drawable.profile_picture);
	}

	protected void resizePortaiView() {
		ViewGroup.LayoutParams layoutParams = (ViewGroup.LayoutParams) portraitView.getLayoutParams();
		int imageWidth = portraitView.getWidth(), imageHeight = portraitView.getHeight();

		if (portraitView.getDrawable().getIntrinsicWidth() > 0)
			imageWidth = portraitView.getDrawable().getIntrinsicWidth();

		if (portraitView.getDrawable().getIntrinsicHeight() > 0)
			imageHeight = portraitView.getDrawable().getIntrinsicHeight();

		float ratio = portraitView.getHeight() / (float) imageHeight;
		layoutParams.width = (int) (imageWidth * ratio);

		portraitView.requestLayout();
	}

	protected void resetPortaitView() {
		ViewGroup.LayoutParams layoutParams = (ViewGroup.LayoutParams) portraitView.getLayoutParams();
		layoutParams.width = DsaTabApplication.getInstance().getResources()
				.getDimensionPixelSize(R.dimen.portrait_width_small);
		layoutParams.height = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
		portraitView.requestLayout();
	}

	public abstract AbstractBeing getBeing();

	public boolean onLongClick(View v) {
		switch (v.getId()) {

		case R.id.gen_specialfeatures:
		case R.id.gen_specialfeatures_title:
		case R.id.gen_advantages:
		case R.id.gen_advantages_title:
		case R.id.gen_disadvantages:
		case R.id.gen_disadvantages_title: {
			boolean showComments = getPreferences().getBoolean(PREF_SHOW_FEATURE_COMMENTS, true);

			showComments = !showComments;
			Editor edit = getPreferences().edit();
			edit.putBoolean(PREF_SHOW_FEATURE_COMMENTS, showComments);
			edit.commit();

			fillSpecialFeatures(getBeing());
			return true;
		}
		case R.id.gen_portrait:
			if (getBeing() == null)
				return false;

			if (mMode == null) {
				mMode = ((ActionBarActivity) getActivity()).startSupportActionMode(mCallback);
				customizeActionModeCloseButton();
				mMode.invalidate();
				resizePortaiView();
			}
			return true;
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.gen_portrait:

			if (getBeing() == null)
				return;

			if (getBeing().getPortraitUri() == null) {
				onLongClick(v);
			} else {
				showPortrait();
			}
			break;
		}

	}

	protected void fillAttributesList(View view) {
		if (getView() == null)
			return;

		if (tfMR == null || tfLabelMU == null) {
			findViews(getView());
		}
		fillAttributeValue(tfMU, AttributeType.Mut);
		fillAttributeValue(tfKL, AttributeType.Klugheit);
		fillAttributeValue(tfIN, AttributeType.Intuition);
		fillAttributeValue(tfCH, AttributeType.Charisma);
		fillAttributeValue(tfFF, AttributeType.Fingerfertigkeit);
		fillAttributeValue(tfGE, AttributeType.Gewandtheit, false);
		fillAttributeValue(tfKO, AttributeType.Konstitution);
		fillAttributeValue(tfKK, AttributeType.Körperkraft);

		fillAttributeLabel((View) tfLabelMU.getParent(), AttributeType.Mut);
		fillAttributeLabel((View) tfLabelKL.getParent(), AttributeType.Klugheit);
		fillAttributeLabel((View) tfLabelIN.getParent(), AttributeType.Intuition);
		fillAttributeLabel((View) tfLabelCH.getParent(), AttributeType.Charisma);
		fillAttributeLabel((View) tfLabelFF.getParent(), AttributeType.Fingerfertigkeit);
		fillAttributeLabel((View) tfLabelGE.getParent(), AttributeType.Gewandtheit);
		fillAttributeLabel((View) tfLabelKO.getParent(), AttributeType.Konstitution);
		fillAttributeLabel((View) tfLabelKK.getParent(), AttributeType.Körperkraft);
	}

	/**
	 * @param hero
	 */
	protected void fillSpecialFeatures(AbstractBeing hero) {

		boolean showComments = getPreferences().getBoolean(PREF_SHOW_FEATURE_COMMENTS, true);

		StyleableSpannableStringBuilder featureBuilder = new StyleableSpannableStringBuilder();
		StyleableSpannableStringBuilder advantageBuilder = new StyleableSpannableStringBuilder();
		StyleableSpannableStringBuilder disadvantageBuilder = new StyleableSpannableStringBuilder();

		tfSpecialFeatures.setMovementMethod(LinkMovementMethod.getInstance());
		String[] featureInfos = DataManager.getWebInfos(getActivity()).toArray(new String[0]);
		Arrays.sort(featureInfos);

		OnSpanClickListener linkClicker = new OnSpanClickListener() {
			@Override
			public void onClick(CharSequence tag, ClickSpan v) {
				if (getActivity() != null) {
					WebInfoDialog.show(getActivity(), tag);
				}
			}
		};
		StyleableSpannableStringBuilder currentBuilder = null;
		if (hero != null && !hero.getSpecialFeatures().isEmpty()) {
			for (Feature feature : hero.getSpecialFeatures().values()) {
				switch (feature.getType().type()) {
				case Advantage:
					currentBuilder = advantageBuilder;
					break;
				case Disadvantage:
					currentBuilder = disadvantageBuilder;
					break;
				case SpecialFeature:
					currentBuilder = featureBuilder;
					break;
				}

				if (currentBuilder.length() > 0) {
					currentBuilder.append(", ");
				}

				String tag = feature.getType().xmlName();

				if (Arrays.binarySearch(featureInfos, tag) >= 0) {
					currentBuilder.appendClick(linkClicker, feature.toString(), tag);
				} else {
					currentBuilder.append(feature.toString());
				}

				if (showComments && !TextUtils.isEmpty(feature.getComment())) {
					currentBuilder.appendColor(Color.GRAY, " (");
					currentBuilder.appendColor(Color.GRAY, feature.getComment());
					currentBuilder.appendColor(Color.GRAY, ")");
				}

			}
		}

		if (featureBuilder.length() > 0) {
			tfSpecialFeaturesTitle.setVisibility(View.VISIBLE);
			tfSpecialFeatures.setVisibility(View.VISIBLE);
			tfSpecialFeatures.setText(featureBuilder);
		} else {
			tfSpecialFeatures.setVisibility(View.GONE);
			tfSpecialFeaturesTitle.setVisibility(View.GONE);
		}

		if (advantageBuilder.length() > 0) {
			tfAdvantagesTitle.setVisibility(View.VISIBLE);
			tfAdvantages.setVisibility(View.VISIBLE);
			tfAdvantages.setText(advantageBuilder);
		} else {
			tfAdvantages.setVisibility(View.GONE);
			tfAdvantagesTitle.setVisibility(View.GONE);
		}

		if (disadvantageBuilder.length() > 0) {
			tfDisadvantgesTitle.setVisibility(View.VISIBLE);
			tfDisadvantages.setVisibility(View.VISIBLE);
			tfDisadvantages.setText(disadvantageBuilder);
		} else {
			tfDisadvantages.setVisibility(View.GONE);
			tfDisadvantgesTitle.setVisibility(View.GONE);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.view.listener.HeroChangedListener#onPortraitChanged()
	 */
	@Override
	public void onPortraitChanged() {
		updatePortrait(getBeing());
		getBaseActivity().setupNavigationDrawer();
	}
}
