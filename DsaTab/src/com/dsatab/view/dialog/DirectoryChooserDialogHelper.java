package com.dsatab.view.dialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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

public class DirectoryChooserDialogHelper implements OnItemClickListener, OnClickListener {

	public interface Result {
		void onChooseDirectory(String dir);
	}

	private List<File> entries = new ArrayList<File>();
	private File currentDir;
	private Context context;
	private ListView list;
	private Result result = null;

	public class DirAdapter extends ArrayAdapter<File> {
		public DirAdapter(int resid) {
			super(context, resid, entries);
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

			if (entries.get(position).equals(currentDir)) {
				textview.setText(entries.get(position).getPath());
				textview.setCompoundDrawablesWithIntrinsicBounds(
						context.getResources().getDrawable(Util.getThemeResourceId(getContext(), R.attr.imgFile)),
						null, null, null);
			} else if (entries.get(position).equals(currentDir.getParentFile())) {
				textview.setText("..");
				textview.setCompoundDrawablesWithIntrinsicBounds(
						context.getResources().getDrawable(Util.getThemeResourceId(getContext(), R.attr.imgUp)), null,
						null, null);
			} else {
				textview.setText(entries.get(position).getName());
				textview.setCompoundDrawablesWithIntrinsicBounds(
						context.getResources().getDrawable(Util.getThemeResourceId(getContext(), R.attr.imgFile)),
						null, null, null);
			}

			return textview;
		}
	}

	private void listDirs() {
		entries.clear();

		// Get files
		File[] files = currentDir.listFiles();

		if (files != null) {
			for (File file : files) {
				if (!file.isDirectory())
					continue;

				entries.add(file);
			}
		}

		Collections.sort(entries, new Comparator<File>() {
			@Override
			public int compare(File f1, File f2) {
				return f1.getName().toLowerCase(Locale.GERMAN).compareTo(f2.getName().toLowerCase(Locale.GERMAN));
			}
		});

		// Add the ".." entry and current dir
		if (currentDir.getParent() != null)
			entries.add(0, currentDir.getParentFile());
		if (currentDir != null)
			entries.add(0, currentDir);

	}

	public DirectoryChooserDialogHelper(Context ctx, Result res, String startDir) {
		context = ctx;
		result = res;

		if (startDir != null)
			currentDir = new File(startDir);
		else
			currentDir = Environment.getExternalStorageDirectory();

		listDirs();
		DirAdapter adapter = new DirAdapter(android.R.layout.simple_list_item_1);

		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
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
		if (pos < 0 || pos >= entries.size())
			return;

		if (entries.get(pos).getName().equals(".."))
			currentDir = currentDir.getParentFile();
		else
			currentDir = entries.get(pos);

		listDirs();
		DirAdapter adapter = new DirAdapter(android.R.layout.simple_list_item_1);
		list.setAdapter(adapter);
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
	}
}