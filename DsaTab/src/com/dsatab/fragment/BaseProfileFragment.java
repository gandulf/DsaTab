package com.dsatab.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.activity.DsaTabPreferenceActivity;
import com.dsatab.data.AbstractBeing;
import com.dsatab.data.Feature;
import com.dsatab.data.Hero;
import com.dsatab.data.enums.AttributeType;
import com.dsatab.db.DataManager;
import com.dsatab.fragment.dialog.ImageChooserDialog;
import com.dsatab.fragment.dialog.WebInfoDialog;
import com.dsatab.util.ClickSpan;
import com.dsatab.util.ClickSpan.OnSpanClickListener;
import com.dsatab.util.StyleableSpannableStringBuilder;
import com.dsatab.util.Util;
import com.ecloud.pulltozoomview.PullToZoomScrollView;
import com.gandulf.guilib.download.AbstractDownloader;
import com.gandulf.guilib.download.DownloaderWrapper;

import java.io.File;
import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;

public abstract class BaseProfileFragment extends BaseAttributesFragment implements OnClickListener,
        OnLongClickListener, PopupMenu.OnMenuItemClickListener {

    private static final String PREF_SHOW_FEATURE_COMMENTS = "SHOW_COMMENTS";

    private static final String PREF_EXPAND_BASEINFO = "SHOW_BASEINFO";
    private static final String PREF_SHOW_BASEINFO = "SHOW_BASEINFO_OPEN";

    private static final int ACTION_PHOTO = 1;
    private static final int ACTION_GALERY = 2;

    private TextView tfSpecialFeatures, tfSpecialFeaturesTitle, tfAdvantages, tfAdvantagesTitle, tfDisadvantages,
            tfDisadvantgesTitle;

    protected ViewGroup descriptions;
    private ImageButton detailsSwitch, detailsHide;

    private int descriptionsHeight;

    @Override
    public View configureContainerView(View view) {
        view = super.configureContainerView(view);

        descriptions = (ViewGroup) view.findViewById(R.id.gen_description);
        descriptions.setOnClickListener(this);

        detailsSwitch = (ImageButton) view.findViewById(R.id.details_switch);
        if (detailsSwitch!=null)
            detailsSwitch.setOnClickListener(this);

        detailsHide = (ImageButton) view.findViewById(R.id.details_hide);
        if (detailsHide != null)
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

        return view;
    }

    @SuppressLint("NewApi")
    public void closeDescription(boolean animate) {
        Editor edit = getPreferences().edit();
        edit.putBoolean(PREF_SHOW_BASEINFO + getClass().getSimpleName(), false);
        edit.apply();

        if (animate && descriptions.getVisibility() != View.GONE) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                float radius = Math.max(descriptions.getWidth(), descriptions.getHeight()) * 2.0f;
                Animator reveal = ViewAnimationUtils.createCircularReveal(descriptions, descriptions.getRight()
                        - detailsHide.getWidth() / 2, descriptions.getTop() + detailsHide.getHeight() / 2, radius, 0);
                reveal.setDuration(600);
                reveal.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        descriptions.setVisibility(View.GONE);
                    }
                });

                ValueAnimator va = ValueAnimator.ofInt(descriptions.getHeight(), 0);
                va.setStartDelay(300);
                va.setDuration(600);
                va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimator animation) {
                        Integer value = (Integer) animation.getAnimatedValue();
                        descriptions.getLayoutParams().height = value.intValue();
                        descriptions.requestLayout();
                    }
                });

                AnimatorSet set = new AnimatorSet();
                set.playTogether(reveal, va);
                set.setInterpolator(new DecelerateInterpolator(2.0f));
                set.start();

            } else {
                descriptions.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
                descriptions.setVisibility(View.GONE);
            }
        } else {
            descriptions.setVisibility(View.GONE);
        }

    }

    @SuppressLint("NewApi")
    public void openDescription(View origin, boolean animate) {
        Editor edit = getPreferences().edit();
        edit.putBoolean(PREF_SHOW_BASEINFO + getClass().getSimpleName(), true);
        edit.apply();

        if (animate && descriptions.getVisibility() != View.VISIBLE) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                float radius = Math.max(descriptions.getWidth(), descriptionsHeight) * 2.0f;
                Animator reveal = ViewAnimationUtils.createCircularReveal(descriptions, descriptions.getRight()
                        - detailsHide.getWidth() / 2, descriptions.getTop() + detailsHide.getHeight() / 2, 0, radius);
                reveal.setDuration(1200);

                ValueAnimator va = ValueAnimator.ofInt(0, descriptionsHeight);
                va.setDuration(600);
                va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimator animation) {
                        Integer value = (Integer) animation.getAnimatedValue();
                        descriptions.getLayoutParams().height = value.intValue();
                        descriptions.requestLayout();
                    }
                });

                AnimatorSet set = new AnimatorSet();
                set.setInterpolator(new DecelerateInterpolator(2.0f));
                set.playTogether(va, reveal);
                set.start();

            } else {
                descriptions.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
            }
        }
        descriptions.setVisibility(View.VISIBLE);

    }

    public void toggleDescriptionDetails(boolean animate) {
        Editor edit = getPreferences().edit();
        edit.putBoolean(PREF_EXPAND_BASEINFO + getClass().getSimpleName(),
                !getPreferences().getBoolean(PREF_EXPAND_BASEINFO + getClass().getSimpleName(), true));
        edit.apply();

        updateBaseInfo(true);
    }

    public boolean isDescriptionDetailsExpanded() {
        return getPreferences().getBoolean(PREF_EXPAND_BASEINFO + getClass().getSimpleName(), true);
    }

    protected void updateBaseInfo(boolean animate) {
        boolean opened = getPreferences().getBoolean(PREF_SHOW_BASEINFO + getClass().getSimpleName(), true);

        descriptions.getLayoutParams().height = LayoutParams.WRAP_CONTENT;

        if (opened) {
            openDescription(null, animate);
        } else {
            closeDescription(animate);
        }

        if (isDescriptionDetailsExpanded()) {
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
                        getDsaActivity().updatePortrait(getBeing());
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

                            getDsaActivity().updatePortrait(getBeing());
                        }
                    }
                }
        }

        super.onActivityResult(requestCode, resultCode, data);
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

        }
        return false;
    }

    //TODO add func
    protected void showPortaitMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(getActivity(), v);
        Menu menu = popupMenu.getMenu();
        popupMenu.getMenuInflater().inflate(R.menu.portrait_popupmenu, menu);

        boolean portraits = ImageChooserDialog.hasPortraits();
        if (menu.findItem(R.id.option_download_avatars) != null)
            menu.findItem(R.id.option_download_avatars).setVisible(!portraits);
        if (menu.findItem(R.id.option_pick_avatar) != null)
            menu.findItem(R.id.option_pick_avatar).setVisible(portraits);

        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (getDsaActivity() != null) {
            switch (item.getItemId()) {
                case R.id.option_take_photo:
                    Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(camera, ACTION_PHOTO);
                    break;
                case R.id.option_pick_image:
                    Util.pickImage(BaseProfileFragment.this, ACTION_GALERY);
                    break;
                case R.id.option_pick_avatar:
                    ImageChooserDialog.pickPortrait(BaseProfileFragment.this, getBeing(), 0);
                    break;
                case R.id.option_download_avatars:
                    AbstractDownloader downloader = DownloaderWrapper.getInstance(DsaTabApplication.getDirectory(),
                            getActivity());
                    downloader.addPath(DsaTabPreferenceActivity.PATH_WESNOTH_PORTRAITS);
                    downloader.downloadZip();
                    Toast.makeText(getActivity(), R.string.message_download_started_in_background, Toast.LENGTH_SHORT)
                            .show();
                    break;
            }
        }
        return false;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.gen_description:
            case R.id.details_switch:
                toggleDescriptionDetails(true);
                break;
            case R.id.details_hide:
                closeDescription(true);
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

    @Override
    public void onResume() {
        super.onResume();

        final int v = descriptions.getVisibility();
        descriptions.setVisibility(View.VISIBLE);
        final ViewTreeObserver vto = descriptions.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                descriptionsHeight = descriptions.getHeight();
                descriptions.setVisibility(v);
                descriptions.requestLayout();
                ViewTreeObserver obs = descriptions.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
            }
        });
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

    /*
     * (non-Javadoc)
     *
     * @see com.dsatab.view.listener.HeroChangedListener#onPortraitChanged()
     */
    @Override
    public void onPortraitChanged() {
        getDsaActivity().updatePortrait(getBeing());
        if (getBeing() instanceof Hero) {
            getDsaActivity().setupDrawerProfile((Hero) getBeing());
        }
    }

}
