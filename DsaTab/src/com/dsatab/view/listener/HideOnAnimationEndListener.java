package com.dsatab.view.listener;

import android.view.View;

import com.nineoldandroids.animation.Animator;

public class HideOnAnimationEndListener implements Animator.AnimatorListener {

	private View view;

	public HideOnAnimationEndListener(View view) {
		this.view = view;
	}

	@Override
	public void onAnimationStart(Animator arg0) {

	}

	@Override
	public void onAnimationRepeat(Animator arg0) {

	}

	@Override
	public void onAnimationEnd(Animator arg0) {
		view.setVisibility(View.GONE);
	}

	@Override
	public void onAnimationCancel(Animator arg0) {

	}
}
