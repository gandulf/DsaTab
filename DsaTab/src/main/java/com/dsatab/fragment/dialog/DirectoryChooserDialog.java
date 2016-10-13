package com.dsatab.fragment.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dsatab.util.Util;
import com.dsatab.util.ViewUtils;
import com.dsatab.util.DirectoryFileFilter;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class DirectoryChooserDialog extends AppCompatDialogFragment implements AdapterView.OnItemClickListener {

	public static final String TAG = "DirectoryChooserDialogHelper";

	public interface OnDirectoryChooserListener {
		void onChooseDirectory(String dir);
	}

	private static final Comparator<File> NAMECOMPARATOR = new Util.FileNameComparator();

	private File currentDir;

	private DirAdapter adapter;
	private OnDirectoryChooserListener result = null;

	public class DirAdapter extends ArrayAdapter<File> {
		public DirAdapter(Context context, int resid) {
			super(context, resid, new ArrayList<File>());
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

			File item = getItem(position);

			if (currentDir!=null &&  item.equals(currentDir)) {
				textview.setText(item.getPath());
				textview.setCompoundDrawablesWithIntrinsicBounds(
                        ViewUtils.icon(getContext(), MaterialDrawableBuilder.IconValue.FILE)
                        ,
						null, null, null);
			} else if (currentDir!=null && item.equals(currentDir.getParentFile())) {
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

		// Add the ".." entry and current dir
		if (currentDir != null) {
            adapter.add(currentDir);
            if (currentDir.getParent() != null)
                adapter.add(currentDir.getParentFile());

            // Get files
            File[] files = currentDir.listFiles(new DirectoryFileFilter());
            if (files != null) {
                Arrays.sort(files, NAMECOMPARATOR);
                adapter.addAll(files);
            }
        }
	}

	public static void show(Fragment parent, String startDir, OnDirectoryChooserListener res, int requestCode) {
		show(parent, parent.getFragmentManager(), startDir, res, requestCode);

	}

	public static void show(Fragment parent, FragmentManager fragmentManager, String startDir,
			OnDirectoryChooserListener res, int requestCode) {
		DirectoryChooserDialog dialog = new DirectoryChooserDialog();

		Bundle args = new Bundle();
		// TODO value should be set as argument
		dialog.result = res;
		if (startDir != null)
			dialog.currentDir = new File(startDir);
		else
			dialog.currentDir = Environment.getExternalStorageDirectory();

		dialog.setArguments(args);
		dialog.setTargetFragment(parent, requestCode);
		dialog.show(fragmentManager, TAG);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

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
					result.onChooseDirectory(currentDir!=null ? currentDir.getAbsolutePath(): null);
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
        refreshAdapter();
    }



}