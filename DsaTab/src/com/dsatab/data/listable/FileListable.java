package com.dsatab.data.listable;

import java.io.File;

import com.dsatab.R;

public class FileListable implements Listable {

	public static final String PDF_SUFFIX = ".pdf";
	public static final String DOC_SUFFIX = ".doc";

	private File file;

	public FileListable(File file) {
		this.file = file;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public int getIcon() {
		if (file.getName().endsWith(FileListable.PDF_SUFFIX)) {
			return R.drawable.tab_pdf;
		} else {
			return 0;
		}
	}

}
