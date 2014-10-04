package com.dsatab.view.dialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.dsatab.R;
import com.dsatab.util.Util;
import com.gandulf.guilib.util.DirectoryFileFilter;

public class DirectoryChooserDialogHelper implements OnItemClickListener, OnClickListener {

	public interface Result {
		void onChooseDirectory(String dir);
	}

	private static final Comparator<File> NAMECOMPARATOR = new Comparator<File>() {
		@Override
		public int compare(File f1, File f2) {
			return f1.getName().toLowerCase(Locale.GERMAN).compareTo(f2.getName().toLowerCase(Locale.GERMAN));
		}
	};

	private File currentDir;
	private ListView list;
	private DirAdapter adapter;
	private Result result = null;

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

			if (item.equals(currentDir)) {
				textview.setText(item.getPath());
				textview.setCompoundDrawablesWithIntrinsicBounds(
						getContext().getResources().getDrawable(Util.getThemeResourceId(getContext(), R.attr.imgFile)),
						null, null, null);
			} else if (item.equals(currentDir.getParentFile())) {
				textview.setText("..");
				textview.setCompoundDrawablesWithIntrinsicBounds(
						getContext().getResources().getDrawable(Util.getThemeResourceId(getContext(), R.attr.imgUp)),
						null, null, null);
			} else {
				textview.setText(item.getName());
				textview.setCompoundDrawablesWithIntrinsicBounds(
						getContext().getResources().getDrawable(Util.getThemeResourceId(getContext(), R.attr.imgFile)),
						null, null, null);
			}

			return textview;
		}
	}

	private void refreshAdapter() {
		adapter.clear();

		// Add the ".." entry and current dir
		if (currentDir != null)
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

	public DirectoryChooserDialogHelper(Context ctx, Result res, String startDir) {

		result = res;

		if (startDir != null)
			currentDir = new File(startDir);
		else
			currentDir = Environment.getExternalStorageDirectory();

		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);

		adapter = new DirAdapter(builder.getContext(), android.R.layout.simple_list_item_1);

		refreshAdapter();

		builder.setTitle("Verzeichnis auswählen");
		builder.setAdapter(adapter, this);

		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				if (result != null)
					result.onChooseDirectory(currentDir.getAbsolutePath());
				dialog.dismiss();
			}
		});

		builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});

		AlertDialog alertDialog = builder.create();
		list = alertDialog.getListView();
		list.setOnItemClickListener(this);
		alertDialog.show();
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
		if (pos < 0 || pos >= adapter.getCount())
			return;

		currentDir = adapter.getItem(pos);

		refreshAdapter();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
	}
}