package com.dsatab.fragment;

import java.io.File;
import java.util.Arrays;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
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
import com.dsatab.fragment.dialog.WebInfoDialog;
import com.dsatab.util.Util;
import com.dsatab.view.dialog.ImageChooserDialog;
import com.ecloud.pulltozoomview.PullToZoomScrollView;
import com.gandulf.guilib.download.AbstractDownloader;
import com.gandulf.guilib.download.DownloaderWrapper;

import de.hdodenhof.circleimageview.CircleImageView;

public abstract class BaseProfileFragment extends BaseAttributesFragment implements OnClickListener,
		OnLongClickListener {

	private static final String PREF_SHOW_FEATURE_COMMENTS = "SHOW_COMMENTS";

	private static final String PREF_EXPAND_BASEINFO = "SHOW_BASEINFO";
	private static final String PREF_SHOW_BASEINFO = "SHOW_BASEINFO_OPEN";

	private static final int ACTION_PHOTO = 1;
	private static final int ACTION_GALERY = 2;

	private TextView tfSpecialFeatures, tfSpecialFeaturesTitle, tfAdvantages, tfAdvantagesTitle, tfDisadvantages,
			tfDisadvantgesTitle;

	protected ViewGroup descriptions;
	private ImageButton detailsSwitch, detailsHide, detailsInfo;

	private ImageView portraitView;
	protected CircleImageView portraitViewSmall;
	protected TextView portraitName;

	protected PullToZoomScrollView scrollView;

	protected PullToZoomScrollView.OnScrollViewChangedListener mOnScrollChangedListener = new PullToZoomScrollView.OnScrollViewChangedListener() {

		@Override
		public void onScrollChanged(int left, int top, int oldLeft, int oldTop) {
			final int headerHeight = findViewById(R.id.gen_portrait).getHeight()
					- getActivity().getActionBar().getHeight();
			final float ratio = (float) Math.min(Math.max(top, 0), headerHeight) / headerHeight;
			final int newAlpha = (int) (ratio * 255);
			if (getBaseActivity() != null) {
				getBaseActivity().setActionbarBackgroundAlpha(newAlpha);
			}
		}
	};

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

		descriptions = (ViewGroup) view.findViewById(R.id.gen_description);
		descriptions.setOnClickListener(this);

		detailsSwitch = (ImageButton) view.findViewById(R.id.details_switch);
		detailsSwitch.setOnClickListener(this);

		detailsInfo = (ImageButton) view.findViewById(R.id.gen_portrait_info);
		detailsInfo.setOnClickListener(this);

		detailsHide = (ImageButton) view.findViewById(R.id.details_hide);
		detailsHide.setOnClickListener(this);

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

		portraitViewSmall = (CircleImageView) view.findViewById(R.id.gen_portrait_small);
		portraitViewSmall.setOnLongClickListener(this);
		portraitViewSmall.setOnClickListener(this);

		portraitName = (TextView) view.findViewById(R.id.gen_portrait_name);
		portraitName.setOnClickListener(this);

		return view;
	}

	public void closeDescription(boolean animate) {
		Editor edit = getPreferences().edit();
		edit.putBoolean(PREF_SHOW_BASEINFO + getClass().getSimpleName(), false);
		edit.apply();

		if (animate && descriptions.getVisibility() != View.GONE) {
			descriptions.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
		}
		descriptions.setVisibility(View.GONE);
		detailsInfo.setVisibility(View.VISIBLE);

	}

	public void openDescription(boolean animate) {
		Editor edit = getPreferences().edit();
		edit.putBoolean(PREF_SHOW_BASEINFO + getClass().getSimpleName(), true);
		edit.apply();

		if (animate && descriptions.getVisibility() != View.VISIBLE) {
			descriptions.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
		}
		descriptions.setVisibility(View.VISIBLE);
		detailsInfo.setVisibility(View.INVISIBLE);

	}

	public void toggleDescription(boolean animate) {
		Editor edit = getPreferences().edit();
		edit.putBoolean(PREF_EXPAND_BASEINFO + getClass().getSimpleName(),
				!getPreferences().getBoolean(PREF_EXPAND_BASEINFO + getClass().getSimpleName(), true));
		edit.apply();

		updateBaseInfo(true);
	}

	public boolean isDescriptionExpanded() {
		return getPreferences().getBoolean(PREF_EXPAND_BASEINFO + getClass().getSimpleName(), true);
	}

	protected void updateBaseInfo(boolean animate) {
		boolean opened = getPreferences().getBoolean(PREF_SHOW_BASEINFO + getClass().getSimpleName(), true);

		if (opened) {
			descriptions.setVisibility(View.VISIBLE);
			detailsInfo.setVisibility(View.INVISIBLE);
		} else {
			descriptions.setVisibility(View.GONE);
			detailsInfo.setVisibility(View.VISIBLE);
		}

		if (isDescriptionExpanded()) {
			if (animate) {
				ObjectAnimator animator = ObjectAnimator.ofFloat(detailsSwitch, "rotation", 180f, 0f);
				animator.setTarget(detailsSwitch);
				animator.setDuration(250);
				animator.start();
			} else {
				detailsSwitch.setRotation(0f);
			}
		} else {
			if (animate) {
				ObjectAnimator animator = ObjectAnimator.ofFloat(detailsSwitch, "rotation", 0f, 180f);
				animator.setTarget(detailsSwitch);
				animator.setDuration(250);
				animator.start();
			} else {
				detailsSwitch.setRotation(180f);
			}
		}
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

				if (uri != null) {
					// set uri for currently selected player
					getBeing().setPortraitUri(uri);
					updatePortrait(getBeing());
				} else {
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

					String photoName = "photo" + System.currentTimeMillis() + ".jpg";

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

	protected void updatePortrait(AbstractBeing being) {

		Uri portraitUri = null;
		if (being != null) {
			portraitUri = being.getPortraitUri();

			portraitName.setText(being.getName());
		} else {
			portraitName.setText(null);
		}

		Util.setImage(portraitView, portraitUri, R.drawable.profile_picture);
		Util.setImage(portraitViewSmall, portraitUri, R.drawable.profile_picture);
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
		case R.id.gen_portrait_small:
			if (getBeing() == null)
				return false;

			if (mMode == null) {
				mMode = getActivity().startActionMode(mCallback);
				mMode.invalidate();
			}
			return true;
		}
		return false;
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.gen_description:
		case R.id.details_switch:
			toggleDescription(true);
			break;
		case R.id.details_hide:
			closeDescription(true);
			break;
		case R.id.gen_portrait_name:
		case R.id.gen_portrait_info:
			openDescription(true);
			break;
		case R.id.gen_portrait_small:
		case R.id.gen_portrait:

			if (getBeing() == null)
				return;

			if (getBeing().getPortraitUri() == null) {
				onLongClick(v);
			} else {
				scrollView.toggleZoomeScale();
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

	@Override
	public void onPause() {
		super.onPause();
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
					WebInfoDialog.show(BaseProfileFragment.this, tag, 0);
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

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		scrollView = (PullToZoomScrollView) view.findViewById(R.id.scroll_view);

		if (scrollView != null) {
			scrollView.setOnScrollListener(mOnScrollChangedListener);
			scrollView.setOverScrollEnabled(false);
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
