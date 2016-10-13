package com.dsatab.util;

import java.io.File;
import java.io.FileFilter;

public class DirectoryFileFilter implements FileFilter {

	public DirectoryFileFilter() {
	}

	@Override
	public boolean accept(File pathname) {
		return pathname.isDirectory();
	}

}
