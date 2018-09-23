package com.dsatab.data.listable;

import com.dsatab.R;

import java.io.File;

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
			return R.drawable.vd_tied_scroll;
		} else {
            return R.drawable.vd_cubes;
		}
	}

    @Override
    public long getId() {
        return file.hashCode();
    }
}
