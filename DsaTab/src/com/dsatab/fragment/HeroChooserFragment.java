package com.dsatab.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences.Editor;
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

import com.bingzer.android.driven.dropbox.app.DropboxActivity;
import com.bingzer.android.driven.gdrive.app.GoogleDriveActivity;
import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.activity.DsaTabPreferenceActivity;
import com.dsatab.cloud.AuthorizationException;
import com.dsatab.cloud.HeroExchange;
import com.dsatab.cloud.HeroExchange.OnHeroExchangeListener;
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
import java.util.Random;

public class HeroChooserFragment extends BaseRecyclerFragment implements LoaderManager.LoaderCallbacks<List<HeroFileInfo>>, ListRecyclerAdapter.EventListener {

    public static final String TAG = "HeroChooser";

    static final int CONNECT_EXCHANGE_RESULT = 1191;
    public static final String INTENT_NAME_HERO_FILE_INFO = "heroPath";

    private static final String DUMMY_FILE = "Dummy.xml";

    private static final int LOCAL_LOADER = 1;
    private static final int REMOTE_LOADER = 2;

    private HeroAdapter adapter;

    private HeroExchange exchange;

    private View loadingView;
    private TextView empty;
    private AlertDialog heldenAustauschDialog;

    public interface OnHeroSelectedListener {
        public void onHeroSelected(HeroFileInfo heroFileInfo);
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
                            if (fragment.exchange.delete(heroInfo)) {
                                adapter.remove(heroInfo);
                            } else {
                                Debug.verbose("Cannot delete online hero: " + heroInfo.getName());
                            }
                        }
                        break;
                    case R.id.option_download:
                        if (heroInfo.isOnline()) {
                            HeroExchange exchange = new HeroExchange(fragment.getActivity());

                            OnHeroExchangeListener listener = new OnHeroExchangeListener() {
                                @Override
                                public void onHeroInfoLoaded(List<HeroFileInfo> infos) {
                                    ViewUtils.snackbar(
                                            fragment.getActivity(),
                                            R.string.message_hero_successfully_downloaded, Snackbar.LENGTH_SHORT,
                                            heroInfo.getName());
                                }

                                @Override
                                public void onError(String errorMessage, Throwable exception) {
                                    ViewUtils.snackbar(fragment.getActivity(), errorMessage, Snackbar.LENGTH_LONG);
                                }

                            };
                            exchange.download(heroInfo, listener);
                        }
                        break;
                    case R.id.option_upload:
                        try {
                            fragment.exchange.upload(heroInfo);
                        } catch (IOException e) {
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
                Editor editor = DsaTabApplication.getPreferences().edit();
                editor.putString(DsaTabPreferenceActivity.KEY_EXCHANGE_TOKEN, editText.getText().toString());
                editor.commit();
            }
        });
        heldenAustauschDialog = builder.create();

        getLoaderManager().initLoader(LOCAL_LOADER, null, this);
    }

    private void loadExampleHeroes() {

        FileOutputStream fos = null;
        InputStream fis = null;
        try {
            File out = new File(DsaTabApplication.getInternalHeroDirectory(), DUMMY_FILE);
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
            return new HeroesLoaderTask(getActivity());
        } else {
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
        if (getActionBarActivity() == null)
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
                            "Token ungültig. Überprüfe ob das Token mit dem in der Helden-Software erstelltem Zugangstoken übereinstimmt.",
                            Snackbar.LENGTH_SHORT);
                } else if (e instanceof IOException) {
                    ViewUtils.snackbar(getActivity(), "Konnte keine Verbindung zum Austausch Server herstellen.",
                            Snackbar.LENGTH_SHORT);
                } else {
                    ViewUtils.snackbar(getActivity(), R.string.download_error, Snackbar.LENGTH_SHORT);
                    Debug.error(e);
                }
            }

            List<HeroFileInfo> fileInfos = adapter.getItems();
            for (HeroFileInfo fileInfo : heroes) {
                int index = fileInfos.indexOf(fileInfo);
                if (index >= 0) {
                    HeroFileInfo info = fileInfos.get(index);
                    info.merge(fileInfo);
                } else {
                    adapter.add(fileInfo);
                }
            }
            adapter.notifyDataSetChanged();
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

    protected void refresh(int loader) {
        getBaseActivity().setToolbarRefreshing(true);
        getActivity().getLoaderManager().restartLoader(loader, null, this);
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
        if (menuItem != null) {
            menuItem.setVisible(!exchange.isConnected(StorageType.Dropbox));
        }

        menuItem = menu.findItem(R.id.option_connect_drive);
        if (menuItem != null) {
            menuItem.setVisible(!exchange.isConnected(StorageType.Drive));
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
                refresh(REMOTE_LOADER);
                return true;
            case R.id.option_load_example_heroes:
                loadExampleHeroes();
                break;
            case R.id.option_connect_dropbox:
                if (!exchange.isConnected(StorageType.Dropbox)) {
                    DropboxActivity.launch(getActivity(), CONNECT_EXCHANGE_RESULT, DsaTabApplication.DROPBOX_API_KEY,
                            DsaTabApplication.DROPBOX_API_SECRET);
                }
                return true;
            case R.id.option_connect_drive:
                if (!exchange.isConnected(StorageType.Drive)) {
                    GoogleDriveActivity.launch(getActivity(), CONNECT_EXCHANGE_RESULT);
                }
                return true;
            case R.id.option_connect_heldenaustausch:
                heldenAustauschDialog.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    static class HeroAdapter extends ListRecyclerAdapter<HeroAdapter.HeroViewHolder, HeroFileInfo> {

        private Random rnd = new Random();

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
                holder.iv.setImageURI(Uri.parse(heroInfo.getPortraitUri()));
            } else {
                holder.iv.setImageResource(R.drawable.profile_picture);
            }

            int defaultHeight = holder.iv.getResources().getDimensionPixelSize(R.dimen.portrait_height);
            int profileHeight = defaultHeight;
            if (holder.iv.getDrawable() != null) {
                profileHeight = holder.iv.getDrawable().getIntrinsicHeight();
            }

            if (profileHeight > defaultHeight) {
                switch (heroInfo.hashCode() % 4) {
                    case 0:
                    case 1:
                        holder.iv.getLayoutParams().height = holder.iv.getResources().getDimensionPixelSize(R.dimen.portrait_height);
                        break;
                    case 2:
                        holder.iv.getLayoutParams().height = (int) (holder.iv.getResources().getDimensionPixelSize(R.dimen.portrait_height) * 1.5);
                        break;
                }
            } else {
                holder.iv.getLayoutParams().height = profileHeight;
            }

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
            if (hero.getStorageType() == StorageType.HeldenAustausch) {
                OnHeroExchangeListener listener = new OnHeroExchangeListener() {
                    @Override
                    public void onHeroInfoLoaded(List<HeroFileInfo> infos) {
                        if (infos != null && !infos.isEmpty()) {
                            if (onHeroSelectedListener != null) {
                                onHeroSelectedListener.onHeroSelected(infos.get(0));
                            }
                        }
                    }

                    @Override
                    public void onError(String errorMessage, Throwable exception) {
                        ViewUtils.snackbar(getActivity(), errorMessage, Snackbar.LENGTH_LONG);
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
