package com.dsatab.activity;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.cloud.HeroExchange;
import com.dsatab.cloud.HeroExchange.OnHeroExchangeListener;
import com.dsatab.data.HeroFileInfo;
import com.dsatab.util.Debug;
import com.dsatab.util.Util;
import com.rokoder.android.lib.support.v4.widget.GridViewCompat;

/**
 * 
 * 
 */
public class HeroChooserActivity extends BaseActivity implements AdapterView.OnItemClickListener,
		OnItemLongClickListener {

	public static final String INTENT_NAME_HERO_PATH = "heroPath";

	private static final String DUMMY_FILE = "Dummy.xml";
	private static final String DUMMY_NAME = "Dummy";

	private GridViewCompat list;
	private HeroAdapter adapter;
	private boolean dummy, writable = true;;

	private ActionMode mMode;

	private ActionMode.Callback mCallback;

	private final class HeroesActionMode implements ActionMode.Callback {
		@Override
		public boolean onActionItemClicked(ActionMode mode, com.actionbarsherlock.view.MenuItem item) {
			boolean notifyChanged = false;
			if (list == null || adapter == null) {
				return false;
			}

			SparseBooleanArray checkedPositions = list.getCheckedItemPositionsC();
			if (checkedPositions != null) {
				adapter.setNotifyOnChange(false);
				for (int i = checkedPositions.size() - 1; i >= 0; i--) {
					if (checkedPositions.valueAt(i)) {
						final HeroFileInfo heroInfo = adapter.getItem(checkedPositions.keyAt(i));

						switch (item.getItemId()) {
						case R.id.option_delete:
							Debug.verbose("Deleting " + heroInfo.getName());
							heroInfo.getFile().delete();
							adapter.remove(heroInfo);
							notifyChanged = true;
							break;
						case R.id.option_download:
							if (heroInfo.isOnline()) {
								HeroExchange exchange = new HeroExchange(HeroChooserActivity.this);
								exchange.setOnHeroExchangeListener(new OnHeroExchangeListener() {
									@Override
									public void onHeroLoaded(String path) {
										Toast.makeText(HeroChooserActivity.this,
												heroInfo.getName() + " wurde erfolgreich heruntergeladen",
												Toast.LENGTH_SHORT).show();
									}

									@Override
									public void onHeroInfoLoaded(HeroFileInfo info) {
									}
								});
								exchange.importHero(heroInfo);
							}
							break;
						case R.id.option_upload:
							HeroExchange exchange2 = new HeroExchange(HeroChooserActivity.this);
							exchange2.exportHero(heroInfo.getFile());
							break;
						}
					}

				}
				adapter.setNotifyOnChange(true);
				if (notifyChanged) {
					adapter.notifyDataSetChanged();
				}
			}
			mode.finish();
			return true;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			mode.getMenuInflater().inflate(R.menu.herochooser_menu, menu);
			mode.setTitle("Helden");
			return true;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			mMode = null;
			if (list != null) {
				list.clearChoicesC();
			}
			if (adapter != null) {
				adapter.notifyDataSetChanged();
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.actionbarsherlock.view.ActionMode.Callback#onPrepareActionMode
		 * (com.actionbarsherlock.view.ActionMode, com.actionbarsherlock.view.Menu)
		 */
		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			int selected = 0;
			boolean online = false;
			SparseBooleanArray checkedPositions = list.getCheckedItemPositionsC();
			if (checkedPositions != null) {
				for (int i = checkedPositions.size() - 1; i >= 0; i--) {
					if (checkedPositions.valueAt(i)) {
						selected++;
						HeroFileInfo heroInfo = adapter.getItem(checkedPositions.keyAt(i));
						online |= heroInfo.isOnline();
					}
				}
			}

			mode.setSubtitle(selected + " ausgewählt");

			boolean changed = false;

			MenuItem download = menu.findItem(R.id.option_download);
			if (download != null && online != download.isEnabled()) {
				download.setEnabled(online);
				changed = true;
			}

			return changed;
		}
	}

	@Override
	protected void onPause() {
		if (mMode != null) {
			mMode.finish();
		}
		super.onPause();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(DsaTabApplication.getInstance().getCustomTheme());
		applyPreferencesToTheme();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sheet_hero_chooser);

		mCallback = new HeroesActionMode();

		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		List<HeroFileInfo> heroes = null;

		String error = Util.checkFileWriteAccess(DsaTabApplication.getDsaTabHeroDirectory());
		if (error != null) {
			Toast.makeText(this, error, Toast.LENGTH_LONG).show();
			writable = false;
		} else {
			writable = true;
		}

		// create test hero if no heroes avialable
		if (writable && !DsaTabApplication.getInstance().hasHeroes()) {
			dummy = true;

			FileOutputStream fos = null;
			InputStream fis = null;
			try {
				fos = new FileOutputStream(DsaTabApplication.getDsaTabHeroPath() + DUMMY_FILE);
				fis = new BufferedInputStream(getAssets().open(DUMMY_FILE));
				byte[] buffer = new byte[8 * 1024];
				int length;

				while ((length = fis.read(buffer)) >= 0) {
					fos.write(buffer, 0, length);
				}
			} catch (FileNotFoundException e) {
				Debug.error(e);
			} catch (IOException e) {
				Debug.error(e);
			} finally {
				try {
					if (fos != null)
						fos.close();

					if (fis != null)
						fis.close();
				} catch (IOException e) {

				}
			}

		} else {
			heroes = DsaTabApplication.getInstance().getHeroes();

			if (heroes.size() == 1 && heroes.get(0).getName().equals(DUMMY_NAME))
				dummy = true;
		}

		if (heroes == null)
			heroes = DsaTabApplication.getInstance().getHeroes();

		list = (GridViewCompat) findViewById(R.id.popup_hero_chooser_list);
		list.setChoiceModeC(AbsListView.CHOICE_MODE_MULTIPLE);
		adapter = new HeroAdapter(this, R.layout.hero_chooser_item, heroes);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
		list.setOnItemLongClickListener(this);

		updateViews();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.activity.BaseActivity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// Util.unbindDrawables(findViewById(android.R.id.content));
		super.onDestroy();
	}

	private void updateViews() {
		TextView empty = (TextView) findViewById(R.id.popup_hero_empty);

		if (!DsaTabApplication.getInstance().hasHeroes() || dummy) {

			if (dummy)
				list.setVisibility(View.VISIBLE);
			else
				list.setVisibility(View.INVISIBLE);

			empty.setVisibility(View.VISIBLE);
			empty.setText(Util.getText(R.string.message_heroes_empty, DsaTabApplication.getDsaTabHeroPath()));
		} else {
			list.setVisibility(View.VISIBLE);
			empty.setVisibility(View.GONE);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.actionbarsherlock.app.SherlockActivity#onPrepareOptionsMenu(com. actionbarsherlock.view.Menu)
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		com.actionbarsherlock.view.MenuItem menuItem = menu.findItem(R.id.option_hero_import);
		if (menuItem != null) {
			ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connMgr != null) {
				NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
				if (networkInfo != null && networkInfo.isConnected()) {
					menuItem.setEnabled(true);
				} else {
					menuItem.setEnabled(false);
				}
			} else {
				menuItem.setEnabled(false);
			}
		}
		return super.onPrepareOptionsMenu(menu);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.actionbarsherlock.app.SherlockActivity#onCreateOptionsMenu(com. actionbarsherlock.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {

		com.actionbarsherlock.view.MenuItem item = menu.add(Menu.NONE, R.id.option_hero_import, Menu.NONE,
				"Aus Heldenaustausch importieren");
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		item.setIcon(R.drawable.ic_menu_account_list);

		item = menu.add(Menu.NONE, R.id.option_settings, Menu.NONE, "Einstellungen");
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
		item.setIcon(R.drawable.ic_menu_preferences);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {

		switch (item.getItemId()) {
		case R.id.option_hero_import:

			HeroExchange exchange = new HeroExchange(this);
			exchange.setOnHeroExchangeListener(new OnHeroExchangeListener() {
				/*
				 * (non-Javadoc)
				 * 
				 * @see com.dsatab.common.HeroExchange.OnHeroExchangeListener# onHeroLoaded(java.lang.String)
				 */
				@Override
				public void onHeroLoaded(String path) {
				}

				/*
				 * (non-Javadoc)
				 * 
				 * @see com.dsatab.common.HeroExchange.OnHeroExchangeListener#
				 * onHeroInfoLoaded(com.dsatab.data.HeroOnlineInfo)
				 */
				@Override
				public void onHeroInfoLoaded(HeroFileInfo info) {

					for (int i = 0; i < adapter.getCount(); i++) {
						HeroFileInfo heroInfo = adapter.getItem(i);

						if (heroInfo.getKey() != null && heroInfo.getKey().equals(info.getKey())) {
							heroInfo.id = info.id;
							// heroInfo.name = info.name;
							adapter.notifyDataSetChanged();
							// found
							return;
						}
					}
					adapter.add(info);
				}
			});
			exchange.syncHeroes();
			return true;
		case R.id.option_settings:
			DsaTabPreferenceActivity.startPreferenceActivity(this);
			return true;
		case android.R.id.home:
			setResult(RESULT_CANCELED);
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	static class HeroAdapter extends ArrayAdapter<HeroFileInfo> {

		LayoutInflater layoutInflater;

		public HeroAdapter(Context context, int textViewResourceId, List<HeroFileInfo> objects) {
			super(context, textViewResourceId, objects);

			layoutInflater = LayoutInflater.from(context);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewGroup layout = null;
			if (convertView instanceof ViewGroup) {
				layout = (ViewGroup) convertView;
			} else {
				layout = (ViewGroup) layoutInflater.inflate(R.layout.hero_chooser_item, parent, false);
				layout.setFocusable(false);
				layout.setClickable(false);
			}

			TextView tv = (TextView) layout.findViewById(android.R.id.text1);
			ImageView iv = (ImageView) layout.findViewById(android.R.id.icon);
			ImageView cloud = (ImageView) layout.findViewById(R.id.hero_cloud);

			HeroFileInfo hero = getItem(position);
			tv.setText(hero.getName());

			if (hero.getPortraitUri() != null) {
				iv.setImageURI(Uri.parse(hero.getPortraitUri()));
			}

			if (hero.isOnline()) {
				cloud.setVisibility(View.VISIBLE);
			} else {
				cloud.setVisibility(View.GONE);
			}
			return layout;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == DsaTabActivity.ACTION_PREFERENCES) {
			adapter = new HeroAdapter(this, R.layout.hero_chooser_item, DsaTabApplication.getInstance().getHeroes());
			list.setAdapter(adapter);

			updateViews();

			updateFullscreenStatus(preferences.getBoolean(DsaTabPreferenceActivity.KEY_FULLSCREEN, true));
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (mMode != null) {
			SparseBooleanArray checked = list.getCheckedItemPositionsC();
			boolean hasCheckedElement = false;
			for (int i = 0; i < checked.size() && !hasCheckedElement; i++) {
				hasCheckedElement = checked.valueAt(i);
			}
			if (hasCheckedElement) {
				mMode.invalidate();
			} else {
				mMode.finish();
			}
		} else {
			list.setItemCheckedC(position, false);

			HeroFileInfo hero = (HeroFileInfo) list.getItemAtPosition(position);

			if (hero.getFile() != null) {
				Intent intent = new Intent();
				intent.putExtra(INTENT_NAME_HERO_PATH, hero.getFile().toString());
				setResult(RESULT_OK, intent);
				finish();
			} else {
				HeroExchange exchange = new HeroExchange(this);
				exchange.setOnHeroExchangeListener(new OnHeroExchangeListener() {
					@Override
					public void onHeroLoaded(String path) {
						Intent intent = new Intent();
						intent.putExtra(INTENT_NAME_HERO_PATH, path);
						setResult(RESULT_OK, intent);
						finish();
					}

					@Override
					public void onHeroInfoLoaded(HeroFileInfo info) {
					}
				});

				exchange.importHero(hero);
			}
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		if (mCallback == null) {
			throw new IllegalArgumentException("ListView with Contextual Action Bar needs mCallback to be defined!");
		}
		GridViewCompat gridView = (GridViewCompat) parent;

		gridView.setItemCheckedC(position, !gridView.isItemCheckedC(position));

		List<Object> checkedObjects = new ArrayList<Object>();

		SparseBooleanArray checked = gridView.getCheckedItemPositionsC();
		boolean hasCheckedElement = false;
		if (checked != null) {
			for (int i = 0; i < checked.size() && !hasCheckedElement; i++) {
				hasCheckedElement = checked.valueAt(i);
				checkedObjects.add(gridView.getItemAtPosition(checked.keyAt(i)));
			}
		}

		if (hasCheckedElement) {
			if (mMode == null) {
				if (mCallback != null) {
					mMode = startActionMode(mCallback);
					customizeActionModeCloseButton();
					mMode.invalidate();
				} else {
					return false;
				}
			} else {
				mMode.invalidate();
			}
		} else {
			if (mMode != null) {
				mMode.finish();
			}
		}
		return true;
	}

	protected void customizeActionModeCloseButton() {
		int buttonId = Resources.getSystem().getIdentifier("action_mode_close_button", "id", "android");
		View v = findViewById(buttonId);
		if (v == null) {
			buttonId = R.id.abs__action_mode_close_button;
			v = findViewById(buttonId);
		}
		if (v == null)
			return;
		LinearLayout ll = (LinearLayout) v;
		if (ll.getChildCount() > 1 && ll.getChildAt(1) != null) {
			TextView tv = (TextView) ll.getChildAt(1);
			tv.setTextColor(getResources().getColor(android.R.color.white));
			tv.setBackgroundResource(Util.getThemeResourceId(this, R.attr.actionBarItemBackground));
		}
	}

}
