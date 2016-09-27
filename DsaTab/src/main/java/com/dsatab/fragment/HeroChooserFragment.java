package com.dsatab.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bingzer.android.driven.Credential;
import com.bingzer.android.driven.DrivenException;
import com.bingzer.android.driven.Result;
import com.bingzer.android.driven.StorageProvider;
import com.bingzer.android.driven.contracts.Task;
import com.bingzer.android.driven.dropbox.Dropbox;
import com.bingzer.android.driven.dropbox.app.DropboxActivity;
import com.bingzer.android.driven.gdrive.GoogleDrive;
import com.bingzer.android.driven.gdrive.app.GoogleDriveActivity;
import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.activity.DsaTabPreferenceActivity;
import com.dsatab.cloud.AuthorizationException;
import com.dsatab.cloud.HeldenAustauschProvider;
import com.dsatab.cloud.HeroExchange;
import com.dsatab.cloud.HeroExchange.StorageType;
import com.dsatab.cloud.HeroesLoaderTask;
import com.dsatab.cloud.HeroesSyncTask;
import com.dsatab.data.Hero;
import com.dsatab.data.HeroFileInfo;
import com.dsatab.data.adapter.BaseRecyclerAdapter;
import com.dsatab.data.adapter.ListRecyclerAdapter;
import com.dsatab.util.Debug;
import com.dsatab.util.Util;
import com.dsatab.util.ViewUtils;
import com.dsatab.view.AutofitRecyclerView;
import com.gandulf.guilib.util.ResUtil;
import com.h6ah4i.android.widget.advrecyclerview.selectable.ElevatingSelectableViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.selectable.RecyclerViewSelectionManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class HeroChooserFragment extends BaseRecyclerFragment implements LoaderManager.LoaderCallbacks<List<HeroFileInfo>>, ListRecyclerAdapter.EventListener {

    public static final String TAG = "HeroChooser";

    static final int CONNECT_EXCHANGE_RESULT = 1191;
    public static final String INTENT_NAME_HERO_FILE_INFO = "heroPath";

    private static final String DUMMY_FILE = "Dummy.xml";

    protected static final int LOCAL_LOADER = 1;
    protected static final int REMOTE_LOADER = 2;

    protected HeroAdapter adapter;

    private HeroExchange exchange;

    private View loadingView;
    private TextView empty;

    public interface OnHeroSelectedListener {
        void onHeroSelected(HeroFileInfo heroFileInfo);
    }

    private OnHeroSelectedListener onHeroSelectedListener;

    private static final class HeroesActionMode extends BaseListableActionMode<HeroChooserFragment> {

        public HeroesActionMode(HeroChooserFragment fragment, RecyclerView listView, RecyclerViewSelectionManager manager) {
            super(fragment, listView, manager);
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            RecyclerView list = listView.get();
            final HeroChooserFragment fragment = listFragment.get();
            if (list == null || fragment == null)
                return false;

            HeroAdapter adapter = WrapperAdapterUtils.findWrappedAdapter(list.getAdapter(), HeroAdapter.class);
            for (int index : getManager().getSelectedPositions()) {
                final HeroFileInfo heroInfo = adapter.getItem(index);

                switch (item.getItemId()) {
                    case R.id.option_delete:
                        if (heroInfo.isDeletable()) {
                            Debug.verbose("Deleting " + heroInfo.getName());
                            fragment.exchange.delete(heroInfo);
                            adapter.remove(heroInfo);
                        }
                        break;
                    case R.id.option_download:
                        if (heroInfo.isOnline()) {
                            HeroExchange exchange = new HeroExchange(fragment.getActivity());
                            exchange.download(heroInfo);
                        }
                        break;
                    case R.id.option_upload:
                        try {
                            fragment.exchange.upload(heroInfo);
                        } catch (Exception e) {
                            Debug.error(e);
                        }
                        break;
                }
            }


            adapter.notifyDataSetChanged();

            mode.finish();
            return true;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.herochooser_popupmenu, menu);
            mode.setTitle("Helden");
            return super.onCreateActionMode(mode, menu);
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            int selected = 0;
            boolean online = false;
            boolean deletable = false;
            boolean uploadable = false;
            RecyclerView list = listView.get();
            final HeroChooserFragment fragment = listFragment.get();
            if (list == null || fragment == null)
                return false;

            HeroAdapter adapter = WrapperAdapterUtils.findWrappedAdapter(list.getAdapter(), HeroAdapter.class);
            for (int index : getManager().getSelectedPositions()) {
                HeroFileInfo heroInfo = adapter.getItem(index);
                selected++;

                online |= heroInfo.isOnline();
                deletable |= heroInfo.isDeletable();
                uploadable |= heroInfo.getStorageType() == StorageType.FileSystem;
            }

            mode.setSubtitle(fragment.getString(R.string.count_selected, selected));

            boolean changed = false;

            MenuItem download = menu.findItem(R.id.option_download);
            if (download != null && online != download.isVisible()) {
                download.setVisible(online);
                changed &= true;
            }

            MenuItem upload = menu.findItem(R.id.option_upload);
            if (upload != null && uploadable != upload.isVisible()) {
                upload.setVisible(uploadable);
                changed &= true;
            }

            MenuItem delete = menu.findItem(R.id.option_delete);
            changed &= ViewUtils.menuIconState(delete, deletable);

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

        recyclerView = (RecyclerView) root.findViewById(R.id.popup_hero_chooser_list);

        loadingView = root.findViewById(R.id.loading);
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initRecycler();

        mCallback = new HeroesActionMode(this, recyclerView, mRecyclerViewSelectionManager);
    }

    protected void initRecycler() {

        adapter = new HeroAdapter(new ArrayList<HeroFileInfo>());
        adapter.setEventListener(this);

        if (recyclerView instanceof AutofitRecyclerView) {
            AutofitRecyclerView autofitRecyclerView = (AutofitRecyclerView) recyclerView;
            autofitRecyclerView.setColumnWidth(getResources().getDimensionPixelSize(R.dimen.portrait_width));
        }

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        initRecyclerView(recyclerView, adapter, layoutManager, false, false, true);

        //recyclerView.addItemDecoration(new SpacesItemDecoration(getResources().getDimensionPixelSize(R.dimen.default_gap)));

        mRecyclerViewSelectionManager.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
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

        exchange = DsaTabApplication.getInstance().getExchange();
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh(LOCAL_LOADER);
    }

    protected void loadExampleHeroes() {

        FileOutputStream fos = null;
        InputStream fis = null;
        try {
            File out = new File(DsaTabApplication.getHeroDirectory(), DUMMY_FILE);
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

        refresh(LOCAL_LOADER);
    }

    @Override
    public Loader<List<HeroFileInfo>> onCreateLoader(int id, Bundle args) {
        if (id == LOCAL_LOADER) {
            return new HeroesLoaderTask(getActivity(),exchange);
        } else { // Remote Loader
            return new HeroesSyncTask(getActivity(), exchange);
        }
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
        if (getActivity() == null)
            return;

        getBaseActivity().setToolbarRefreshing(false);
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
                    ViewUtils.snackbar(
                            getActivity(),
                            "Authorisierung fehlgeschlagen, überprüfe bitte ob deine eigegebenen Zugangsdaten korrekt sind.",
                            Snackbar.LENGTH_SHORT);
                } else if (e instanceof IOException) {
                    ViewUtils.snackbar(getActivity(), "Konnte keine Verbindung zum Austausch Server herstellen.",
                            Snackbar.LENGTH_SHORT);
                } else {
                    ViewUtils.snackbar(getActivity(), R.string.download_error, Snackbar.LENGTH_SHORT);
                    Debug.error(e);
                }
            }
            for (HeroFileInfo fileInfo : heroes) {
                int index = adapter.indexOf(fileInfo);
                if (index >= 0) {
                    HeroFileInfo info = adapter.getItem(index);
                    info.merge(fileInfo);
                    adapter.notifyItemChanged(index);
                } else {
                    adapter.add(fileInfo);
                }
            }
        } else if (loader instanceof HeroesLoaderTask) {
            HeroesLoaderTask heroLoader = (HeroesLoaderTask) loader;

            for (Exception e : heroLoader.getExceptions()) {
                ViewUtils.snackbar(getActivity(), R.string.download_error, Snackbar.LENGTH_SHORT);
                Debug.error(e);
            }
            adapter.clear();
            adapter.addAll(heroes);
        }

        updateViews();

        getActionBarActivity().invalidateOptionsMenu();
    }

    private void updateViews() {
        if (adapter == null || adapter.getItemCount() == 0) {
            recyclerView.setVisibility(View.INVISIBLE);

            empty.setVisibility(View.VISIBLE);
            empty.setText(Util.getText(R.string.message_heroes_empty, DsaTabApplication.getExternalHeroPath()));
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            empty.setVisibility(View.GONE);
        }
    }

    protected void refresh(final int loader) {
        getBaseActivity().setToolbarRefreshing(true);

        exchange.connect(getActivity(), new Task<Result<DrivenException>>() {
            @Override
            public void onCompleted(Result<DrivenException> result) {
                if (getActivity()!=null) {
                    getActivity().getLoaderManager().restartLoader(loader, null, HeroChooserFragment.this);
                }
            }
        });
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
            menuItem.setVisible(adapter != null && adapter.getItemCount() == 0);
        }

        menuItem = menu.findItem(R.id.option_connect_dropbox);
        if (menuItem != null && getActivity()!=null) {
            menuItem.setChecked(exchange.isConnected(getActivity(), StorageType.Dropbox));
        }
        menuItem = menu.findItem(R.id.option_connect_drive);
        if (menuItem != null && getActivity()!=null) {
            menuItem.setChecked(exchange.isConnected(getActivity(), StorageType.Drive));
        }
        menuItem = menu.findItem(R.id.option_connect_heldenaustausch);
        if (menuItem != null && getActivity()!=null) {
            menuItem.setChecked(exchange.isConnected(getActivity(), StorageType.HeldenAustausch));
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.herochooser_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (getActivity()!=null) {
            switch (item.getItemId()) {
                case R.id.option_refresh:
                    refresh(REMOTE_LOADER);
                    return true;
                case R.id.option_load_example_heroes:
                    loadExampleHeroes();
                    break;
                case R.id.option_connect_dropbox:

                    if (!exchange.isConnected(getActivity(), StorageType.Dropbox)) {
                        Intent intent = DropboxActivity.createLoginIntent(getActivity(), DsaTabApplication.DROPBOX_API_KEY,
                                DsaTabApplication.DROPBOX_API_SECRET);
                        startActivityForResult(intent, CONNECT_EXCHANGE_RESULT);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Dropbox");
                        builder.setMessage("Dropbox Synchronisation aufheben?");
                        builder.setPositiveButton("Aufheben", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                StorageProvider dropbox = new Dropbox();

                                dropbox.clearSavedCredentialAsync(getActivity(), new Task<Result<DrivenException>>() {
                                    @Override
                                    public void onCompleted(Result<DrivenException> result) {
                                        if (!result.isSuccess()) {
                                            if (result.getException() != null)
                                                ViewUtils.snackbar(getActivity(), "Konnte Dropbox nicht deaktivieren: " + result.getException().getLocalizedMessage());
                                            else
                                                ViewUtils.snackbar(getActivity(), "Konnte Dropbox nicht deaktivieren.");
                                        }
                                    }
                                });
                                dialog.dismiss();
                            }
                        });
                        builder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        builder.show();
                    }

                    return true;

                case R.id.option_connect_drive:
                    if (!exchange.isConnected(getActivity(), StorageType.Drive)) {
                        Intent intent = GoogleDriveActivity.createLoginIntent(getActivity());
                        startActivityForResult(intent, CONNECT_EXCHANGE_RESULT);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Google Drive");
                        builder.setMessage("Drive Synchronisation aufheben?");
                        builder.setPositiveButton("Aufheben", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                StorageProvider drive = new GoogleDrive();

                                drive.clearSavedCredentialAsync(getActivity(), new Task<Result<DrivenException>>() {
                                    @Override
                                    public void onCompleted(Result<DrivenException> result) {
                                        if (!result.isSuccess()) {
                                            if (result.getException() != null)
                                                ViewUtils.snackbar(getActivity(), "Konnte Drive nicht deaktivieren: " + result.getException().getLocalizedMessage());
                                            else
                                                ViewUtils.snackbar(getActivity(), "Konnte Drive nicht deaktivieren.");
                                        }
                                    }
                                });
                                dialog.dismiss();
                            }
                        });
                        builder.setNegativeButton(android.R.string.cancel, null);

                        builder.show();
                    }
                    return true;
                case R.id.option_connect_heldenaustausch:
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                    final EditText editText = new EditText(builder.getContext());
                    editText.setHint("Helden-Austausch Token");
                    editText.setText(DsaTabApplication.getPreferences().getString(DsaTabPreferenceActivity.KEY_EXCHANGE_TOKEN, ""));
                    builder.setTitle("Berechtigungstoken der Heldenaustauschseite");
                    builder.setView(editText);
                    builder.setNegativeButton(android.R.string.cancel, null);
                    builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String token = editText.getText().toString();
                            HeldenAustauschProvider provider = new HeldenAustauschProvider();
                            if (TextUtils.isEmpty(token)) {
                                provider.clearSavedCredential(getActivity());
                            } else {
                                Credential credential = new Credential(getActivity());
                                credential.setAccountName(editText.getText().toString());
                                provider.authenticate(credential);
                            }
                        }
                    });
                    builder.show();

                    return true;
                case R.id.option_settings:
                    Intent intent = new Intent(getActivity(), DsaTabPreferenceActivity.class);
                    startActivity(intent);
                    return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    static class HeroAdapter extends ListRecyclerAdapter<HeroAdapter.HeroViewHolder, HeroFileInfo> {

        public class HeroViewHolder extends ElevatingSelectableViewHolder {

            TextView tv, version, tag1, tag2;
            ImageView iv;

            public HeroViewHolder(View v) {
                super(v);
                tv = (TextView) v.findViewById(android.R.id.text1);
                version = (TextView) v.findViewById(android.R.id.text2);
                tag1 = (TextView) v.findViewById(R.id.tag1);
                tag2 = (TextView) v.findViewById(R.id.tag2);
                iv = (ImageView) v.findViewById(android.R.id.icon);
            }
        }

        public HeroAdapter(List<HeroFileInfo> objects) {
            super(objects);
        }


        @Override
        public HeroViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View layout = (View) inflater.inflate(R.layout.item_hero_chooser, parent, false);
            return new HeroViewHolder(layout);
        }

        @Override
        public void onBindViewHolder(HeroViewHolder holder, int position) {
            super.onBindViewHolder(holder, position);

            HeroFileInfo heroInfo = getItem(position);
            holder.tv.setText(heroInfo.getName());

            if (heroInfo.getPortraitUri() != null) {
                holder.iv.setImageDrawable(ResUtil.getDrawableByUri(holder.iv.getContext(), Uri.parse(heroInfo.getPortraitUri())));
            } else {
                holder.iv.setImageResource(R.drawable.profile_picture);
            }


            int profileHeight = holder.iv.getResources().getDimensionPixelSize(R.dimen.portrait_height);
            if (holder.iv.getDrawable() != null) {
                profileHeight = Math.max(holder.iv.getDrawable().getIntrinsicHeight(),profileHeight);
            }

            switch (heroInfo.hashCode() % 4) {
                case 2:
                    profileHeight = (int) (profileHeight * 1.25f);
                case 3:
                    profileHeight = (int) (profileHeight * 0.75f);
                    break;
                default:
                    break;
            }
            holder.iv.setMinimumHeight(profileHeight);
            holder.iv.getLayoutParams().height = profileHeight;

            if (TextUtils.isEmpty(heroInfo.getVersion())) {
                holder.version.setVisibility(View.GONE);
            } else {
                holder.version.setText("v" + heroInfo.getVersion());
                int v = heroInfo.getVersionInt();
                if (v < DsaTabApplication.HS_VERSION_INT) {
                    holder.version.setBackgroundColor(holder.version.getContext().getResources().getColor(R.color.ValueRedAlpha));
                } else if (v > DsaTabApplication.HS_VERSION_INT) {
                    holder.version.setBackgroundColor(holder.version.getContext().getResources().getColor(R.color.ValueYellowAlpha));
                } else {
                    holder.version.setBackgroundColor(holder.version.getContext().getResources().getColor(R.color.ValueGreenAlpha));
                }
                holder.version.setVisibility(View.VISIBLE);
            }

            if (heroInfo.getStorageType() != null) {
                switch (heroInfo.getStorageType()) {
                    case Dropbox:
                        holder.tag1.setText("Dropbox");
                        holder.tag1.setBackgroundColor(holder.tag1.getContext().getResources().getColor(R.color.ValueBlueAlpha));
                        holder.tag1.setVisibility(View.VISIBLE);
                        break;
                    case Drive:
                        holder.tag1.setText("Drive");
                        holder.tag1.setBackgroundColor(holder.tag1.getContext().getResources().getColor(R.color.ValueViolettAlpha));
                        holder.tag1.setVisibility(View.VISIBLE);
                        break;
                    case FileSystem:
                        holder.tag1.setText("Storage");
                        holder.tag1.setBackgroundColor(holder.tag1.getContext().getResources().getColor(R.color.ValueRedAlpha));
                        holder.tag1.setVisibility(View.VISIBLE);
                        break;
                    case HeldenAustausch:
                        holder.tag1.setText("Austausch");
                        holder.tag1.setBackgroundColor(holder.tag1.getContext().getResources().getColor(R.color.ValueYellowAlpha));
                        holder.tag1.setVisibility(View.VISIBLE);
                        break;
                    default:
                        holder.tag1.setVisibility(View.GONE);
                        break;
                }
            } else {
                holder.tag1.setVisibility(View.GONE);
            }

            if (heroInfo.isInternal()) {
                holder.tag2.setText("Phone");
                holder.tag2.setBackgroundColor(holder.tag2.getContext().getResources().getColor(R.color.ValueGreenAlpha));
                holder.tag2.setVisibility(View.VISIBLE);
            } else {
                holder.tag2.setVisibility(View.GONE);
            }
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CONNECT_EXCHANGE_RESULT) {
            if (resultCode == Activity.RESULT_OK) {
                refresh(REMOTE_LOADER);
            } else {
                // ... Link failed or was cancelled by the user.
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onItemClicked(BaseRecyclerAdapter adapter, int position, View v) {
        if (mMode != null) {
            if (!mRecyclerViewSelectionManager.getSelectedPositions().isEmpty()) {
                mMode.invalidate();
            } else {
                mMode.finish();
            }
        } else {
            mRecyclerViewSelectionManager.setSelected(position, false);
            HeroFileInfo hero = this.adapter.getItem(position);
            if (onHeroSelectedListener != null) {
                onHeroSelectedListener.onHeroSelected(hero);
            }
        }
    }

    @Override
    public boolean onItemLongClicked(BaseRecyclerAdapter adapter, int position, View v) {
        mRecyclerViewSelectionManager.toggleSelection(position);

        if (!mRecyclerViewSelectionManager.getSelectedPositions().isEmpty()) {
            if (mMode == null) {
                if (mCallback != null) {
                    mMode = recyclerView.startActionMode(mCallback);
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
