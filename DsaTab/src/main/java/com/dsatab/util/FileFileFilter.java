package com.dsatab.util;

import java.io.File;
import java.io.FileFilter;

public class FileFileFilter implements FileFilter {

	public FileFileFilter() {

	}

	@Override
	public boolean accept(File pathname) {
		return pathname.isFile();
	}

}
