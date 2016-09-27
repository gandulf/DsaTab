package com.dsatab.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.activity.TabEditActivity;
import com.dsatab.config.TabInfo;
import com.dsatab.data.Hero;
import com.dsatab.data.adapter.ListItemConfigAdapter;
import com.dsatab.data.adapter.SpinnerSimpleAdapter;
import com.dsatab.fragment.dialog.ImageChooserDialog;
import com.dsatab.util.Util;
import com.dsatab.view.ListSettings;
import com.dsatab.view.ListSettings.ListItem;
import com.dsatab.view.ListSettings.ListItemType;
import com.gandulf.guilib.util.DefaultTextWatcher;
import com.gandulf.guilib.util.ResUtil;
import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class TabEditFragment extends BaseRecyclerFragment implements
		OnItemSelectedListener, OnCheckedChangeListener, EditFragment {

	private TabInfo info;
	private ListSettings listSettings;
	private int index;

    private ImageView iconView;
    private EditText editTitle;
	private Spinner spinner;
	private CheckBox modifier;

    private FloatingActionButton fab;

	private ListItemConfigAdapter mAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle extra = getExtra();
        if (extra != null) {
            TabInfo tabInfo = extra.getParcelable(TabEditActivity.DATA_INTENT_TABINFO);
            if (tabInfo!=null) {
                setTabInfo(tabInfo, getTabInfoIndex());
            }
        }
    }

    public int getTabInfoIndex() {
        return Util.parseInt(getTag(),0);
    }

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View root = inflater.inflate(R.layout.tab_edit_content, container, false);

		spinner = (Spinner) root.findViewById(R.id.popup_tab_type);
		spinner.setOnItemSelectedListener(this);

		recyclerView = (RecyclerView) root.findViewById(android.R.id.list);

		spinner.setAdapter(new SpinnerSimpleAdapter<String>(getActivity(), BaseFragment.activities));

		modifier = (CheckBox) root.findViewById(R.id.popup_edit_include_modifiers);

        editTitle = (EditText) root.findViewById(R.id.popup_edit_title);
        editTitle.addTextChangedListener(new DefaultTextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (info != null) {
                    info.setTitle(s.toString());
                }
            }

        });

        iconView = (ImageView) root.findViewById(R.id.popup_edit_icon);
        iconView.setOnClickListener(this);

        fab =(FloatingActionButton) root.findViewById(R.id.fab);
        fab.setOnClickListener(this);
		return root;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

        mAdapter = new ListItemConfigAdapter(getActivity(), DsaTabApplication.getInstance().getHero(),
                new ArrayList<ListSettings.ListItem>());

        View details = view.findViewById(R.id.popup_edit_details);
        if (getTabInfoIndex()>0)
            details.setVisibility(View.GONE);
        else
            details.setVisibility(View.VISIBLE);

        initRecyclerView(recyclerView,mAdapter,true,true,false);
	}



	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mAdapter = null;
	}
	/*
         * (non-Javadoc)
         *
         * @see android.widget.AdapterView.OnItemSelectedListener#onItemSelected(android .widget.AdapterView,
         * android.view.View, int, long)
         */
	@Override
	public void onItemSelected(AdapterView<?> adapter, View view, int position, long id) {
		if (info != null && adapter == spinner) {
			Class<? extends BaseFragment> clazz = BaseFragment.activityValues.get(adapter.getSelectedItemPosition());
			info.setActivityClazz(index, clazz);
			updateListSettings();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.CompoundButton.OnCheckedChangeListener#onCheckedChanged (android.widget.CompoundButton,
	 * boolean)
	 */
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (info != null) {
			switch (buttonView.getId()) {
			case R.id.popup_edit_include_modifiers:
				listSettings.setIncludeModifiers(isChecked);
				break;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.AdapterView.OnItemSelectedListener#onNothingSelected(android .widget.AdapterView)
	 */
	@Override
	public void onNothingSelected(AdapterView<?> adapter) {
		if (info != null && adapter == spinner) {
			info.setActivityClazz(index, null);
			updateListSettings();
		}
	}

    private void pickIcon() {

        ImageChooserDialog.pickIcons(null, getFragmentManager(), new ImageChooserDialog.OnImageSelectedListener() {

            @Override
            public void onImageSelected(Uri imageUri) {
                info.setIconUri(imageUri);
                iconView.setImageDrawable(ResUtil.getDrawableByUri(iconView.getContext(),imageUri));
            }
        }, 0);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.popup_edit_icon:
                pickIcon();
                break;
            case R.id.fab:
                openListablePopup(v);
                break;
        }
        super.onClick(v);
    }

	public void setTabInfo(TabInfo info, int index) {
		this.info = info;
		this.index = index;
		spinner.setEnabled(info != null);
		if (info != null) {
			this.listSettings = info.getListSettings(index);

			Class<? extends BaseFragment> clazz = info.getActivityClazz(index);
			spinner.setSelection(BaseFragment.activityValues.indexOf(clazz));
            iconView.setImageDrawable(ResUtil.getDrawableByUri(iconView.getContext(),info.getIconUri()));
            editTitle.setText(info.getTitle());

        } else {
			listSettings = null;
            editTitle.setText(null);
		}

        editTitle.setEnabled(info != null);
        iconView.setEnabled(info != null);

		updateListSettings();


	}

	private void updateListSettings() {

		if (info != null && info.getListSettings(index) != null) {
			listSettings = info.getListSettings(index);

			if (info.getActivityClazz(index) == ListableFragment.class) {
                modifier.setVisibility(View.VISIBLE);
                modifier.setChecked(listSettings.isIncludeModifiers());
                modifier.setOnCheckedChangeListener(this);

				recyclerView.setVisibility(View.VISIBLE);
                fab.setVisibility(View.VISIBLE);
				mAdapter.clear();
				mAdapter.addAll(listSettings.getListItems());
			} else {
                modifier.setVisibility(View.GONE);
                modifier.setOnCheckedChangeListener(null);
				recyclerView.setVisibility(View.GONE);
                fab.setVisibility(View.GONE);
			}
		} else {
            modifier.setVisibility(View.GONE);
            modifier.setOnCheckedChangeListener(null);
			recyclerView.setVisibility(View.GONE);
            fab.setVisibility(View.GONE);
		}
	}

	public Bundle accept() {
		if (listSettings != null && mAdapter != null) {
			listSettings.getListItems().clear();
			listSettings.getListItems().addAll(mAdapter.getItems());
		}

        Bundle data = new Bundle();
        data.putParcelable(TabEditActivity.DATA_INTENT_TABINFO, info);
        return data;
	}

	public void cancel() {

	}

    public boolean openListablePopup(View anchor) {
        if (listSettings!=null) {
            List<String> listTypeTitles = new ArrayList<String>();
            for (ListItemType listItemType : ListItemType.values()) {
                listTypeTitles.add(listItemType.title());
            }

            PopupMenu popupMenu = new PopupMenu(getActivity(), anchor);
            for (int i = 0; i < listTypeTitles.size(); i++) {
                popupMenu.getMenu().add(0, i, i, listTypeTitles.get(i));
            }

            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    ListItem newListItem = new ListItem((ListItemType) ListItemType.values()[item.getItemId()]);
                    mAdapter.add(newListItem);
                    listSettings.getListItems().add(newListItem);

                    mAdapter.editListItem(newListItem);
                    return true;
                }
            });
            popupMenu.show();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onHeroLoaded(Hero hero) {

    }
}
