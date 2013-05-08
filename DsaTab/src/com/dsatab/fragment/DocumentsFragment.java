/*
 * Copyright (C) 2010 Gandulf Kohlweiss
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms
 * of the GNU General Public License as published by the Free Software Foundation;
 * either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program;
 * if not, see <http://www.gnu.org/licenses/>.
 * 
 */
package com.dsatab.fragment;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.activity.DsaTabPreferenceActivity;
import com.dsatab.data.Hero;
import com.dsatab.data.adapter.FileAdapter;
import com.dsatab.util.Util;
import com.dsatab.view.DirectoryChooserDialogHelper;
import com.dsatab.view.DirectoryChooserDialogHelper.Result;

public class DocumentsFragment extends BaseFragment implements OnItemClickListener {

	private ListView listView;
	private TextView empty;
	private FileAdapter documentsListAdapter;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = configureContainerView(inflater.inflate(R.layout.sheet_documents, container, false));

		listView = (ListView) root.findViewById(android.R.id.list);
		empty = (TextView) root.findViewById(android.R.id.empty);

		return root;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.actionbarsherlock.app.SherlockFragment#onCreateOptionsMenu(com.
	 * actionbarsherlock.view.Menu, com.actionbarsherlock.view.MenuInflater)
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.documents_menu, menu);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.actionbarsherlock.app.SherlockFragment#onOptionsItemSelected(com.
	 * actionbarsherlock.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == R.id.option_documents_choose) {
			Result resultListener = new Result() {
				/*
				 * (non-Javadoc)
				 * 
				 * @see com.dsatab.view.DirectoryChooserDialogHelper.Result#
				 * onChooseDirectory(java.lang.String)
				 */
				@Override
				public void onChooseDirectory(String dir) {

					File directory = new File(dir);
					if (directory.exists()) {
						Editor edit = preferences.edit();
						edit.putString(DsaTabPreferenceActivity.KEY_SETUP_SDCARD_DOCUMENTS_PATH, dir);
						edit.commit();

						reloadDirectory();
					} else {
						Toast.makeText(getActivity(), "Verzeichnis existiert nicht. Wähle bitte ein anderes aus.",
								Toast.LENGTH_LONG).show();
					}
				}
			};
			File docFile = DsaTabApplication.getDirectory(DsaTabApplication.DIR_PDFS);
			new DirectoryChooserDialogHelper(getActivity(), resultListener, docFile.getAbsolutePath());
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		registerForContextMenu(listView);
		listView.setOnItemClickListener(this);

		reloadDirectory();

		super.onActivityCreated(savedInstanceState);
	}

	private void reloadDirectory() {

		File pdfsDir = DsaTabApplication.getDirectory(DsaTabApplication.DIR_PDFS);
		if (pdfsDir != null && pdfsDir.exists() && pdfsDir.isDirectory()) {
			File[] pdfFiles = pdfsDir.listFiles();
			List<File> documents;
			if (pdfFiles != null) {
				documents = Arrays.asList(pdfFiles);
			} else
				documents = Collections.emptyList();

			if (documents.isEmpty()) {
				String path = pdfsDir.getAbsolutePath();
				empty.setVisibility(View.VISIBLE);
				listView.setVisibility(View.GONE);
				empty.setText(Util.getText(R.string.message_documents_empty, path));
			} else {
				Collections.sort(documents, new Util.FileNameComparator());
				empty.setVisibility(View.GONE);
				listView.setVisibility(View.VISIBLE);
			}
			documentsListAdapter = new FileAdapter(getActivity(), android.R.layout.simple_list_item_1, documents);
			listView.setAdapter(documentsListAdapter);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.dsatab.activity.BaseMenuActivity#onHeroLoaded(com.dsatab.data.Hero)
	 */
	@Override
	public void onHeroLoaded(Hero hero) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget
	 * .AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		File file = (File) listView.getItemAtPosition(position);

		if (file.exists() && file.isFile()) {
			Uri path = Uri.fromFile(file);
			Intent intent = new Intent(Intent.ACTION_VIEW);
			String ext = MimeTypeMap.getFileExtensionFromUrl(path.toString());
			if (ext != null)
				intent.setDataAndType(path, MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext));
			else {
				intent.setData(path);
			}
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

			try {
				startActivity(intent);
			} catch (ActivityNotFoundException e) {
				Toast.makeText(getActivity(), "Keine App zum Betrachten von " + file.getName() + " gefunden",
						Toast.LENGTH_SHORT).show();
			}
		}

	}
}
