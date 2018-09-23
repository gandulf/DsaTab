package com.dsatab.fragment.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cloudrail.si.types.CloudMetaData;
import com.dsatab.cloud.HeroExchange;
import com.dsatab.util.ViewUtils;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CloudDirectoryChooserDialog extends AppCompatDialogFragment implements AdapterView.OnItemClickListener {

	public static final String TAG = "CloudDirectoryChooserDialogHelper";

    private static final String KEY_DIRECTORY ="directory";
    private static final String KEY_STORAGE_TYPE="storageType";

	public interface OnDirectoryChooserListener {
		void onChooseDirectory(CloudMetaData dirMetaData);
	}

    public static class CloudMetaDataFileNameComparator implements Comparator<CloudMetaData> {
        /*
         * (non-Javadoc)
         *
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare(CloudMetaData object1, CloudMetaData object2) {
            return object1.getName().compareToIgnoreCase(object2.getName());
        }
    }
	public static final Comparator<CloudMetaData> NAMECOMPARATOR = new CloudMetaDataFileNameComparator();

	private CloudMetaData currentDir;
    private CloudMetaData parentDir;

	private DirAdapter adapter;
	private OnDirectoryChooserListener result = null;

	public class DirAdapter extends ArrayAdapter<CloudMetaData> {
		public DirAdapter(Context context, int resid) {
			super(context, resid, new ArrayList<CloudMetaData>());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.BaseAdapter#isEnabled(int)
		 */
		@Override
		public boolean isEnabled(int position) {
			return position != 0;
		}

		// This function is called to show each view item
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView textview = (TextView) super.getView(position, convertView, parent);

			CloudMetaData item = getItem(position);

			if (currentDir!=null &&  item.equals(currentDir)) {
				textview.setText(item.getPath());
				textview.setCompoundDrawablesWithIntrinsicBounds(
                        ViewUtils.icon(getContext(), MaterialDrawableBuilder.IconValue.FILE)
                        ,
						null, null, null);
			} else if (currentDir!=null && item.equals(parentDir)) {
				textview.setText("..");
				textview.setCompoundDrawablesWithIntrinsicBounds(
                        ViewUtils.icon(getContext(), MaterialDrawableBuilder.IconValue.CHEVRON_UP),
						null, null, null);
			} else {
				textview.setText(item.getName());
				textview.setCompoundDrawablesWithIntrinsicBounds(
                        ViewUtils.icon(getContext(), MaterialDrawableBuilder.IconValue.FILE),
						null, null, null);
			}

			return textview;
		}
	}

	private void refreshAdapter() {
		adapter.clear();

        HeroExchange.StorageType storageType = HeroExchange.StorageType.valueOf(getArguments().getString(KEY_STORAGE_TYPE));
		// Add the ".." entry and current dir

        HeroExchange.getInstance().getDirectories(storageType, currentDir.getPath(), new HeroExchange.CloudResult<List<CloudMetaData>>() {
            @Override
            public void onSuccess(List<CloudMetaData> result) {
                adapter.clear();
                adapter.add(currentDir);
                if (parentDir!=null) {
                    adapter.add(parentDir);
                }
                if (result!=null) {
                    adapter.addAll(result);
                }
            }
        });

	}

	public static void show(Fragment parent, String startDir, HeroExchange.StorageType storageType, OnDirectoryChooserListener res, int requestCode) {
		show(parent, parent.getFragmentManager(), startDir, storageType, res, requestCode);

	}

	public static void show(Fragment parent, FragmentManager fragmentManager, String startDir, HeroExchange.StorageType storageType,
			OnDirectoryChooserListener res, int requestCode) {
		CloudDirectoryChooserDialog dialog = new CloudDirectoryChooserDialog();

		Bundle args = new Bundle();
        args.putString(KEY_DIRECTORY,startDir);
        args.putString(KEY_STORAGE_TYPE,storageType.name());
		// TODO value should be set as argument
		dialog.result = res;
		dialog.setArguments(args);
		dialog.setTargetFragment(parent, requestCode);
		dialog.show(fragmentManager, TAG);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

        currentDir = new CloudMetaData();
        currentDir.setPath(getArguments().getString(KEY_DIRECTORY));
        parentDir =getParentMetaData(currentDir);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		adapter = new DirAdapter(builder.getContext(), android.R.layout.simple_list_item_1);
		refreshAdapter();

		builder.setTitle("Verzeichnis auswählen");
		builder.setAdapter(adapter,null);

		builder.setNegativeButton(android.R.string.cancel, null);
		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				if (result != null)
					result.onChooseDirectory(currentDir);
				dialog.dismiss();
			}
		});

		AlertDialog dialog = builder.create();
        dialog.getListView().setOnItemClickListener(this);
		return dialog;
	}

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position < 0 || position >= adapter.getCount())
            return;

        currentDir = adapter.getItem(position);
        parentDir =getParentMetaData(currentDir);
        refreshAdapter();
    }

    private CloudMetaData getParentMetaData(CloudMetaData dir) {
        if ("/".equals(dir.getPath()))
            return null;

        CloudMetaData parent = new CloudMetaData();
        String parentPath = StringUtils.substringBeforeLast(dir.getPath(),"/");
        if (StringUtils.isBlank(parentPath)) {
            parentPath="/";
        }
        parent.setPath(parentPath);
        return parent;
    }

}