package com.dsatab.fragment;

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
import android.app.LoaderManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.sync.android.DbxException;
import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.activity.DsaTabActivity;
import com.dsatab.activity.DsaTabPreferenceActivity;
import com.dsatab.cloud.AuthorizationException;
import com.dsatab.cloud.HeroExchange;
import com.dsatab.cloud.HeroExchange.OnHeroExchangeListener;
import com.dsatab.cloud.HeroesSyncTask;
import com.dsatab.data.Hero;
import com.dsatab.data.HeroFileInfo;
import com.dsatab.data.HeroFileInfo.StorageType;
import com.dsatab.util.Debug;
import com.dsatab.util.Util;
import com.gandulf.guilib.data.OpenArrayAdapter;
import com.gandulf.guilib.util.ListViewCompat;

public class HeroChooserFragment extends BaseFragment implements AdapterView.OnItemClickListener,
		OnItemLongClickListener, LoaderManager.LoaderCallbacks<List<HeroFileInfo>>, OnClickListener {

	public static final String TAG = "HeroChooser";

	static final String PREF_DONT_SHOW_CONNECT = "dont_show_connect";

	static final int REQUEST_LINK_TO_DBX = 1190;

	public static final String INTENT_NAME_HERO_FILE_INFO = "heroPath";

	private static final String DUMMY_FILE = "Dummy.xml";

	private GridView list;
	private HeroAdapter adapter;

	private ActionMode mMode;

	private ActionMode.Callback mCallback;

	private HeroExchange exchange;

	private View loadingView;
	private TextView empty;
	private AlertDialog dropboxDialog;
	private AlertDialog heldenAustauschDialog;

	public interface OnHeroSelectedListener {
		public void onHeroSelected(HeroFileInfo heroFileInfo);
	}

	private OnHeroSelectedListener onHeroSelectedListener;

	private final class HeroesActionMode implements ActionMode.Callback {

		@TargetApi(Build.VERSION_CODES.HONEYCOMB)
		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			boolean notifyChanged = false;
			if (list == null || adapter == null) {
				return false;
			}

			SparseBooleanArray checkedPositions = ListViewCompat.getCheckedItemPositions(list);
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
								HeroExchange exchange = new HeroExchange(getActivity());

								OnHeroExchangeListener listener = new OnHeroExchangeListener() {
									@Override
									public void onHeroInfoLoaded(List<HeroFileInfo> infos) {
										Toast.makeText(
												getActivity(),
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
			SparseBooleanArray checkedPositions = ListViewCompat.getCheckedItemPositions(list);
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
	public void onPause() {
		if (mMode != null) {
			mMode.finish();
		}
		super.onPause();
	}

	@Override
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.sheet_hero_chooser, container, false);

		empty = (TextView) root.findViewById(R.id.popup_hero_empty);
		empty.setVisibility(View.GONE);

		list = (GridView) root.findViewById(R.id.popup_hero_chooser_list);
		list.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

		list.setOnItemClickListener(this);
		list.setOnItemLongClickListener(this);

		adapter = new HeroAdapter(getActivity(), R.layout.item_hero_chooser, new ArrayList<HeroFileInfo>());
		try {
			adapter.addAll(exchange.getHeroes(StorageType.FileSystem, StorageType.Dropbox));
		} catch (Exception e) {
			Debug.error(e);
		}
		list.setAdapter(adapter);

		loadingView = root.findViewById(R.id.loading);
		return root;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		if (activity instanceof OnHeroSelectedListener) {
			onHeroSelectedListener = (OnHeroSelectedListener) activity;
		}
	}

	@Override
	public void onDetach() {
		onHeroSelectedListener = null;
		super.onDetach();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setHasOptionsMenu(true);

		exchange = new HeroExchange(getActivity());
		mCallback = new HeroesActionMode();

		String error = Util.checkFileWriteAccess(DsaTabApplication.getDsaTabHeroDirectory());
		if (error != null) {
			Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
		}

		if (!exchange.isConnected(StorageType.Dropbox)) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			View popupcontent = LayoutInflater.from(builder.getContext()).inflate(R.layout.popup_cloud, null);
			builder.setView(popupcontent);
			popupcontent.findViewById(R.id.connect_dropbox).setOnClickListener(this);

			CheckBox show = (CheckBox) popupcontent.findViewById(R.id.cb_dont_show_again);
			show.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					Editor editor = getActivity().getPreferences(Activity.MODE_PRIVATE).edit();
					editor.putBoolean(PREF_DONT_SHOW_CONNECT, isChecked);
					editor.commit();
				}
			});

			if (DsaTabApplication.getInstance().isFirstRun()
					|| getActivity().getPreferences(Activity.MODE_PRIVATE).getBoolean(PREF_DONT_SHOW_CONNECT, false)) {
				dropboxDialog = builder.create();
			} else {
				dropboxDialog = builder.show();
			}

			dropboxDialog.setCanceledOnTouchOutside(true);
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		final EditText editText = new EditText(getActivity());
		editText.setHint("Helden-Austausch Token");
		editText.setText(DsaTabApplication.getPreferences().getString(DsaTabPreferenceActivity.KEY_EXCHANGE_TOKEN, ""));
		builder.setTitle("Berechtigungstoken der Heldenaustauschseite");
		builder.setView(editText);
		builder.setNegativeButton(android.R.string.cancel, null);
		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Editor editor = DsaTabApplication.getPreferences().edit();
				editor.putString(DsaTabPreferenceActivity.KEY_EXCHANGE_TOKEN, editText.getText().toString());
				editor.commit();
			}
		});
		heldenAustauschDialog = builder.create();

		refresh();
	}

	private void loadExampleHeroes() {

		FileOutputStream fos = null;
		InputStream fis = null;
		try {
			File out = new File(DsaTabApplication.getDsaTabHeroPath() + DUMMY_FILE);
			fos = new FileOutputStream(out);
			fis = new BufferedInputStream(getResources().getAssets().open(DUMMY_FILE));
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
		return new HeroesSyncTask(getActivity(), exchange);
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
		// if the loader finishes after activity is gone already just skip it
		if (getActionBarActivity() == null)
			return;

		getActionBarActivity().setProgressBarIndeterminateVisibility(false);
		if (loadingView.getVisibility() != View.GONE) {
			loadingView.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
			loadingView.setVisibility(View.GONE);
		}

		// Swap the new cursor in. (The framework will take care of closing the
		// old cursor once we return.)
		if (loader instanceof HeroesSyncTask) {
			HeroesSyncTask heroLoader = (HeroesSyncTask) loader;

			for (Exception e : heroLoader.getExceptions()) {
				if (e instanceof AuthorizationException) {
					Toast.makeText(
							getActivity(),
							"Token ungültig. Überprüfe ob das Token mit dem in der Helden-Software erstelltem Zugangstoken übereinstimmt.",
							Toast.LENGTH_SHORT).show();
				} else if (e instanceof IOException) {
					Toast.makeText(getActivity(), "Konnte keine Verbindung zum Austausch Server herstellen.",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getActivity(), R.string.download_error, Toast.LENGTH_SHORT).show();
					Debug.error(e);
				}
			}

		}

		adapter.clear();
		adapter.addAll(heroes);

		updateViews();

		getActionBarActivity().invalidateOptionsMenu();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.connect_dropbox:
			exchange.syncDropbox(REQUEST_LINK_TO_DBX);
			if (dropboxDialog != null) {
				dropboxDialog.dismiss();
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
		getActionBarActivity().setProgressBarIndeterminateVisibility(true);
		getActivity().getLoaderManager().restartLoader(0, null, this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.actionbarsherlock.app.SherlockActivity#onPrepareOptionsMenu(com. actionbarsherlock.view.Menu)
	 */
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);

		MenuItem menuItem = menu.findItem(R.id.option_load_example_heroes);
		if (menuItem != null) {
			menuItem.setVisible(adapter != null && adapter.isEmpty());
		}

		menuItem = menu.findItem(R.id.option_connect_dropbox);
		if (menuItem != null) {
			menuItem.setVisible(dropboxDialog != null);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.herochooser_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.option_refresh:
			refresh();
			return true;
		case R.id.option_load_example_heroes:
			loadExampleHeroes();
			break;
		case R.id.option_connect_dropbox:
			dropboxDialog.show();
			return true;
		case R.id.option_connect_heldenaustausch:
			heldenAustauschDialog.show();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	static class HeroAdapter extends OpenArrayAdapter<HeroFileInfo> {

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
				layout = (ViewGroup) layoutInflater.inflate(R.layout.item_hero_chooser, parent, false);
				layout.setFocusable(false);
				layout.setClickable(false);
			}

			TextView tv = (TextView) layout.findViewById(android.R.id.text1);
			TextView version = (TextView) layout.findViewById(android.R.id.text2);
			TextView tag1 = (TextView) layout.findViewById(R.id.tag1);
			TextView tag2 = (TextView) layout.findViewById(R.id.tag2);
			ImageView iv = (ImageView) layout.findViewById(android.R.id.icon);

			HeroFileInfo heroInfo = getItem(position);
			tv.setText(heroInfo.getName());

			if (heroInfo.getPortraitUri() != null) {
				iv.setImageURI(Uri.parse(heroInfo.getPortraitUri()));
			} else {
				iv.setImageResource(R.drawable.profile_picture);
			}

			if (TextUtils.isEmpty(heroInfo.getVersion())) {
				version.setVisibility(View.GONE);
			} else {
				version.setText("v" + heroInfo.getVersion());
				int v = heroInfo.getVersionInt();
				if (v < DsaTabApplication.HS_VERSION_INT) {
					version.setBackgroundColor(getContext().getResources().getColor(R.color.ValueRedAlpha));
				} else if (v > DsaTabApplication.HS_VERSION_INT) {
					version.setBackgroundColor(getContext().getResources().getColor(R.color.ValueYellowAlpha));
				} else {
					version.setBackgroundColor(getContext().getResources().getColor(R.color.ValueGreenAlpha));
				}
				version.setVisibility(View.VISIBLE);
			}

			switch (heroInfo.getStorageType()) {
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

			if (heroInfo.isOnline()) {
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
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == DsaTabActivity.ACTION_PREFERENCES) {
			refresh();
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
			SparseBooleanArray checked = ListViewCompat.getCheckedItemPositions(list);
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
				HeroExchange exchange = new HeroExchange(getActivity());
				OnHeroExchangeListener listener = new OnHeroExchangeListener() {
					@Override
					public void onHeroInfoLoaded(List<HeroFileInfo> infos) {
						if (infos != null && !infos.isEmpty()) {
							if (onHeroSelectedListener != null) {
								onHeroSelectedListener.onHeroSelected(infos.get(0));
							}
						}
					}
				};

				exchange.download(hero, listener);
			} else {
				if (onHeroSelectedListener != null) {
					onHeroSelectedListener.onHeroSelected(hero);
				}
			}
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		if (mCallback == null) {
			throw new IllegalArgumentException("ListView with Contextual Action Bar needs mCallback to be defined!");
		}
		GridView gridView = (GridView) parent;

		gridView.setItemChecked(position, !gridView.isItemChecked(position));

		List<Object> checkedObjects = new ArrayList<Object>();

		SparseBooleanArray checked = ListViewCompat.getCheckedItemPositions(gridView);
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
					mMode = getActionBarActivity().startActionMode(mCallback);
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

	@Override
	public void onHeroLoaded(Hero hero) {

	}

}
