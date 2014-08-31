package com.dsatab.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

import com.dsatab.DsaTabApplication;
import com.dsatab.data.HeroFileInfo;
import com.dsatab.fragment.HeroChooserFragment;
import com.dsatab.fragment.HeroChooserFragment.OnHeroSelectedListener;

public class HeroChooserActivity extends BaseActivity implements OnHeroSelectedListener {

	private HeroChooserFragment fragment;

	public HeroChooserActivity() {
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(DsaTabApplication.getInstance().getCustomTheme());
		applyPreferencesToTheme();
		super.onCreate(savedInstanceState);

		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		fragment = (HeroChooserFragment) getSupportFragmentManager().findFragmentById(android.R.id.content);

		if (fragment == null) {
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			if (fragment != null) {
				ft.remove(fragment);
			}

			fragment = new HeroChooserFragment();
			fragment.setArguments(getIntent().getExtras());
			ft.add(android.R.id.content, fragment);
			ft.commit();
		}

		DsaTabApplication.getInstance().showNewsInfoPopup(this);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onHeroSelected(HeroFileInfo heroFileInfo) {
		getIntent().putExtra(HeroChooserFragment.INTENT_NAME_HERO_FILE_INFO, heroFileInfo);
		setResult(RESULT_OK, getIntent());
		finish();
	}
}