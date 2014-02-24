package com.dsatab.activity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.dropbox.sync.android.DbxException;
import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.cloud.AuthorizationException;
import com.dsatab.cloud.HeroExchange;
import com.dsatab.cloud.HeroExchange.OnHeroExchangeListener;
import com.dsatab.cloud.HeroesSyncTask;
import com.dsatab.data.HeroFileInfo;
import com.dsatab.data.HeroFileInfo.StorageType;
import com.dsatab.util.Debug;
import com.dsatab.util.Util;
import com.rokoder.android.lib.support.v4.widget.GridViewCompat;

public class HeroChooserActivity extends BaseActivity implements AdapterView.OnItemClickListener,
		OnItemLongClickListener, LoaderManager.LoaderCallbacks<List<HeroFileInfo>>, OnClickListener {

	static final String PREF_DONT_SHOW_CONNECT = "dont_show_connect";

	static final int REQUEST_LINK_TO_DBX = 1190;

	public static final String INTENT_NAME_HERO_FILE_INFO = "heroPath";

	private static final String DUMMY_FILE = "Dummy.xml";
	private static final String DUMMY_NAME = "Dummy";

	private GridViewCompat list;
	private HeroAdapter adapter;
	private boolean writable = true;

	private ActionMode mMode;

	private ActionMode.Callback mCallback;

	private HeroExchange exchange;

	private View loadingView;
	private TextView empty;
	private AlertDialog connectDialog;

	private final class HeroesActionMode implements ActionMode.Callback {
		@TargetApi(Build.VERSION_CODES.HONEYCOMB)
		@Override
		public boolean onActionItemClicked(ActionMode mode, com.actionbarsherlock.view.MenuItem item) {
			boolean notifyChanged = false;
			if (list == null || adapter == null) {
				return false;
			}

			SparseBooleanArray checkedPositions = list.getCheckedItemPositions();
			if (checkedPositions != null) {
				adapter.setNotifyOnChange(false);
				for (int i = checkedPositions.size() - 1; i >= 0; i--) {
					if (checkedPositions.valueAt(i)) {
						final HeroFileInfo heroInfo = adapter.getItem(checkedPositions.keyAt(i));

						switch (item.getItemId()) {
						case R.id.option_delete:
							Debug.verbose("Deleting " + heroInfo.getName());
							if (exchange.delete(heroInfo)) {
								adapter.remove(heroInfo);
								notifyChanged = true;
							} else {
								Debug.verbose("Cannot delete online hero: " + heroInfo.getName());
							}
							break;
						case R.id.option_download:
							if (heroInfo.isOnline()) {
								HeroExchange exchange = new HeroExchange(HeroChooserActivity.this);

								OnHeroExchangeListener listener = new OnHeroExchangeListener() {
									@Override
									public void onHeroInfoLoaded(List<HeroFileInfo> infos) {
										Toast.makeText(
												HeroChooserActivity.this,
												getString(R.string.message_hero_successfully_downloaded,
														heroInfo.getName()), Toast.LENGTH_SHORT).show();
									}

								};
								exchange.download(heroInfo, listener);
							}
							break;
						case R.id.option_upload:
							try {
								exchange.upload(StorageType.Dropbox, heroInfo);
								notifyChanged = true;
							} catch (DbxException e) {
								Debug.error(e);
							} catch (IOException e) {
								Debug.error(e);
							}
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
			mode.getMenuInflater().inflate(R.menu.herochooser_popupmenu, menu);
			mode.setTitle("Helden");
			return true;
		}

		@TargetApi(Build.VERSION_CODES.HONEYCOMB)
		@Override
		public void onDestroyActionMode(ActionMode mode) {
			mMode = null;
			if (list != null) {
				list.clearChoices();
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
		@TargetApi(Build.VERSION_CODES.HONEYCOMB)
		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			int selected = 0;
			boolean online = false;
			boolean deletable = false;
			boolean uploadable = false;
			SparseBooleanArray checkedPositions = list.getCheckedItemPositions();
			if (checkedPositions != null) {
				for (int i = checkedPositions.size() - 1; i >= 0; i--) {
					if (checkedPositions.valueAt(i)) {
						selected++;
						HeroFileInfo heroInfo = adapter.getItem(checkedPositions.keyAt(i));
						online |= heroInfo.isOnline();
						deletable |= heroInfo.isDeletable();
						uploadable |= heroInfo.getStorageType() == StorageType.FileSystem;
					}
				}
			}

			mode.setSubtitle(getString(R.string.count_selected, selected));

			boolean changed = false;

			MenuItem download = menu.findItem(R.id.option_download);
			if (download != null && online != download.isVisible()) {
				download.setVisible(online);
				changed = true;
			}

			MenuItem upload = menu.findItem(R.id.option_upload);
			if (upload != null && uploadable != upload.isVisible()) {
				upload.setVisible(uploadable);
				changed = true;
			}

			MenuItem delete = menu.findItem(R.id.option_delete);
			if (delete != null && deletable != delete.isEnabled()) {
				delete.setEnabled(deletable);
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
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(DsaTabApplication.getInstance().getCustomTheme());
		applyPreferencesToTheme();
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		setContentView(R.layout.sheet_hero_chooser);

		exchange = new HeroExchange(this);
		mCallback = new HeroesActionMode();

		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		String error = Util.checkFileWriteAccess(DsaTabApplication.getDsaTabHeroDirectory());
		if (error != null) {
			Toast.makeText(this, error, Toast.LENGTH_LONG).show();
			writable = false;
		} else {
			writable = true;
		}

		empty = (TextView) findViewById(R.id.popup_hero_empty);
		empty.setVisibility(View.GONE);

		list = (GridViewCompat) findViewById(R.id.popup_hero_chooser_list);
		list.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

		list.setOnItemClickListener(this);
		list.setOnItemLongClickListener(this);

		loadingView = findViewById(R.id.loading);

		if (!exchange.isConnected(StorageType.Dropbox)
				&& !getPreferences(MODE_PRIVATE).getBoolean(PREF_DONT_SHOW_CONNECT, false)) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			View popupcontent = getLayoutInflater().inflate(R.layout.popup_cloud, null);
			builder.setView(popupcontent);
			popupcontent.findViewById(R.id.connect_dropbox).setOnClickListener(this);

			CheckBox show = (CheckBox) popupcontent.findViewById(R.id.cb_dont_show_again);
			show.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					Editor editor = getPreferences(MODE_PRIVATE).edit();
					editor.putBoolean(PREF_DONT_SHOW_CONNECT, isChecked);
					editor.commit();
				}
			});
			connectDialog = builder.show();
			connectDialog.setCanceledOnTouchOutside(true);
		}

		refresh();
	}

	private void loadExampleHeroes() {

		FileOutputStream fos = null;
		InputStream fis = null;
		try {
			File out = new File(DsaTabApplication.getDsaTabHeroPath() + DUMMY_FILE);
			fos = new FileOutputStream(out);
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
			Util.close(fos);
			Util.close(fis);
		}

		refresh();
	}

	@Override
	public Loader<List<HeroFileInfo>> onCreateLoader(int id, Bundle args) {
		return new HeroesSyncTask(this, exchange);
	}

	@Override
	public void onLoaderReset(Loader<List<HeroFileInfo>> loader) {
		// This is called when the last Cursor provided to onLoadFinished()
		// above is about to be closed. We need to make sure we are no
		// longer using it.
		// mAdapter.swapCursor(null);
	}

	@Override
	public void onLoadFinished(Loader<List<HeroFileInfo>> loader, List<HeroFileInfo> heroes) {
		setSupportProgressBarIndeterminateVisibility(false);
		loadingView.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
		loadingView.setVisibility(View.GONE);

		// Swap the new cursor in. (The framework will take care of closing the
		// old cursor once we return.)
		if (loader instanceof HeroesSyncTask) {
			HeroesSyncTask heroLoader = (HeroesSyncTask) loader;

			for (Exception e : heroLoader.getExceptions()) {
				if (e instanceof AuthorizationException) {
					Toast.makeText(
							this,
							"Token ungültig. Überprüfe ob das Token mit dem in der Helden-Software erstelltem Zugangstoken übereinstimmt.",
							Toast.LENGTH_SHORT).show();
				} else if (e instanceof IOException) {
					Toast.makeText(this, "Konnte keine Verbindung zum Austausch Server herstellen.", Toast.LENGTH_SHORT)
							.show();
				} else {
					Toast.makeText(this, R.string.download_error, Toast.LENGTH_SHORT).show();
					Debug.error(e);
				}
			}

		}

		adapter = new HeroAdapter(this, R.layout.hero_chooser_item, heroes);
		list.setAdapter(adapter);

		updateViews();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.connect_dropbox:
			exchange.syncDropbox(REQUEST_LINK_TO_DBX);
			if (connectDialog != null) {
				connectDialog.dismiss();
				connectDialog = null;
			}
			break;
		}

	}

	private void updateViews() {
		if (adapter == null && adapter.getCount() == 0) {
			list.setVisibility(View.INVISIBLE);

			empty.setVisibility(View.VISIBLE);
			empty.setText(Util.getText(R.string.message_heroes_empty, DsaTabApplication.getDsaTabHeroPath()));
		} else {
			list.setVisibility(View.VISIBLE);
			empty.setVisibility(View.GONE);
		}
	}

	protected void refresh() {
		setSupportProgressBarIndeterminateVisibility(true);

		loadingView.setVisibility(View.VISIBLE);
		loadingView.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));

		getSupportLoaderManager().restartLoader(0, null, this);
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
	 * @see com.actionbarsherlock.app.SherlockActivity#onCreateOptionsMenu(com.actionbarsherlock.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		getSupportMenuInflater().inflate(R.menu.herochooser_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
		switch (item.getItemId()) {
		case R.id.option_refresh:
			refresh();
			return true;
		case R.id.option_load_example_heroes:
			loadExampleHeroes();
			break;
		case R.id.option_items:
			startActivity(new Intent(this, ItemsActivity.class));
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
			TextView version = (TextView) layout.findViewById(android.R.id.text2);
			TextView tag1 = (TextView) layout.findViewById(R.id.tag1);
			TextView tag2 = (TextView) layout.findViewById(R.id.tag2);
			ImageView iv = (ImageView) layout.findViewById(android.R.id.icon);

			HeroFileInfo hero = getItem(position);
			tv.setText(hero.getName());

			if (hero.getPortraitUri() != null) {
				iv.setImageURI(Uri.parse(hero.getPortraitUri()));
			}

			if (TextUtils.isEmpty(hero.getVersion())) {
				version.setVisibility(View.GONE);
			} else {
				version.setText("v" + hero.getVersion());
				int v = hero.getVersionInt();
				if (v < DsaTabApplication.HS_VERSION_INT) {
					version.setBackgroundColor(getContext().getResources().getColor(R.color.ValueRedAlpha));
				} else if (v > DsaTabApplication.HS_VERSION_INT) {
					version.setBackgroundColor(getContext().getResources().getColor(R.color.ValueYellowAlpha));
				} else {
					version.setBackgroundColor(getContext().getResources().getColor(R.color.ValueGreenAlpha));
				}
				version.setVisibility(View.VISIBLE);
			}

			switch (hero.getStorageType()) {
			case Dropbox:
				tag1.setText("Dropbox");
				tag1.setBackgroundColor(getContext().getResources().getColor(R.color.ValueBlueAlpha));
				tag1.setVisibility(View.VISIBLE);
				break;
			case FileSystem:
				tag1.setText("Phone");
				tag1.setBackgroundColor(getContext().getResources().getColor(R.color.ValueYellowAlpha));
				tag1.setVisibility(View.VISIBLE);
				break;
			default:
				tag1.setVisibility(View.GONE);
				break;
			}

			if (hero.isOnline()) {
				tag2.setText("Austausch");
				tag2.setBackgroundColor(getContext().getResources().getColor(R.color.ValueGreenAlpha));
				tag2.setVisibility(View.VISIBLE);
			} else {
				tag2.setVisibility(View.GONE);
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
			refresh();
			updateFullscreenStatus(preferences.getBoolean(DsaTabPreferenceActivity.KEY_FULLSCREEN, true));
		} else if (requestCode == REQUEST_LINK_TO_DBX) {
			if (resultCode == Activity.RESULT_OK) {
				refresh();
			} else {
				// ... Link failed or was cancelled by the user.
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (mMode != null) {
			SparseBooleanArray checked = list.getCheckedItemPositions();
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
			list.setItemChecked(position, false);

			HeroFileInfo hero = (HeroFileInfo) list.getItemAtPosition(position);
			if (hero.getStorageType() == StorageType.HeldenAustausch) {
				HeroExchange exchange = new HeroExchange(this);
				OnHeroExchangeListener listener = new OnHeroExchangeListener() {
					@Override
					public void onHeroInfoLoaded(List<HeroFileInfo> infos) {
						if (infos != null && !infos.isEmpty()) {
							Intent intent = new Intent();
							intent.putExtra(INTENT_NAME_HERO_FILE_INFO, infos.get(0));
							setResult(RESULT_OK, intent);
							finish();
						}
					}
				};

				exchange.download(hero, listener);
			} else {
				Intent intent = new Intent();
				intent.putExtra(INTENT_NAME_HERO_FILE_INFO, hero);
				setResult(RESULT_OK, intent);
				finish();
			}
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		if (mCallback == null) {
			throw new IllegalArgumentException("ListView with Contextual Action Bar needs mCallback to be defined!");
		}
		GridViewCompat gridView = (GridViewCompat) parent;

		gridView.setItemChecked(position, !gridView.isItemChecked(position));

		List<Object> checkedObjects = new ArrayList<Object>();

		SparseBooleanArray checked = gridView.getCheckedItemPositions();
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

}
