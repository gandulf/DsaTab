package com.dsatab.db;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class AssetsDatabaseHelper extends SQLiteAssetHelper {

	public AssetsDatabaseHelper(Context context) {
		super(context, DatabaseHelper.DATABASE_NAME, null, DatabaseHelper.DATABASE_VERSION);
	}

}
