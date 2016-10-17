package com.franlopez.flipcheckbox;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.gandulf.guilib.R;

/*
 * Copyright 2014 Francisco Manuel Lopez Jurado
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

/**
 * FlipCheckBox is a View that holds a boolean state ("not checked" and
 * "checked"). Although it's called <i>FlipCheckBox</i>, the View has no
 * similarity nor implement in any way the Android's CheckBox component.
 * <p/>
 * Usage is very simple. You just need to add the view to any layout (like you
 * would do with any other View) and you are good to go. Of course, if you want
 * further customizations, you can assign values to the needed properties in the
 * layout or programmatically. Please, refer to those attributes documentation.
 * <p/>
 * By default, when the View is clicked, it will switch its state, and an event
 * to the assigned OnCheckedChangeListener
 * will be fired. Subscribe to that listener (using
 * {@link #setOnCheckedChangeListener(OnCheckedChangeListener)}) method.
 *
 * @author Francisco Manuel Lopez Jurado
 */
public class FlipCheckBox extends ViewFlipper implements CheckableListenable {

    private boolean mChecked = false;

    private static final int[] CHECKED_STATE_SET = {android.R.attr.state_checked};

    private OnCheckedChangeListener mOnCheckedChangeListener;

    private OnClickListener mOnClickListener;

    private OnClickListener mUnCheckOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            setChecked(false);
        }
    };
    /**
     * Child index to access the <i>front</i> view.
     */
    private static final int FRONT_VIEW_CHILD_INDEX = 0;
    /**
     * Child index to access the <i>rear</i> view.
     */
    private static final int REAR_VIEW_CHILD_INDEX = 1;

    /**
     * The item is not checked (and is displaying the <i>front</i> view).
     */
    public static final int STATUS_NOT_CHECKED = FRONT_VIEW_CHILD_INDEX;
    /**
     * The item is checked (and is displaying the <i>rear</i> view).
     */
    public static final int STATUS_CHECKED = REAR_VIEW_CHILD_INDEX;

    /**
     * Accept Animation.
     */
    private Animation acceptAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.scale);

	/* UI element references */

    /**
     * Reference to the <i>rear</i> view's ImageView.
     */
    private ImageView mIVAccept;

	/* Attributes */

    /**
     * Show animations on checked status changes.
     */
    private boolean mShowAnimations;
    /**
     * The <i>in</i> flip animation (not check -> check) resource identifier.
     */
    private int mInAnimationResId;
    /**
     * The <i>out</i> flip animation (not check -> check) resource identifier.
     */
    private int mOutAnimationResId;
    /**
     * The duration of the flip animation.
     */
    private long mFlipAnimationDuration;
    /**
     * Show the "accept" image.
     */
    private boolean mShowAcceptImage;

    private Drawable mFrontBackground;
    private Drawable mFrontClickableBackground;
	/* Constructors */

    /**
     * Constructor.
     *
     * @param context The Activity Context.
     */
    public FlipCheckBox(Context context) {
        super(context);
        if (!isInEditMode()) {
            initComponent(context, null, R.attr.flipCheckBoxStyle);
        }
    }

    /**
     * Constructor.
     *
     * @param context The Activity Context.
     * @param attrs   The view's attributes.
     */
    public FlipCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            initComponent(context, attrs, R.attr.flipCheckBoxStyle);
        }
    }

    // Uncomment if some day this components gets to API level 11 or above
    // /**
    // * Constructor.
    // *
    // * @param context
    // * The Activity Context.
    // * @param attrs
    // * The view's attributes.
    // * @param defStyle
    // * The default style to apply, if no one was provided.
    // */
    // public FlipCheckBox(Context context, AttributeSet attrs, int defStyle) {
    // super(context, attrs, defStyle);
    // initComponent(context, attrs, defStyle, 0);
    // }

	/* Initializers */

    /**
     * Initialize this view, by inflating it, finding its UI element references,
     * and applying the custom attributes provided by the programmer.
     *
     * @param context  The Activity Context.
     * @param attrs    The view's attributes.
     * @param defStyleAttr The default style to apply, if no one was provided.
     */
    private void initComponent(Context context, AttributeSet attrs,
                               int defStyleAttr) {
        // Inflate the view and find its UI elements references
        LayoutInflater.from(getContext()).inflate(R.layout.fcb_view,
                this, true);

        mIVAccept = (ImageView) findViewById(R.id.iv__card_back__accept);

        // Read and apply provided attributes
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.FlipCheckBox, defStyleAttr, R.style.FlipCheckBoxStyle);
        try {
            setFrontView(a.getResourceId(R.styleable.FlipCheckBox_frontLayout,
                    0));

            mFrontClickableBackground = a.getDrawable(R.styleable.FlipCheckBox_frontClickableBackgroundDrawable);
            mFrontBackground = a.getDrawable(R.styleable.FlipCheckBox_frontBackgroundDrawable);
            if (isClickable())
                setFrontBackgroundDrawable(mFrontClickableBackground);
            else
                setFrontBackgroundDrawable(mFrontBackground);

            setCheckedImmediate(a.getBoolean(R.styleable.FlipCheckBox_checked, false));
            setInAnimation(a.getResourceId(
                    R.styleable.FlipCheckBox_inAnimation, 0));
            setOutAnimation(a.getResourceId(
                    R.styleable.FlipCheckBox_outAnimation, 0));
            setShowAnimations(a.getBoolean(
                    R.styleable.FlipCheckBox_showAnimations, true));
            setFlipAnimationDuration((long) a.getInteger(
                    R.styleable.FlipCheckBox_flipAnimationDuration, 150));
        } catch (Exception ex) {
            Log.e(FlipCheckBox.class.getSimpleName(),
                    "Error applying provided attributes");
            throw new RuntimeException(ex);
        } finally {
            a.recycle();
        }
    }

    @Override
    public void setClickable(boolean clickable) {
        super.setClickable(clickable);
        if (isClickable())
            setFrontBackgroundDrawable(mFrontClickableBackground);
        else
            setFrontBackgroundDrawable(mFrontBackground);
    }
    public void setFrontResource(int drawableId) {
        if (getFrontView() instanceof ImageView) {
            ((ImageView) getFrontView()).setImageResource(drawableId);
        }
    }
    public void setFrontDrawable(Drawable drawable) {
        if (getFrontView() instanceof ImageView) {
            ((ImageView) getFrontView()).setImageDrawable(drawable);
        }
    }

    public void setFrontBackgroundDrawable(Drawable drawable) {
        if (getFrontView() instanceof ImageView) {
            ((ImageView) getFrontView()).setBackgroundDrawable(drawable);
        }
    }

    /**
     * Initialize "in" animation (check state from "not checked" to "checked"),
     * if needed. Will do nothing if the animation was already prepared.
     * <p/>
     * <b>Important:</b> Call this method only after calling
     * {@link #setInAnimation(int)}. Otherwise an exception will be thrown.
     */
    private void initInAnimation() {
        if (isShowingAnimations()) {
            setInAnimation(getContext(), getInAnimationResId());
            getInAnimation().setDuration(mFlipAnimationDuration);
        }
    }

    /**
     * Initialize "out" animation (check state from "checked" to "not checked"),
     * if needed. Will do nothing if the animation was already prepared.
     * <p/>
     * <b>Important:</b> Call this method only after calling
     * {@link #setOutAnimation(int)}. Otherwise an exception will be thrown.
     */
    private void initOutAnimation() {
        if (isShowingAnimations()) {
            setOutAnimation(getContext(), getOutAnimationResId());
            getOutAnimation().setDuration(mFlipAnimationDuration);
        }
    }

	/* Attribute access */

    /**
     * Set the front view to be displayed when this component is in a <i>not
     * checked</i> state. If an invalid resource or 0 is
     * passed, then the default view will be applied.
     *
     * @param layoutResId The layout resource identifier.
     */
    public void setFrontView(int layoutResId) {
        setFrontView(LayoutInflater.from(getContext()).inflate(
                layoutResId > 0 ? layoutResId : R.layout.fcb_view_front,
                null));
    }

    /**
     * Set the front view to be displayed when this component is in a <i>not
     * checked</i> state. The provided <i>view</i> must not be {@code null}, or
     * an exception will be thrown.
     *
     * @param view The view. Must not be {@code null}.
     */
    public void setFrontView(View view) {
        if (view == null)
            throw new IllegalArgumentException(
                    "The provided view must not be null");

        removeViewAt(FRONT_VIEW_CHILD_INDEX);
        addView(view, FRONT_VIEW_CHILD_INDEX);
    }

    /**
     * Get the View being displayed on the <i>front</i>. The front view is
     * displayed when the component is in a "not checked" state.
     *
     * @return The <i>front</i> view.
     */
    public View getFrontView() {
        return getChildAt(FRONT_VIEW_CHILD_INDEX);
    }

    /**
     * Get the View being displayed on the <i>rear</i>. The front view is
     * displayed when the component is in a "not checked" state.
     *
     * @return The <i>rear</i> view.
     */
    public View getRearView() {
        return getChildAt(REAR_VIEW_CHILD_INDEX);
    }

    /**
     * Get the ImageView reference which is being used to display the "accept"
     * mark onto the rear view, so can apply further customizations if needed.
     *
     * @return The "accept" ImageView.
     */
    public ImageView getAcceptImage() {
        return mIVAccept;
    }


    /**
     * Set whether or not the "accept" image on rear face should be displayed
     * when the user checks this component.
     *
     * @param showAcceptImage Show or hide the "accept" image.
     * @see #getAcceptImage()
     */
    public void setShowAcceptImage(boolean showAcceptImage) {
        mShowAcceptImage = showAcceptImage;
        mIVAccept.setVisibility(showAcceptImage ? View.VISIBLE : View.GONE);
    }

    /**
     * Returns whether or not the "accept" image on rear face is being
     * displayed.
     *
     * @return Will return {@code true} if showing, {@code false} otherwise.
     */
    public boolean isShowingAcceptImage() {
        return mShowAcceptImage;
    }

    /**
     * Set the state of this component to the given value, without applying the
     * corresponding animation, and without firing an event.
     *
     * @param checked The component state.
     */
    public void setCheckedImmediate(boolean checked) {
        mChecked = checked;
        if (getInAnimation() != null)
            setInAnimation(null);
        if (getOutAnimation() != null)
            setOutAnimation(null);
        setDisplayedChild(checked ? STATUS_CHECKED : STATUS_NOT_CHECKED);

        refreshDrawableState();

        initInAnimation();
        initOutAnimation();
    }

    /**
     * Returns whether or not this component is in a checked state.
     *
     * @return Whether or not this component is checked.
     */
    public boolean isChecked() {
        return mChecked;
    }

    /**
     * Set whether or not animations should be played on checked state changes.
     *
     * @param showAnimations Show animations on checked state changes.
     */
    public void setShowAnimations(boolean showAnimations) {
        mShowAnimations = showAnimations;
        initInAnimation();
        initOutAnimation();
    }

    /**
     * Return whether or not this component is displaying animations upon state
     * changes.
     *
     * @return Whether or not the component is animation state changes.
     */
    public boolean isShowingAnimations() {
        return mShowAnimations;
    }

    /**
     * Set the animation to be used when the component goes from "not checked"
     * to "checked" state. If an invalid resource or 0
     * is provided, then the default "in" animation will be set.
     *
     * @param inAnimationResId The animation resource identifier.
     */
    public void setInAnimation(int inAnimationResId) {
        mInAnimationResId = inAnimationResId > 0 ? inAnimationResId
                : R.anim.grow_from_middle;
        initInAnimation();
    }

    /**
     * Get the animation resource being used as "in" animation.
     *
     * @return The "in" animation resource.
     */
    public int getInAnimationResId() {
        return mInAnimationResId;
    }

    /**
     * Set the animation to be used when the component goes from "checked" to
     * "not checked" state. If an invalid resource or 0
     * is provided, then the default "out" animation will be set.
     *
     * @param outAnimationResId The animation resource identifier
     */
    public void setOutAnimation(int outAnimationResId) {
        mOutAnimationResId = outAnimationResId > 0 ? outAnimationResId
                : R.anim.shrink_to_middle;
        initOutAnimation();
    }

    /**
     * Get the animation resource being used as "out" animation.
     *
     * @return The "out" animation resource.
     */
    public int getOutAnimationResId() {
        return mOutAnimationResId;
    }

    /**
     * Set the duration of the state change animation.
     *
     * @param flipAnimationDuration The animation duration in milliseconds.
     */
    public void setFlipAnimationDuration(long flipAnimationDuration) {
        mFlipAnimationDuration = flipAnimationDuration;
        initInAnimation();
        initOutAnimation();
    }

    /**
     * Get the duration of the state change animation.
     *
     * @return The animation duration in milliseconds.
     */
    public long getFlipAnimationDuration() {
        return mFlipAnimationDuration;
    }

	/* Logic */

    /**
     * Set the state of this component to the given value, applying the
     * corresponding animation, if possible.
     *
     * @param checked The component state.
     */
    @Override
    public void setChecked(boolean checked) {
        if (mChecked != checked) {
            mChecked = checked;

            setDisplayedChild(checked ? STATUS_CHECKED : STATUS_NOT_CHECKED);
            if (isChecked() && isShowingAcceptImage() && isShowingAnimations()) {
                mIVAccept.startAnimation(acceptAnimation);
            }

            refreshDrawableState();

            if (mOnCheckedChangeListener != null) {
                mOnCheckedChangeListener.onCheckedChanged(this, mChecked);
            }

            // disable onclick listener in checked mode and replace by default unselect listener
            if (mChecked) {
                super.setOnClickListener(mUnCheckOnClickListener);
            } else {
                super.setOnClickListener(mOnClickListener);
            }
        }
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        super.setOnClickListener(l);

        mOnClickListener=l;
    }

    @Override
    public void toggle() {
        setChecked(!mChecked);
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        this.mOnCheckedChangeListener = onCheckedChangeListener;
    }

	/* State management */

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.checked = isChecked();
        ss.showAnimations = isShowingAnimations();
        ss.inAnimationResId = getInAnimationResId();
        ss.outAnimationResId = getOutAnimationResId();
        ss.flipAnimationDuration = getFlipAnimationDuration();
        ss.showAcceptImage = isShowingAcceptImage();
        return ss;
    }


    @Override
    // BUGFIX: http://daniel-codes.blogspot.co.at/2010/05/viewflipper-receiver-not-registered.html
    protected void onDetachedFromWindow() {
        try {
            super.onDetachedFromWindow();
        }
        catch (IllegalArgumentException e) {
            stopFlipping();
        }
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        setCheckedImmediate(ss.checked);
        setInAnimation(ss.inAnimationResId);
        setOutAnimation(ss.outAnimationResId);
        setShowAnimations(ss.showAnimations);
        setFlipAnimationDuration(ss.flipAnimationDuration);
        setShowAcceptImage(ss.showAcceptImage);
        requestLayout();
    }

    static class SavedState extends BaseSavedState {
        boolean checked;
        boolean showAnimations;
        int inAnimationResId;
        int outAnimationResId;
        long flipAnimationDuration;
        boolean showAcceptImage;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            checked = in.readInt() == 1;
            showAnimations = in.readInt() == 1;
            inAnimationResId = in.readInt();
            outAnimationResId = in.readInt();
            flipAnimationDuration = in.readLong();
            showAcceptImage = in.readInt() == 1;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(checked ? 1 : 0);
            out.writeInt(showAnimations ? 1 : 0);
            out.writeInt(inAnimationResId);
            out.writeInt(outAnimationResId);
            out.writeLong(flipAnimationDuration);
            out.writeInt(showAcceptImage ? 1 : 0);
        }

        @Override
        public String toString() {
            return "CompoundButton.SavedState{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " checked=" + checked + " showAnimations="
                    + showAnimations + " inAnimationResId=" + inAnimationResId
                    + " outAnimationResId=" + outAnimationResId
                    + " flipAnimationDuration=" + flipAnimationDuration
                    + " showAcceptImage=" + showAcceptImage + "}";
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}